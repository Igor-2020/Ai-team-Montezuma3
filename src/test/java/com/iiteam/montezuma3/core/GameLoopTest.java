package com.iiteam.montezuma3.core;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GameLoopTest {

    @Test
    void firstCallOnlySynchronisesTheClock() {
        GameLoop loop = new GameLoop();
        loop.reset();
        assertEquals(0, loop.advance(1_000L, () -> {
        }));
    }

    @Test
    void runsOneTickPerFixedStep() {
        GameLoop loop = new GameLoop();
        int[] ticks = {0};
        loop.reset();
        loop.advance(0L, () -> ticks[0]++);

        assertEquals(0, ticks[0]);
        assertEquals(1, loop.advance(20_000_000L, () -> ticks[0]++));
        assertEquals(1, ticks[0]);
        assertEquals(1, loop.advance(40_000_000L, () -> ticks[0]++));
        assertEquals(2, ticks[0]);
    }

    @Test
    void largeFrameProducesProportionalNumberOfTicks() {
        GameLoop loop = new GameLoop();
        int[] ticks = {0};
        loop.reset();
        loop.advance(0L, () -> ticks[0]++);

        int produced = loop.advance(200_000_000L, () -> ticks[0]++);

        assertEquals(12, produced);
        assertEquals(12, ticks[0]);
    }

    @Test
    void hugeFrameIsClampedToAvoidSpiralOfDeath() {
        GameLoop loop = new GameLoop();
        int[] ticks = {0};
        loop.reset();
        loop.advance(0L, () -> ticks[0]++);

        int produced = loop.advance(10_000_000_000L, () -> ticks[0]++);

        assertTrue(produced >= 14 && produced <= 16, "expected clamped tick count, got " + produced);
        assertEquals(produced, ticks[0]);
    }

    @Test
    void tickCountIsDeterministic() {
        GameLoop loop = new GameLoop();
        int[] ticks = {0};
        loop.reset();
        loop.advance(0L, () -> ticks[0]++);
        long step = 16_666_666L;
        for (int i = 1; i <= 60; i++) {
            loop.advance(step * i, () -> ticks[0]++);
        }
        assertTrue(ticks[0] >= 59 && ticks[0] <= 60, "60 frames ~= 60 ticks, got " + ticks[0]);
    }
}
