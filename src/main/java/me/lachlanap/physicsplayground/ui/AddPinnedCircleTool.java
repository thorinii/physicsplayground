package me.lachlanap.physicsplayground.ui;

import me.lachlanap.physicsplayground.physics.World;

/**
 *
 * @author lachlan
 */
public class AddPinnedCircleTool implements Tool {

    private final World world;
    private final double radius;

    public AddPinnedCircleTool(World world, double radius) {
        this.world = world;
        this.radius = radius;
    }

    @Override
    public void mouseDown(double x, double y) {
        world.addPinnedObject(x, y, radius);
    }

    @Override
    public void mouseDrag(double x, double y) {
    }

    @Override
    public void mouseUp(double x, double y) {
    }

}
