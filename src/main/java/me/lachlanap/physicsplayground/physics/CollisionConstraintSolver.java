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
        if (world.isEWO())
            pool.invoke(new SplitterTask(world, 0, world.getObjects()));
        else
            solveRange(world, 0, world.getObjects());
    }

    private void solveRange(World world, int start, int finish) {
        Vector2 pin = new Vector2();

        for (int i = start; i < finish; i++) {
            solveWallAndFloor(world, i);
            solveCollisions(world, i);

            if (world.isPinned(i)) {
                world.setPosition(i, world.getPin(i, pin));
            }
        }
    }

    private void solveWallAndFloor(World world, int i) {
        Vector2 pos = world.getPosition(i, new Vector2());
        Vector2 ppos = world.getPrevious(i, new Vector2());

        if (!world.isDeleteAtFloor() && pos.y - world.getRadius(i) < world.getFloor()) {
            pos.y = world.getFloor() + world.getRadius(i);
            world.setY(i, pos.y);

            ppos.x = pos.x - (pos.x - ppos.x) * 0.97; // friction
            ppos.y = ppos.y + (pos.y - ppos.y) * 2; // bounce
            world.setPrevious(i, ppos);
        }
    }

    private void solveCollisions(World world, int i) {
        Vector2 pos1 = world.getPosition(i, new Vector2());
        Vector2 pos2 = new Vector2();
        Vector2 difference = new Vector2();

        for (int j = 0; j < world.getObjects(); j++) {
            if (i == j)
                continue;

            world.getPosition(j, pos2);

            double rBoth = world.getRadius(i) + world.getRadius(j);

            if (Math.abs(pos1.x - pos2.x) > rBoth)
                continue;
            if (Math.abs(pos1.y - pos2.y) > rBoth)
                continue;

            double rBothSq = rBoth * rBoth;
            pos1.minus(pos2, difference);

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

                world.setPosition(i, pos1.plus(difference));
                //world.setPosition(j, pos2.minus(difference));
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
