package com.iiteam.montezuma3.core;

/** High level state machine of a running game. */
public enum GameState {

    /** Title screen, nothing is simulated yet. */
    MENU,
    /** Normal gameplay, the simulation advances. */
    PLAYING,
    /** Simulation frozen by the user. */
    PAUSED,
    /** All pickups collected and the exit door reached, waiting for confirmation. */
    LEVEL_COMPLETE,
    /** No lives left. */
    GAME_OVER,
    /** Every level finished. */
    VICTORY;

    public boolean isPlaying() {
        return this == PLAYING;
    }

    public boolean isFinal() {
        return this == GAME_OVER || this == VICTORY;
    }
}
