package dev.emberline.core.render;

import javafx.application.Platform;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;

import java.util.Objects;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

import dev.emberline.core.game.components.Renderable;

public class Renderer {
    // JavaFX Canvas, only JavaFX thread can modify the scene graph, do not modify the scene graph from another thread
    private final Canvas canvas;
    private final GraphicsContext gc;
    private final AtomicBoolean isRunningLater = new AtomicBoolean(false);

    private final Renderable root;

    // Rendering queue
    private final PriorityBlockingQueue<RenderTask> renderQueue = new PriorityBlockingQueue<>();

    public Renderer(Renderable root, Canvas canvas) {
        this.root = root;
        this.canvas = canvas;
        this.gc = canvas.getGraphicsContext2D();
    }

    public GraphicsContext getGraphicsContext() {
        return gc;
    }

    public void render() {
        if (isRunningLater.get()) return; // Busy waiting
        isRunningLater.set(true);

        // Fills up the renderQueue
        root.render();

        Platform.runLater(() -> {
            gc.setImageSmoothing(false);
            gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());

            while (!renderQueue.isEmpty()) {
                RenderTask rt = renderQueue.poll();
                rt.run();
            }

            isRunningLater.set(false);
        });
    }

    /**
     * Adds a task to the rendering queue.
     *
     * @param renderTask the task to add, must not be null
     * @throws NullPointerException if renderTask is null
     */
    public void addRenderTask(RenderTask renderTask) {
        renderQueue.offer(Objects.requireNonNull(renderTask));
    }
}
