package me.lachlanap.physicsplayground.physics;

/**
 *
 * @author lachlan
 */
public class PointObject {

    public int id;
    public double x, y;
    public double px, py;
    public double radius;
    public double pinX, pinY;

    public boolean isPinned() {
        return pinX != Double.MAX_VALUE;
    }
}
