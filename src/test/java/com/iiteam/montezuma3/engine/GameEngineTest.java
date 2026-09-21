package com.iiteam.montezuma3.engine;

import com.iiteam.montezuma3.core.GameState;
import com.iiteam.montezuma3.core.InputState;
import com.iiteam.montezuma3.level.LevelParser;
import com.iiteam.montezuma3.model.GameConstants;
import com.iiteam.montezuma3.model.Level;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GameEngineTest {

    /** One-key room: collect the key on the right, walk to the door. */
    private static final String ROOM = String.join("\n",
            "############",
            "#..........#",
            "#..........#",
            "#P...K....D#",
            "############");

    private static final String PIT = String.join("\n",
            "############",
            "#..........#",
            "#P.........#",
            "#..........#",
            "#####.######");

    private static final String GUARDED = String.join("\n",
            "######",
            "#....#",
            "#PE..#",
            "######");

    private static GameEngine engineOf(String map, int count) {
        List<Level> levels = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            levels.add(LevelParser.parse("level" + i, map));
        }
        return new GameEngine(levels);
    }

    private static void runTicks(GameEngine engine, InputState input, int ticks) {
        for (int i = 0; i < ticks; i++) {
            engine.tick(input);
        }
    }

    @Test
    void rejectsEmptyLevelList() {
        assertThrows(IllegalArgumentException.class, () -> new GameEngine(List.of()));
    }

    @Test
    void startsInMenuAndEntersPlayingOnNewGame() {
        GameEngine engine = engineOf(ROOM, 2);

        assertEquals(GameState.MENU, engine.getState());
        assertEquals(2, engine.getTotalLevels());
        assertNotNull(engine.getLevel());

        engine.startNewGame();

        assertEquals(GameState.PLAYING, engine.getState());
        assertEquals(GameConstants.STARTING_LIVES, engine.getLives());
        assertEquals(0, engine.getScore());
        assertEquals(1, engine.getLevelNumber());
        assertNotNull(engine.getPlayer());
        assertNotNull(engine.getDoor());
        assertEquals(1, engine.getPickups().size());
        assertFalse(engine.getDoor().isOpen());
    }

    @Test
    void simulationDoesNotAdvanceOutsidePlayingState() {
        GameEngine engine = engineOf(ROOM, 1);
        InputState input = new InputState();
        input.setRight(true);

        double before = engine.getPlayer().getX();
        runTicks(engine, input, 30);
        assertEquals(before, engine.getPlayer().getX(), 1e-9);

        engine.startNewGame();
        runTicks(engine, input, 30);
        assertTrue(engine.getPlayer().getX() > before);
    }

    @Test
    void collectsPickupAddsScoreAndOpensDoor() {
        GameEngine engine = engineOf(ROOM, 2);
        engine.startNewGame();
        InputState input = new InputState();
        input.setRight(true);

        runTicks(engine, input, 10);
        assertFalse(engine.getPickups().get(0).isCollected());
        assertFalse(engine.getDoor().isOpen());
        assertEquals(1, engine.getRemainingPickups());

        runTicks(engine, input, 190);

        assertTrue(engine.getPickups().get(0).isCollected());
        assertTrue(engine.getDoor().isOpen());
        assertTrue(engine.allPickupsCollected());
        assertEquals(0, engine.getRemainingPickups());
        assertTrue(engine.getScore() >= GameConstants.KEY_SCORE);
    }

    @Test
    void reachingOpenDoorCompletesLevel() {
        GameEngine engine = engineOf(ROOM, 2);
        engine.startNewGame();
        InputState input = new InputState();
        input.setRight(true);

        runTicks(engine, input, 400);

        assertEquals(GameState.LEVEL_COMPLETE, engine.getState());
        assertTrue(engine.getScore() >= GameConstants.KEY_SCORE + GameConstants.LEVEL_CLEAR_SCORE);
    }

    @Test
    void confirmAdvancesToNextLevel() {
        GameEngine engine = engineOf(ROOM, 3);
        engine.startNewGame();
        InputState input = new InputState();
        input.setRight(true);

        runTicks(engine, input, 400);
        assertEquals(GameState.LEVEL_COMPLETE, engine.getState());

        int scoreAfterFirstLevel = engine.getScore();
        engine.confirm();

        assertEquals(GameState.PLAYING, engine.getState());
        assertEquals(2, engine.getLevelNumber());
        assertEquals(scoreAfterFirstLevel, engine.getScore(), "score must survive level transition");
        assertFalse(engine.getPickups().get(0).isCollected(), "new level starts fresh");
    }

    @Test
    void completingLastLevelResultsInVictory() {
        GameEngine engine = engineOf(ROOM, 1);
        engine.startNewGame();
        InputState input = new InputState();
        input.setRight(true);

        runTicks(engine, input, 400);

        assertEquals(GameState.VICTORY, engine.getState());
    }

    @Test
    void confirmOnVictoryReturnsToMenuAndResetsProgress() {
        GameEngine engine = engineOf(ROOM, 1);
        engine.startNewGame();
        InputState input = new InputState();
        input.setRight(true);
        runTicks(engine, input, 400);
        assertEquals(GameState.VICTORY, engine.getState());

        engine.confirm();

        assertEquals(GameState.MENU, engine.getState());
        assertEquals(0, engine.getScore());
        assertEquals(GameConstants.STARTING_LIVES, engine.getLives());
    }

    @Test
    void pauseTogglesOnlyBetweenPlayingAndPaused() {
        GameEngine engine = engineOf(ROOM, 1);

        engine.togglePause();
        assertEquals(GameState.MENU, engine.getState(), "menu must not be paused");

        engine.startNewGame();
        engine.togglePause();
        assertEquals(GameState.PAUSED, engine.getState());

        engine.togglePause();
        assertEquals(GameState.PLAYING, engine.getState());
    }

    @Test
    void pausedGameDoesNotMove() {
        GameEngine engine = engineOf(ROOM, 1);
        engine.startNewGame();
        InputState input = new InputState();
        input.setRight(true);

        runTicks(engine, input, 10);
        engine.togglePause();
        double frozen = engine.getPlayer().getX();
        runTicks(engine, input, 50);

        assertEquals(frozen, engine.getPlayer().getX(), 1e-9);
    }

    @Test
    void fallingIntoAPitCostsALifeAndEventuallyEndsTheGame() {
        GameEngine engine = engineOf(PIT, 1);
        engine.startNewGame();
        InputState input = new InputState();
        input.setRight(true);

        runTicks(engine, input, 600);

        assertEquals(GameState.GAME_OVER, engine.getState());
        assertEquals(0, engine.getLives());
    }

    @Test
    void touchingAnEnemyCostsALife() {
        GameEngine engine = engineOf(GUARDED, 1);
        engine.startNewGame();
        InputState input = new InputState();

        runTicks(engine, input, 400);

        assertTrue(engine.getLives() < GameConstants.STARTING_LIVES,
                "a patrolling enemy must eventually hit the idle player");
    }

    @Test
    void playerCanClimbALadder() {
        String map = String.join("\n",
                "##########",
                "#........#",
                "#........#",
                "#...HH...#",
                "#P..HH...#",
                "##########");
        GameEngine engine = engineOf(map, 1);
        engine.startNewGame();

        double startY = engine.getPlayer().getY();
        InputState input = new InputState();
        input.setRight(true);
        runTicks(engine, input, 60);
        input.setRight(false);
        input.setUp(true);
        runTicks(engine, input, 60);

        assertTrue(engine.getPlayer().getY() < startY, "climbing up must raise the player");
    }

    @Test
    void defaultEngineLoadsBuiltInLevelsAndRunsHeadless() {
        GameEngine engine = GameEngine.createDefault();
        assertTrue(engine.getTotalLevels() >= 3);
        engine.startNewGame();
        assertEquals(GameState.PLAYING, engine.getState());

        InputState input = new InputState();
        for (int i = 0; i < 120; i++) {
            engine.tick(input);
        }
        assertEquals(GameState.PLAYING, engine.getState(), "idle player must survive two seconds");
        assertTrue(engine.getTotalTicks() >= 120);
    }

    @Test
    void tickRejectsNullInput() {
        GameEngine engine = engineOf(ROOM, 1);
        engine.startNewGame();
        assertThrows(IllegalArgumentException.class, () -> engine.tick(null));
    }
}
