package me.lachlanap.physicsplayground.physics;

/**
 *
 * @author lachlan
 */
public class World {

    private static final int MAX_OBJECTS = 1024 * 32;
    private static final int MAX_CONSTRAINTS = MAX_OBJECTS * 2;

    private final double gravity;
    private final double floor;

    private Buffer bufRead, bufWrite;

    private boolean ewo;
    private boolean deleteAtFloor;

    private double inflowX, inflowY;
    private boolean inflowEnabled;

    public World() {
        gravity = -9.81;
        floor = 0;

        bufRead = new Buffer();
        bufWrite = new Buffer();

        ewo = false;

        reset();
    }

    public void reset() {
        bufRead.reset();
        bufWrite.reset();
    }

    public void swapBuffers() {
        Buffer tmp = bufWrite;
        bufWrite = bufRead;
        bufRead = tmp;

        bufWrite.copyFrom(bufRead);
    }

    public void addPinnedObject(double x, double y, double radius) {
        int id = addObject(x, y, radius);
        pin(id);
    }

    public int addObject(double posX, double posY, double radius) {
        int id = bufRead.addObject();
        bufWrite.addObject();

        if (id != -1) {
            initialiseObject(bufRead, id, posX, posY, radius);
            initialiseObject(bufWrite, id, posX, posY, radius);
            return id;
        } else
            return -1;
    }

    private void initialiseObject(Buffer buf, int i, double posx, double posy, double radius) {
        buf.x.set(i, posx);
        buf.y.set(i, posy);
        buf.r.set(i, radius);

        buf.px.set(i, posx);
        buf.py.set(i, posy);

        buf.pinX.set(i, Double.MAX_VALUE);
        buf.pinY.set(i, Double.MAX_VALUE);
    }

    public void pin(int i) {
        bufRead.pinX.set(i, bufRead.x.get(i));
        bufRead.pinY.set(i, bufRead.y.get(i));
        bufWrite.pinX.set(i, bufRead.x.get(i));
        bufWrite.pinY.set(i, bufRead.y.get(i));
    }

    public void unpin(int i) {
        bufRead.pinX.set(i, Double.MAX_VALUE);
        bufRead.pinY.set(i, Double.MAX_VALUE);
        bufWrite.pinX.set(i, Double.MAX_VALUE);
        bufWrite.pinY.set(i, Double.MAX_VALUE);
    }

    public void addConstraint(int a, int b, double strength) {
        bufRead.addConstraint(a, b, strength);
        bufWrite.addConstraint(a, b, strength);
    }

    public double getGravity() {
        return gravity;
    }

    public PointObject getObject(int i, PointObject store) {
        return getObjectFromBuffer(bufRead, i, store);
    }

    public PointObject getObjectFromWriteBuffer(int i, PointObject store) {
        return getObjectFromBuffer(bufWrite, i, store);
    }

    private PointObject getObjectFromBuffer(Buffer buf, int i, PointObject store) {
        store.id = i;
        store.pos.x = buf.x.get(i);
        store.pos.y = buf.y.get(i);
        store.prev.x = buf.px.get(i);
        store.prev.y = buf.py.get(i);
        store.radius = buf.r.get(i);
        store.pin.x = buf.pinX.get(i);
        store.pin.y = buf.pinY.get(i);

        return store;
    }

    public PointObject updateObject(PointObject store) {
        bufWrite.x.set(store.id, store.pos.x);
        bufWrite.y.set(store.id, store.pos.y);
        bufWrite.px.set(store.id, store.prev.x);
        bufWrite.py.set(store.id, store.prev.y);
        bufWrite.r.set(store.id, store.radius);
        bufWrite.pinX.set(store.id, store.pin.x);
        bufWrite.pinY.set(store.id, store.pin.y);

        return store;
    }

    public Constraint getConstraint(int i, Constraint store) {
        store.id = i;
        store.a = bufRead.constraintA.get(i);
        store.b = bufRead.constraintB.get(i);
        store.distance = bufRead.constraintDistance.get(i);
        store.strength = bufRead.constraintStrength.get(i);

        return store;
    }

    public double getX(int i) {
        return bufRead.x.get(i);
    }

    public double getY(int i) {
        return bufRead.y.get(i);
    }

    public int getObjects() {
        return bufRead.x.size();
    }

    public int getConstraints() {
        return bufRead.constraintA.size();
    }

    public double getFloor() {
        return floor;
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

    public boolean isInflowEnabled() {
        return inflowEnabled;
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
        bufRead.deleteObject(i);
        bufWrite.deleteObject(i);
    }

    public void deleteConstraint(int i) {
        bufRead.deleteConstraint(i);
        bufWrite.deleteConstraint(i);
    }

    public static class Buffer {

        private final DoubleList x, y, px, py;
        private final DoubleList r;
        private final DoubleList pinX, pinY;

        private final IntList constraintA, constraintB;
        private final DoubleList constraintDistance, constraintStrength;

        public Buffer() {
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
        }

        public int addObject() {
            int id = x.add(0);
            y.add(0);
            px.add(0);
            py.add(0);
            r.add(0);
            pinX.add(0);
            pinY.add(0);

            return id;
        }

        public int addConstraint(int a, int b, double strength) {
            int id = constraintA.add(a);

            if (id != -1) {
                constraintB.add(b);
                constraintDistance.add(Math.hypot(x.get(a) - x.get(b), y.get(a) - y.get(b)));
                constraintStrength.add(strength);
            }

            return id;
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

        public void copyFrom(Buffer from) {
            x.copyFrom(from.x);
            y.copyFrom(from.y);
            px.copyFrom(from.px);
            py.copyFrom(from.py);

            r.copyFrom(from.r);
            pinX.copyFrom(from.pinX);
            pinY.copyFrom(from.pinY);

            constraintA.copyFrom(from.constraintA);
            constraintB.copyFrom(from.constraintB);
            constraintDistance.copyFrom(from.constraintDistance);
            constraintStrength.copyFrom(from.constraintStrength);
        }
    }
}
