package dev.emberline.game.world.entities.enemies.enemy;

import dev.emberline.core.GameLoop;
import dev.emberline.core.animations.Animation;
import dev.emberline.core.components.Renderable;
import dev.emberline.core.render.CoordinateSystem;
import dev.emberline.core.render.RenderPriority;
import dev.emberline.core.render.RenderTask;
import dev.emberline.core.render.Renderer;
import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Paint;

public class EnemyRenderComponent implements Renderable {
    
    private final Enemy owner;

    private EnemyAnimation enemyAnimation;

    public EnemyRenderComponent(Enemy owner) {
        this.owner = owner;
        this.enemyAnimation = new EnemyAnimation(owner);
    }

    @Override
    public void render() {
        Renderer renderer = GameLoop.getInstance().getRenderer();
        GraphicsContext gc = renderer.getGraphicsContext();
        CoordinateSystem cs = renderer.getWorldCoordinateSystem();

        double enemyWidth = owner.getWidth() * cs.getScale();
        double enemyHeight = owner.getHeight() * cs.getScale();

        Point2D position = owner.getPosition();
        double enemyScreenX = cs.toScreenX(position.getX()) - enemyWidth/2;
        double enemyScreenY = cs.toScreenY(position.getY()) - enemyHeight/2;

        // TODO
        double healthbarFullWidth = enemyWidth - 30;
        double healthbarHeight = 10;
        double healthbarScreenX = enemyScreenX + 15;
        double healthbarScreenY = enemyScreenY - 15;
        //

        Image currAnimationState = enemyAnimation.getAnimationState();

        renderer.addRenderTask(new RenderTask(RenderPriority.ENEMIES, () -> {
            gc.drawImage(currAnimationState, enemyScreenX, enemyScreenY, enemyWidth, enemyHeight);

            gc.setFill(Paint.valueOf("#696969"));
            gc.fillRect(healthbarScreenX, healthbarScreenY, healthbarFullWidth, healthbarHeight);
            gc.setFill(Paint.valueOf("#00CC00"));
            gc.fillRect(healthbarScreenX, healthbarScreenY, (owner.getHealthPercentage()) * healthbarFullWidth, healthbarHeight);
        }));
    }
    
    Animation getAnimation() {
        return enemyAnimation;
    }
}
