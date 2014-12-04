package me.lachlanap.physicsplayground.physics;

/**
 *
 * @author lachlan
 */
public class ConstraintSolver {

    public void solve(World world) {
        solveDistanceConstraints(world);
        solveObjectConstraints(world);
    }

    private void solveDistanceConstraints(World world) {
        Vector2 aPos = new Vector2();
        Vector2 bPos = new Vector2();
        Vector2 difference = new Vector2();

        for (int i = 0; i < world.getConstraints(); i++) {
            int a = world.getConstraintA(i);
            int b = world.getConstraintB(i);
            double restingDistance = world.getConstraintRestingDistance(i);
            double strength = world.getConstraintStrength(i);

            world.getPosition(a, aPos);
            world.getPosition(b, bPos);

            aPos.minus(bPos, difference);
            double actualDistance = difference.length();

            double error;
            if (actualDistance == 0)
                error = 1;
            else
                error = (restingDistance - actualDistance) / actualDistance;

            difference.mul(0.5 * error);

            world.setPosition(a, aPos.plus(difference));
            world.setPosition(b, bPos.minus(difference));
        }
    }


    private void solveObjectConstraints(World world) {
        Vector2 pin = new Vector2();

        for (int i = 0; i < world.getObjects(); i++) {
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

        for (int j = i + 1; j < world.getObjects(); j++) {
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
                world.setPosition(j, pos2.minus(difference));
            }
        }
    }
}
