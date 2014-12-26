package me.lachlanap.physicsplayground.ui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.Executor;
import javax.swing.JComponent;
import javax.swing.SwingUtilities;
import me.lachlanap.physicsplayground.physics.Constraint;
import me.lachlanap.physicsplayground.physics.PointObject;
import me.lachlanap.physicsplayground.physics.Timer;
import me.lachlanap.physicsplayground.physics.World;
import me.lachlanap.physicsplayground.ui.tools.AddCircleTool;
import me.lachlanap.physicsplayground.ui.tools.Tool;

/**
 *
 * @author lachlan
 */
class WorldRenderer extends JComponent {

    private final World world;
    private final View view;
    private final Timer timer;

    private Executor executor;

    private int renderFps;
    private int physicsFps;

    private Tool tool;

    public WorldRenderer(World world) {
        this.world = world;
        this.view = new View();
        this.timer = new Timer();

        ViewPanningMouseListener vpml = new ViewPanningMouseListener();
        addMouseListener(vpml);
        addMouseMotionListener(vpml);
        addMouseWheelListener(vpml);

        setFocusable(true);

        tool = new AddCircleTool(world, 0.5);
    }

    public void setExecutor(Executor executor) {
        this.executor = executor;
    }

    public Timer getTimer() {
        return timer;
    }


    public void setTool(Tool tool) {
        this.tool = tool;
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

        drawScale((Graphics2D) g);
        drawWorld((Graphics2D) g);

        drawTool((Graphics2D) g);
        drawDiagnostics((Graphics2D) g);
    }

    private void drawScale(Graphics2D g) {
        double ppm = view.getPixelsPerMetre();

        g.setColor(Color.LIGHT_GRAY);
        drawScaleLines(ppm, g);

        g.setColor(Color.DARK_GRAY);
        drawScaleLines(ppm * 10, g);
    }

    private void drawScaleLines(double ppm, Graphics2D g) {
        if (ppm < 5)
            return;

        double start = Math.floor(view.getOffsetPixelsX() % ppm);
        double end = start + Math.ceil(getWidth() / ppm) * ppm;
        for (double x = start; x < end; x += ppm)
            g.drawLine((int) x, 0, (int) x, getHeight());

        start = Math.floor(view.getOffsetPixelsY() % ppm);
        end = start + Math.ceil(getHeight() / ppm) * ppm;
        for (double y = start; y < end; y += ppm)
            g.drawLine(0, (int) y, getWidth(), (int) y);
    }

    private void drawWorld(Graphics2D g) {
        drawConstraints(g);
        drawObjects(g);
        drawFloor(g);
    }

    private void drawObjects(Graphics2D g) {
        PointObject obj = new PointObject();

        for (int i = 0; i < world.getObjects(); i++) {
            world.getObject(i, obj);

            double x = obj.pos.x, y = obj.pos.y;
            double w = obj.radius * 2, h = obj.radius * 2;

            x = view.absoluteToViewX(x);
            y = view.absoluteToViewY(y);
            w = view.relativeToView(w);
            h = view.relativeToView(h);

            if (x + w / 2 < 0 || x - w / 2 > getWidth())
                continue;
            if (y + h / 2 < 0 || y - h / 2 > getHeight())
                continue;

            g.setColor(obj.isPinned() ? Color.ORANGE : Color.RED);
            g.drawOval((int) (x - w / 2), (int) (y - h / 2), (int) w, (int) h);
        }
    }

    private void drawConstraints(Graphics2D g) {
        Constraint constraint = new Constraint();
        for (int i = 0; i < world.getConstraints(); i++) {
            world.getConstraint(i, constraint);

            double x1 = world.getX(constraint.a), y1 = world.getY(constraint.a);
            double x2 = world.getX(constraint.b), y2 = world.getY(constraint.b);

            x1 = view.absoluteToViewX(x1);
            y1 = view.absoluteToViewY(y1);
            x2 = view.absoluteToViewX(x2);
            y2 = view.absoluteToViewY(y2);

            g.setColor(Color.BLUE);
            g.drawLine((int) x1, (int) y1, (int) x2, (int) y2);
        }
    }

