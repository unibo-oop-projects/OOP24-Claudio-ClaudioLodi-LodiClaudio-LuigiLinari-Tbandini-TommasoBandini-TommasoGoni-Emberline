package dev.emberline.core.render;

import javafx.application.Platform;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;

import java.util.concurrent.atomic.AtomicBoolean;

public class Renderer {
    // JavaFX Canvas, only JavaFX thread can modify the scene graph, do not modify the scene graph from another thread
    private final Canvas canvas;
    private final GraphicsContext gc;
    private final AtomicBoolean isRunningLater = new AtomicBoolean(false);

    public Renderer(Canvas canvas) {
        this.canvas = canvas;
        gc = canvas.getGraphicsContext2D();
    }

    public void render() {
        if (isRunningLater.get()) return; // Busy waiting
        isRunningLater.set(true);

        // Test rendering
        Platform.runLater(() -> {
            gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
            gc.setStroke(javafx.scene.paint.Color.RED);
            gc.strokeText("Hello, World!", canvas.getWidth()/2, canvas.getHeight()/2);
            double x = (Math.sin(System.currentTimeMillis() / 1000.0)+1)/2 * (canvas.getWidth()-30);
            gc.setFill(javafx.scene.paint.Color.BLUE);
            gc.fillOval(x, canvas.getHeight() / 2, 30, 30);
            isRunningLater.set(false);
        });
    }
}
