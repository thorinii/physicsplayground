package me.lachlanap.physicsplayground.ui.tools;

import java.awt.Color;
import java.awt.Graphics2D;
import me.lachlanap.physicsplayground.physics.World;
import me.lachlanap.physicsplayground.ui.View;

/**
 *
 * @author lachlan
 */
public class InflowTool implements Tool {

    private final World world;

    public InflowTool(World world) {
        this.world = world;
    }

    @Override
    public void mouseDown(double x, double y) {
        world.setInflowEnabled(true);
        world.setInflow(x, y);
    }

    @Override
    public void mouseDrag(double x, double y) {
        world.setInflowEnabled(true);
        world.setInflow(x, y);
    }

    @Override
    public void mouseUp(double x, double y) {
    }

    @Override
    public void draw(Graphics2D g, int x, int y, View view) {
        g.setColor(Color.BLACK);
        g.drawLine(x - 10, y, x + 10, y);
        g.drawLine(x, y - 10, x, y + 10);
    }
}
