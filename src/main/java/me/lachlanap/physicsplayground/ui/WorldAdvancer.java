package me.lachlanap.physicsplayground.ui;

import me.lachlanap.physicsplayground.physics.World;

/**
 *
 * @author lachlan
 */
class WorldAdvancer {

    private static final double DEFAULT_TIMESTEP = 1 / 60.0;

    private final World world;
    private final WorldRenderer renderer;
    private final Thread thread;

    private double timestep;
    private volatile boolean active;

    public WorldAdvancer(World world, WorldRenderer renderer) {
        this.world = world;
        this.renderer = renderer;

        this.thread = new Thread(new AdvancerTask());
        thread.setDaemon(true);

        timestep = DEFAULT_TIMESTEP;
        active = true;
    }

    public void start() {
        thread.start();
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public boolean isActive() {
        return active;
    }

    private class AdvancerTask implements Runnable {

        final double NANOS_IN_SECOND = 1_000_000_000;
        long lastUpdate;
        long lastPhysicsUpdate;

        double physicsFps;

        @Override
        public void run() {
            lastUpdate = System.nanoTime();
            lastPhysicsUpdate = lastUpdate;

            try {
                while (true) {
                    gameLoop();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        private void gameLoop() {
            final long now = System.nanoTime();
            final double delta = (now - lastUpdate) / NANOS_IN_SECOND;
            lastUpdate = now;

            double renderFps = 1 / delta;

            if (active)
                update(now);
            else {
                lastPhysicsUpdate = now;
                physicsFps = 0;
            }

            render(renderFps, physicsFps);
        }

        private void update(long now) {
            final double sinceLastUpdate = (now - lastPhysicsUpdate) / NANOS_IN_SECOND;
            double timeToUse = sinceLastUpdate;

            if (sinceLastUpdate > timestep) {
                physicsFps = 1 / ((now - lastPhysicsUpdate) / NANOS_IN_SECOND);
            }

            while (timeToUse > timestep) {
                world.update(timestep);
                timeToUse -= timestep;

                lastPhysicsUpdate = now;
            }
        }

        private void render(double renderFps, double physicsFps) {
            renderer.render(renderFps, physicsFps);
        }
    }
}
