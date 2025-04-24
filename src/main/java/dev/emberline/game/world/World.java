package dev.emberline.game.world;

import dev.emberline.core.GameLoop;
import dev.emberline.core.components.Renderable;
import dev.emberline.core.components.NavigationState;
import dev.emberline.core.render.CoordinateSystem;
import dev.emberline.core.render.RenderPriority;
import dev.emberline.core.render.RenderTask;
import dev.emberline.core.render.Renderer;
import dev.emberline.game.world.entities.enemy.EnemiesManager;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class World implements NavigationState, Renderable {
    
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

        double width = guics.toScreenX(Renderer.GUICS_WIDTH);
        double height = guics.toScreenY(Renderer.GUICS_HEIGHT);

        gameRenderer.addRenderTask(new RenderTask(RenderPriority.BACKGROUND, () -> {
            gc.setFill(Color.GREENYELLOW);
            gc.fillRect(0,0,width,height);
        }));
    }
}
