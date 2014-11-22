package me.lachlanap.physicsplayground.physics;

/**
 *
 * @author lachlan
 */
public class World {

    private static final int MAX_OBJECTS = 1024 * 8;
    private static final int MAX_CONSTRAINTS = 1024;

    private int numberOfConstraintSolves;
    private double gravity;

    private double floor;

    private final double[] x, y, px, py;
    private final double[] r;
    private final double[] pinX, pinY;
    private int objects;

    private final int[] constraintA, constraintB;
    private final double[] constraintDistance, constraintStrength;
    private int constraints;

    private boolean ewo;
    private boolean deleteAtFloor;

    private double inflowX, inflowY;
    private boolean inflowEnabled;

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

        constraintA = new int[MAX_CONSTRAINTS];
        constraintB = new int[MAX_CONSTRAINTS];
        constraintDistance = new double[MAX_CONSTRAINTS];
        constraintStrength = new double[MAX_CONSTRAINTS];
        constraints = 0;

        ewo = false;

        reset();
    }

    public void reset() {
        objects = 0;
        constraints = 0;
        floor = -3;
    }

    public void addPinnedObject(double x, double y, double radius) {
        int id = addObject(x, y, radius);
        pin(id);
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

    public void pin(int i) {
        pinX[i] = x[i];
        pinY[i] = y[i];
    }

    public void unpin(int i) {
        pinX[i] = Double.MAX_VALUE;
        pinY[i] = Double.MAX_VALUE;
    }

    public void addConstraint(int a, int b, double strength) {
        if (constraints < MAX_CONSTRAINTS) {
            int id = constraints;
            constraints++;

            constraintA[id] = a;
            constraintB[id] = b;
            constraintDistance[id] = Math.hypot(x[a] - x[b], y[a] - y[b]);
            constraintStrength[id] = strength;
        }
    }


    public void update(double timestep) {
        for (int i = 0; i < numberOfConstraintSolves; i++)
            solveConstraints();

        delete();
        processInflow();

        integrate(timestep);
    }

    private void solveConstraints() {
        solveDistanceConstraints();

        for (int i = 0; i < objects; i++) {
            solveWallAndFloor(i);
            solveCollisions(i);

            if (pinX[i] != Double.MAX_VALUE) {
                x[i] = pinX[i];
                y[i] = pinY[i];
            }
        }
    }

    private void solveDistanceConstraints() {
        for (int i = 0; i < constraints; i++) {
            int a = constraintA[i];
            int b = constraintB[i];
            double restingDistance = constraintDistance[i];
            double strength = constraintStrength[i];

            double xA = x[a];
            double yA = y[a];
            double xB = x[b];
            double yB = y[b];

            double diffX = xA - xB;
            double diffY = yA - yB;
            double actualDistance = Math.sqrt(diffX * diffX + diffY * diffY);

            double difference = (restingDistance - actualDistance) / actualDistance;

            x[a] += diffX * 0.5 * difference;
            y[a] += diffY * 0.5 * difference;
            x[b] -= diffX * 0.5 * difference;
            y[b] -= diffY * 0.5 * difference;
        }
    }

    private void solveWallAndFloor(int i) {
        if (!deleteAtFloor && y[i] - r[i] < floor) {
            y[i] = floor + r[i];
            py[i] = py[i] + (y[i] - py[i]) * 2;

            px[i] = x[i] - (x[i] - px[i]) * 0.97;
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

    private void delete() {
        if (!deleteAtFloor)
            return;
        for (int i = 0; i < objects;) {
            if (y[i] - r[i] < floor) {
                deleteObject(i);
            } else {
                i++;
            }
        }
    }

    private void processInflow() {
        if (!inflowEnabled)
            return;

        double radius = 5;

        for (int i = 0; i < 2; i++) {
            double x = inflowX + (Math.random() * radius * 2 - radius);
            double y = inflowY + (Math.random() * radius * 2 - radius);

            addObject(x, y, 1);
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

    public int getConstraintA(int i) {
        return constraintA[i];
    }

    public int getConstraintB(int i) {
        return constraintB[i];
    }

    public int getObjects() {
        return objects;
    }

    public int getConstraints() {
        return constraints;
    }

    public double getFloor() {
        return floor;
    }

    public void setVelocity(int i, double vx, double vy) {
        px[i] = x[i] - vx / 60;
        py[i] = y[i] - vy / 60;
    }

    public boolean isEWO() {
        return ewo;
    }

    public void setEWO(boolean ewo) {
        this.ewo = ewo;
    }

    public boolean isDeleteAtFloor() {
        return deleteAtFloor;
    }

    public void setDeleteAtFloor(boolean deleteAtFloor) {
        this.deleteAtFloor = deleteAtFloor;
    }

    public void setInflowEnabled(boolean inflowEnabled) {
        this.inflowEnabled = inflowEnabled;
    }

    public void setInflow(double x, double y) {
        this.inflowX = x;
        this.inflowY = y;

        if (y < floor)
            inflowEnabled = false;
    }

    public double getInflowX() {
        return inflowX;
    }

    public double getInflowY() {
        return inflowY;
    }


    public void deleteObject(int i) {
        deleteFromArray(i, objects, x);
        deleteFromArray(i, objects, y);
        deleteFromArray(i, objects, px);
        deleteFromArray(i, objects, py);
        deleteFromArray(i, objects, r);
        deleteFromArray(i, objects, pinX);
        deleteFromArray(i, objects, pinY);

        for (int j = 0; j < constraints;) {
            if (constraintA[j] == i || constraintB[j] == i)
                deleteConstraint(j);
            else
                j++;
        }

        objects--;

        for (int j = 0; j < constraints; j++) {
            if (constraintA[j] == objects)
                constraintA[j] = i;
            else if (constraintB[j] == objects)
                constraintB[j] = i;
        }
    }

    public void deleteConstraint(int i) {
        deleteFromArray(i, constraints, constraintA);
        deleteFromArray(i, constraints, constraintB);
        deleteFromArray(i, constraints, constraintDistance);
        deleteFromArray(i, constraints, constraintStrength);

        constraints--;
    }

    private void deleteFromArray(int i, int last, double[] array) {
        array[i] = array[last - 1];
    }

    private void deleteFromArray(int i, int last, int[] array) {
        array[i] = array[last - 1];
    }
}