    private void drawFloor(Graphics2D g) {
        double floorLevel = world.getFloor();
        floorLevel = view.absoluteToViewY(floorLevel);
        if (floorLevel < getHeight()) {
            g.setColor(world.isDeleteAtFloor() ? Color.BLACK : Color.DARK_GRAY);
            g.fillRect(0, (int) floorLevel, getWidth(), getHeight());
        }
    }

    private void drawTool(Graphics2D g) {
        Point mouse = getMousePosition();
        if (mouse != null)
            tool.draw(g, mouse.x, mouse.y, view);
    }

    private void drawDiagnostics(Graphics2D g) {
        g.setColor(Color.BLACK);
        g.fillRect(0, getHeight() - 10 - 15 * 7, 300, getHeight());

        g.setColor(Color.GREEN);
        g.drawString("Times: " + timer.toString(), 10, getHeight() - 10 - 15 * 6);
        g.drawString("Tool: " + tool.getLabel(), 10, getHeight() - 10 - 15 * 5);
        g.drawString("EWO: " + (world.isEWO() ? "On" : "Off"), 10, getHeight() - 10 - 15 * 4);
        g.drawString("Objects: " + world.getObjects(), 10, getHeight() - 10 - 15 * 3);
        g.drawString("Constraints: " + world.getConstraints(), 10, getHeight() - 10 - 15 * 2);
        g.drawString("R FPS: " + renderFps, 10, getHeight() - 10 - 15);
        g.drawString("P FPS: " + physicsFps, 10, getHeight() - 10);
    }

    private class ViewPanningMouseListener extends MouseAdapter {

        private Point start = null;
        private double initialOffsetX, initialOffsetY;
        private boolean b1;

        @Override
        public void mousePressed(MouseEvent e) {
            start = null;
            requestFocusInWindow();

            if (e.getButton() == MouseEvent.BUTTON1) {
                b1 = true;

                Point diff = e.getPoint();
                final double x = view.absoluteFromViewX(diff.x);
                final double y = view.absoluteFromViewY(diff.y);

                executor.execute(new Runnable() {
                    @Override
                    public void run() {
                        tool.mouseDown(x, y);
                    }
                });
            } else if (e.getButton() == MouseEvent.BUTTON2) {
                start = e.getPoint();

                initialOffsetX = view.getOffsetPixelsX();
                initialOffsetY = view.getOffsetPixelsY();
            } else if (e.getButton() == MouseEvent.BUTTON3) {
                executor.execute(new Runnable() {
                    @Override
                    public void run() {
                        world.reset();
                    }
                });
            }

            repaint();
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            start = null;

            if (e.getButton() == MouseEvent.BUTTON1) {
                b1 = false;

                Point diff = e.getPoint();
                final double x = view.absoluteFromViewX(diff.x);
                final double y = view.absoluteFromViewY(diff.y);

                executor.execute(new Runnable() {
                    @Override
                    public void run() {
                        tool.mouseUp(x, y);
                    }
                });
            }

            repaint();
        }

        @Override
        public void mouseDragged(MouseEvent e) {
            if (start != null) {
                Point diff = e.getPoint();
                diff.translate(-start.x, -start.y);

                view.setPixelOffset(initialOffsetX + diff.x, initialOffsetY + diff.y);
            }

            if (b1) {
                Point diff = e.getPoint();
                final double x = view.absoluteFromViewX(diff.x);
                final double y = view.absoluteFromViewY(diff.y);

                executor.execute(new Runnable() {
                    @Override
                    public void run() {
                        tool.mouseDrag(x, y);
                    }
                });
            }

            repaint();
        }

        @Override
        public void mouseWheelMoved(MouseWheelEvent e) {
            double rotation = e.getPreciseWheelRotation();
            Point base = e.getPoint();

            double ox, oy;
            ox = view.getOffsetPixelsX() - base.x;
            oy = view.getOffsetPixelsY() - base.y;
            ox = view.relativeFromView(ox);
            oy = view.relativeFromView(oy);

            double zoom = view.getPixelsPerMetre();
            view.setPixelsPerMetre(zoom - rotation * zoom * 0.1);

            ox = view.relativeToView(ox) + base.x;
            oy = view.relativeToView(oy) + base.y;
            view.setPixelOffset(ox, oy);

            repaint();
        }
    }
}
