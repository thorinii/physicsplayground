package me.lachlanap.physicsplayground.physics;

/**
 *
 * @author lachlan
 */
public class ConstraintSolver {

    private final DistanceConstraintSolver distanceSolver;
    private final CollisionConstraintSolver collisionSolver;

    public ConstraintSolver() {
        this.distanceSolver = new DistanceConstraintSolver();
        this.collisionSolver = new CollisionConstraintSolver();
    }

    public void solve(World world, Timer t) {
        long start = System.nanoTime();

        distanceSolver.solveDistanceConstraints(world);
        t.computeTime("Distance Constraints", start);

        start = System.nanoTime();
        collisionSolver.solveObjectConstraints(world);
        t.computeTime("Object Constraints", start);
    }

}
