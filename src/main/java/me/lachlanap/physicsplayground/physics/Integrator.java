package me.lachlanap.physicsplayground.physics;

/**
 *
 * @author lachlan
 */
public class Integrator {

    public void integrate(World world, double timestep, Timer timer) {
        long start = System.nanoTime();

        verletIntegration(world, timestep);

        timer.computeTime("Integration", start);
    }

    private void verletIntegration(World world, double timestep) {
        PointObject obj = new PointObject();

        Vector2 next = new Vector2();
        Vector2 velocity = new Vector2();
        Vector2 acceleration = new Vector2();

        for (int i = 0; i < world.getObjects(); i++) {
            world.getObject(i, obj);
            if (obj.isPinned())
                continue;

            obj.getVelocity(velocity);

            // Air Resistance + gravity
            acceleration.set(velocity).negate().plus(0, world.getGravity());

            // Integration
            next.set(obj.pos).plus(velocity).plus(acceleration.mul(timestep * timestep));

            obj.prev.set(obj.pos);
            obj.pos.set(next);
            world.updateObject(obj);
        }
    }

}
