package dev.emberline.core;

import dev.emberline.core.render.Renderer;
import dev.emberline.core.update.Updater;
import javafx.scene.canvas.Canvas;
import javafx.stage.Stage;

import java.util.concurrent.atomic.AtomicBoolean;

public class GameLoop extends Thread {
    // JavaFX Stage
    private final Stage stage;

    // Game object
    // public static final Game game = new Game();

    // Components
    private final Renderer renderer;
    private final Updater updater;

    // Game loop settings
    private final long TICKS_PER_SECOND = 20;
    private final long NS_PER_UPDATE = (long) 1e9 / TICKS_PER_SECOND;

    // To stop the game loop
    public final AtomicBoolean running = new AtomicBoolean(false);

    public GameLoop(Stage stage, Canvas canvas) {
        super("Game Thread");
        this.stage = stage;
        renderer = new Renderer(canvas);
        updater = new Updater();
    }

    @Override
    public void run() {
        long previous = System.nanoTime();
        long lagUpdate = 0;

        running.set(true);
        while(running.get()) {
            // Timings
            long now = System.nanoTime();
            long elapsed = now - previous;
            previous = now;
            lagUpdate += elapsed;

            // Input todo

            // Update with fixed time step
            while(lagUpdate >= NS_PER_UPDATE) {
                updater.update(elapsed);
                lagUpdate -= NS_PER_UPDATE;
            }

            renderer.render();
        }
    }
}
