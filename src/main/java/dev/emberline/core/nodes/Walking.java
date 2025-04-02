package dev.emberline.core.nodes;

import dev.emberline.core.game.components.Updatable;
import dev.emberline.core.game.components.Renderable;
import dev.emberline.core.render.RenderPriority;
import dev.emberline.core.render.RenderTask;
import dev.emberline.core.render.Renderer;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Paint;

public class Walking implements Updatable, Renderable {

    public Walking() {
        super();
    }

    @Override
    public void update(long elapsed) {
        System.out.println("Update Walking...");
        System.out.println("Walked for " + elapsed + "ns");
    }

    @Override
    public void render() {
        if (Renderer.getGraphicsContext().isEmpty())
            return;

        GraphicsContext gc = Renderer.getGraphicsContext().get();

        Renderer.addRenderTask(new RenderTask(RenderPriority.GUI, () -> {
            gc.setFill(Paint.valueOf("#00f"));
            gc.fillRect(20,20,100,100);
        }));
    }
}
