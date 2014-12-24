package me.lachlanap.physicsplayground.physics;

/**
 *
 * @author lachlan
 */
public class IntList {

    private final int capacity;
    private final int[] array;
    private int size;

    public IntList(int capacity) {
        this.capacity = capacity;
        this.array = new int[capacity];
        this.size = 0;
    }

    public void set(int i, int value) {
        array[i] = value;
    }

    public int get(int i) {
        return array[i];
    }

    public void clear() {
        size = 0;
    }

    public int add(int value) {
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

    public void copyFrom(IntList from) {
        size = from.size;
        System.arraycopy(from.array, 0, array, 0, size);
    }
}
