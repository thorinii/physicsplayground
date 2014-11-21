package me.lachlanap.physicsplayground.ui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
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

        ViewPanningMouseListener vpml = new ViewPanningMouseListener();
        addMouseListener(vpml);
        addMouseMotionListener(vpml);
        addMouseWheelListener(vpml);
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

        double floorLevel = world.getFloor();
        floorLevel = view.absoluteToViewY(floorLevel);
        if (floorLevel < getHeight()) {
            g.setColor(Color.BLACK);
            g.fillRect(0, (int) floorLevel, getWidth(), getHeight());
        }
    }

    private void drawDiagnostics(Graphics2D g) {
        g.setColor(Color.GREEN);
        g.drawString("R FPS: " + renderFps, 10, getHeight() - 10 - 15);
        g.drawString("P FPS: " + physicsFps, 10, getHeight() - 10);
    }

    private class ViewPanningMouseListener extends MouseAdapter {

        private Point start = null;
        private double initialOffsetX, initialOffsetY;

        @Override
        public void mousePressed(MouseEvent e) {
            start = e.getPoint();

            initialOffsetX = view.getOffsetPixelsX();
            initialOffsetY = view.getOffsetPixelsY();
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            start = null;
        }

        @Override
        public void mouseDragged(MouseEvent e) {
            Point diff = e.getPoint();
            diff.translate(-start.x, -start.y);

            view.setPixelOffset(initialOffsetX + diff.x, initialOffsetY + diff.y);
        }

        @Override
        public void mouseWheelMoved(MouseWheelEvent e) {
            double rotation = e.getPreciseWheelRotation();
            Point base = e.getPoint();

            double zoom = view.getPixelsPerMetre();
            view.setPixelsPerMetre(zoom - rotation * zoom * 0.1);
        }
    }
}
