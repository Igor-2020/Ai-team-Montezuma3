package com.iiteam.montezuma3.model;

/** The hero controlled by the user. */
public class Player extends Entity {

    private boolean grounded;
    private boolean onLadder;
    private boolean facingRight = true;

    public Player(double x, double y) {
        super(x, y, GameConstants.PLAYER_WIDTH, GameConstants.PLAYER_HEIGHT);
    }

    public boolean isGrounded() {
        return grounded;
    }

    public void setGrounded(boolean grounded) {
        this.grounded = grounded;
    }

    public boolean isOnLadder() {
        return onLadder;
    }

    public void setOnLadder(boolean onLadder) {
        this.onLadder = onLadder;
    }

    public boolean isFacingRight() {
        return facingRight;
    }

    public void setFacingRight(boolean facingRight) {
        this.facingRight = facingRight;
    }
}
