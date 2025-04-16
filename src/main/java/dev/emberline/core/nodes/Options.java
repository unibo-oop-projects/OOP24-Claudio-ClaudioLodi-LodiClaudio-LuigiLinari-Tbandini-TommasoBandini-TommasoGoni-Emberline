package dev.emberline.core.nodes;

import dev.emberline.core.render.*;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Paint;
import dev.emberline.core.GameLoop;

public class Options implements NavigationState {

    public Options () {
    }

    public void render() {
        Renderer renderer = GameLoop.getInstance().getRenderer();
        GraphicsContext gc = renderer.getGraphicsContext();
        CoordinateSystem worldCS = renderer.getWorldCoordinateSystem();

        double squareSize = 3.0;

        double centerX = worldCS.getRegionCenterX();
        double centerY = worldCS.getRegionCenterY();

        double screenX = worldCS.toScreenX(centerX - squareSize / 2);
        double screenY = worldCS.toScreenY(centerY - squareSize / 2);
        double screenSize = squareSize * worldCS.getScale();

        renderer.addRenderTask(new RenderTask(RenderPriority.GUI, () -> {
            gc.setFill(Paint.valueOf("#f00"));
            gc.fillRect(screenX, screenY, screenSize, screenSize);
        }));
    }
}

