package dev.emberline.core.nodes;

import dev.emberline.core.GameLoop;
import dev.emberline.core.render.RenderPriority;
import dev.emberline.core.render.RenderTask;
import dev.emberline.core.render.Renderer;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Paint;

public class Fast implements RunningState {
    @Override
    public void render() {
        Renderer renderer = GameLoop.getInstance().getRenderer();
        GraphicsContext gc = renderer.getGraphicsContext();

        renderer.addRenderTask(new RenderTask(RenderPriority.GUI, () -> {
            gc.setFill(Paint.valueOf("#f00"));
            gc.fillRect(0,0,100,100);
        }));
    }
}
