package dev.emberline.core;

import dev.emberline.core.render.Renderer;
import dev.emberline.core.update.Updater;

import java.util.concurrent.atomic.AtomicBoolean;

public class GameLoop extends Thread {
    private final long TICKS_PER_SECOND = 20;
    private final long NS_PER_UPDATE = (long) 1e9 / TICKS_PER_SECOND;
    public AtomicBoolean running = new AtomicBoolean(false);
    private Renderer renderer;
    private Updater updater;

    @Override
    public void run() {
        running.set(true);
        Thread.currentThread().setName("Game thread");

        long previous = System.nanoTime();
        long lagUpdate = 0;
        while(running.get()) {
            // Timings
            long now = System.nanoTime();
            long elapsed = now - previous;
            previous = now;
            lagUpdate += elapsed;

            // Input todo

            // Update
            while(lagUpdate >= NS_PER_UPDATE) {
                updater.update(elapsed);
                lagUpdate -= NS_PER_UPDATE;
            }

            renderer.render();
        }
    }
}
