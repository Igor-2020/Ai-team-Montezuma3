package com.iiteam.montezuma3.level;

import com.iiteam.montezuma3.model.Level;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * Provides the built-in level set shipped in {@code src/main/resources/levels}.
 * The resources end up inside the executable jar, so a packaged game starts without
 * touching the file system.
 */
public class LevelRepository {

    public static final String LEVEL_1 = "/levels/level1.txt";
    public static final String LEVEL_2 = "/levels/level2.txt";
    public static final String LEVEL_3 = "/levels/level3.txt";

    private static final String[] BUILT_IN = {LEVEL_1, LEVEL_2, LEVEL_3};

    /** Load every built-in level in play order. */
    public List<Level> loadAll() {
        List<Level> levels = new ArrayList<>();
        for (String resource : BUILT_IN) {
            levels.add(load(resource));
        }
        return levels;
    }

    /** Load a single level from a classpath resource. */
    public Level load(String resource) {
        String text = readResource(resource);
        String fileName = resource.substring(resource.lastIndexOf('/') + 1);
        String name = fileName.endsWith(".txt") ? fileName.substring(0, fileName.length() - 4) : fileName;
        return LevelParser.parse(name, text);
    }

    private String readResource(String resource) {
        try (InputStream in = LevelRepository.class.getResourceAsStream(resource)) {
            if (in == null) {
                throw new IllegalStateException("Level resource not found on classpath: " + resource);
            }
            return new String(in.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to read level resource: " + resource, e);
        }
    }
}
