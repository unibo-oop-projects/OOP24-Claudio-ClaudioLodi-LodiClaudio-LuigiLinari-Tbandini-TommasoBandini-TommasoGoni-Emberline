package dev.emberline.core.nodes;

import dev.emberline.core.render.RenderContext;
import dev.emberline.core.render.RenderPriority;
import dev.emberline.core.render.RenderTask;
import dev.emberline.core.render.Renderer;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Paint;
import dev.emberline.core.GameLoop;

public class Options implements NavigationState {

    public Options () {
    }

    public void render() {
        Renderer renderer = GameLoop.getInstance().getRenderer();
        GraphicsContext gc = renderer.getGraphicsContext();
        RenderContext worldContext = renderer.getWorldContext();

        double squareSize = 3.0;

        double centerX = worldContext.getCS().getRegionCenterX();
        double centerY = worldContext.getCS().getRegionCenterY();

        double screenX = worldContext.getCS().toScreenX(centerX - squareSize / 2);
        double screenY = worldContext.getCS().toScreenY(centerY - squareSize / 2);
        double screenSize = squareSize * worldContext.getCS().getScale();

        renderer.addRenderTask(new RenderTask(RenderPriority.GUI, () -> {
            gc.setFill(Paint.valueOf("#f00"));
            gc.fillRect(screenX, screenY, screenSize, screenSize);
        }));
    }
}

