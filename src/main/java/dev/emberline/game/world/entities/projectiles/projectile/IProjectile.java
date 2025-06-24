package dev.emberline.game.world.entities.projectiles.projectile;

import dev.emberline.core.components.Renderable;
import dev.emberline.core.components.Updatable;

/**
 * Represents a projectile entity in the game world that will hit a target.
 *
 * Classes implementing this interface should define the specific logic for
 * the projectile's update and rendering behavior.
 */
public interface IProjectile extends Updatable, Renderable {

    /**
     * @return whether the projectile has hit the target
     */
    boolean hasHit();
}
