package dev.emberline.core;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.layout.Background;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class EmberlineApp extends Application {

    private final long MIN_WINDOW_WIDTH = 400;
    private final long MIN_WINDOW_HEIGHT = 400;
    private GameLoop gameLoop;

    @Override
    public void start(Stage stage) {
        // The root node of the scene graph is a Pane.
        // A pane's parent will resize the pane within the pane's resizable range during layout
        Pane root = new Pane();
        root.setBackground(Background.fill(Color.BLACK));

        // Canvas
        Canvas canvas = new Canvas(MIN_WINDOW_WIDTH, MIN_WINDOW_HEIGHT);
        canvas.widthProperty().bind(root.widthProperty());
        canvas.heightProperty().bind(root.heightProperty());
        root.getChildren().add(canvas);

        // Scene
        Scene scene = new Scene(root);
        stage.setScene(scene);

        // Stage settings
        stage.setMaximized(true);
        stage.setMinWidth(MIN_WINDOW_WIDTH);
        stage.setMinHeight(MIN_WINDOW_HEIGHT);
        stage.setTitle("Emberline");

        stage.show();

        // Starting the Game Thread
        this.gameLoop = new GameLoop("Game Thread", stage, canvas);
        this.gameLoop.start();
    }

    /** Calling Platform.exit() is the preferred way to explicitly terminate a JavaFX Application.
     *  Directly calling System.exit(int) is an acceptable alternative, but doesn't allow the Application stop() method to run.
     */
    @Override
    public void stop() {
        if (gameLoop != null) gameLoop.running.set(false);
    }
}
