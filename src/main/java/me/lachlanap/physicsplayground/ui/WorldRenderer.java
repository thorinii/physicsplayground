package me.lachlanap.physicsplayground.ui;

import java.awt.Color;
import java.awt.Graphics;
import java.lang.reflect.InvocationTargetException;
import javax.swing.JComponent;
import javax.swing.SwingUtilities;
import me.lachlanap.physicsplayground.physics.World;

/**
 *
 * @author lachlan
 */
class WorldRenderer extends JComponent {

    private final World world;
    private final View view;

    private int renderFps;
    private int physicsFps;

    public WorldRenderer(World world) {
        this.world = world;
        this.view = new View();
    }

    public void render(double renderFps, double physicsFps) {
        this.renderFps = (int) renderFps;
        this.physicsFps = (int) physicsFps;

        repaint();

        try {
            SwingUtilities.invokeAndWait(new NullRunnable());
        } catch (InterruptedException | InvocationTargetException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, getWidth(), getHeight());

        g.setColor(Color.BLACK);
        g.drawString("R FPS: " + renderFps, 10, getHeight() - 10 - 40);
        g.drawString("P FPS: " + physicsFps, 10, getHeight() - 10);
    }
}
