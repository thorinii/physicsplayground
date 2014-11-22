package me.lachlanap.physicsplayground.physics;

/**
 *
 * @author lachlan
 */
public class World {

    private static final int MAX_OBJECTS = 1024 * 8;

    private int numberOfConstraintSolves;
    private double gravity;

    private double floor;
    private double walls;

    private final double[] x, y, px, py;
    private final double[] r;
    private final double[] pinX, pinY;
    private int objects;

    private boolean ewo;

    public World() {
        numberOfConstraintSolves = 3;
        gravity = -9.81;

        x = new double[MAX_OBJECTS];
        y = new double[MAX_OBJECTS];
        px = new double[MAX_OBJECTS];
        py = new double[MAX_OBJECTS];
        r = new double[MAX_OBJECTS];
        pinX = new double[MAX_OBJECTS];
        pinY = new double[MAX_OBJECTS];
        objects = 0;

        ewo = false;

        initialise();
    }

    public void initialise() {
        objects = 0;
        floor = -3;
        walls = 20;

        objects = 1;
    }

    public void addPinnedObject(double x, double y, double radius) {
        int id = addObject(x, y, radius);
        pinX[id] = x;
        pinY[id] = y;
    }

    public int addObject(double x, double y, double radius) {
        if (objects < MAX_OBJECTS) {
            int id = objects;
            initialiseObject(id, x, y, radius);
            objects++;

            return id;
        } else
            return -1;
    }

    private void initialiseObject(int i, double posx, double posy, double radius) {
        x[i] = posx;
        y[i] = posy;
        r[i] = radius;

        px[i] = posx;
        py[i] = posy;

        pinX[i] = Double.MAX_VALUE;
        pinY[i] = Double.MAX_VALUE;
    }

    public void update(double timestep) {
        long start;

        start = System.nanoTime();
        for (int i = 0; i < numberOfConstraintSolves; i++)
            solveConstraints();
        System.out.println("Constraints: " + (System.nanoTime() - start) + "ns");

        start = System.nanoTime();
        integrate(timestep);
        System.out.println("Integration: " + (System.nanoTime() - start) + "ns");
    }

    private void solveConstraints() {
        for (int i = 0; i < objects; i++) {
            solveWallAndFloor(i);
            solveCollisions(i);

            if (pinX[i] != Double.MAX_VALUE) {
                x[i] = pinX[i];
                y[i] = pinY[i];
            }
        }
    }

    private void solveWallAndFloor(int i) {
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
    }

    private void solveCollisions(int i) {
        double x1 = x[i];
        double y1 = y[i];

        for (int j = i + 1; j < objects; j++) {
            double x2 = x[j];
            double y2 = y[j];

            double rBoth = r[i] + r[j];

            if (Math.abs(x1 - x2) > rBoth)
                continue;
            if (Math.abs(y1 - y2) > rBoth)
                continue;

            double rBothSq = rBoth * rBoth;
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

    private void integrate(double timestep) {
        for (int i = 0; i < objects; i++) {
            double vx = x[i] - px[i];
            double vy = y[i] - py[i];

            double ax = -vx * 5;
            double ay = gravity + -vy * 5;

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

    public void setVelocity(int i, double vx, double vy) {
        px[i] = x[i] - vx;
        py[i] = y[i] - vy;
    }

    public boolean isEWO() {
        return ewo;
    }

    public void setEWO(boolean ewo) {
        this.ewo = ewo;
    }


}
