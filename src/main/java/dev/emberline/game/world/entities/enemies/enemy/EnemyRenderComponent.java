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

public class EnemyRenderComponent implements Renderable {
    private static class HealthbarLayout {
        private static final double FULL_WIDTH = 1;
        private static final double HEIGHT = 0.1;
        private static final double X_OFFSET = 0.1;
        private static final double Y_OFFSET = 0.1;
    }

    private final AbstractEnemy enemy;
    private final EnemyAnimation enemyAnimation;

    public EnemyRenderComponent(AbstractEnemy enemy) {
        this.enemy = enemy;
        this.enemyAnimation = new EnemyAnimation(enemy);
    }

    @Override
    public void render() {
        Renderer renderer = GameLoop.getInstance().getRenderer();
        GraphicsContext gc = renderer.getGraphicsContext();
        CoordinateSystem cs = renderer.getWorldCoordinateSystem();
        // enemy body
        Vector2D position = enemy.getPosition();
        double enemyScreenWidth = enemy.getWidth() * cs.getScale();
        double enemyScreenHeight = enemy.getHeight() * cs.getScale();
        double enemyScreenX = cs.toScreenX(position.getX()) - enemyScreenWidth/2;
        double enemyScreenY = cs.toScreenY(position.getY()) - enemyScreenHeight/2;
        // healthbar
        double hbScreenWidth = HealthbarLayout.FULL_WIDTH * cs.getScale();
        double hbScreenHeight = HealthbarLayout.HEIGHT * cs.getScale();
        double hbScreenX = enemyScreenX + HealthbarLayout.X_OFFSET;
        double hbScreenY = enemyScreenY + HealthbarLayout.Y_OFFSET;

        Image currentFrame = enemyAnimation.getImage();

        renderer.addRenderTask(new RenderTask(RenderPriority.ENEMIES, () -> {
            gc.drawImage(currentFrame, enemyScreenX, enemyScreenY, enemyScreenWidth, enemyScreenHeight);

            gc.setFill(Paint.valueOf("#696969"));
            gc.fillRect(hbScreenX, hbScreenY, hbScreenWidth, hbScreenHeight);
            gc.setFill(Paint.valueOf("#00CC00"));
            gc.fillRect(hbScreenX, hbScreenY, (enemy.getHealthPercentage()) * hbScreenWidth, hbScreenHeight);
        }));
    }

    boolean isDyingAnimationFinished() {
        return enemyAnimation.isDyingAnimationFinished();
    }

    Updatable getAnimationUpdatable() {
        return enemyAnimation;
    }
}
