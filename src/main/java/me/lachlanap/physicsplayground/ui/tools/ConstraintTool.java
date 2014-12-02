package me.lachlanap.physicsplayground.ui.tools;

import java.awt.Color;
import java.awt.Graphics2D;
import me.lachlanap.physicsplayground.physics.World;
import me.lachlanap.physicsplayground.ui.View;

/**
 *
 * @author lachlan
 */
public class ConstraintTool implements Tool {

    private final World world;
    private final double strength;

    private int objectA, objectB;
    private double ax, ay, bx, by;

    public ConstraintTool(World world, double strength) {
        this.world = world;
        this.strength = strength;

        objectA = objectB = -1;
    }

    @Override
    public void mouseDown(double x, double y) {
        for (int i = 0; i < world.getObjects(); i++) {
            if (Math.abs(world.getX(i) - x) < world.getWidth(i) && Math.abs(world.getY(i) - y) < world.getHeight(i)) {
                objectA = i;
                ax = world.getX(i);
                ay = world.getY(i);
                break;
            }
        }
    }

    @Override
    public void mouseDrag(double x, double y) {
        objectB = -1;

        for (int i = 0; i < world.getObjects(); i++) {
            if (i == objectA)
                continue;

            if (Math.abs(world.getX(i) - x) < world.getWidth(i) && Math.abs(world.getY(i) - y) < world.getHeight(i)) {
                objectB = i;
                bx = world.getX(i);
                by = world.getY(i);
                break;
            }
        }
    }

    @Override
    public void mouseUp(double x, double y) {
        if (objectA != -1 && objectB != -1) {
            world.addConstraint(objectA, objectB, strength);
        }

        objectA = -1;
        objectB = -1;
    }

    @Override
    public void draw(Graphics2D g, int x, int y, View view) {
        g.setColor(objectA == -1 ? Color.BLACK : Color.CYAN);
        g.drawLine(x - 10, y, x + 10, y);
        g.drawLine(x, y - 10, x, y + 10);

        if (objectA != -1) {
            int vax = (int) view.absoluteToViewX(ax);
            int vay = (int) view.absoluteToViewY(ay);

            g.setColor(Color.BLUE);
            g.drawLine(vax - 10, vay, vax + 10, vay);
            g.drawLine(vax, vay - 10, vax, vay + 10);

            if (objectB == -1) {
                g.setColor(Color.GREEN);
                g.drawLine(x, y, vax, vay);
            } else {
                int vbx = (int) view.absoluteToViewX(bx);
                int vby = (int) view.absoluteToViewY(by);

                g.setColor(Color.BLUE);
                g.drawLine(vbx - 10, vby, vbx + 10, vby);
                g.drawLine(vbx, vby - 10, vbx, vby + 10);

                g.setColor(Color.GREEN);
                g.drawLine(vbx, vby, vax, vay);
            }
        }
    }

    @Override
    public String getLabel() {
        if (strength == 0)
            return "Add Constraint (infinite)";
        else
            return "Add Constraint (" + strength + "N)";
    }
}
