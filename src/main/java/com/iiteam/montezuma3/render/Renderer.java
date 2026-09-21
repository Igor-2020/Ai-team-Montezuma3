package com.iiteam.montezuma3.render;

import com.iiteam.montezuma3.engine.GameEngine;
import com.iiteam.montezuma3.model.Door;
import com.iiteam.montezuma3.model.Enemy;
import com.iiteam.montezuma3.model.GameConstants;
import com.iiteam.montezuma3.model.Level;
import com.iiteam.montezuma3.model.Pickup;
import com.iiteam.montezuma3.model.Player;
import com.iiteam.montezuma3.model.Tile;

import java.awt.Color;
import java.awt.Graphics2D;

/** Draws the world (tiles, door, pickups, actors) with plain {@link Graphics2D} primitives. */
public class Renderer {

    private static final Color BACKGROUND = new Color(18, 16, 32);
    private static final Color SOLID = new Color(146, 96, 54);
    private static final Color SOLID_TOP = new Color(190, 138, 86);
    private static final Color SOLID_EDGE = new Color(96, 60, 32);
    private static final Color LADDER = new Color(214, 178, 92);
    private static final Color DOOR_CLOSED = new Color(112, 70, 36);
    private static final Color DOOR_OPEN = new Color(64, 200, 120);
    private static final Color KEY_COLOR = new Color(255, 214, 64);
    private static final Color TREASURE_COLOR = new Color(90, 226, 255);
    private static final Color PLAYER_BODY = new Color(240, 240, 250);
    private static final Color PLAYER_ACCENT = new Color(60, 120, 230);
    private static final Color ENEMY_COLOR = new Color(226, 72, 72);
    private static final Color ENEMY_EYE = new Color(255, 240, 240);

    public void render(Graphics2D g, GameEngine engine) {
        Level level = engine.getLevel();
        int tile = GameConstants.TILE_SIZE;

        g.setColor(BACKGROUND);
        g.fillRect(0, 0, level.getPixelWidth(), level.getPixelHeight());

        drawTiles(g, level, tile);
        drawDoor(g, engine.getDoor(), tile);

        for (Pickup pickup : engine.getPickups()) {
            drawPickup(g, pickup);
        }
        for (Enemy enemy : engine.getEnemies()) {
            drawEnemy(g, enemy);
        }
        drawPlayer(g, engine.getPlayer(), engine.getTotalTicks());
    }

    private void drawTiles(Graphics2D g, Level level, int tile) {
        for (int ty = 0; ty < level.getHeight(); ty++) {
            for (int tx = 0; tx < level.getWidth(); tx++) {
                Tile t = level.getTile(tx, ty);
                int px = tx * tile;
                int py = ty * tile;
                if (t == Tile.SOLID) {
                    g.setColor(SOLID);
                    g.fillRect(px, py, tile, tile);
                    g.setColor(SOLID_TOP);
                    g.fillRect(px, py, tile, 5);
                    g.setColor(SOLID_EDGE);
                    g.drawRect(px, py, tile - 1, tile - 1);
                } else if (t == Tile.LADDER) {
                    g.setColor(LADDER);
                    g.fillRect(px + 5, py, 3, tile);
                    g.fillRect(px + tile - 8, py, 3, tile);
                    for (int rung = 0; rung < 4; rung++) {
                        g.fillRect(px + 4, py + 4 + rung * 8, tile - 8, 2);
                    }
                }
            }
        }
    }

    private void drawDoor(Graphics2D g, Door door, int tile) {
        if (door == null) {
            return;
        }
        int x = (int) Math.round(door.getX());
        int y = (int) Math.round(door.getY());
        int w = (int) Math.round(door.getWidth());
        int h = (int) Math.round(door.getHeight());
        g.setColor(door.isOpen() ? DOOR_OPEN : DOOR_CLOSED);
        g.fillRect(x, y, w, h);
        g.setColor(Color.BLACK);
        g.drawRect(x, y, w, h);
        g.fillOval(x + w - 9, y + h / 2 - 3, 5, 5);
    }

    private void drawPickup(Graphics2D g, Pickup pickup) {
        if (pickup.isCollected()) {
            return;
        }
        int x = (int) Math.round(pickup.getX());
        int y = (int) Math.round(pickup.getY());
        int s = (int) Math.round(pickup.getWidth());
        if (pickup.getType() == Pickup.Type.KEY) {
            g.setColor(KEY_COLOR);
            g.fillOval(x, y, s / 2 + 4, s / 2 + 4);
            g.fillRect(x + s / 2 + 2, y + s / 4 + 1, s / 2 - 1, 3);
            g.fillRect(x + s - 4, y + s / 4 + 4, 3, 5);
        } else {
            g.setColor(TREASURE_COLOR);
            int[] xs = {x + s / 2, x + s, x + s / 2, x};
            int[] ys = {y, y + s / 2, y + s, y + s / 2};
            g.fillPolygon(xs, ys, 4);
        }
    }

    private void drawEnemy(Graphics2D g, Enemy enemy) {
        if (!enemy.isAlive()) {
            return;
        }
        int x = (int) Math.round(enemy.getX());
        int y = (int) Math.round(enemy.getY());
        int w = (int) Math.round(enemy.getWidth());
        int h = (int) Math.round(enemy.getHeight());
        g.setColor(ENEMY_COLOR);
        g.fillOval(x, y, w, h);
        g.setColor(ENEMY_EYE);
        g.fillOval(x + w / 4, y + h / 3, 4, 4);
        g.fillOval(x + w / 2 + 1, y + h / 3, 4, 4);
    }

    private void drawPlayer(Graphics2D g, Player player, long ticks) {
        if (player == null) {
            return;
        }
        int x = (int) Math.round(player.getX());
        int y = (int) Math.round(player.getY());
        int w = (int) Math.round(player.getWidth());
        int h = (int) Math.round(player.getHeight());

        g.setColor(PLAYER_ACCENT);
        g.fillRect(x, y + h / 2, w, h / 2 + 1);

        g.setColor(PLAYER_BODY);
        g.fillOval(x + 2, y, w - 4, w - 4);

        g.setColor(PLAYER_ACCENT.darker());
        int legShift = (int) ((ticks / 6) % 2 == 0 ? 0 : 2);
        g.fillRect(x + 3, y + h - 5 + legShift / 2, 6, 4);
        g.fillRect(x + w - 9, y + h - 5 - legShift / 2, 6, 4);
    }
}
