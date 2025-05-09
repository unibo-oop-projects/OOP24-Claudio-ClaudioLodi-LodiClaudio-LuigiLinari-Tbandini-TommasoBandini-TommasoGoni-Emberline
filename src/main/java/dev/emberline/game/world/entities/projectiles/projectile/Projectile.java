package dev.emberline.game.world.entities.projectiles.projectile;

import dev.emberline.game.model.EnchantmentInfo;
import dev.emberline.game.model.ProjectileInfo;
import dev.emberline.game.world.World;
import dev.emberline.game.world.entities.enemies.enemy.IEnemy;
import javafx.geometry.Point2D;

public class Projectile implements IProjectile {
    
    record PositionAndRotation(Point2D position, Double rotation) {}

    private final ProjectileUpdateComponent projectileUpdateComponent;
    private final ProjectileRenderComponent projectileRenderComponent;

    public Projectile(Point2D start, IEnemy target, ProjectileInfo projInfo, EnchantmentInfo enchInfo, World world) throws IllegalStateException {
        this.projectileUpdateComponent = new ProjectileUpdateComponent(start, target, projInfo, enchInfo, world, this);
        this.projectileRenderComponent = new ProjectileRenderComponent(projInfo, enchInfo, this);
    }

    @Override
    public void update(long elapsed) {
        projectileUpdateComponent.update(elapsed);
    }

    @Override
    public void render() {
        projectileRenderComponent.render();
    }

    @Override
    public boolean hasHit() {
        return projectileUpdateComponent.hasHit();
    }

    PositionAndRotation getPositionAndRotation() {
        return projectileUpdateComponent.getPositionAndRotation();
    }
}
