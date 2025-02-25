package dev.emberline.core;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class EmberlineApp extends Application {

    @Override
    public void start(Stage stage) {
        GameLoop gameLoop = new GameLoop();
        gameLoop.start();

        Scene scene = new Scene(new Pane(new Canvas(400,400)));

        stage.setScene(scene);
        stage.show();
    }

    /** Calling Platform.exit() is the preferred way to explicitly terminate a JavaFX Application.
     *  Directly calling System.exit(int) is an acceptable alternative, but doesn't allow the Application stop() method to run.
     */
    @Override
    public void stop() {
        System.out.println("STO USCENDO");
    }
}
