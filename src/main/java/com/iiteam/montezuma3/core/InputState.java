package com.iiteam.montezuma3.core;

/**
 * Snapshot of everything the player currently presses. Deliberately free of AWT types so
 * the engine can be driven from tests as well as from a Swing key listener.
 */
public class InputState {

    private boolean left;
    private boolean right;
    private boolean up;
    private boolean down;
    private boolean jump;

    public boolean isLeft() {
        return left;
    }

    public void setLeft(boolean left) {
        this.left = left;
    }

    public boolean isRight() {
        return right;
    }

    public void setRight(boolean right) {
        this.right = right;
    }

    public boolean isUp() {
        return up;
    }

    public void setUp(boolean up) {
        this.up = up;
    }

    public boolean isDown() {
        return down;
    }

    public void setDown(boolean down) {
        this.down = down;
    }

    public boolean isJump() {
        return jump;
    }

    public void setJump(boolean jump) {
        this.jump = jump;
    }

    public void clear() {
        left = false;
        right = false;
        up = false;
        down = false;
        jump = false;
    }

    public boolean isAnyDirectionPressed() {
        return left || right || up || down;
    }
}
