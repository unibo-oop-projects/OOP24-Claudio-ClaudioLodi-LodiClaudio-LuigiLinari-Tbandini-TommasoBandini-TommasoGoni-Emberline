package dev.emberline.core.render;

import dev.emberline.core.components.RenderComponent;
import dev.emberline.core.graphics.SpriteLoader;
import dev.emberline.core.graphics.spritekeys.StringSpriteKey;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import javafx.application.Platform;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

import java.util.Locale;
import java.util.Objects;
import java.util.Queue;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * The {@code Renderer} class is responsible for executing {@link RenderTask} sent to it by
 * a root Renderable which will fill a {@code renderQueue}.
 * It does that in a thread-safe manner by ensuring they are executed
 * on the JavaFX Application thread using the {@link Platform#runLater(Runnable)} method.
 * <p>
 * To make sure the JavaFX run later queue doesn't get flooded due to not keeping up, when a run later
 * call is due an {@link AtomicBoolean} {@code isRunningLater} is set to {@code true} and no other {@link RenderTask}
 * are added until it has finished. The {@code Runnable} in the run later call is responsible for resetting the flag.
 * <p>
 * The {@code Renderer} is also responsible to keep updated the {@code worldCoordinateSystem},
 * and the {@code guiCoordinateSystem} based on the attached {@link Canvas}.
 */
public class Renderer {
    /**
     * Defines the default height in GUI coordinate space used for rendering GUI elements.
     * This constant is utilized as a standard height measurement when rendering
     * or placing graphical user interface components.
     */
    public static final int GUICS_HEIGHT = 18;
    /**
     * Defines the default width in GUI coordinate space used for rendering GUI elements.
     * This constant is utilized as a standard width measurement when rendering
     * or placing graphical user interface components.
     */
    public static final int GUICS_WIDTH = 32;

    // JavaFX Canvas, only JavaFX thread can modify the scene graph, do not modify the scene graph from another thread
    private final Canvas canvas;
    // Last used canvas dimensions
    private double lastUsedCanvasWidth;
    private double lastUsedCanvasHeight;

    private final GraphicsContext gc;
    private final AtomicBoolean isRunningLater = new AtomicBoolean(false);

    private final RenderComponent root;

    private final CoordinateSystem worldCoordinateSystem = new CoordinateSystem(0, 0, 32, 18);
    private final CoordinateSystem guiCoordinateSystem = new CoordinateSystem(0, 0, GUICS_WIDTH, GUICS_HEIGHT);

    // Rendering queue
    private final Queue<RenderTask> renderQueue = new PriorityBlockingQueue<>();
    private long taskOrderingCounter;

    // drawtext centering height margin
    private static final double CENTER_TEXT_H_MARGIN = 0.07;
    // Minimum area in pixels before using uppercase
    private static final double MIN_TEXT_AREA_PX_UPPERCASE = 500;
    // Minimum text height in pixels before enabling image smoothing
    private static final double MIN_TEXT_HEIGHT_PX_SMOOTH = 20;

    /**
     * Constructs a Renderer instance.
     *
     * @param root   the root Renderable object of the rendering hierarchy, used as the starting point for rendering operations
     * @param canvas the Canvas object on where the draw calls are performed
     * @see Renderer
     */
    @SuppressFBWarnings(
            value = "EI_EXPOSE_REP2", // May expose internal representation by 
                                      // storing an externally mutable object into ...
            justification = "This is intended behavior, the canvas is "
                    + "mutable and the Renderer reads its changes to keep "
                    + "up with the internal rendering logic."
    )
    public Renderer(final RenderComponent root, final Canvas canvas) {
        this.root = root;
        this.canvas = canvas;
        this.gc = canvas.getGraphicsContext2D();
        this.lastUsedCanvasWidth = canvas.getWidth();
        this.lastUsedCanvasHeight = canvas.getHeight();
    }

    /**
     * Triggers the rendering process for the associated root {@link RenderComponent} on the attached {@link Canvas}.
     * @see Renderer
     */
    public void render() {
        if (isRunningLater.get()) {
            return; // Busy waiting
        }
        isRunningLater.set(true);

        lastUsedCanvasWidth = canvas.getWidth();
        lastUsedCanvasHeight = canvas.getHeight();

        // Updates of the coordinate systems
        worldCoordinateSystem.update(lastUsedCanvasWidth, lastUsedCanvasHeight);
        guiCoordinateSystem.update(lastUsedCanvasWidth, lastUsedCanvasHeight);

        // Fills up the renderQueue
        root.render();

        Platform.runLater(() -> {
            gc.setImageSmoothing(false);
            gc.clearRect(0, 0, lastUsedCanvasWidth, lastUsedCanvasHeight);

            while (!renderQueue.isEmpty()) {
                final RenderTask rt = renderQueue.poll();
                rt.run();
            }

            isRunningLater.set(false);
        });
    }

    /**
     * Retrieves the current screen width in canvas coordinates.
     *
     * @return the current screen width as a double value.
     */
    public double getScreenWidth() {
        return lastUsedCanvasWidth;
    }

    /**
     * Retrieves the current screen height in canvas coordinates.
     *
     * @return the current screen height as a double value.
     */
    public double getScreenHeight() {
        return lastUsedCanvasHeight;
    }

    /**
     * Adds a task to the rendering queue.
     *
     * @param renderTask the task to add, must not be null
     * @throws NullPointerException if renderTask is null
     */
    public void addRenderTask(final RenderTask renderTask) {
        renderTask.setSecondaryPriority(taskOrderingCounter++);
        renderQueue.add(Objects.requireNonNull(renderTask));
    }

    /**
     * Retrieves the {@code GraphicsContext} associated with the current renderer.
     *
     * @return the {@code GraphicsContext} used by the renderer.
     */
    @SuppressFBWarnings(
            value = "EI_EXPOSE_REP", // TODO
            justification = "")
    public GraphicsContext getGraphicsContext() {
        return gc;
    }

    /**
     * Retrieves the world coordinate system used by the renderer.
     *
     * @return the {@code CoordinateSystem} instance representing the world coordinate system.
     */
    @SuppressFBWarnings(
            value = "EI_EXPOSE_REP", // TODO
            justification = "")
    public CoordinateSystem getWorldCoordinateSystem() {
        return worldCoordinateSystem;
    }

    /**
     * Retrieves the GUI coordinate system used by the renderer.
     *
     * @return the {@code CoordinateSystem} instance representing the GUI coordinate system.
     */
    @SuppressFBWarnings(
            value = "EI_EXPOSE_REP", // TODO
            justification = "")
    public CoordinateSystem getGuiCoordinateSystem() {
        return guiCoordinateSystem;
    }

    /**
     * Utility method for drawing an image onto the specified {@code GraphicsContext} given a position and an area.
     * It helps by transforming coordinates and scaling based on the provided {@code CoordinateSystem}.
     * <p>
     * Note: Since it's an operation on the {@code GraphicsContext} it must be called from the JavaFX Application Thread.
     *
     * @param image  the {@code Image} to be drawn
     * @param gc     the {@code GraphicsContext} on which the image will be drawn
     * @param cs     the {@code CoordinateSystem} used for transforming coordinates
     * @param x      the X coordinate of the top-left corner in the coordinate system
     * @param y      the Y coordinate of the top-left corner in the coordinate system
     * @param width  the width of the image in the coordinate system
     * @param height the height of the image in the coordinate system
     */
    public static void drawImage(
            final Image image, final GraphicsContext gc, final CoordinateSystem cs,
            final double x, final double y, final double width, final double height
    ) {
        gc.drawImage(image, cs.toScreenX(x), cs.toScreenY(y), cs.getScale() * width, cs.getScale() * height);
    }

    /**
     * Draws an image on the specified {@code GraphicsContext}, scaling it to fit within the specified rectangular area
     * while preserving its aspect ratio. The image is centered vertically in the given area.
     *
     * @param image  the {@code Image} to be drawn
     * @param gc     the {@code GraphicsContext} on which the image will be drawn
     * @param cs     the {@code CoordinateSystem} used for transforming coordinates
     * @param x      the X coordinate of the top-left corner in the coordinate system
     * @param y      the Y coordinate of the top-left corner in the coordinate system
     * @param width  the width of the target area in the coordinate system
     * @param height the height of the target area in the coordinate system
     */
    public static void drawImageFit(
            final Image image, final GraphicsContext gc, final CoordinateSystem cs,
            final double x, final double y, final double width, final double height
    ) {
        final double scalingFactor = Math.min(width / image.getWidth(), height / image.getHeight());
        final double newY = y + (height - image.getHeight() * scalingFactor) / 2; // vertical centering
        drawImage(image, gc, cs, x, newY, image.getWidth() * scalingFactor, image.getHeight() * scalingFactor);
    }

    /**
     * Draws an image on the specified {@code GraphicsContext}, with a fixed aspect ratio,
     * aligned to the center of the given rectangular area in both axes.
     *
     * @param image  the {@code Image} to be drawn
     * @param gc     the {@code GraphicsContext} on which the image will be drawn
     * @param cs     the {@code CoordinateSystem} used for transforming coordinates
     * @param x      the X coordinate of the top-left corner in the coordinate system
     * @param y      the Y coordinate of the top-left corner in the coordinate system
     * @param width  the width of the target area in the coordinate system
     * @param height the height of the target area in the coordinate system
     */
    public static void drawImageFitCenter(
            final Image image, final GraphicsContext gc, final CoordinateSystem cs,
            final double x, final double y, final double width, final double height
    ) {
        final double scalingFactor = Math.min(width / image.getWidth(), height / image.getHeight());
        final double newY = y + (height - image.getHeight() * scalingFactor) / 2; // vertical centering
        final double newX = x + (width - image.getWidth() * scalingFactor) / 2; // horizontal centering
        drawImage(image, gc, cs, newX, newY, image.getWidth() * scalingFactor, image.getHeight() * scalingFactor);
    }

    /**
     * Draws a text string onto the specified {@code GraphicsContext} within the given rectangular
     * area optimizing for readability.
     *
     * @param text   the text string to be drawn
     * @param gc     the {@code GraphicsContext} on which the text will be rendered
     * @param cs     the {@code CoordinateSystem} used for transforming coordinates
     * @param x      the X coordinate of the top-left corner for the text area in the coordinate system
     * @param y      the Y coordinate of the top-left corner for the text area in the coordinate system
     * @param width  the width of the area available for rendering the text in the coordinate system
     * @param height the height of the area available for rendering the text in the coordinate system
     */
    public static void drawText(
            final String text, final GraphicsContext gc, final CoordinateSystem cs,
            final double x, final double y, final double width, final double height
    ) {
        final boolean gcImageSmoothing = gc.isImageSmoothing();
        final double areaInPixels = width * height * cs.getScale() * cs.getScale();
        String formattedText = text;
        // Convert to uppercase if the area is too small
        if (areaInPixels < MIN_TEXT_AREA_PX_UPPERCASE) {
            formattedText = text.toUpperCase(Locale.US);
        }
        // Use image smoothing if the area is too small
        if (height * cs.getScale() < MIN_TEXT_HEIGHT_PX_SMOOTH) {
            gc.setImageSmoothing(true);
        }
        // Fit image or stretch vertically
        final Image image = SpriteLoader.loadSprite(new StringSpriteKey(formattedText)).image();
        if (width / image.getWidth() < height / image.getHeight()) {
            drawImage(image, gc, cs, x, y + height * CENTER_TEXT_H_MARGIN, width, height * (1 - 2 * CENTER_TEXT_H_MARGIN));
        } else {
            drawImageFit(image, gc, cs, x, y, width, height);
        }
        gc.setImageSmoothing(gcImageSmoothing);
    }

    /**
     * Utility method wrapping the {@link GraphicsContext#fillRect(double, double, double, double)} and
     * transforming coordinates and dimension based on the provided {@link CoordinateSystem}.
     *
     * @param gc     the {@code GraphicsContext} on which the rectangle will be drawn
     * @param cs     the {@code CoordinateSystem} used for transforming coordinates and scaling
     * @param x      the X coordinate of the top-left corner in the coordinate system
     * @param y      the Y coordinate of the top-left corner in the coordinate system
     * @param width  the width of the rectangle in the coordinate system
     * @param height the height of the rectangle in the coordinate system
     */
    public static void fillRect(
            final GraphicsContext gc, final CoordinateSystem cs, final double x,
            final double y, final double width, final double height
    ) {
        gc.fillRect(cs.toScreenX(x), cs.toScreenY(y), cs.getScale() * width, cs.getScale() * height);
    }

}
