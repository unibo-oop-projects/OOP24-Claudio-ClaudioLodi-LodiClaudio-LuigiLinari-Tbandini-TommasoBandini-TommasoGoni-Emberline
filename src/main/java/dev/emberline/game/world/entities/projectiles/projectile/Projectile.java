package dev.emberline.game.world.entities.projectiles.projectile;

import dev.emberline.core.animations.Animation;
import dev.emberline.game.model.EnchantmentInfo;
import dev.emberline.game.model.ProjectileInfo;
import dev.emberline.game.world.World;
import dev.emberline.game.world.entities.enemies.enemy.IEnemy;
import dev.emberline.utility.Vector2D;

public class Projectile implements IProjectile {
    
    record PositionAndRotation(Vector2D position, Double rotation) {}

    private final ProjectileUpdateComponent updateComponent;
    private final ProjectileRenderComponent renderComponent;

    public Projectile(Vector2D start, IEnemy target, ProjectileInfo projInfo, EnchantmentInfo enchInfo, World world) {
        this.updateComponent = new ProjectileUpdateComponent(start, target, projInfo, enchInfo, world, this);
        this.renderComponent = new ProjectileRenderComponent(projInfo, enchInfo, this);
    }

    @Override
    public void update(long elapsed) {
        updateComponent.update(elapsed);
    }

    @Override
    public void render() {
        renderComponent.render();
    }

    @Override
    public boolean hasHit() {
        return updateComponent.hasHit();
    }

    PositionAndRotation getPositionAndRotation() {
        return updateComponent.getPositionAndRotation();
    }

    Animation getAnimation() {
        return renderComponent.getAnimation();
    }
}
