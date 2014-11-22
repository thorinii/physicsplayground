package me.lachlanap.physicsplayground.physics;

/**
 *
 * @author lachlan
 */
public final class World {

    private static final int MAX_OBJECTS = 128;

    private double gravity = -9.81;

    private double floor;
    private double walls;

    private final double[] x, y, px, py;
    private final double[] r;
    private int objects;

    public World() {
        x = new double[MAX_OBJECTS];
        y = new double[MAX_OBJECTS];
        px = new double[MAX_OBJECTS];
        py = new double[MAX_OBJECTS];
        r = new double[MAX_OBJECTS];
        objects = 0;

        initialise();
    }

    public void initialise() {
        objects = 0;
        floor = -3;
        walls = 4;

        objects = 1;
        initialiseObject(0);
    }

    public void addObject() {
        if (objects < MAX_OBJECTS) {
            initialiseObject(objects);
            objects++;
        }
    }

    private void initialiseObject(int i) {
        x[i] = 0;
        y[i] = 1;
        r[i] = 0.5;

        px[i] = x[i] + 0.1;
        py[i] = y[i];
    }

    public void update(double timestep) {
        for (int i = 0; i < 3; i++)
            solveConstraints();
        integrate(timestep);
    }

    private void solveConstraints() {
        for (int i = 0; i < objects; i++) {
            // Wall and Floor
            if (y[i] - r[i] < floor) {
                y[i] = floor + r[i];
                py[i] = py[i] + (y[i] - py[i]) * 2;

                px[i] = x[i] - (x[i] - px[i]) * 0.97;
            }

            if (x[i] + r[i] > walls) {
                x[i] = walls - r[i];
                px[i] = px[i] + (x[i] - px[i]) * 2;

                py[i] = y[i] - (y[i] - py[i]) * 0.97;
            } else if (x[i] - r[i] < -walls) {
                x[i] = -walls + r[i];
                px[i] = px[i] + (x[i] - px[i]) * 2;

                py[i] = y[i] - (y[i] - py[i]) * 0.97;
            }

            // Circle-circle collisions
            double x1 = x[i];
            double y1 = y[i];
            for (int j = i + 1; j < objects; j++) {
                double rBoth = r[i] + r[j];
                double rBothSq = rBoth * rBoth;

                double x2 = x[j];
                double y2 = y[j];

                double distSq = (x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2);

                if (distSq < rBothSq) {
                    double dist = Math.sqrt(distSq);
                    double penetration = rBoth - dist;
                    double normalX;
                    double normalY;

                    if (dist > 0) {
                        normalX = (x1 - x2) / dist;
                        normalY = (y1 - y2) / dist;
                    } else {
                        normalX = 1;
                        normalY = 0;
                    }

                    x[i] += penetration * normalX * 0.5 * 0.99;
                    y[i] += penetration * normalY * 0.5 * 0.99;
                    x[j] -= penetration * normalX * 0.5 * 0.99;
                    y[j] -= penetration * normalY * 0.5 * 0.99;
                }
            }
        }
    }

    private void integrate(double timestep) {
        for (int i = 0; i < objects; i++) {
            double vx = x[i] - px[i];
            double vy = y[i] - py[i];

            double ax = -vx * 9;
            double ay = gravity + -vy * 9;

            double nx = x[i] + vx + ax * timestep * timestep;
            double ny = y[i] + vy + ay * timestep * timestep;

            px[i] = x[i];
            py[i] = y[i];

            x[i] = nx;
            y[i] = ny;
        }
    }

    public double getX(int i) {
        return x[i];
    }

    public double getY(int i) {
        return y[i];
    }

    public double getWidth(int i) {
        return r[i] * 2;
    }

    public double getHeight(int i) {
        return r[i] * 2;
    }

    public int getObjects() {
        return objects;
    }

    public double getFloor() {
        return floor;
    }

    public double getWalls() {
        return walls;
    }

}
