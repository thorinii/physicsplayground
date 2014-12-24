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

        Constraint constraint = new Constraint();
        for (int i = 0; i < world.getConstraints(); i++) {
            world.getConstraint(i, constraint);

            world.getObjectFromWriteBuffer(constraint.a, a);
            world.getObjectFromWriteBuffer(constraint.b, b);

            a.pos.minus(b.pos, difference);
            double actualDistance = difference.length();

            double error;
            if (actualDistance == 0)
                error = 1;
            else
                error = (constraint.distance - actualDistance) / actualDistance;

            difference.mul(0.5 * error);
            a.pos.plus(difference);
            b.pos.minus(difference);

            world.updateObject(a);
            world.updateObject(b);
        }

        world.swapBuffers();
    }
}
