package dev.emberline.game.world.entities.enemies.enemy;

import dev.emberline.core.GameLoop;
import dev.emberline.core.components.Renderable;
import dev.emberline.core.components.Updatable;
import dev.emberline.core.render.CoordinateSystem;
import dev.emberline.core.render.RenderPriority;
import dev.emberline.core.render.RenderTask;
import dev.emberline.core.render.Renderer;
import dev.emberline.utility.Vector2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Paint;

import java.io.Serial;
import java.io.Serializable;

public class EnemyRenderComponent implements Renderable, Serializable {
    @Serial
    private static final long serialVersionUID = -288209243117357956L;

    private final AbstractEnemy enemy;
    private final EnemyAnimation enemyAnimation;

    private static class HealthbarLayout {
        private static final double FULL_WIDTH = 1;
        private static final double HEIGHT = 0.1;
        private static final double X_OFFSET = 0.1;
        private static final double Y_OFFSET = 0.1;
    }

    EnemyRenderComponent(final AbstractEnemy enemy) {
        this.enemy = enemy;
        this.enemyAnimation = new EnemyAnimation(enemy);
    }

    /**
     * Renders the enemy along with its health bar on the screen.
     * <p>
     * The method calculates the enemy's position and dimensions on the screen
     * based on the world coordinate system and scales them as needed.
     * The rendering tasks are added to the renderer with the appropriate {@link RenderPriority}
     * and given a z-order based on the y coordinate of its feet.
     */
    @Override
    public void render() {
        final Renderer renderer = GameLoop.getInstance().getRenderer();
        final GraphicsContext gc = renderer.getGraphicsContext();
        final CoordinateSystem cs = renderer.getWorldCoordinateSystem();
        // enemy body
        final Vector2D position = enemy.getPosition();
        final double enemyScreenWidth = enemy.getWidth() * cs.getScale();
        final double enemyScreenHeight = enemy.getHeight() * cs.getScale();
        final double enemyScreenX = cs.toScreenX(position.getX()) - enemyScreenWidth / 2;
        final double enemyScreenY = cs.toScreenY(position.getY()) - enemyScreenHeight / 2;
        // healthbar
        final double hbScreenWidth = HealthbarLayout.FULL_WIDTH * cs.getScale();
        final double hbScreenHeight = HealthbarLayout.HEIGHT * cs.getScale();
        final double hbScreenX = enemyScreenX + HealthbarLayout.X_OFFSET;
        final double hbScreenY = enemyScreenY + HealthbarLayout.Y_OFFSET;

        final Image currentFrame = enemyAnimation.getImage();

        renderer.addRenderTask(new RenderTask(RenderPriority.ENEMIES, () -> {
            gc.drawImage(currentFrame, enemyScreenX, enemyScreenY, enemyScreenWidth, enemyScreenHeight);

            gc.setFill(Paint.valueOf("#696969"));
            gc.fillRect(hbScreenX, hbScreenY, hbScreenWidth, hbScreenHeight);
            gc.setFill(Paint.valueOf("#00CC00"));
            gc.fillRect(hbScreenX, hbScreenY, enemy.getHealthPercentage() * hbScreenWidth, hbScreenHeight);
        }).enableZOrder(position.getY() + enemy.getHeight() / 2));
    }

    boolean isDyingAnimationFinished() {
        return enemyAnimation.isDyingAnimationFinished();
    }

    Updatable getAnimationUpdatable() {
        return enemyAnimation;
    }
}
