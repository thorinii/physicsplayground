package me.lachlanap.physicsplayground.physics;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;

/**
 *
 * @author lachlan
 */
public class CollisionConstraintSolver {

    private final ForkJoinPool pool;

    public CollisionConstraintSolver() {
        this.pool = new ForkJoinPool();
    }

    public void solveObjectConstraints(World world) {
        world.buildPartitionGrid();

        if (true)
            pool.invoke(new SplitterTask(world, 0, world.getObjects()));
        else
            solveRange(world, 0, world.getObjects());

        world.swapBuffers();
    }

    private void solveRange(World world, int start, int finish) {
        PointObject obj = new PointObject();
        for (int i = start; i < finish; i++) {
            world.getObject(i, obj);

            solveWallAndFloor(world, obj);

            if (world.isEWO())
                solveCollisionsNew(world, obj);
            else
                solveCollisionsOld(world, obj);


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

    private void solveCollisionsOld(World world, PointObject a) {
        PointObject b = new PointObject();
        Vector2 difference = new Vector2();

        for (int j = 0; j < world.getObjects(); j++) {
            if (a.id == j)
                continue;

            world.getObject(j, b);

            double rBoth = a.radius + b.radius;

            if (Math.abs(a.pos.x - b.pos.x) > rBoth)
                continue;
            if (Math.abs(a.pos.y - b.pos.y) > rBoth)
                continue;

            double rBothSq = rBoth * rBoth;
            a.pos.minus(b.pos, difference);

            double distSq = difference.lengthSq();
            if (distSq < rBothSq) {
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
        }
    }

    private void solveCollisionsNew(World world, PointObject a) {
        Vector2 difference = new Vector2();

        for (PointObject b : world.getPartitionGrid().getObjectsNear(world, a)) {
            if (a.id == b.id)
                continue;

            double rBoth = a.radius + b.radius;

            /*if (Math.abs(a.pos.x - b.pos.x) > rBoth)
             continue;
             if (Math.abs(a.pos.y - b.pos.y) > rBoth)
             continue;*/

            double rBothSq = rBoth * rBoth;
            a.pos.minus(b.pos, difference);

            double distSq = difference.lengthSq();
            if (distSq < rBothSq) {
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
        }
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
