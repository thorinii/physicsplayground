package me.lachlanap.physicsplayground.ui;

import java.util.Queue;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import me.lachlanap.physicsplayground.physics.World;
import me.lachlanap.physicsplayground.physics.WorldStepper;

/**
 *
 * @author lachlan
 */
class WorldAdvancer implements Executor {

    private static final double DEFAULT_TIMESTEP = 1 / 60.0;

    private final World world;
    private final WorldStepper stepper;
    private final WorldRenderer renderer;
    private final Thread thread;

    private final Queue<Runnable> tasks;

    private double timestep;
    private volatile boolean active;

    public WorldAdvancer(World world, WorldStepper stepper, WorldRenderer renderer) {
        this.world = world;
        this.stepper = stepper;
        this.renderer = renderer;

        this.thread = new Thread(new AdvancerTask());
        thread.setDaemon(true);

        this.tasks = new LinkedBlockingQueue<>();

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

    @Override
    public void execute(Runnable command) {
        tasks.add(command);
    }

    private class AdvancerTask implements Runnable {

        final double NANOS_IN_SECOND = 1_000_000_000;
        long lastUpdate;
        long lastPhysicsUpdate;

        double renderFps;
        double physicsFps;

        @Override
        public void run() {
            lastUpdate = System.nanoTime();
            lastPhysicsUpdate = lastUpdate;

            try {
                while (true) {
                    gameLoop();
                    executeTasks();

                    if (!active)
                        Thread.sleep(1);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        private void gameLoop() {
            final long now = System.nanoTime();
            final double delta = (now - lastUpdate) / NANOS_IN_SECOND;

            renderFps = 1 / delta;

            if (active) {
                update(now);
            } else {
                lastPhysicsUpdate = now;
                physicsFps = 0;

                render(now);
            }
        }

        private void executeTasks() {
            Runnable r;
            while ((r = tasks.poll()) != null) {
                r.run();
            }
        }

        private void update(long now) {
            final double sinceLastUpdate = (now - lastPhysicsUpdate) / NANOS_IN_SECOND;
            double timeToUse = sinceLastUpdate;

            if (sinceLastUpdate > timestep) {
                physicsFps = 1 / ((now - lastPhysicsUpdate) / NANOS_IN_SECOND);
            }

            if (timeToUse > timestep) {
                stepper.step(timestep, 3, renderer.getTimer());
                timeToUse -= timestep;

                lastPhysicsUpdate = now;

                render(now);
            }
        }

        private void render(long now) {
            renderer.render(renderFps, physicsFps);
            lastUpdate = now;
        }
    }
}
