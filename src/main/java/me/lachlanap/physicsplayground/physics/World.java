package me.lachlanap.physicsplayground.physics;

/**
 *
 * @author lachlan
 */
public class World {

    private double gravity = -9.81;

    private double x, y, px, py;
    private double w, h;
    private double floor;

    public World() {
        initialise();
    }

    public void initialise() {
        x = 1;
        y = 1;
        w = 1;
        h = 1;
        floor = -3;

        px = x;
        py = y;
    }

    public void update(double timestep) {
        double vx = x - px;
        double vy = y - py;

        px = x;
        py = y;

        x = x + vx + 0;
        y = y + vy + gravity * timestep * timestep;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getWidth() {
        return w;
    }

    public double getHeight() {
        return h;
    }

    public double getFloor() {
        return floor;
    }

}
