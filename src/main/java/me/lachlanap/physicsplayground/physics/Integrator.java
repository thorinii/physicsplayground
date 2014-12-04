package me.lachlanap.physicsplayground.physics;

/**
 *
 * @author lachlan
 */
public class Integrator {

    public void integrate(World world, double timestep) {
        Vector2 position = new Vector2();
        Vector2 previous = new Vector2();
        Vector2 next = new Vector2();
        Vector2 velocity = new Vector2();
        Vector2 acceleration = new Vector2();

        for (int i = 0; i < world.getObjects(); i++) {
            if (world.isPinned(i))
                continue;

            world.getPosition(i, position);
            world.getPrevious(i, previous);
            position.minus(previous, velocity);

            acceleration.set(velocity).negate().plus(0, world.getGravity());

            next.set(position).plus(velocity).plus(acceleration.mul(timestep * timestep));

            world.setPrevious(i, position);
            world.setPosition(i, next);
        }
    }

}
