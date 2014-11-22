package me.lachlanap.physicsplayground.ui;

import java.awt.Color;
import java.awt.Graphics2D;
import me.lachlanap.physicsplayground.physics.World;

/**
 *
 * @author lachlan
 */
public class PinTool implements Tool {

    private final World world;
    private final double radius;
    private final boolean pin;

    public PinTool(World world, double radius, boolean pin) {
        this.world = world;
        this.radius = radius;
        this.pin = pin;
    }

    @Override
    public void mouseDown(double x, double y) {
        for (int i = 0; i < world.getObjects(); i++) {
            if (Math.abs(world.getX(i) - x) < radius && Math.abs(world.getY(i) - y) < radius) {
                if (pin)
                    world.pin(i);
                else
                    world.unpin(i);
            }
        }
    }

    @Override
    public void mouseDrag(double x, double y) {
        for (int i = 0; i < world.getObjects(); i++) {
            if (Math.abs(world.getX(i) - x) < radius && Math.abs(world.getY(i) - y) < radius) {
                if (pin)
                    world.pin(i);
                else
                    world.unpin(i);
            }
        }
    }

    @Override
    public void mouseUp(double x, double y) {
    }

    @Override
    public void draw(Graphics2D g, int x, int y, View view) {
        int radiusPixels = (int) (radius * view.getPixelsPerMetre());

        g.setColor(Color.BLACK);
        g.drawOval(x - radiusPixels, y - radiusPixels, radiusPixels * 2, radiusPixels * 2);
    }
}
