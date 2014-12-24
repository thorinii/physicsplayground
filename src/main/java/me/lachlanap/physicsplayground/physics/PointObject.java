package me.lachlanap.physicsplayground.physics;

/**
 *
 * @author lachlan
 */
public class PointObject {

    public int id;
    public final Vector2 pos = new Vector2();
    public final Vector2 prev = new Vector2();
    public double radius;
    public final Vector2 pin = new Vector2();

    public boolean isPinned() {
        return pin.x != Double.MAX_VALUE;
    }

    public Vector2 getVelocity(Vector2 store) {
        return pos.minus(prev, store);
    }

    /**
     * Sets the velocity per timestep (NOT per second).
     */
    public PointObject setVelocity(double vx, double vy) {
        prev.x = pos.x - vx;
        prev.y = pos.y - vy;
        return this;
    }
}
