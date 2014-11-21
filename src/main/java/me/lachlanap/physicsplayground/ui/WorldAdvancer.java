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

    public WorldAdvancer(World world, WorldRenderer renderer) {
        this.world = world;
        this.renderer = renderer;

        this.thread = new Thread(new AdvancerTask());
        thread.setDaemon(true);

        timestep = DEFAULT_TIMESTEP;
    }

    public void start() {
        thread.start();
    }

    private class AdvancerTask implements Runnable {

        final double NANOS_IN_SECOND = 1_000_000_000;
        long lastUpdate;
        long lastPhysicsUpdate;

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
            double physicsFps = 1 / ((now - lastPhysicsUpdate) / NANOS_IN_SECOND);

            update(now);
            render(renderFps, physicsFps);
        }

        private void update(long now) {
            double sinceLastUpdate = (now - lastPhysicsUpdate) / NANOS_IN_SECOND;

            while (sinceLastUpdate > timestep) {
                world.update(timestep);
                sinceLastUpdate -= timestep;

                lastPhysicsUpdate = now;
            }
        }

        private void render(double renderFps, double physicsFps) {
            renderer.render(renderFps, physicsFps);
        }
    }
}
