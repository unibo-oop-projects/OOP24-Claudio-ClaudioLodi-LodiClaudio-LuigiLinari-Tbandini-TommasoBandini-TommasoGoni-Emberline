package dev.emberline.core;

import dev.emberline.core.input.InputDispatcher;
import dev.emberline.core.render.Renderer;
import dev.emberline.core.update.Updater;
import dev.emberline.game.GameRoot;
import javafx.application.Platform;
import javafx.scene.canvas.Canvas;
import javafx.stage.Stage;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Represents the main game loop thread responsible for managing game processes in a loop,
 * such as input handling, updating game logic, and rendering frames. The game loop runs
 * with a fixed update rate to ensure consistent gameplay.
 * <p>
 * This class is implemented as a singleton and must be initialized using the static
 * {@link GameLoop#init(Stage, Canvas)} method before obtaining an instance.
 */
public final class GameLoop extends Thread {
    // GameLoop initialized only once
    private static GameLoop instance;
    private static boolean initialized = false;

    // JavaFX Stage
    private final Stage stage;

    // Components
    private final Updater updater;
    private final Renderer renderer;
    private final InputDispatcher inputDispatcher;

    // Game loop settings
    private static final long TICKS_PER_SECOND = 20;
    private static final long NS_PER_UPDATE = (long) 1e9 / TICKS_PER_SECOND;

    // To stop the game loop
    public final AtomicBoolean running = new AtomicBoolean(false);

    // Game
    private final GameRoot gameRoot;

    private GameLoop(final Stage stage, final Canvas canvas) {
        super("Game Thread");
        this.stage = stage;
        this.gameRoot = new GameRoot();
        this.renderer = new Renderer(gameRoot, canvas);
        this.updater = new Updater(gameRoot);
        this.inputDispatcher = new InputDispatcher(gameRoot);
    }

    /**
     * Initializes the {@code GameLoop} as a singleton instance. This method must be called
     * before getting an instance of the game loop or attempting to start the game processes.
     * It associates the game loop with the specified JavaFX {@link Stage} and {@link Canvas}.
     *
     * @param stage the primary {@link Stage} of the JavaFX application, used for managing the GUI and resources.
     * @param canvas the {@link Canvas} on which the game is rendered, dynamically bound to the stage dimensions.
     * @throws IllegalStateException if the {@code GameLoop} is already initialized.
     */
    public static synchronized void init(final Stage stage, final Canvas canvas) {
        if (instance != null) {
            throw new IllegalStateException("GameLoop already initialized");
        }
        instance = new GameLoop(stage, canvas);
        initialized = true;
    }

    /**
     * Provides the singleton instance of the {@code GameLoop}.
     * This method ensures the instance is properly initialized before it is returned.
     *
     * @return the singleton instance of the {@code GameLoop}.
     * @throws IllegalStateException if the {@code GameLoop} has not been initialized by calling {@code init()}.
     */
    public static synchronized GameLoop getInstance() {
        if (!initialized) {
            throw new IllegalStateException("GameLoop not initialized yet. Call init() first.");
        }
        return instance;
    }

    /**
     * Executes the game loop that manages the core lifecycle of the game, including input processing,
     * updating, and rendering. This method runs until explicitly stopped, maintaining a fixed
     * update rate for consistent gameplay.
     */
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

    /**
     * Retrieves the renderer associated with the game loop.
     *
     * @return the {@link Renderer} responsible for rendering the game state.
     */
    public Renderer getRenderer() {
        return renderer;
    }

    /**
     * Sets the fullscreen mode of the game window.
     * @param fullscreen if true, the game will run in fullscreen mode; otherwise, it will run in windowed mode.
     */
    public void setFullscreen(final boolean fullscreen) {
        Platform.runLater(() -> {
            stage.setFullScreen(fullscreen);
        });
    }
}
