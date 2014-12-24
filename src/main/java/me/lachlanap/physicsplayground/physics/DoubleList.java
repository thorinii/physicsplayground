package me.lachlanap.physicsplayground.physics;

/**
 *
 * @author lachlan
 */
public class DoubleList {

    private final int capacity;
    private final double[] array;
    private int size;

    public DoubleList(int capacity) {
        this.capacity = capacity;
        this.array = new double[capacity];
        this.size = 0;
    }

    public void set(int i, double value) {
        array[i] = value;
    }

    public double get(int i) {
        return array[i];
    }

    public void clear() {
        size = 0;
    }

    public int add(double value) {
        if (size < capacity) {
            int index = size;
            array[index] = value;
            size++;
            return index;
        } else {
            return -1;
        }
    }

    public int size() {
        return size;
    }

    public void delete(int i) {
        array[i] = array[size - 1];
        size--;
    }

    public void copyFrom(DoubleList from) {
        size = from.size;
        System.arraycopy(from.array, 0, array, 0, size);
    }
}
