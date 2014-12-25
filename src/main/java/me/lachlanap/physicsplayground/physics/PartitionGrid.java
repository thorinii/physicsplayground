package me.lachlanap.physicsplayground.physics;

import java.util.Arrays;
import java.util.Iterator;

/**
 *
 * @author lachlan
 */
public class PartitionGrid {

    private static final double MIN_GRID_SPACING = 1;

    private final int divisions;
    private final Cell[] cells;
    private double xMin, yMin, xMax, yMax;
    private double xSpacing, ySpacing;

    public PartitionGrid(int divisions) {
        this.divisions = divisions;
        cells = new Cell[divisions * divisions];

        for (int i = 0; i < cells.length; i++)
            cells[i] = new Cell();
    }

    public void clear() {
        for (Cell cell : cells)
            cell.clear();
    }

    public void setBounds(double xMin, double yMin, double xMax, double yMax) {
        clear();

        this.xMin = xMin;
        this.yMin = yMin;
        this.xMax = xMax;
        this.yMax = yMax;

        xSpacing = Math.max(MIN_GRID_SPACING, (xMax - xMin) / divisions);
        ySpacing = Math.max(MIN_GRID_SPACING, (yMax - yMin) / divisions);
    }

    public void insert(int id, double x, double y, double radius) {
        int cellX = (int) Math.min(divisions - 1, (x - xMin) / xSpacing);
        int cellY = (int) Math.min(divisions - 1, (y - yMin) / ySpacing);
        double expanseX = radius / xSpacing;
        double expanseY = radius / ySpacing;

        int startCellX = (int) Math.max(Math.floor(cellX - expanseX), 0);
        int startCellY = (int) Math.max(Math.floor(cellY - expanseY), 0);
        int endCellX = (int) Math.min(Math.ceil(cellX + expanseX + 1), divisions);
        int endCellY = (int) Math.min(Math.ceil(cellY + expanseY + 1), divisions);

        for (int i = startCellX; i < endCellX; i++) {
            for (int j = startCellY; j < endCellY; j++) {
                getCell(i, j).insert(id);
            }
        }
    }

    public Iterable<PointObject> getObjectsNear(final World world, final PointObject test) {
        return new Iterable<PointObject>() {

            @Override
            public Iterator<PointObject> iterator() {
                return new Iterator<PointObject>() {
                    final PointObject tmp = new PointObject();
                    final SortedCell objectsFound = new SortedCell();
                    final int cellX, cellY;
                    final Cell cell;
                    final int startCellX, startCellY, endCellX, endCellY;

                    int currentCellX, currentCellY, currentCellIndex;
                    boolean consumed = false;

                    {
                        cellX = (int) Math.min(divisions - 1, (test.pos.x - xMin) / xSpacing);
                        cellY = (int) Math.min(divisions - 1, (test.pos.y - yMin) / ySpacing);
                        cell = getCell(cellX, cellY);

                        double expanseX = test.radius / xSpacing;
                        double expanseY = test.radius / ySpacing;

                        startCellX = (int) Math.max(Math.floor(cellX - expanseX), 0);
                        startCellY = (int) Math.max(Math.floor(cellY - expanseY), 0);
                        endCellX = (int) Math.min(Math.ceil(cellX + expanseX + 1), divisions);
                        endCellY = (int) Math.min(Math.ceil(cellY + expanseY + 1), divisions);

                        currentCellX = startCellX;
                        currentCellY = startCellY;
                    }

                    private void findNext() {
                        if (!consumed)
                            return;

                        for (; currentCellIndex < cell.length();) {
                            int id = cell.get(currentCellIndex);

                            if (id == test.id)
                                currentCellIndex++;
                            else {
                                if (id == test.id || objectsFound.contains(id)) {
                                    currentCellIndex++;
                                } else {
                                    //objectsFound.insert(id);
                                    world.getObject(id, tmp);

                                    consumed = false;
                                    currentCellIndex++;
                                    return;
                                }
                            }
                        }
                    }

                    @Override
                    public boolean hasNext() {
                        if (consumed)
                            findNext();
                        return !consumed;
                    }

                    @Override
                    public PointObject next() {
                        if (consumed) {
                            findNext();

                            if (hasNext()) {
                                consumed = true;
                                return tmp;
                            } else
                                throw new IllegalStateException("Iterator finished");
                        } else {
                            consumed = true;
                            return tmp;
                        }
                    }
                };
            }
        };
    }

    private Cell getCell(int x, int y) {
        if (x >= divisions || y >= divisions)
            throw new IllegalArgumentException("Cell index out of range: (" + x + "," + y + ")");
        if (x < 0 || y < 0)
            throw new IllegalArgumentException("Cell index out of range: (" + x + "," + y + ")");

        return cells[x + y * divisions];
    }

    private static final class Cell {

        private int[] list = new int[2];
        private int count = 0;

        public void clear() {
            count = 0;
        }

        public int length() {
            return count;
        }

        public int get(int i) {
            if (i < count)
                return list[i];
            else
                throw new IllegalArgumentException("Index " + i + " out of range (" + length() + ")");
        }

        public void insert(int id) {
            if (count >= list.length)
                grow();


            list[count] = id;
            count++;
        }

        private void grow() {
            int[] bigger = new int[list.length * 2];
            System.arraycopy(list, 0, bigger, 0, count);

            list = bigger;
        }
    }

    private static final class SortedCell {

        private int[] list = new int[2];
        private int count = 0;

        public boolean contains(int id) {
            int low = 0;
            int high = count - 1;

            while (low <= high) {
                int mid = (low + high) >>> 1;
                int midVal = list[mid];

                if (midVal < id)
                    low = mid + 1;
                else if (midVal > id)
                    high = mid - 1;
                else
                    return true;
            }
            return false;
        }

        public void insert(int id) {
            if (count >= list.length)
                grow();

            int index = -Arrays.binarySearch(list, 0, count, id) - 1;

            System.arraycopy(list, index, list, index + 1, count - index);

            list[index] = id;
            count++;
        }

        private void grow() {
            int[] bigger = new int[list.length * 2];
            System.arraycopy(list, 0, bigger, 0, count);

            list = bigger;
        }
    }
}
