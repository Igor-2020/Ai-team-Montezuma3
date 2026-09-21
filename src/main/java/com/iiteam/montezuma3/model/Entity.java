package com.iiteam.montezuma3.model;

/**
 * Axis aligned, movable object living in the level, expressed in pixels.
 * Pure logic - no rendering knowledge.
 */
public abstract class Entity {

    protected double x;
    protected double y;
    protected double width;
    protected double height;
    protected double vx;
    protected double vy;
    protected boolean alive = true;

    protected Entity(double x, double y, double width, double height) {
        if (width <= 0 || height <= 0) {
            throw new IllegalArgumentException("Entity size must be positive");
        }
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getWidth() {
        return width;
    }

    public double getHeight() {
        return height;
    }

    public double getVx() {
        return vx;
    }

    public void setVx(double vx) {
        this.vx = vx;
    }

    public double getVy() {
        return vy;
    }

    public void setVy(double vy) {
        this.vy = vy;
    }

    public boolean isAlive() {
        return alive;
    }

    public void setAlive(boolean alive) {
        this.alive = alive;
    }

    public double getCenterX() {
        return x + width / 2.0;
    }

    public double getCenterY() {
        return y + height / 2.0;
    }

    public double getRight() {
        return x + width;
    }

    public double getBottom() {
        return y + height;
    }

    /** Static AABB overlap test on raw rectangles. */
    public static boolean overlaps(double ax, double ay, double aw, double ah,
                                   double bx, double by, double bw, double bh) {
        return ax < bx + bw && ax + aw > bx && ay < by + bh && ay + ah > by;
    }

    public boolean intersects(Entity other) {
        return overlaps(x, y, width, height, other.x, other.y, other.width, other.height);
    }

    public boolean intersects(double ox, double oy, double ow, double oh) {
        return overlaps(x, y, width, height, ox, oy, ow, oh);
    }
}
