package dev.emberline.game.world.entities.projectiles.projectile;

import dev.emberline.core.GameLoop;
import dev.emberline.core.components.Renderable;
import dev.emberline.core.render.CoordinateSystem;
import dev.emberline.core.render.RenderPriority;
import dev.emberline.core.render.RenderTask;
import dev.emberline.core.render.Renderer;
import dev.emberline.game.model.EnchantmentInfo;
import dev.emberline.game.model.ProjectileInfo;
import dev.emberline.game.world.entities.projectiles.projectile.Projectile.PositionAndRotation;
import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Paint;

public class ProjectileRenderComponent implements Renderable {

    private final Projectile owner;
    // sprite

    public ProjectileRenderComponent(ProjectileInfo projInfo, EnchantmentInfo enchInfo, Projectile owner) {
        this.owner = owner;
        // set sprite based on projInfo and enchInfo
    }

    @Override
    public void render() {
        Renderer renderer = GameLoop.getInstance().getRenderer();
        GraphicsContext gc = renderer.getGraphicsContext();
        CoordinateSystem cs = renderer.getWorldCoordinateSystem();

        PositionAndRotation posAndRot = owner.getPositionAndRotation();
        Point2D position = posAndRot.position();
        double rotation = posAndRot.rotation();

        double positionScreenX = cs.toScreenX(position.getX());
        double positionScreenY = cs.toScreenY(position.getY());

        double width = 15;
        double height = 7;

        renderer.addRenderTask(new RenderTask(RenderPriority.GUI, () -> {
            gc.save();

            // hit the middle of the enemy TODO
            gc.translate(positionScreenX + 19, positionScreenY);
            gc.rotate(rotation);
            
            gc.setFill(Paint.valueOf("#FFF"));
            gc.fillRect(-width, -height, width, height); // make so that the tip of the projectile hits

            gc.restore();
        }));
    }
}
