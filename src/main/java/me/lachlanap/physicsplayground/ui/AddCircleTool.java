package me.lachlanap.physicsplayground.ui;

import me.lachlanap.physicsplayground.physics.World;

/**
 *
 * @author lachlan
 */
public class AddCircleTool implements Tool {

    private final World world;
    private final double radius;

    public AddCircleTool(World world, double radius) {
        this.world = world;
        this.radius = radius;
    }

    @Override
    public void mouseDown(double x, double y) {
        world.addObject(x, y, radius);
    }

    @Override
    public void mouseDrag(double x, double y) {
        world.addObject(x, y, radius);
    }

    @Override
    public void mouseUp(double x, double y) {
    }

}
