package me.lachlanap.physicsplayground.physics;

/**
 *
 * @author lachlan
 */
public class World {

    private static final int MAX_OBJECTS = 1024 * 8;
    private static final int MAX_CONSTRAINTS = MAX_OBJECTS * 2;

    private int numberOfConstraintSolves;
    private double gravity;

    private double floor;

    private final DoubleList x, y, px, py;
    private final DoubleList r;
    private final DoubleList pinX, pinY;

    private final IntList constraintA, constraintB;
    private final DoubleList constraintDistance, constraintStrength;

    private boolean ewo;
    private boolean deleteAtFloor;

    private double inflowX, inflowY;
    private boolean inflowEnabled;

    public World() {
        numberOfConstraintSolves = 3;
        gravity = -9.81;

        x = new DoubleList(MAX_OBJECTS);
        y = new DoubleList(MAX_OBJECTS);
        px = new DoubleList(MAX_OBJECTS);
        py = new DoubleList(MAX_OBJECTS);

        r = new DoubleList(MAX_OBJECTS);
        pinX = new DoubleList(MAX_OBJECTS);
        pinY = new DoubleList(MAX_OBJECTS);

        constraintA = new IntList(MAX_CONSTRAINTS);
        constraintB = new IntList(MAX_CONSTRAINTS);
        constraintDistance = new DoubleList(MAX_CONSTRAINTS);
        constraintStrength = new DoubleList(MAX_CONSTRAINTS);

        ewo = false;

        reset();
    }

    public void reset() {
        x.clear();
        y.clear();
        px.clear();
        py.clear();
        r.clear();
        pinX.clear();
        pinY.clear();

        constraintA.clear();
        constraintB.clear();
        constraintDistance.clear();
        constraintStrength.clear();

        floor = 0;
    }

    public void addPinnedObject(double x, double y, double radius) {
        int id = addObject(x, y, radius);
        pin(id);
    }

    public int addObject(double posX, double posY, double radius) {
        int id = x.add(0);
        y.add(0);
        px.add(0);
        py.add(0);
        r.add(0);
        pinX.add(0);
        pinY.add(0);

        if (id != -1) {
            initialiseObject(id, posX, posY, radius);
            return id;
        } else
            return -1;
    }

    private void initialiseObject(int i, double posx, double posy, double radius) {
        x.set(i, posx);
        y.set(i, posy);
        r.set(i, radius);

        px.set(i, posx);
        py.set(i, posy);

        pinX.set(i, Double.MAX_VALUE);
        pinY.set(i, Double.MAX_VALUE);
    }

    public void pin(int i) {
        pinX.set(i, x.get(i));
        pinY.set(i, y.get(i));
    }

    public void unpin(int i) {
        pinX.set(i, Double.MAX_VALUE);
        pinY.set(i, Double.MAX_VALUE);
    }

    public boolean isPinned(int i) {
        return pinX.get(i) != Double.MAX_VALUE;
    }

    public void addConstraint(int a, int b, double strength) {
        int id = constraintA.add(a);

        if (id != -1) {
            constraintB.add(b);
            constraintDistance.add(Math.hypot(x.get(a) - x.get(b), y.get(a) - y.get(b)));
            constraintStrength.add(strength);
        }
    }


    public void update(double timestep) {
        for (int i = 0; i < numberOfConstraintSolves; i++)
            solveConstraints();

        delete();
        processInflow();
    }

    private void solveConstraints() {
        solveDistanceConstraints();

        for (int i = 0; i < x.size(); i++) {
            solveWallAndFloor(i);
            solveCollisions(i);

            if (isPinned(i)) {
                x.set(i, pinX.get(i));
                y.set(i, pinY.get(i));
            }
        }
    }

