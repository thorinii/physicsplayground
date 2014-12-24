package me.lachlanap.physicsplayground.physics;

/**
 *
 * @author lachlan
 */
public class DistanceConstraintSolver {

    public void solveDistanceConstraints(World world) {
        PointObject a = new PointObject();
        PointObject b = new PointObject();
        Vector2 difference = new Vector2();

        for (int i = 0; i < world.getConstraints(); i++) {
            int aid = world.getConstraintA(i);
            int bid = world.getConstraintB(i);

            double restingDistance = world.getConstraintRestingDistance(i);
            double strength = world.getConstraintStrength(i);
            world.getObject(aid, a);
            world.getObject(bid, b);

            a.pos.minus(b.pos, difference);
            double actualDistance = difference.length();

            double error;
            if (actualDistance == 0)
                error = 1;
            else
                error = (restingDistance - actualDistance) / actualDistance;

            difference.mul(0.5 * error);
            a.pos.plus(difference);
            b.pos.minus(difference);

            world.updateObject(a);
            world.updateObject(b);
        }
    }

}
