package dev.emberline.core.nodes;

import dev.emberline.core.render.RenderContext;
import dev.emberline.core.render.RenderPriority;
import dev.emberline.core.render.RenderTask;
import dev.emberline.core.render.Renderer;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Paint;
import dev.emberline.core.GameLoop;

public class Menu implements NavigationState {

    public Menu() {
    }

    public void render() {
        Renderer renderer = GameLoop.getInstance().getRenderer();
        GraphicsContext gc = renderer.getGraphicsContext();
        
        RenderContext guiContext = renderer.getGuiContext();

        double squareSize = 3.0;

        double gCenterX = guiContext.getCS().getRegionCenterX();
        double gCenterY = guiContext.getCS().getRegionCenterY();

        double gScreenX = guiContext.getCS().toScreenX(gCenterX - squareSize / 2);
        double gScreenY = guiContext.getCS().toScreenY(gCenterY - squareSize / 2);
        double gScreenSize = squareSize * guiContext.getCS().getScale();

        renderer.addRenderTask(new RenderTask(RenderPriority.GUI, () -> {
            gc.setFill(Paint.valueOf("#0f0"));
            gc.fillRect(gScreenX, gScreenY, gScreenSize, gScreenSize);
        }));

        RenderContext worldContext = renderer.getWorldContext();

        double wCenterX = worldContext.getCS().getRegionCenterX();
        double wCenterY = worldContext.getCS().getRegionCenterY();

        double wScreenX = worldContext.getCS().toScreenX(wCenterX - squareSize / 2);
        double wScreenY = worldContext.getCS().toScreenY(wCenterY - squareSize / 2);
        double wScreenSize = squareSize * worldContext.getCS().getScale();

        renderer.addRenderTask(new RenderTask(RenderPriority.BACKGROUND, () -> {
            gc.setFill(Paint.valueOf("#0ff"));
            gc.fillRect(wScreenX, wScreenY, wScreenSize, wScreenSize);
        }));
    }
}

