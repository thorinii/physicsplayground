package me.lachlanap.physicsplayground.physics;

/**
 *
 * @author lachlan
 */
public class WorldStepper {

    private final World world;
    private final Integrator integrator;
    private final ConstraintSolver constraintSolver;

    public WorldStepper(World world) {
        this.world = world;

        this.integrator = new Integrator();
        this.constraintSolver = new ConstraintSolver();
    }

    public void step(double timestep, int numberOfConstraintSolves) {
        /*for (int i = 0; i < numberOfConstraintSolves; i++)
         constraintSolver.solve(world);

         delete();
         processInflow();*/
        world.update(timestep);

        integrator.integrate(world, timestep);
    }
}
