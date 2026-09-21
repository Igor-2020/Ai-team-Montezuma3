package com.iiteam.montezuma3.level;

import com.iiteam.montezuma3.model.Level;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LevelRepositoryTest {

    @Test
    void shipsAtLeastThreePlayableLevels() {
        List<Level> levels = new LevelRepository().loadAll();

        assertTrue(levels.size() >= 3, "contract requires at least 3 built-in levels");

        for (Level level : levels) {
            assertNotNull(level.getPlayerSpawn(), level.getName() + ": player spawn is mandatory");
            assertNotNull(level.getDoorSpawn(), level.getName() + ": exit door is mandatory");
            assertFalse(level.getEnemySpawns().isEmpty(), level.getName() + ": at least one enemy expected");
            assertFalse(level.getPickupSpawns().isEmpty(), level.getName() + ": at least one pickup expected");
            assertTrue(level.getWidth() >= 10, level.getName() + ": level is too narrow");
            assertTrue(level.getHeight() >= 10, level.getName() + ": level is too short");
        }
    }

    @Test
    void everyLevelIsRectangularWithSolidBorder() {
        for (Level level : new LevelRepository().loadAll()) {
            for (int tx = 0; tx < level.getWidth(); tx++) {
                assertTrue(level.isSolid(tx, 0), level.getName() + ": top border broken at " + tx);
                assertTrue(level.isSolid(tx, level.getHeight() - 1),
                        level.getName() + ": bottom border broken at " + tx);
            }
            for (int ty = 0; ty < level.getHeight(); ty++) {
                assertTrue(level.isSolid(0, ty), level.getName() + ": left border broken at " + ty);
                assertTrue(level.isSolid(level.getWidth() - 1, ty),
                        level.getName() + ": right border broken at " + ty);
            }
        }
    }

    @Test
    void everyLevelContainsALadder() {
        for (Level level : new LevelRepository().loadAll()) {
            boolean ladderFound = false;
            for (int ty = 0; ty < level.getHeight() && !ladderFound; ty++) {
                for (int tx = 0; tx < level.getWidth(); tx++) {
                    if (level.isLadder(tx, ty)) {
                        ladderFound = true;
                        break;
                    }
                }
            }
            assertTrue(ladderFound, level.getName() + ": expected at least one ladder");
        }
    }
}
