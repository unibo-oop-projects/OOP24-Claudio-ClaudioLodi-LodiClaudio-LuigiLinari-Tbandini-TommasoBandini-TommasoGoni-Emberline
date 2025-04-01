package dev.emberline.core.nodes;

import dev.emberline.core.game.components.Updatable;
import dev.emberline.core.game.components.Renderable;
import dev.emberline.core.render.RenderPriority;
import dev.emberline.core.render.RenderTask;
import dev.emberline.core.render.Renderer;

public class Fast implements Updatable, Renderable {
    
    public Fast() {
        super();
    }

    @Override
    public void update(long elapsed) {
        System.out.println("Update Running Fast...");
        System.out.println("Ran for " + elapsed + "ns");
    }

    @Override
    public void render() {
        //System.out.println("Rendered Running Fast " + Thread.currentThread().getName());

        Renderer.addRenderTask(new RenderTask(RenderPriority.GUI, () -> {
            // Chiamate al GC
        }));
    }
}
