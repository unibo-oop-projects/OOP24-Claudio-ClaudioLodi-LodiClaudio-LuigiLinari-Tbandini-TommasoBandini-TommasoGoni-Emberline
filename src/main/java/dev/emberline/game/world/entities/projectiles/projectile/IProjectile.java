package dev.emberline.game.world.entities.projectiles.projectile;

import dev.emberline.core.components.RenderComponent;
import dev.emberline.core.components.UpdateComponent;

import java.io.Serializable;

/**
 * Represents a projectile entity in the game world that will hit a target.
 * <p>
 * Classes implementing this interface should define the specific logic for
 * the projectile's update and rendering behavior.
 */
public interface IProjectile extends UpdateComponent, RenderComponent, Serializable {

    /**
     * Returns whether the projectile has hit the target.
     * @return whether the projectile has hit the target
     */
    boolean hasHit();
}
