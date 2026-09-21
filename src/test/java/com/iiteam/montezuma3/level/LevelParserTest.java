package com.iiteam.montezuma3.level;

import com.iiteam.montezuma3.model.Level;
import com.iiteam.montezuma3.model.Pickup;
import com.iiteam.montezuma3.model.Tile;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LevelParserTest {

    private static final String MAP = String.join("\n",
            "##########",
            "#........#",
            "#P..K..T.#",
            "#..###...#",
            "#....H...D",
            "##########");

    @Test
    void parsesGridDimensions() {
        Level level = LevelParser.parse("demo", MAP);
        assertEquals(10, level.getWidth());
        assertEquals(6, level.getHeight());
        assertEquals(320, level.getPixelWidth());
        assertEquals(192, level.getPixelHeight());
        assertEquals("demo", level.getName());
    }

    @Test
    void parsesTileKinds() {
        Level level = LevelParser.parse("demo", MAP);
        assertEquals(Tile.SOLID, level.getTile(0, 0));
        assertEquals(Tile.EMPTY, level.getTile(1, 1));
        assertEquals(Tile.LADDER, level.getTile(5, 4));
        assertEquals(Tile.SOLID, level.getTile(3, 3));

        assertTrue(level.isSolid(0, 0));
        assertTrue(level.isSolid(3, 3));
        assertFalse(level.isSolid(5, 4));
        assertTrue(level.isLadder(5, 4));
        assertFalse(level.isLadder(1, 1));
    }

    @Test
    void parsesSpawnMarkers() {
        Level level = LevelParser.parse("demo", MAP);

        assertNotNull(level.getPlayerSpawn());
        assertEquals(1, level.getPlayerSpawn().tx());
        assertEquals(2, level.getPlayerSpawn().ty());

        assertNotNull(level.getDoorSpawn());
        assertEquals(9, level.getDoorSpawn().tx());
        assertEquals(4, level.getDoorSpawn().ty());

        assertEquals(2, level.getPickupSpawns().size());
        assertEquals(Pickup.Type.KEY, level.getPickupSpawns().get(0).type());
        assertEquals(4, level.getPickupSpawns().get(0).tx());
        assertEquals(2, level.getPickupSpawns().get(0).ty());
        assertEquals(Pickup.Type.TREASURE, level.getPickupSpawns().get(1).type());
        assertEquals(7, level.getPickupSpawns().get(1).tx());
        assertEquals(2, level.getPickupSpawns().get(1).ty());

        assertTrue(level.getEnemySpawns().isEmpty());
    }

    @Test
    void spawnMarkersAreNotSolid() {
        Level level = LevelParser.parse("demo", MAP);
        assertFalse(level.isSolid(level.getPlayerSpawn().tx(), level.getPlayerSpawn().ty()));
        assertFalse(level.isSolid(level.getDoorSpawn().tx(), level.getDoorSpawn().ty()));
    }

    @Test
    void padsShortRowsAndIgnoresTrailingBlankLines() {
        Level level = LevelParser.parse("short", "####\n#P.#\n####\n\n");
        assertEquals(4, level.getWidth());
        assertEquals(3, level.getHeight());
        assertNotNull(level.getPlayerSpawn());
    }

    @Test
    void rejectsEmptyMaps() {
        assertThrows(IllegalArgumentException.class, () -> LevelParser.parse("empty", "   \n \n"));
    }

    @Test
    void outOfBoundsIsSolidOnSidesButOpenBelow() {
        Level level = LevelParser.parse("demo", MAP);
        assertTrue(level.isSolid(-1, 3));
        assertTrue(level.isSolid(10, 3));
        assertTrue(level.isSolid(3, -1));
        assertFalse(level.isSolid(3, 6));
    }
}
