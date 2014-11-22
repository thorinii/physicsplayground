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
    private double walls;

    public World() {
        initialise();
    }

    public void initialise() {
        x = 0;
        y = 1;
        w = 1;
        h = 1;
        floor = -3;
        walls = 5;

        px = x + 0.1;
        py = y;
    }

    public void update(double timestep) {
        solveConstraints();
        integrate(timestep);
    }

    private void solveConstraints() {
        if (y - h / 2 < floor) {
            y = floor + w / 2;
            py = py + (y - py) * 2;

            px = x - (x - px) * 0.97;
        }

        if (x + w / 2 > walls) {
            x = walls - w / 2;
            px = px + (x - px) * 2;

            py = y - (y - py) * 0.97;
        } else if (x - w / 2 < -walls) {
            x = -walls + w / 2;
            px = px + (x - px) * 2;

            py = y - (y - py) * 0.97;
        }
    }

    private void integrate(double timestep) {
        double vx = x - px;
        double vy = y - py;

        double ax = -vx * 9;
        double ay = gravity + -vy * 9;

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

    public double getWalls() {
        return walls;
    }

}
