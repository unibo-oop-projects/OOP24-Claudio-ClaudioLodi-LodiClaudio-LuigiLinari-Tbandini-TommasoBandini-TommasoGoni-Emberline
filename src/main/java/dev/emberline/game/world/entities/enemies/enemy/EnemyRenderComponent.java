package dev.emberline.game.world.entities.enemies.enemy;

import dev.emberline.core.GameLoop;
import dev.emberline.core.components.Renderable;
import dev.emberline.core.render.CoordinateSystem;
import dev.emberline.core.render.RenderPriority;
import dev.emberline.core.render.RenderTask;
import dev.emberline.core.render.Renderer;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Paint;
import utility.Coordinate2D;

public class EnemyRenderComponent implements Renderable {
    
    private final Enemy owner;

    public EnemyRenderComponent(Enemy owner) {
        this.owner = owner;
    }

    @Override
    public void render() {
        Renderer renderer = GameLoop.getInstance().getRenderer();
        GraphicsContext gc = renderer.getGraphicsContext();
        CoordinateSystem cs = renderer.getWorldCoordinateSystem();

        double sizeX = 25;
        double sizeY = 25;

        Coordinate2D position = owner.getPosition();
        double screenX = cs.toScreenX(position.getX() + 0.5) - sizeX/2;
        double screenY = cs.toScreenY(position.getY() + 0.5) - sizeY/2;

        double healthbarScreenX = cs.toScreenX(position.getX() + 0.2);
        double healthbarScreenY = cs.toScreenY(position.getY() - 0.2);
        double healthbarFullWidth = 40;
        double healthbarHeight = 10;

        // FacingDirection facingDirection = owner.getFacingDirection();

        // Image currAnimationState = animation.getAnimationState();

        renderer.addRenderTask(new RenderTask(RenderPriority.ENEMIES, () -> {
            // gc.drawImage(currAnimationState, screenX, screenY, sizeX, sizeY);
            gc.setFill(Paint.valueOf("#FF0000"));
            gc.fillRect(screenX, screenY, sizeX, sizeY);

            gc.setFill(Paint.valueOf("#696969"));
            gc.fillRect(healthbarScreenX, healthbarScreenY, healthbarFullWidth, healthbarHeight);
            gc.setFill(Paint.valueOf("#00CC00"));
            gc.fillRect(healthbarScreenX, healthbarScreenY, (owner.getHealthPercentage()) * healthbarFullWidth, healthbarHeight);
        }));
    }
}
