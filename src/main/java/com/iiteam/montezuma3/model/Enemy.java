package com.iiteam.montezuma3.model;

/** A patrolling hazard. Reverses direction when it bumps into a wall. */
public class Enemy extends Entity {

    /** +1 = moving right, -1 = moving left. */
    private double direction = 1.0;

    public Enemy(double x, double y) {
        super(x, y, GameConstants.ENEMY_WIDTH, GameConstants.ENEMY_HEIGHT);
    }

    public double getDirection() {
        return direction;
    }

    public void setDirection(double direction) {
        this.direction = direction;
    }

    public void reverseDirection() {
        this.direction = -this.direction;
    }
}