    private void solveDistanceConstraints() {
        for (int i = 0; i < constraintA.size(); i++) {
            int a = constraintA.get(i);
            int b = constraintB.get(i);
            double restingDistance = constraintDistance.get(i);
            double strength = constraintStrength.get(i);

            double xA = x.get(a);
            double yA = y.get(a);
            double xB = x.get(b);
            double yB = y.get(b);

            double diffX = xA - xB;
            double diffY = yA - yB;
            double actualDistance = Math.sqrt(diffX * diffX + diffY * diffY);

            double difference;
            if (actualDistance == 0)
                difference = 1;
            else
                difference = (restingDistance - actualDistance) / actualDistance;

            x.set(a, x.get(a) + diffX * 0.5 * difference);
            y.set(a, y.get(a) + diffY * 0.5 * difference);
            x.set(b, x.get(b) - diffX * 0.5 * difference);
            y.set(b, y.get(b) - diffY * 0.5 * difference);
        }
    }


    private void solveWallAndFloor(int i) {
        if (!deleteAtFloor && y.get(i) - r.get(i) < floor) {
            y.set(i, floor + r.get(i));
            py.set(i, py.get(i) + (y.get(i) - py.get(i)) * 2);

            px.set(i, x.get(i) - (x.get(i) - px.get(i)) * 0.97);
        }
    }

    private void solveCollisions(int i) {
        double x1 = x.get(i);
        double y1 = y.get(i);

        for (int j = i + 1; j < x.size(); j++) {
            double x2 = x.get(j);
            double y2 = y.get(j);

            double rBoth = r.get(i) + r.get(j);

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

                x.set(i, x.get(i) + penetration * normalX * 0.5 * 0.99);
                y.set(i, y.get(i) + penetration * normalY * 0.5 * 0.99);
                x.set(j, x.get(j) - penetration * normalX * 0.5 * 0.99);
                y.set(j, y.get(j) - penetration * normalY * 0.5 * 0.99);
            }
        }
    }

    private void delete() {
        if (!deleteAtFloor)
            return;
        for (int i = 0; i < x.size();) {
            if (y.get(i) - r.get(i) < floor) {
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

    public double getGravity() {
        return gravity;
    }


    public Vector2 getPosition(int i, Vector2 store) {
        store.x = x.get(i);
        store.y = y.get(i);
        return store;
    }

    public Vector2 getPrevious(int i, Vector2 store) {
        store.x = px.get(i);
        store.y = py.get(i);
        return store;
    }


    public void setPosition(int i, Vector2 value) {
        x.set(i, value.x);
        y.set(i, value.y);
    }

    public void setPrevious(int i, Vector2 value) {
        px.set(i, value.x);
        py.set(i, value.y);
    }

    public double getX(int i) {
        return x.get(i);
    }

    public double getY(int i) {
        return y.get(i);
    }

    public double getWidth(int i) {
        return r.get(i) * 2;
    }

    public double getHeight(int i) {
        return r.get(i) * 2;
    }

    public int getConstraintA(int i) {
        return constraintA.get(i);
    }

    public int getConstraintB(int i) {
        return constraintB.get(i);
    }

    public int getObjects() {
        return x.size();
    }

    public int getConstraints() {
        return constraintA.size();
    }

    public double getFloor() {
        return floor;
    }

    public void setVelocity(int i, double vx, double vy) {
        px.set(i, x.get(i) - vx / 60);
        py.set(i, y.get(i) - vy / 60);
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
        x.delete(i);
        y.delete(i);
        px.delete(i);
        py.delete(i);
        r.delete(i);
        pinX.delete(i);
        pinY.delete(i);

        for (int j = 0; j < constraintA.size();) {
            if (constraintA.get(j) == i || constraintB.get(j) == i)
                deleteConstraint(j);
            else
                j++;
        }

        for (int j = 0; j < constraintA.size(); j++) {
            if (constraintA.get(j) == x.size())
                constraintA.set(j, i);
            else if (constraintB.get(j) == x.size())
                constraintB.set(j, i);
        }
    }

    public void deleteConstraint(int i) {
        constraintA.delete(i);
        constraintB.delete(i);
        constraintDistance.delete(i);
        constraintStrength.delete(i);
    }
}
