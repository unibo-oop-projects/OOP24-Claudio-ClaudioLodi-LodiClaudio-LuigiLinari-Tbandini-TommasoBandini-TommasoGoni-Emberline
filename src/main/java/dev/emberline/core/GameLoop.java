package dev.emberline.core;

import dev.emberline.core.event.EventDispatcher;
import dev.emberline.core.input.InputDispatcher;
import dev.emberline.core.render.Renderer;
import dev.emberline.core.update.Updater;
import dev.emberline.game.GameRoot;
import javafx.scene.canvas.Canvas;
import javafx.stage.Stage;

import java.util.concurrent.atomic.AtomicBoolean;

public class GameLoop extends Thread {
    // GameLoop initialized only once
    private static GameLoop instance;
    private static boolean initialized = false;

    // JavaFX Stage
    private final Stage stage;

    // Components
    private final Updater updater;
    private final Renderer renderer;
    private final InputDispatcher inputDispatcher;
    private final EventDispatcher eventDispatcher;

    // Game loop settings
    private final long TICKS_PER_SECOND = 20;
    private final long NS_PER_UPDATE = (long) 1e9 / TICKS_PER_SECOND;

    // To stop the game loop
    public final AtomicBoolean running = new AtomicBoolean(false);

    // Game
    private final GameRoot gameRoot;

    private GameLoop(final Stage stage, final Canvas canvas) {
        super("Game Thread");
        this.stage = stage;
        this.eventDispatcher = new EventDispatcher();
        this.gameRoot = new GameRoot();
        this.renderer = new Renderer(gameRoot, canvas);
        this.updater = new Updater(gameRoot);
        this.inputDispatcher = new InputDispatcher(gameRoot);
    }

    public static synchronized void init(final Stage stage, final Canvas canvas) {
        if (instance != null) {
            throw new IllegalStateException("GameLoop already initialized");
        }
        instance = new GameLoop(stage, canvas);
        initialized = true;
    }

    public static synchronized GameLoop getInstance() {
        if (!initialized) {
            throw new IllegalStateException("GameLoop not initialized yet. Call init() first.");
        }
        return instance;
    }

    @Override
    public void run() {
        long previous = System.nanoTime();
        long lagUpdate = 0;

        running.set(true);
        while (running.get()) {
            // Timings
            final long now = System.nanoTime();
            final long elapsed = now - previous;
            previous = now;
            lagUpdate += elapsed;

            // Resolve inputs
            inputDispatcher.dispatchInputs();

            // Update with fixed time step
            while (lagUpdate >= NS_PER_UPDATE) {
                lagUpdate -= NS_PER_UPDATE;
                updater.update(NS_PER_UPDATE);
            }

            // Render (maybe add a synchronization concept to avoid the busy waiting)
            renderer.render();

            // sleep (for fixed FPS, although I'm not sure if we actually have control over
            // the rate at which JavaFX sends screen update requests)
        }
    }

    public Renderer getRenderer() {
        return renderer;
    }

    public EventDispatcher getEventDispatcher() {
        return eventDispatcher;
    }
}
