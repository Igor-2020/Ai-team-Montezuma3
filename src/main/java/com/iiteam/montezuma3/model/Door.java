package com.iiteam.montezuma3.model;

/** Level exit. Opens as soon as every pickup of the level is collected. */
public class Door {

    private final double x;
    private final double y;
    private final double width;
    private final double height;
    private boolean open;

    public Door(double x, double y) {
        this.x = x;
        this.y = y;
        this.width = GameConstants.DOOR_WIDTH;
        this.height = GameConstants.DOOR_HEIGHT;
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

    public boolean isOpen() {
        return open;
    }

    public void setOpen(boolean open) {
        this.open = open;
    }

    public boolean intersects(Entity e) {
        return Entity.overlaps(x, y, width, height, e.getX(), e.getY(), e.getWidth(), e.getHeight());
    }
}
