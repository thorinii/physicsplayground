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
        delete();
        processInflow();
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

    public Vector2 getPin(int i, Vector2 store) {
        store.x = pinX.get(i);
        store.y = pinY.get(i);
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

    public double getRadius(int i) {
        return r.get(i);
    }

    public double getWidth(int i) {
        return r.get(i) * 2;
    }

    public double getHeight(int i) {
        return r.get(i) * 2;
    }

    public void setY(int i, double value) {
        y.set(i, value);
    }


    public int getConstraintA(int i) {
        return constraintA.get(i);
    }

    public int getConstraintB(int i) {
        return constraintB.get(i);
    }

    public double getConstraintRestingDistance(int i) {
        return constraintDistance.get(i);
    }

    public double getConstraintStrength(int i) {
        return constraintStrength.get(i);
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
