package dev.emberline.game.world.entities.projectiles.projectile;

import dev.emberline.core.components.Updatable;
import dev.emberline.game.model.EnchantmentInfo;
import dev.emberline.game.model.ProjectileInfo;
import dev.emberline.game.world.World;
import dev.emberline.game.world.entities.enemies.enemy.IEnemy;
import dev.emberline.game.world.entities.projectiles.FlightPathNotFound;
import dev.emberline.utility.Vector2D;

public class Projectile implements IProjectile {

    private final ProjectileUpdateComponent updateComponent;
    private final ProjectileRenderComponent renderComponent;

    record PositionAndRotation(Vector2D position, Double rotation) {
    }

    public Projectile(final Vector2D start, final IEnemy target, final ProjectileInfo projInfo, final EnchantmentInfo enchInfo, final World world) throws FlightPathNotFound {
        this.updateComponent = new ProjectileUpdateComponent(start, target, projInfo, enchInfo, world, this);
        this.renderComponent = new ProjectileRenderComponent(projInfo, enchInfo, this);
    }

    @Override
    public void update(final long elapsed) {
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

    ProjectileInfo.Type getSizeType() {
        return updateComponent.getSizeType();
    }

    EnchantmentInfo.Type getEnchantmentType() {
        return updateComponent.getEnchantmentType();
    }

    Updatable getAnimationUpdatable() {
        return renderComponent.getAnimationUpdatable();
    }
}
