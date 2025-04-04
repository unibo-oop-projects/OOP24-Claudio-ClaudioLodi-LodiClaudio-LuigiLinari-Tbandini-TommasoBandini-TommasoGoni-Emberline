package dev.emberline.core.render;

import javafx.application.Platform;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.*;

import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.DoubleFunction;

import dev.emberline.core.game.components.Renderable;

public class Renderer {
    // JavaFX Canvas, only JavaFX thread can modify the scene graph, do not modify the scene graph from another thread
    private final Canvas canvas;
    private static GraphicsContext gc = null; //Nullable
    private final AtomicBoolean isRunningLater = new AtomicBoolean(false);

    private final Renderable root;

    // Rendering queue
    private static final PriorityBlockingQueue<RenderTask> renderQueue = new PriorityBlockingQueue<>();

    /////// DEBUG ///////
    private final int _IMAGE_N = 39;
    private final Image[] _IMAGES = new Image[_IMAGE_N];
    private final AtomicLong _PREV = new AtomicLong(0);
    /////////////////////

    public Renderer(Canvas canvas, Renderable root) {
        this.canvas = canvas;
        Renderer.gc = canvas.getGraphicsContext2D();

        this.root = root;

        /////// DEBUG ///////
        for (int i = 1; i <= _IMAGE_N; ++i) {
            _IMAGES[i-1] = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/debug/" + i + ".png")));
        }
        _PREV.set(System.nanoTime());
        /////////////////////
    }

    // Could return null
    public static Optional<GraphicsContext> getGraphicsContext() {
        return Optional.ofNullable(Renderer.gc);
    }

    public void render() {
        if (isRunningLater.get()) return; // Busy waiting
        isRunningLater.set(true);

        // Fills up the renderQueue
        root.render();

        Platform.runLater(() -> {
            gc.setImageSmoothing(false);
            gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());

            while (!Renderer.renderQueue.isEmpty()) {
                RenderTask rt = Renderer.renderQueue.poll();
                rt.run();
            }

            isRunningLater.set(false);
        });
    }

    private void drawRegionDEBUG() {
        /////// DEBUG ///////
        // Screen coordinates:  [SCREEN]
        // World coordinates:   [WORLD]
        // World region bounded by two points (TL & BR) [WORLD]
        double region_x1 = 0;
        double region_y1 = 0;
        double region_x2 = 25;
        double region_y2 = 15;
        double tolerance = 10;

        ////// TEST ZOOM LERP EASE IN OUT
        double curr_time = System.currentTimeMillis();
        double t = curr_time*curr_time / (2*(curr_time*curr_time+curr_time)+1);

        // Screen dimensions    [SCREEN]
        double screen_width = canvas.getWidth();
        double screen_height = canvas.getHeight();
        // System.out.println(screen_width + " " + screen_height);
        // World region center  [WORLD]
        double reg_cx = region_x1+(region_x2-region_x1)/2;
        double reg_cy = region_y1+(region_y2-region_y1)/2;
        // Screen center        [SCREEN]
        double scx = screen_width / 2;
        double scy = screen_height / 2;
        // Scaling factor
        double k = Math.min(screen_width/(region_x2-region_x1), screen_height/(region_y2-region_y1));
        //double maxk = Math.max(screen_width/(region_x2-region_x1), screen_height/(region_y2-region_y1));
        // Screen origin (TL)   [WORLD]
        double sox = reg_cx - scx/k;
        double soy = reg_cy - scy/k;

        // Lambdas to convert between [SCREEN] and [WORLD]
        DoubleFunction<Double> scr_to_wrd_x = (double scr_x) -> sox + scr_x/k;
        DoubleFunction<Double> scr_to_wrd_y = (double scr_y) -> soy + scr_y/k;
        DoubleFunction<Double> wrd_to_scr_x = (double wrd_x) -> (wrd_x - sox)*k;
        DoubleFunction<Double> wrd_to_scr_y = (double wrd_y) -> (wrd_y - soy)*k;

        Platform.runLater(() -> {
            gc.setImageSmoothing(false);
            gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());

            for (int curr_wx = (int)Math.floor(scr_to_wrd_x.apply(0)); curr_wx < (int)Math.ceil(scr_to_wrd_x.apply(screen_width)); curr_wx++) {
                for (int curr_wy = (int)Math.floor(scr_to_wrd_y.apply(0)); curr_wy < (int)Math.ceil(scr_to_wrd_y.apply(screen_height)); curr_wy++)  {
                    double to_x = wrd_to_scr_x.apply(curr_wx);
                    double to_y = wrd_to_scr_y.apply(curr_wy);
                    boolean is_inside = curr_wx < region_x2 && curr_wx >= region_x1 && curr_wy < region_y2 && curr_wy >= region_y1;
                    gc.drawImage(_IMAGES[is_inside ? 16 : 10], to_x, to_y, k,k);
                    //gc.fillOval(to_x-2,to_y-2, 4,4);
                }
            }

            // DRAWING REGION
            double p1_sx = wrd_to_scr_x.apply(region_x1);
            double p1_sy = wrd_to_scr_y.apply(region_y1);

            double p2_sx = wrd_to_scr_x.apply(region_x2);
            double p2_sy = wrd_to_scr_y.apply(region_y1);

            double p3_sx = wrd_to_scr_x.apply(region_x2);
            double p3_sy = wrd_to_scr_y.apply(region_y2);

            double p4_sx = wrd_to_scr_x.apply(region_x1);
            double p4_sy = wrd_to_scr_y.apply(region_y2);

            gc.setStroke(Paint.valueOf("#ff0000"));
            gc.strokeLine(p1_sx,p1_sy, p2_sx,p2_sy);
            gc.strokeLine(p2_sx,p2_sy, p3_sx,p3_sy);
            gc.strokeLine(p3_sx,p3_sy, p4_sx,p4_sy);
            gc.strokeLine(p4_sx,p4_sy, p1_sx,p1_sy);

            gc.setFill(Paint.valueOf("#ff00ff"));
            gc.fillOval(wrd_to_scr_x.apply(region_x1)-5, wrd_to_scr_y.apply(region_y1)-5, 10,10);

            // DRAWING BLACK BARS
            gc.setFill(Paint.valueOf("#000"));
            gc.fillRect(p1_sx-screen_width,0,screen_width-tolerance*k,screen_height); //left bar
            gc.fillRect(p2_sx+tolerance*k,0,screen_width,screen_height); //right bar
            gc.fillRect(0,p1_sy-screen_height,screen_width,screen_height-tolerance*k); //top bar
            gc.fillRect(0,p4_sy+tolerance*k,screen_width,screen_height); //bottom bar

            isRunningLater.set(false);
        });
        /////////////////////
    }

    /**
     * Aggiunge un task alla coda di rendering.
     *
     * @param renderTask il task da aggiungere, non può essere null
     * @throws NullPointerException se renderTask è null
     */
    public static void addRenderTask(RenderTask renderTask) {
        renderQueue.offer(Objects.requireNonNull(renderTask));
    }
}
