package me.lachlanap.physicsplayground.physics;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;
import java.util.concurrent.atomic.AtomicInteger;

/**
 *
 * @author lachlan
 */
public class CollisionConstraintSolver {

    private final ForkJoinPool pool;
    private final AtomicInteger maxInCell;

    public CollisionConstraintSolver() {
        this.pool = new ForkJoinPool();
        this.maxInCell = new AtomicInteger();
    }

    public void solveObjectConstraints(World world) {
        world.buildPartitionGrid();

        if (world.isEWO())
            pool.invoke(new SplitterTask(world, 0, world.getObjects()));
        else
            solveRange(world, 0, world.getObjects());

        world.swapBuffers();

        System.out.println(maxInCell.get());
        maxInCell.set(0);
    }

    private void solveRange(World world, int start, int finish) {
        PointObject obj = new PointObject();
        for (int i = start; i < finish; i++) {
            world.getObject(i, obj);

            solveWallAndFloor(world, obj);

            solveCollisionsNew(world, obj);


            if (obj.isPinned()) {
                obj.pos.x = obj.pin.x;
                obj.pos.y = obj.pin.y;
                obj.prev.x = obj.pin.x;
                obj.prev.y = obj.pin.y;
            }

            world.updateObject(obj);
        }
    }

    private void solveWallAndFloor(World world, PointObject obj) {
        if (!world.isDeleteAtFloor() && obj.pos.y - obj.radius < world.getFloor()) {
            obj.pos.y = world.getFloor() + obj.radius;

            obj.prev.x = obj.pos.x - (obj.pos.x - obj.prev.x) * 0.97; // friction
            obj.prev.y = obj.prev.y + (obj.pos.y - obj.prev.y) * 2; // bounce
        }
    }

    private void solveCollisionsNew(World world, PointObject a) {
        Vector2 difference = new Vector2();

        int count = 0;
        for (PointObject b : world.getPartitionGrid().getObjectsNear(world, a)) {
            solvePotentialCollision(a, b, difference);
            count++;
        }

        int original;
        do {
            original = maxInCell.get();
        } while (original < count && !maxInCell.compareAndSet(original, count));
    }

    private void solvePotentialCollision(PointObject a, PointObject b, Vector2 difference) {
        double rBoth = a.radius + b.radius;
        if (Math.abs(a.pos.x - b.pos.x) > rBoth)
            return;
        if (Math.abs(a.pos.y - b.pos.y) > rBoth)
            return;

        double rBothSq = rBoth * rBoth;

        a.pos.minus(b.pos, difference);
        double distSq = difference.lengthSq();

        if (distSq < rBothSq)
            solveCollision(distSq, rBoth, difference, a);
    }

    private void solveCollision(double distSq, double rBoth, Vector2 difference, PointObject a) {
        double dist = Math.sqrt(distSq);
        double penetration = rBoth - dist;
        double normalX;
        double normalY;

        if (dist > 0) {
            normalX = difference.x / dist;
            normalY = difference.y / dist;
        } else {
            normalX = 1;
            normalY = 0;
        }

        difference.x = penetration * normalX * 0.5 * 0.99;
        difference.y = penetration * normalY * 0.5 * 0.99;

        a.pos.plus(difference);
    }

    private class SplitterTask extends RecursiveAction {

        private final World world;
        private final int start, finish;

        public SplitterTask(World world, int start, int finish) {
            this.world = world;
            this.start = start;
            this.finish = finish;
        }

        @Override
        protected void compute() {
            int range = finish - start;

            if (range < 100) {
                solveRange(world, start, finish);
            } else {
                invokeAll(new SplitterTask(world, start, start + range / 2),
                          new SplitterTask(world, finish - range / 2, finish));
            }
        }

    }
}
