package com.iiteam.montezuma3.level;

import com.iiteam.montezuma3.model.Level;
import com.iiteam.montezuma3.model.Pickup;
import com.iiteam.montezuma3.model.Tile;

import java.util.ArrayList;
import java.util.List;

/**
 * Turns an ASCII map into a {@link Level}.
 *
 * <pre>
 *   '#' / '=' / 'X'  solid block
 *   'H'              ladder
 *   '.' or ' '       empty space
 *   'P'              player spawn      (empty tile)
 *   'E'              enemy spawn       (empty tile)
 *   'K'              key pickup        (empty tile)
 *   'T'              treasure pickup   (empty tile)
 *   'D'              exit door         (empty tile)
 * </pre>
 * Short rows are padded with empty space so maps stay easy to author.
 */
public final class LevelParser {

    private LevelParser() {
    }

    public static Level parse(String name, String ascii) {
        if (ascii == null) {
            throw new IllegalArgumentException("Level map must not be null");
        }
        List<String> lines = new ArrayList<>();
        for (String raw : ascii.split("\n", -1)) {
            lines.add(raw.replace("\r", ""));
        }
        while (!lines.isEmpty() && lines.get(lines.size() - 1).isBlank()) {
            lines.remove(lines.size() - 1);
        }
        while (!lines.isEmpty() && lines.get(0).isBlank()) {
            lines.remove(0);
        }
        if (lines.isEmpty()) {
            throw new IllegalArgumentException("Level map is empty");
        }

        int height = lines.size();
        int width = 0;
        for (String line : lines) {
            width = Math.max(width, line.length());
        }
        if (width == 0) {
            throw new IllegalArgumentException("Level map has zero width");
        }

        Level level = new Level(name, width, height);
        for (int ty = 0; ty < height; ty++) {
            String line = lines.get(ty);
            for (int tx = 0; tx < width; tx++) {
                char c = tx < line.length() ? line.charAt(tx) : ' ';
                switch (c) {
                    case '#', '=', 'X' -> level.setTile(tx, ty, Tile.SOLID);
                    case 'H' -> level.setTile(tx, ty, Tile.LADDER);
                    case 'P' -> {
                        level.setTile(tx, ty, Tile.EMPTY);
                        level.setPlayerSpawn(tx, ty);
                    }
                    case 'E' -> {
                        level.setTile(tx, ty, Tile.EMPTY);
                        level.addEnemySpawn(tx, ty);
                    }
                    case 'K' -> {
                        level.setTile(tx, ty, Tile.EMPTY);
                        level.addPickupSpawn(Pickup.Type.KEY, tx, ty);
                    }
                    case 'T' -> {
                        level.setTile(tx, ty, Tile.EMPTY);
                        level.addPickupSpawn(Pickup.Type.TREASURE, tx, ty);
                    }
                    case 'D' -> {
                        level.setTile(tx, ty, Tile.EMPTY);
                        level.setDoorSpawn(tx, ty);
                    }
                    default -> level.setTile(tx, ty, Tile.EMPTY);
                }
            }
        }
        return level;
    }
}
