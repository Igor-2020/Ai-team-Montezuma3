package com.iiteam.montezuma3.model;

/** Collectible item lying in the world. */
public class Pickup {

    public enum Type {
        /** Required to open the exit door. */
        KEY,
        /** Pure score bonus. */
        TREASURE
    }

    private final Type type;
    private final double x;
    private final double y;
    private final double width;
    private final double height;
    private boolean collected;

    public Pickup(Type type, double x, double y) {
        this.type = type;
        this.x = x;
        this.y = y;
        this.width = GameConstants.PICKUP_SIZE;
        this.height = GameConstants.PICKUP_SIZE;
    }

    public Type getType() {
        return type;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getWidth() {
        return width;
    }

    public double getHeight() {
        return height;
    }

    public boolean isCollected() {
        return collected;
    }

    public void setCollected(boolean collected) {
        this.collected = collected;
    }

    public int getScoreValue() {
        return type == Type.KEY ? GameConstants.KEY_SCORE : GameConstants.TREASURE_SCORE;
    }

    public String getLabel() {
        return type == Type.KEY ? "key" : "treasure";
    }

    public boolean intersects(Entity e) {
        return Entity.overlaps(x, y, width, height, e.getX(), e.getY(), e.getWidth(), e.getHeight());
    }
}
