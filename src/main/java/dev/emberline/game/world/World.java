package dev.emberline.game.world;

import dev.emberline.core.GameLoop;
import dev.emberline.core.components.Renderable;
import dev.emberline.core.input.MouseLocation;
import dev.emberline.core.render.CoordinateSystem;
import dev.emberline.core.render.RenderPriority;
import dev.emberline.core.render.RenderTask;
import dev.emberline.core.render.Renderer;
import dev.emberline.game.world.entities.enemy.EnemiesManager;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class World implements Renderable {
    
    // Enemies
    private final EnemiesManager enemiesManager;
    // Towers
    // Waves

    public World() {
        this.enemiesManager = new EnemiesManager(this);
    }

    public EnemiesManager getEnemiesManager() {
        return enemiesManager;
    }

    @Override
    public void render() {
        Renderer gameRenderer = GameLoop.getInstance().getRenderer();
        GraphicsContext gc = gameRenderer.getGraphicsContext();
        CoordinateSystem guics = gameRenderer.getGuiCoordinateSystem();

        double width = Renderer.GUICS_WIDTH * guics.getScale();
        double height = Renderer.GUICS_HEIGHT * guics.getScale();

        gameRenderer.addRenderTask(new RenderTask(RenderPriority.BACKGROUND, () -> {
            gc.setFill(MouseLocation.isMouseInside() ? Color.GREENYELLOW : Color.RED);
            gc.fillRect(guics.toScreenX(0),guics.toScreenY(0),width,height);
            gc.setFill(Color.BLUE);
            if (MouseLocation.isMouseInside())
                gc.fillRect(MouseLocation.getX()-10, MouseLocation.getY()-10, 20,20);
        }));
    }
}
