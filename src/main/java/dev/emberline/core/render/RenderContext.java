package dev.emberline.core.render;
import javafx.scene.canvas.Canvas;

public class RenderContext {
    private final Canvas canvas;
    private final CoordinateSystem cs;

    public RenderContext(Canvas canvas, CoordinateSystem cs) {
        this.canvas = canvas;
        this.cs = cs;
        update();
    }

    public void update() {
        cs.update(canvas.getWidth(), canvas.getHeight());
    }

    public Canvas getCanvas() {
        return canvas;
    }

    public CoordinateSystem getCS() {
        return cs;
    }
}