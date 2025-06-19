package dev.emberline.core.render;

import dev.emberline.core.graphics.SpriteLoader;
import dev.emberline.core.graphics.spritekeys.StringSpriteKey;
import javafx.application.Platform;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;

import java.util.Objects;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

import dev.emberline.core.components.Renderable;
import javafx.scene.image.Image;

public class Renderer {
    public static final int GUICS_HEIGHT = 18;
    public static final int GUICS_WIDTH = 32;

    // JavaFX Canvas, only JavaFX thread can modify the scene graph, do not modify the scene graph from another thread
    private final Canvas canvas;

    private final GraphicsContext gc;
    private final AtomicBoolean isRunningLater = new AtomicBoolean(false);

    private final Renderable root;

    private final CoordinateSystem worldCoordinateSystem = new CoordinateSystem(0, 0, 32, 18);
    private final CoordinateSystem guiCoordinateSystem = new CoordinateSystem(0, 0, GUICS_WIDTH, GUICS_HEIGHT);

    // Rendering queue
    private final PriorityBlockingQueue<RenderTask> renderQueue = new PriorityBlockingQueue<>();
    private long taskOrderingCounter = 0;

    public Renderer(Renderable root, Canvas canvas) {
        this.root = root;
        this.canvas = canvas;
        this.gc = canvas.getGraphicsContext2D();
    }

    public void render() {
        if (isRunningLater.get()) return; // Busy waiting
        isRunningLater.set(true);

        // Updates of the coordinate systems
        worldCoordinateSystem.update(canvas.getWidth(), canvas.getHeight());
        guiCoordinateSystem.update(canvas.getWidth(), canvas.getHeight());

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
        renderTask.setSecondaryPriority(taskOrderingCounter++);
        renderQueue.offer(Objects.requireNonNull(renderTask));
    }

    public GraphicsContext getGraphicsContext() {
        return gc;
    }

    public CoordinateSystem getWorldCoordinateSystem() {
        return worldCoordinateSystem;
    }

    public CoordinateSystem getGuiCoordinateSystem() {
        return guiCoordinateSystem;
    }

    // UTILITY METHODS FOR RENDERING; must be called from the JavaFX Application Thread (rendertask lambda) // TODO DOCUMENTATION
    public static void drawImage(Image image, GraphicsContext gc, CoordinateSystem cs, double x, double y, double width, double height) {
        gc.drawImage(image, cs.toScreenX(x), cs.toScreenY(y), cs.getScale() * width, cs.getScale() * height);
    }

    // Draw an Image with a fixed aspect ratio, aligned to the left and centered vertically of the given rectangular area
    public static void drawImageFit(Image image, GraphicsContext gc, CoordinateSystem cs, double x, double y, double width, double height) {
        double scalingFactor = Math.min(width / image.getWidth(), height / image.getHeight());
        y += (height - image.getHeight() * scalingFactor) / 2; // vertical centering
        drawImage(image, gc, cs, x, y, image.getWidth() * scalingFactor, image.getHeight() * scalingFactor);
    }

    // Draw an Image with a fixed aspect ratio, aligned to the center of the given rectangular area in both axes
    public static void drawImageFitCenter(Image image, GraphicsContext gc, CoordinateSystem cs, double x, double y, double width, double height) {
        double scalingFactor = Math.min(width / image.getWidth(), height / image.getHeight());
        y += (height - image.getHeight() * scalingFactor) / 2; // vertical centering
        x += (width - image.getWidth() * scalingFactor) / 2; // horizontal centering
        drawImage(image, gc, cs, x, y, image.getWidth() * scalingFactor, image.getHeight() * scalingFactor);
    }

    // drawtext centering height margin
    private static final double CENTER_TEXT_H_MARGIN = 0.07;
    // Minimum area in pixels before using uppercase
    private static final double MIN_TEXT_AREA_PX_UPPERCASE = 500;
    // Minimum text height in pixels before enabling image smoothing
    private static final double MIN_TEXT_HEIGHT_PX_SMOOTH = 20;

    // Draw a string within the given rectangular area optimizing for readability
    public static void drawText(String text, GraphicsContext gc, CoordinateSystem cs, double x, double y, double width, double height) {
        boolean gcImageSmoothing = gc.isImageSmoothing();
        double areaInPixels = width * height * cs.getScale() * cs.getScale();
        // Convert to uppercase if the area is too small
        if (areaInPixels < MIN_TEXT_AREA_PX_UPPERCASE) text = text.toUpperCase();
        // Use image smoothing if the area is too small
        if (height * cs.getScale() < MIN_TEXT_HEIGHT_PX_SMOOTH) gc.setImageSmoothing(true);
        // Fit image or stretch vertically
        Image image = SpriteLoader.loadSprite(new StringSpriteKey(text)).image();
        if (width / image.getWidth() < height / image.getHeight()) {
            drawImage(image, gc, cs, x, y + height * CENTER_TEXT_H_MARGIN, width, height * (1 - 2 * CENTER_TEXT_H_MARGIN));
        } else {
            drawImageFit(image, gc, cs, x, y, width, height);
        }
        gc.setImageSmoothing(gcImageSmoothing);
    }

    // Draw a rectangle with the given coordinates and size, filling it with the current fill color
    public static void fillRect(GraphicsContext gc, CoordinateSystem cs, double x, double y, double width, double height) {
        gc.fillRect(cs.toScreenX(x), cs.toScreenY(y), cs.getScale() * width, cs.getScale() * height);
    }

}
