package dev.emberline.game.world.entities.projectiles.projectile;

import dev.emberline.core.components.Renderable;
import dev.emberline.core.components.Updatable;

public interface IProjectile extends Updatable, Renderable {

    boolean hasHit();
}
