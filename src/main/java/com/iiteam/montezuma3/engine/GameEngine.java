package com.iiteam.montezuma3.engine;

import com.iiteam.montezuma3.core.GameState;
import com.iiteam.montezuma3.core.InputState;
import com.iiteam.montezuma3.level.LevelRepository;
import com.iiteam.montezuma3.model.Door;
import com.iiteam.montezuma3.model.Enemy;
import com.iiteam.montezuma3.model.GameConstants;
import com.iiteam.montezuma3.model.Level;
import com.iiteam.montezuma3.model.Pickup;
import com.iiteam.montezuma3.model.Player;
import com.iiteam.montezuma3.physics.Physics;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Central orchestrator. Owns the mutable game session (level, entities, score, lives) and
 * advances it exactly one fixed simulation step per {@link #tick(InputState)} call.
 * <p>
 * The class knows nothing about Swing/AWT, which keeps the whole rule set testable head-less.
 */
public class GameEngine {

    public static final int TICKS_PER_SECOND = 60;

    private final List<Level> levels;

    private GameState state = GameState.MENU;
    private int levelIndex;
    private int score;
    private int lives = GameConstants.STARTING_LIVES;
    private long totalTicks;

    private Level level;
    private Player player;
    private Door door;
    private final List<Enemy> enemies = new ArrayList<>();
    private final List<Pickup> pickups = new ArrayList<>();

    private double playerSpawnX;
    private double playerSpawnY;

    public GameEngine(List<Level> levels) {
        if (levels == null || levels.isEmpty()) {
            throw new IllegalArgumentException("At least one level is required");
        }
        this.levels = new ArrayList<>(levels);
        loadLevel(0);
    }

    /** Engine wired with the built-in level set from the classpath. */
    public static GameEngine createDefault() {
        return new GameEngine(new LevelRepository().loadAll());
    }

    // ------------------------------------------------------------------
    // Level loading
    // ------------------------------------------------------------------

    private void loadLevel(int index) {
        if (index < 0 || index >= levels.size()) {
            throw new IllegalArgumentException("Level index out of range: " + index);
        }
        this.levelIndex = index;
        this.level = levels.get(index);

        Level.TilePos spawn = level.getPlayerSpawn();
        if (spawn == null) {
            throw new IllegalStateException("Level '" + level.getName() + "' has no player spawn");
        }
        this.playerSpawnX = tileCenterX(spawn.tx(), GameConstants.PLAYER_WIDTH);
        this.playerSpawnY = tileBottomY(spawn.ty(), GameConstants.PLAYER_HEIGHT);
        this.player = new Player(playerSpawnX, playerSpawnY);

        this.enemies.clear();
        for (Level.TilePos p : level.getEnemySpawns()) {
            this.enemies.add(new Enemy(tileCenterX(p.tx(), GameConstants.ENEMY_WIDTH),
                    tileBottomY(p.ty(), GameConstants.ENEMY_HEIGHT)));
        }

        this.pickups.clear();
        for (Level.PickupSpawn ps : level.getPickupSpawns()) {
            this.pickups.add(new Pickup(ps.type(),
                    tileCenterX(ps.tx(), GameConstants.PICKUP_SIZE),
                    tileBottomY(ps.ty(), GameConstants.PICKUP_SIZE)));
        }

        Level.TilePos doorPos = level.getDoorSpawn();
        this.door = doorPos == null
                ? null
                : new Door(tileCenterX(doorPos.tx(), GameConstants.DOOR_WIDTH),
                            tileBottomY(doorPos.ty(), GameConstants.DOOR_HEIGHT));
        if (this.door != null) {
            this.door.setOpen(allPickupsCollected());
        }
    }

    private static double tileCenterX(int tx, double width) {
        return tx * GameConstants.TILE_SIZE + (GameConstants.TILE_SIZE - width) / 2.0;
    }

    private static double tileBottomY(int ty, double height) {
        return ty * GameConstants.TILE_SIZE + GameConstants.TILE_SIZE - height;
    }

    // ------------------------------------------------------------------
    // Simulation
    // ------------------------------------------------------------------

    /** Advance the world by exactly one fixed timestep. */
    public void tick(InputState input) {
        if (input == null) {
            throw new IllegalArgumentException("input must not be null");
        }
        if (state != GameState.PLAYING) {
            return;
        }
        totalTicks++;

        updatePlayer(input);
        updateEnemies();
        collectPickups();
        updateDoor();

        if (checkLevelCompleted()) {
            return;
        }
        checkHazards();
    }

    private void updatePlayer(InputState input) {
        boolean onLadder = Physics.isOnLadder(level, player);
        player.setOnLadder(onLadder);

        boolean jumped = false;
        if (input.isJump() && (player.isGrounded() || onLadder)) {
            player.setVy(GameConstants.JUMP_VELOCITY);
            player.setOnLadder(false);
            jumped = true;
        }

        if (player.isOnLadder() && !jumped) {
            double vy = 0.0;
            if (input.isUp()) {
                vy -= GameConstants.CLIMB_SPEED;
            }
            if (input.isDown()) {
                vy += GameConstants.CLIMB_SPEED;
            }
            player.setVy(vy);
        } else {
            Physics.applyGravity(player, GameConstants.GRAVITY, GameConstants.MAX_FALL_SPEED);
        }

        double dx = 0.0;
        if (input.isLeft()) {
            dx -= GameConstants.MOVE_SPEED;
            player.setFacingRight(false);
        }
        if (input.isRight()) {
            dx += GameConstants.MOVE_SPEED;
            player.setFacingRight(true);
        }
        player.setVx(dx);
        Physics.moveX(level, player, dx);

        boolean grounded = Physics.moveY(level, player, player.getVy());
        player.setGrounded(grounded);
    }

    private void updateEnemies() {
        for (Enemy enemy : enemies) {
            if (!enemy.isAlive()) {
                continue;
            }
            Physics.applyGravity(enemy, GameConstants.GRAVITY, GameConstants.MAX_FALL_SPEED);

            double step = enemy.getDirection() * GameConstants.ENEMY_SPEED;
            if (Physics.moveX(level, enemy, step)) {
                enemy.reverseDirection();
                Physics.moveX(level, enemy, enemy.getDirection() * GameConstants.ENEMY_SPEED);
            }
            Physics.moveY(level, enemy, enemy.getVy());
        }
    }

    private void collectPickups() {
        for (Pickup pickup : pickups) {
            if (!pickup.isCollected() && pickup.intersects(player)) {
                pickup.setCollected(true);
                score += pickup.getScoreValue();
            }
        }
    }

    private void updateDoor() {
        if (door != null) {
            door.setOpen(allPickupsCollected());
        }
    }

    private boolean checkLevelCompleted() {
        if (door == null || !door.isOpen() || !door.intersects(player)) {
            return false;
        }
        score += GameConstants.LEVEL_CLEAR_SCORE;
        state = (levelIndex + 1 >= levels.size()) ? GameState.VICTORY : GameState.LEVEL_COMPLETE;
        return true;
    }

    private void checkHazards() {
        if (player.getY() > level.getPixelHeight() + GameConstants.TILE_SIZE * 2) {
            loseLife();
            return;
        }
        for (Enemy enemy : enemies) {
            if (enemy.isAlive() && enemy.intersects(player)) {
                loseLife();
                return;
            }
        }
    }

    private void loseLife() {
        lives--;
        if (lives <= 0) {
            lives = 0;
            state = GameState.GAME_OVER;
        } else {
            respawn();
        }
    }

    private void respawn() {
        player.setX(playerSpawnX);
        player.setY(playerSpawnY);
        player.setVx(0.0);
        player.setVy(0.0);
        player.setGrounded(false);
        player.setOnLadder(false);

        List<Level.TilePos> spawns = level.getEnemySpawns();
        for (int i = 0; i < enemies.size() && i < spawns.size(); i++) {
            Enemy enemy = enemies.get(i);
            Level.TilePos p = spawns.get(i);
            enemy.setX(tileCenterX(p.tx(), GameConstants.ENEMY_WIDTH));
            enemy.setY(tileBottomY(p.ty(), GameConstants.ENEMY_HEIGHT));
            enemy.setVx(0.0);
            enemy.setVy(0.0);
            enemy.setAlive(true);
            enemy.setDirection(1.0);
        }
    }

    // ------------------------------------------------------------------
    // Commands
    // ------------------------------------------------------------------

    /** Start a fresh run from level one. */
    public void startNewGame() {
        score = 0;
        lives = GameConstants.STARTING_LIVES;
        totalTicks = 0;
        loadLevel(0);
        state = GameState.PLAYING;
    }

    /** Freeze / resume the simulation. Does nothing outside PLAYING / PAUSED. */
    public void togglePause() {
        if (state == GameState.PLAYING) {
            state = GameState.PAUSED;
        } else if (state == GameState.PAUSED) {
            state = GameState.PLAYING;
        }
    }

    /** Context sensitive confirmation: start, next level, back to menu. */
    public void confirm() {
        switch (state) {
            case MENU -> startNewGame();
            case LEVEL_COMPLETE -> advanceLevel();
            case GAME_OVER, VICTORY -> {
                score = 0;
                lives = GameConstants.STARTING_LIVES;
                loadLevel(0);
                state = GameState.MENU;
            }
            default -> {
                // PLAYING / PAUSED: nothing to confirm
            }
        }
    }

    private void advanceLevel() {
        int next = levelIndex + 1;
        if (next >= levels.size()) {
            state = GameState.VICTORY;
        } else {
            loadLevel(next);
            state = GameState.PLAYING;
        }
    }

    // ------------------------------------------------------------------
    // Queries
    // ------------------------------------------------------------------

    public GameState getState() {
        return state;
    }

    public Level getLevel() {
        return level;
    }

    public Player getPlayer() {
        return player;
    }

    public Door getDoor() {
        return door;
    }

    public List<Enemy> getEnemies() {
        return Collections.unmodifiableList(enemies);
    }

    public List<Pickup> getPickups() {
        return Collections.unmodifiableList(pickups);
    }

    public int getScore() {
        return score;
    }

    public int getLives() {
        return lives;
    }

    public int getLevelIndex() {
        return levelIndex;
    }

    public int getLevelNumber() {
        return levelIndex + 1;
    }

    public int getTotalLevels() {
        return levels.size();
    }

    public long getTotalTicks() {
        return totalTicks;
    }

    public boolean allPickupsCollected() {
        for (Pickup pickup : pickups) {
            if (!pickup.isCollected()) {
                return false;
            }
        }
        return true;
    }

    public int getRemainingPickups() {
        int remaining = 0;
        for (Pickup pickup : pickups) {
            if (!pickup.isCollected()) {
                remaining++;
            }
        }
        return remaining;
    }
}
