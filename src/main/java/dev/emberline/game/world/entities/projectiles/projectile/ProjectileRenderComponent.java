package dev.emberline.game.world.entities.projectiles.projectile;

import dev.emberline.core.GameLoop;
import dev.emberline.core.components.Renderable;
import dev.emberline.core.components.Updatable;
import dev.emberline.core.render.CoordinateSystem;
import dev.emberline.core.render.RenderPriority;
import dev.emberline.core.render.RenderTask;
import dev.emberline.core.render.Renderer;
import dev.emberline.game.model.EnchantmentInfo;
import dev.emberline.game.model.ProjectileInfo;
import dev.emberline.game.world.entities.projectiles.projectile.Projectile.PositionAndRotation;
import dev.emberline.utility.Vector2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

public class ProjectileRenderComponent implements Renderable {

    private static final double WIDTH = 1;
    private static final double HEIGHT = 1;

    private final Projectile owner;
    private final ProjectileAnimation projectileAnimation;

    public ProjectileRenderComponent(ProjectileInfo projInfo, EnchantmentInfo enchInfo, Projectile owner) {
        this.owner = owner;
        this.projectileAnimation = new ProjectileAnimation(owner);
    }

    @Override
    public void render() {
        Renderer renderer = GameLoop.getInstance().getRenderer();
        GraphicsContext gc = renderer.getGraphicsContext();
        CoordinateSystem cs = renderer.getWorldCoordinateSystem();

        PositionAndRotation posAndRot = owner.getPositionAndRotation();
        Vector2D position = posAndRot.position();
        double rotation = posAndRot.rotation();

        double _width = WIDTH * cs.getScale();
        double _height = HEIGHT * cs.getScale();

        double positionScreenX = cs.toScreenX(position.getX());
        double positionScreenY = cs.toScreenY(position.getY());

        Image currentFrame = projectileAnimation.getImage();

        renderer.addRenderTask(new RenderTask(RenderPriority.GUI, () -> {
            gc.save();

            gc.translate(positionScreenX, positionScreenY);
            gc.rotate(rotation);

            // make so that the tip of the projectile hits
            gc.drawImage(currentFrame, -_width / 2, -_height / 2, _width, _height);

            gc.restore();
        }));
    }

    Updatable getAnimationUpdatable() {
        return projectileAnimation;
    }
}
