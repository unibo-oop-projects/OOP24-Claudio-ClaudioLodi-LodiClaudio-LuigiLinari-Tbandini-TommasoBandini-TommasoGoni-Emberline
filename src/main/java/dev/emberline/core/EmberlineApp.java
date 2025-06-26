package dev.emberline.core;

import dev.emberline.core.input.InputDispatcher;
import dev.emberline.core.sounds.AudioController;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.input.InputEvent;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

/**
 * The {@code EmberlineApp} class serves as the main entry point for the Emberline JavaFX
 * application. It initializes the application's graphical components, directs where input events
 * should be dispatched and starts the game loop.
 * <p>
 * Once the application is required to stop, this class is responsible for notifying the game loop thread.
 */
public class EmberlineApp extends Application {

    private static final long MIN_WINDOW_WIDTH = 400;
    private static final long MIN_WINDOW_HEIGHT = 400;
    private GameLoop gameLoop;
    private AudioController audioController;

    /**
     * The entry point for the JavaFX application. This method is called after the
     * JavaFX system is initialized and sets up the primary stage for the application.
     * It includes initializing the scene graph, creating a canvas for rendering, configuring
     * event routing for input handling, and starting the game loop.
     *
     * @param stage the primary stage for this application, provided by the JavaFX runtime. This stage
     *              is configured with a scene, event handlers, and other settings such as title,
     *              dimensions, and maximized state.
     */
    @Override
    public void start(final Stage stage) {
        // The root node of the scene graph is a Pane.
        // A pane's parent will resize the pane within the pane's resizable range during layout
        final Pane root = new Pane();
        root.setBackground(Background.fill(Color.BLACK));

        // Canvas
        final Canvas canvas = new Canvas(MIN_WINDOW_WIDTH, MIN_WINDOW_HEIGHT);
        canvas.widthProperty().bind(root.widthProperty());
        canvas.heightProperty().bind(root.heightProperty());
        root.getChildren().add(canvas);

        // Scene
        final Scene scene = new Scene(root);
        stage.setScene(scene);

        // Routing of input events
        final EventHandler<InputEvent> eventHandler = InputDispatcher::sendInput;
        scene.addEventHandler(MouseEvent.MOUSE_CLICKED, eventHandler);
        scene.addEventHandler(KeyEvent.KEY_PRESSED, eventHandler);
        // MouseLocation events
        scene.addEventHandler(MouseEvent.MOUSE_ENTERED, eventHandler);
        scene.addEventHandler(MouseEvent.MOUSE_EXITED, eventHandler);
        scene.addEventHandler(MouseEvent.MOUSE_MOVED, eventHandler);

        // Stage settings
        stage.setMaximized(true);
        stage.setMinWidth(MIN_WINDOW_WIDTH);
        stage.setMinHeight(MIN_WINDOW_HEIGHT);
        stage.setTitle("Emberline");

        stage.show();

        // Starting the Game Thread
        GameLoop.init(stage, canvas);
        this.gameLoop = GameLoop.getInstance();
        this.gameLoop.start();
        // Loops musical track of the game
        this.audioController = new AudioController();
        this.audioController.startSoundtrack();
    }

    /**
     * Calling Platform.exit() is the preferred way to explicitly terminate a JavaFX Application.
     * Directly calling System.exit(int) is an acceptable alternative, but doesn't allow the Application stop() method to run.
     */
    @Override
    public void stop() {
        if (gameLoop != null) {
            gameLoop.running.set(false);
        }
    }
}
