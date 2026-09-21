package com.iiteam.montezuma3.physics;

import com.iiteam.montezuma3.level.LevelParser;
import com.iiteam.montezuma3.model.Entity;
import com.iiteam.montezuma3.model.GameConstants;
import com.iiteam.montezuma3.model.Level;
import com.iiteam.montezuma3.model.Player;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PhysicsTest {

    private static final String FLAT = String.join("\n",
            "##########",
            "#........#",
            "#........#",
            "#........#",
            "##########");

    private static final String LADDERS = String.join("\n",
            "######",
            "#....#",
            "#.HH.#",
            "#.HH.#",
            "######");

    private static Level flat() {
        return LevelParser.parse("flat", FLAT);
    }

    @Test
    void gravityAcceleratesDownwards() {
        Entity e = new Entity(40, 40, 20, 20) {
        };
        assertEquals(0.0, e.getVy(), 1e-9);
        Physics.applyGravity(e, 0.5, 10.0);
        assertEquals(0.5, e.getVy(), 1e-9);
        Physics.applyGravity(e, 0.5, 10.0);
        assertEquals(1.0, e.getVy(), 1e-9);
    }

    @Test
    void gravityIsClampedToTerminalSpeed() {
        Entity e = new Entity(40, 40, 20, 20) {
        };
        for (int i = 0; i < 100; i++) {
            Physics.applyGravity(e, 1.0, 8.0);
        }
        assertEquals(8.0, e.getVy(), 1e-9);
    }

    @Test
    void fallingBodyLandsOnSolidGround() {
        Level level = flat();
        Entity e = new Entity(40, 40, 20, 20) {
        };

        for (int i = 0; i < 200; i++) {
            Physics.applyGravity(e, GameConstants.GRAVITY, GameConstants.MAX_FALL_SPEED);
            Physics.moveY(level, e, e.getVy());
        }

        assertEquals(4 * 32 - 20, e.getY(), 0.01);
        assertTrue(Physics.isGrounded(level, e));
    }

    @Test
    void horizontalMovementStopsAtWall() {
        Level level = flat();
        Entity e = new Entity(40, 40, 20, 20) {
        };

        boolean blocked = false;
        for (int i = 0; i < 100 && !blocked; i++) {
            blocked = Physics.moveX(level, e, 5.0);
        }

        assertTrue(blocked);
        assertEquals(9 * 32 - 20, e.getX(), 0.01);
    }

    @Test
    void upwardMovementStopsAtCeiling() {
        Level level = LevelParser.parse("box", String.join("\n",
                "######",
                "#....#",
                "#....#",
                "######"));
        Entity e = new Entity(40, 36, 20, 20) {
        };
        e.setVy(-20.0);

        Physics.moveY(level, e, e.getVy());

        assertEquals(32.0, e.getY(), 0.01);
        assertEquals(0.0, e.getVy(), 1e-9);
    }

    @Test
    void ladderTilesAreDetected() {
        Level level = LevelParser.parse("ladders", LADDERS);
        Entity onLadder = new Player(70, 64);
        Entity offLadder = new Player(6, 64);

        assertTrue(Physics.isOnLadder(level, onLadder));
        assertFalse(Physics.isOnLadder(level, offLadder));
    }

    @Test
    void ladderTilesAreNeverSolid() {
        Level level = LevelParser.parse("ladders", LADDERS);
        assertFalse(level.isSolid(2, 2));
        assertTrue(Physics.isOnLadder(level, new Player(2 * 32 + 5, 2 * 32)));
    }
}
