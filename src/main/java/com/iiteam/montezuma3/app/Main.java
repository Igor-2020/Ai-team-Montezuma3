package com.iiteam.montezuma3.app;

import com.iiteam.montezuma3.core.InputState;
import com.iiteam.montezuma3.engine.GameEngine;
import com.iiteam.montezuma3.render.GamePanel;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import java.awt.GraphicsEnvironment;

/** Entry point: wires the engine, the input state and the Swing window together. */
public final class Main {

    private Main() {
    }

    public static void main(String[] args) {
        if (GraphicsEnvironment.isHeadless()) {
            System.out.println("Montezuma3 needs a graphical environment (headless JVM detected).");
            return;
        }

        SwingUtilities.invokeLater(() -> {
            GameEngine engine = GameEngine.createDefault();
            InputState input = new InputState();
            GamePanel panel = new GamePanel(engine, input);

            JFrame frame = new JFrame("Montezuma3");
            frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            frame.setContentPane(panel);
            frame.setResizable(false);
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);

            panel.start();
        });
    }
}
