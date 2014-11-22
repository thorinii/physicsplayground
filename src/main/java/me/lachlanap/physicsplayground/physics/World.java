package me.lachlanap.physicsplayground.physics;

/**
 *
 * @author lachlan
 */
public final class World {

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
        solveConstraints();
        integrate(timestep);
    }

    private void solveConstraints() {
        if (y - w / 2 < floor) {
            y = floor + w / 2;
            py = py + (y - py) * 2;
        }
    }

    private void integrate(double timestep) {
        double vx = x - px;
        double vy = y - py;

        double ax = -vx * 0.1;
        double ay = gravity - vy * 0.1;

        double nx = x + vx + ax * timestep * timestep;
        double ny = y + vy + ay * timestep * timestep;

        px = x;
        py = y;

        x = nx;
        y = ny;
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
