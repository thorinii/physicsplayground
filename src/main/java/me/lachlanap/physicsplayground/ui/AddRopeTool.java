package me.lachlanap.physicsplayground.ui;

import java.awt.Graphics2D;
import me.lachlanap.physicsplayground.physics.World;

/**
 *
 * @author lachlan
 */
public class AddRopeTool implements Tool {

    private final World world;
    private final double radius;
    private int last;
    private double lx, ly;

    public AddRopeTool(World world, double radius) {
        this.world = world;
        this.radius = radius;
    }

    @Override
    public void mouseDown(double x, double y) {
        last = world.addObject(x, y, radius);
        lx = x;
        ly = y;
    }

    @Override
    public void mouseDrag(double x, double y) {
        double mouseMove = Math.hypot(x - lx, y - ly);
        double spacing = radius * 2;

        if (mouseMove > spacing) {
            int nodes = (int) (mouseMove / spacing);

            double dx = (x - lx) / mouseMove * spacing;
            double dy = (y - ly) / mouseMove * spacing;

            for (int i = 0; i < nodes; i++) {
                int next = world.addObject(lx + i * dx, y + i * dy, radius);

                world.addConstraint(last, next, 1);

                last = next;

            }

            lx = x;
            ly = y;
        }
    }

    @Override
    public void mouseUp(double x, double y) {
    }

    @Override
    public void draw(Graphics2D g, int x, int y, View view) {
    }
}
