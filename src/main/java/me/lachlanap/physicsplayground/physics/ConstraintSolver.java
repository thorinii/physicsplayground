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

    public void solve(World world) {
        distanceSolver.solveDistanceConstraints(world);
        collisionSolver.solveObjectConstraints(world);
    }

}
