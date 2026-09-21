package com.iiteam.montezuma3.render;

import com.iiteam.montezuma3.core.GameState;
import com.iiteam.montezuma3.engine.GameEngine;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

/** Overlay: score, lives, level number and the state dependent messages. */
public class HudRenderer {

    private static final Color BAR_BG = new Color(0, 0, 0, 170);
    private static final Color TEXT = new Color(245, 245, 250);
    private static final Color ACCENT = new Color(255, 214, 64);
    private static final Color OVERLAY = new Color(0, 0, 0, 190);

    private static final Font SMALL = new Font(Font.SANS_SERIF, Font.PLAIN, 12);
    private static final Font NORMAL = new Font(Font.SANS_SERIF, Font.BOLD, 14);
    private static final Font TITLE = new Font(Font.SANS_SERIF, Font.BOLD, 26);

    public void render(Graphics2D g, GameEngine engine, int panelWidth, int panelHeight) {
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        drawStatusBar(g, engine, panelWidth);

        GameState state = engine.getState();
        switch (state) {
            case MENU -> drawOverlay(g, panelWidth, panelHeight, "MONTEZUMA 3",
                    new String[]{
                            "Collect every key and treasure,",
                            "then reach the exit door.",
                            "ENTER / SPACE - start",
                            "ARROWS or WASD - move, SPACE - jump",
                            "UP / DOWN while on a ladder - climb",
                            "= or P - pause"});
            case PAUSED -> drawOverlay(g, panelWidth, panelHeight, "PAUSED",
                    new String[]{"= or P to resume"});
            case LEVEL_COMPLETE -> drawOverlay(g, panelWidth, panelHeight, "LEVEL COMPLETE!",
                    new String[]{"Score: " + engine.getScore(), "ENTER to continue"});
            case GAME_OVER -> drawOverlay(g, panelWidth, panelHeight, "GAME OVER",
                    new String[]{"Final score: " + engine.getScore(), "ENTER for the menu"});
            case VICTORY -> drawOverlay(g, panelWidth, panelHeight, "VICTORY!",
                    new String[]{"You escaped with " + engine.getScore() + " points.",
                            "ENTER for the menu"});
            default -> {
                // PLAYING: the status bar is enough
            }
        }
    }

    private void drawStatusBar(Graphics2D g, GameEngine engine, int panelWidth) {
        g.setColor(BAR_BG);
        g.fillRect(0, 0, panelWidth, 24);

        g.setFont(NORMAL);
        g.setColor(TEXT);
        g.drawString("SCORE " + engine.getScore(), 10, 17);

        g.setColor(ACCENT);
        g.drawString("LIVES " + engine.getLives(), panelWidth / 2 - 30, 17);

        g.setColor(TEXT);
        String levelText = "LEVEL " + engine.getLevelNumber() + "/" + engine.getTotalLevels();
        g.drawString(levelText, panelWidth - 110, 17);

        g.setFont(SMALL);
        g.setColor(new Color(210, 210, 220));
        g.drawString("keys left: " + engine.getRemainingPickups(), 10, 38);
    }

    private void drawOverlay(Graphics2D g, int width, int height, String title, String[] lines) {
        g.setColor(OVERLAY);
        g.fillRect(0, 0, width, height);

        FontMetrics metrics = g.getFontMetrics();

        g.setFont(TITLE);
        metrics = g.getFontMetrics();
        int titleY = height / 2 - 40 - lines.length * 9;
        g.setColor(ACCENT);
        g.drawString(title, centeredX(metrics, title, width), titleY);

        g.setFont(SMALL);
        metrics = g.getFontMetrics();
        g.setColor(TEXT);
        int y = titleY + 26;
        for (String line : lines) {
            g.drawString(line, centeredX(metrics, line, width), y);
            y += 18;
        }

        g.setStroke(new BasicStroke(2f));
        g.setColor(ACCENT.darker());
        g.drawRect(8, 8, width - 17, height - 17);
    }

    private int centeredX(FontMetrics metrics, String text, int width) {
        return Math.max(4, (width - metrics.stringWidth(text)) / 2);
    }
}
