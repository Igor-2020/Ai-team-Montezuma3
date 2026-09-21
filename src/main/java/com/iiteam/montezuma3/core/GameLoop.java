package com.iiteam.montezuma3.core;

/**
 * Deterministic fixed timestep driver.
 * <p>
 * The renderer only decides <em>when</em> the loop is pumped; the number of simulation steps
 * is derived purely from elapsed wall clock time, so the game logic is independent of the
 * frame rate. Fully head-less friendly.
 */
public class GameLoop {

    public static final int TICKS_PER_SECOND = 60;
    public static final double FIXED_DT = 1.0 / TICKS_PER_SECOND;

    /** Upper bound for a single frame, protects against "spiral of death" after a pause. */
    public static final double MAX_FRAME_TIME = 0.25;

    private double accumulator;
    private long lastNanos = -1L;

    /** Forget any accumulated time; the next {@link #advance(long, Runnable)} only syncs the clock. */
    public void reset() {
        accumulator = 0.0;
        lastNanos = -1L;
    }

    /**
     * Advance the simulation for the time elapsed since the previous call.
     *
     * @param nowNanos   current value of {@link System#nanoTime()}
     * @param tickAction action executed exactly once per fixed simulation step
     * @return number of simulation steps executed by this call
     */
    public int advance(long nowNanos, Runnable tickAction) {
        if (tickAction == null) {
            throw new IllegalArgumentException("tickAction must not be null");
        }
        if (lastNanos < 0L) {
            lastNanos = nowNanos;
            return 0;
        }
        double elapsed = (nowNanos - lastNanos) / 1_000_000_000.0;
        lastNanos = nowNanos;
        if (elapsed < 0.0) {
            elapsed = 0.0;
        }
        if (elapsed > MAX_FRAME_TIME) {
            elapsed = MAX_FRAME_TIME;
        }
        accumulator += elapsed;

        int ticks = 0;
        while (accumulator >= FIXED_DT) {
            tickAction.run();
            accumulator -= FIXED_DT;
            ticks++;
        }
        return ticks;
    }

    public double getAccumulator() {
        return accumulator;
    }
}
