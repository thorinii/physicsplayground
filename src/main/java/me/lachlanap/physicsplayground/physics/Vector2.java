package me.lachlanap.physicsplayground.physics;

/**
 *
 * @author lachlan
 */
public class Vector2 {

    public double x, y;

    public Vector2(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public Vector2() {
    }

    public Vector2 minus(Vector2 b, Vector2 store) {
        store.x = x - b.x;
        store.y = y - b.y;
        return store;
    }

    public Vector2 plus(Vector2 b) {
        x = x + b.x;
        y = y + b.y;
        return this;
    }

    public Vector2 plus(double bx, double by) {
        x = x + bx;
        y = y + by;
        return this;
    }

    public Vector2 mul(double v) {
        x = x * v;
        y = y * v;
        return this;
    }

    public Vector2 set(Vector2 copy) {
        x = copy.x;
        y = copy.y;
        return this;
    }

    public Vector2 negate() {
        x = -x;
        y = -y;
        return this;
    }

    @Override
    public String toString() {
        return "[" + x + "," + y + "]";
    }

}
