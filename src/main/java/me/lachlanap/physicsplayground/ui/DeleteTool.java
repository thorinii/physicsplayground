package me.lachlanap.physicsplayground.ui;

import java.awt.Color;
import java.awt.Graphics2D;
import me.lachlanap.physicsplayground.physics.World;

/**
 *
 * @author lachlan
 */
public class DeleteTool implements Tool {

    private final World world;
    private final double radius;

    public DeleteTool(World world, double radius) {
        this.world = world;
        this.radius = radius;
    }

    @Override
    public void mouseDown(double x, double y) {
        for (int i = 0; i < world.getObjects();) {
            if (Math.abs(world.getX(i) - x) < radius && Math.abs(world.getY(i) - y) < radius)
                world.delete(i);
            else
                i++;
        }
    }

    @Override
    public void mouseDrag(double x, double y) {
        for (int i = 0; i < world.getObjects();) {
            if (Math.abs(world.getX(i) - x) < radius && Math.abs(world.getY(i) - y) < radius)
                world.delete(i);
            else
                i++;
        }
    }

    @Override
    public void mouseUp(double x, double y) {
    }

    @Override
    public void draw(Graphics2D g, int x, int y, double toPixels) {
        int radiusPixels = (int) (radius * toPixels);

        g.setColor(Color.RED);
        g.drawOval(x - radiusPixels, y - radiusPixels, radiusPixels * 2, radiusPixels * 2);
    }
}
