package com.iiteam.montezuma3.physics;

import com.iiteam.montezuma3.model.Entity;
import com.iiteam.montezuma3.model.GameConstants;
import com.iiteam.montezuma3.model.Level;

/**
 * Gravity, axis separated AABB movement resolution and ground / ladder probing.
 * Pure math against the tile grid - no rendering, no input, no Swing.
 */
public final class Physics {

    private static final int TILE = GameConstants.TILE_SIZE;
    private static final double EPS = 0.001;

    private Physics() {
    }

    /** Accelerate the body downwards, clamped to the terminal fall speed. */
    public static void applyGravity(Entity e, double gravity, double maxFallSpeed) {
        double vy = e.getVy() + gravity;
        if (vy > maxFallSpeed) {
            vy = maxFallSpeed;
        }
        e.setVy(vy);
    }

    /**
     * Move the body horizontally and resolve collisions with solid tiles.
     *
     * @return {@code true} when the movement was blocked
     */
    public static boolean moveX(Level level, Entity e, double dx) {
        if (dx == 0.0) {
            return false;
        }
        e.setX(e.getX() + dx);

        int y0 = tileY(e.getY() + EPS);
        int y1 = tileY(e.getY() + e.getHeight() - EPS);
        int x0 = tileX(e.getX() + EPS);
        int x1 = tileX(e.getX() + e.getWidth() - EPS);

        if (dx > 0) {
            for (int tx = x1; tx >= x0; tx--) {
                for (int ty = y0; ty <= y1; ty++) {
                    if (level.isSolid(tx, ty)) {
                        e.setX((double) tx * TILE - e.getWidth() - EPS);
                        e.setVx(0.0);
                        return true;
                    }
                }
            }
        } else {
            for (int tx = x0; tx <= x1; tx++) {
                for (int ty = y0; ty <= y1; ty++) {
                    if (level.isSolid(tx, ty)) {
                        e.setX((double) (tx + 1) * TILE + EPS);
                        e.setVx(0.0);
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * Move the body vertically and resolve collisions with solid tiles.
     *
     * @return {@code true} when the body landed on top of a solid tile, i.e. is grounded
     */
    public static boolean moveY(Level level, Entity e, double dy) {
        if (dy == 0.0) {
            return false;
        }
        e.setY(e.getY() + dy);

        int x0 = tileX(e.getX() + EPS);
        int x1 = tileX(e.getX() + e.getWidth() - EPS);
        int y0 = tileY(e.getY() + EPS);
        int y1 = tileY(e.getY() + e.getHeight() - EPS);

        if (dy > 0) {
            for (int ty = y1; ty >= y0; ty--) {
                for (int tx = x0; tx <= x1; tx++) {
                    if (level.isSolid(tx, ty)) {
                        e.setY((double) ty * TILE - e.getHeight() - EPS);
                        e.setVy(0.0);
                        return true;
                    }
                }
            }
        } else {
            for (int ty = y0; ty <= y1; ty++) {
                for (int tx = x0; tx <= x1; tx++) {
                    if (level.isSolid(tx, ty)) {
                        e.setY((double) (ty + 1) * TILE + EPS);
                        e.setVy(0.0);
                        return false;
                    }
                }
            }
        }
        return false;
    }

    /** Probe one pixel below the body for solid ground. */
    public static boolean isGrounded(Level level, Entity e) {
        int probeRow = tileY(e.getY() + e.getHeight() + 1.0);
        int x0 = tileX(e.getX() + EPS);
        int x1 = tileX(e.getX() + e.getWidth() - EPS);
        for (int tx = x0; tx <= x1; tx++) {
            if (level.isSolid(tx, probeRow)) {
                return true;
            }
        }
        return false;
    }

    /** True when the tile under the horizontal centre of the body is a ladder. */
    public static boolean isOnLadder(Level level, Entity e) {
        int tx = tileX(e.getX() + e.getWidth() / 2.0);
        int y0 = tileY(e.getY() + EPS);
        int y1 = tileY(e.getY() + e.getHeight() - EPS);
        for (int ty = y0; ty <= y1; ty++) {
            if (level.isLadder(tx, ty)) {
                return true;
            }
        }
        return false;
    }

    private static int tileX(double px) {
        return (int) Math.floor(px / TILE);
    }

    private static int tileY(double py) {
        return (int) Math.floor(py / TILE);
    }
}
