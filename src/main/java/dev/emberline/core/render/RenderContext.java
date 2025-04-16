package dev.emberline.core.render;
import javafx.scene.canvas.Canvas;

public class RenderContext {
    private final Canvas canvas;
    private final CoordinateSystem cs;

    // Package private, should only be constructed by Renderer
    RenderContext(Canvas canvas, CoordinateSystem cs) {
        this.canvas = canvas;
        this.cs = cs;
        update();
    }

    // Package private, should only be called by Renderer
    void update() {
        cs.update(canvas.getWidth(), canvas.getHeight());
    }

    public CoordinateSystem getCS() {
        return cs;
    }
}