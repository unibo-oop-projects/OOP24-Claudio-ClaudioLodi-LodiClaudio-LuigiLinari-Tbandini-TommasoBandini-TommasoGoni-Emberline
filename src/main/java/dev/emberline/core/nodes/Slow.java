package dev.emberline.core.nodes;

import dev.emberline.core.game.components.Updatable;
import dev.emberline.core.game.components.Renderable;
import dev.emberline.core.render.RenderPriority;
import dev.emberline.core.render.RenderTask;
import dev.emberline.core.render.Renderer;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Paint;

public class Slow implements Updatable, Renderable {

    public Slow() {
        super();
    }

    @Override
    public void update(long elapsed) {
        System.out.println("Update Running Slow...");
        System.out.println("Ran for " + elapsed + "ns");
    }

    @Override
    public void render() {
        if (Renderer.getGraphicsContext().isEmpty())
            return;

        GraphicsContext gc = Renderer.getGraphicsContext().get();

        Renderer.addRenderTask(new RenderTask(RenderPriority.GUI, () -> {
            gc.setFill(Paint.valueOf("#ff0"));
            gc.fillRect(10,20,100,100);
        }));
    }
}
