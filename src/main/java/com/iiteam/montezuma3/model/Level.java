package com.iiteam.montezuma3.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Immutable shape of a level: the tile grid plus every spawn marker found in the ASCII map.
 * Pixel positions of entities are derived by the engine, so the level itself stays free of
 * any concrete entity instance.
 */
public class Level {

    /** Tile coordinate of a spawn marker. */
    public record TilePos(int tx, int ty) {
    }

    /** Pickup marker with its kind. */
    public record PickupSpawn(Pickup.Type type, int tx, int ty) {
    }

    private final String name;
    private final int width;
    private final int height;
    private final Tile[][] tiles;

    private TilePos playerSpawn;
    private TilePos doorSpawn;

    private final List<TilePos> enemySpawns = new ArrayList<>();
    private final List<PickupSpawn> pickupSpawns = new ArrayList<>();

    public Level(String name, int width, int height) {
        if (width <= 0 || height <= 0) {
            throw new IllegalArgumentException("Level size must be positive");
        }
        this.name = name;
        this.width = width;
        this.height = height;
        this.tiles = new Tile[height][width];
        for (int ty = 0; ty < height; ty++) {
            for (int tx = 0; tx < width; tx++) {
                tiles[ty][tx] = Tile.EMPTY;
            }
        }
    }

    public String getName() {
        return name;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getPixelWidth() {
        return width * GameConstants.TILE_SIZE;
    }

    public int getPixelHeight() {
        return height * GameConstants.TILE_SIZE;
    }

    public void setTile(int tx, int ty, Tile tile) {
        if (inBounds(tx, ty)) {
            tiles[ty][tx] = (tile == null ? Tile.EMPTY : tile);
        }
    }

    public Tile getTile(int tx, int ty) {
        if (!inBounds(tx, ty)) {
            return Tile.EMPTY;
        }
        return tiles[ty][tx];
    }

    public boolean inBounds(int tx, int ty) {
        return tx >= 0 && tx < width && ty >= 0 && ty < height;
    }

    /**
     * Solid test used by the physics layer. Out of bounds behaviour is intentional:
     * the sides and the ceiling act as walls, while below the map is open so the player
     * can fall out of the level.
     */
    public boolean isSolid(int tx, int ty) {
        if (tx < 0 || tx >= width) {
            return true;
        }
        if (ty < 0) {
            return true;
        }
        if (ty >= height) {
            return false;
        }
        return tiles[ty][tx].isSolid();
    }

    public boolean isLadder(int tx, int ty) {
        if (!inBounds(tx, ty)) {
            return false;
        }
        return tiles[ty][tx].isLadder();
    }

    public void setPlayerSpawn(int tx, int ty) {
        this.playerSpawn = new TilePos(tx, ty);
    }

    public TilePos getPlayerSpawn() {
        return playerSpawn;
    }

    public void setDoorSpawn(int tx, int ty) {
        this.doorSpawn = new TilePos(tx, ty);
    }

    public TilePos getDoorSpawn() {
        return doorSpawn;
    }

    public void addEnemySpawn(int tx, int ty) {
        enemySpawns.add(new TilePos(tx, ty));
    }

    public List<TilePos> getEnemySpawns() {
        return Collections.unmodifiableList(enemySpawns);
    }

    public void addPickupSpawn(Pickup.Type type, int tx, int ty) {
        pickupSpawns.add(new PickupSpawn(type, tx, ty));
    }

    public List<PickupSpawn> getPickupSpawns() {
        return Collections.unmodifiableList(pickupSpawns);
    }
}
