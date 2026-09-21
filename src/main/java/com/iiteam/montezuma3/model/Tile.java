package com.iiteam.montezuma3.model;

/** Kind of a single cell of a level map. */
public enum Tile {

    /** Impassable block (ground, wall, platform). */
    SOLID,
    /** Free space the player can walk / fall through. */
    EMPTY,
    /** Climbable ladder segment. Never solid. */
    LADDER;

    public boolean isSolid() {
        return this == SOLID;
    }

    public boolean isLadder() {
        return this == LADDER;
    }

    public boolean isPassable() {
        return this != SOLID;
    }
}
