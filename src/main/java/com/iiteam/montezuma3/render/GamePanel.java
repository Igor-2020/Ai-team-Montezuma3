package com.iiteam.montezuma3.render;

import com.iiteam.montezuma3.core.GameLoop;
import com.iiteam.montezuma3.core.InputState;
import com.iiteam.montezuma3.engine.GameEngine;
import com.iiteam.montezuma3.model.Level;

import javax.swing.JPanel;
import javax.swing.Timer;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/**
 * Swing surface of the game: pumps the {@link GameLoop}, translates key events into an
 * {@link InputState} and paints the world plus HUD. Contains no rule logic.
 */
public class GamePanel extends JPanel implements ActionListener, KeyListener {

    private static final long serialVersionUID = 1L;

    private final GameEngine engine;
    private final InputState input;
    private final Renderer renderer = new Renderer();
    private final HudRenderer hudRenderer = new HudRenderer();
    private final GameLoop loop = new GameLoop();
    private final Timer timer;

    public GamePanel(GameEngine engine, InputState input) {
        this.engine = engine;
        this.input = input;

        Level level = engine.getLevel();
        setPreferredSize(new Dimension(level.getPixelWidth(), level.getPixelHeight()));
        setBackground(new Color(18, 16, 32));
        setDoubleBuffered(true);
        setFocusable(true);
        addKeyListener(this);

        this.timer = new Timer(1000 / GameLoop.TICKS_PER_SECOND, this);
        this.timer.setCoalesce(true);
    }

    /** Start the render/simulation pump. */
    public void start() {
        loop.reset();
        timer.start();
        requestFocusInWindow();
    }

    public void stop() {
        timer.stop();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        loop.advance(System.nanoTime(), () -> engine.tick(input));
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        renderer.render(g2, engine);
        hudRenderer.render(g2, engine, getWidth(), getHeight());
        g2.dispose();
    }

    @Override
    public void keyTyped(KeyEvent e) {
        // not used
    }

    @Override
    public void keyPressed(KeyEvent e) {
        handleKey(e.getKeyCode(), true);
    }

    @Override
    public void keyReleased(KeyEvent e) {
        handleKey(e.getKeyCode(), false);
    }

    private void handleKey(int code, boolean pressed) {
        switch (code) {
            case KeyEvent.VK_LEFT, KeyEvent.VK_A -> input.setLeft(pressed);
            case KeyEvent.VK_RIGHT, KeyEvent.VK_D -> input.setRight(pressed);
            case KeyEvent.VK_UP, KeyEvent.VK_W -> input.setUp(pressed);
            case KeyEvent.VK_DOWN, KeyEvent.VK_S -> input.setDown(pressed);
            case KeyEvent.VK_SPACE -> {
                input.setJump(pressed);
                if (pressed) {
                    engine.confirm();
                }
            }
            case KeyEvent.VK_ENTER -> {
                if (pressed) {
                    engine.confirm();
                }
            }
            case KeyEvent.VK_EQUALS, KeyEvent.VK_PLUS, KeyEvent.VK_P, KeyEvent.VK_ESCAPE -> {
                if (pressed) {
                    engine.togglePause();
                }
            }
            default -> {
                // ignore other keys
            }
        }
    }
}
