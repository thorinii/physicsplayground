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

    public void step(double timestep, int numberOfConstraintSolves, Timer timer) {
        for (int i = 0; i < numberOfConstraintSolves; i++)
            constraintSolver.solve(world, timer);

        delete();
        processInflow();

        integrator.integrate(world, timestep, timer);
    }


    private void delete() {
        if (!world.isDeleteAtFloor())
            return;
        for (int i = 0; i < world.getObjects();) {
            if (world.getY(i) - world.getRadius(i) < world.getFloor()) {
                world.deleteObject(i);
            } else {
                i++;
            }
        }
    }

    private void processInflow() {
        if (!world.isInflowEnabled())
            return;

        double radius = .5;

        for (int i = 0; i < 5; i++) {
            double x = world.getInflowX() + (Math.random() * radius * 2 - radius) * 2;
            double y = world.getInflowY() + (Math.random() * radius * 2 - radius) * 2;

            world.addObject(x, y, radius);
        }
    }
}
