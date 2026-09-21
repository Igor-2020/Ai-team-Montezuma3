package com.iiteam.montezuma3.model;

/**
 * Central tuning constants of the game world.
 * <p>
 * This type belongs to the pure logic layer and deliberately has no dependency on
 * Swing/AWT so that every simulation aspect can be exercised head-less.
 */
public final class GameConstants {

    private GameConstants() {
    }

    /** Size of a single map tile in pixels. */
    public static final int TILE_SIZE = 32;

    // --- Physics ---
    public static final double GRAVITY = 0.55;
    public static final double MAX_FALL_SPEED = 12.0;
    public static final double JUMP_VELOCITY = -9.2;
    public static final double MOVE_SPEED = 2.4;
    public static final double CLIMB_SPEED = 2.2;
    public static final double ENEMY_SPEED = 1.2;

    // --- Entity sizes (pixels) ---
    public static final double PLAYER_WIDTH = 22.0;
    public static final double PLAYER_HEIGHT = 28.0;
    public static final double ENEMY_WIDTH = 22.0;
    public static final double ENEMY_HEIGHT = 22.0;
    public static final double PICKUP_SIZE = 16.0;
    public static final double DOOR_WIDTH = 26.0;
    public static final double DOOR_HEIGHT = 30.0;

    // --- Rules ---
    public static final int STARTING_LIVES = 3;
    public static final int KEY_SCORE = 150;
    public static final int TREASURE_SCORE = 100;
    public static final int LEVEL_CLEAR_SCORE = 500;
}
