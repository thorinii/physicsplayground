package me.lachlanap.physicsplayground.physics;

/**
 *
 * @author lachlan
 */
public class DistanceConstraintSolver {

    public void solveDistanceConstraints(World world) {
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

}
