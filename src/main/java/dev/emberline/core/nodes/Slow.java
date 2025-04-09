package dev.emberline.core.nodes;

import dev.emberline.core.GameLoop;
import dev.emberline.core.game.components.Updatable;
import dev.emberline.core.game.components.Renderable;
import dev.emberline.core.render.RenderPriority;
import dev.emberline.core.render.RenderTask;
import dev.emberline.core.render.Renderer;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Paint;

public class Slow implements RunningState {
    @Override
    public void render() {
        Renderer renderer = GameLoop.getInstance().getRenderer();
        GraphicsContext gc = renderer.getGraphicsContext();

        renderer.addRenderTask(new RenderTask(RenderPriority.GUI, () -> {
            gc.setFill(Paint.valueOf("#ff0"));
            gc.fillRect(10,20,100,100);
        }));
    }
}
