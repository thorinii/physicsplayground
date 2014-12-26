package me.lachlanap.physicsplayground.physics;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author lachlan
 */
public class Timer {

    private final Map<String, Double> times;

    public Timer() {
        this.times = new HashMap<>();
    }

    public synchronized void computeTime(String name, long beginNanos) {
        long now = System.nanoTime();
        long dtNanos = now - beginNanos;

        double dt = dtNanos / 1_000_000_000.0;

        times.put(name, dt);
    }

    @Override
    public synchronized String toString() {
        StringBuilder builder = new StringBuilder();

        for (Map.Entry<String, Double> e : times.entrySet()) {
            builder.append(e.getKey()).append(": ")
                    .append(String.format("%04.2f", e.getValue() * 1_000_000))
                    .append("us; ");
        }

        return builder.toString();
    }
}
