package me.lachlanap.physicsplayground.ui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
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

        drawWorld((Graphics2D) g);

        drawDiagnostics((Graphics2D) g);
    }

    private void drawWorld(Graphics2D g) {
        double x = world.getX(), y = world.getY();
        double w = world.getWidth(), h = world.getHeight();

        x = view.absoluteToViewX(x);
        y = view.absoluteToViewY(y);
        w = view.relativeToView(w);
        h = view.relativeToView(h);

        g.setColor(Color.RED);
        g.drawOval((int) (x - w / 2), (int) (y - h / 2), (int) w, (int) h);
    }

    private void drawDiagnostics(Graphics2D g) {
        g.setColor(Color.BLACK);
        g.drawString("R FPS: " + renderFps, 10, getHeight() - 10 - 15);
        g.drawString("P FPS: " + physicsFps, 10, getHeight() - 10);
    }
}
