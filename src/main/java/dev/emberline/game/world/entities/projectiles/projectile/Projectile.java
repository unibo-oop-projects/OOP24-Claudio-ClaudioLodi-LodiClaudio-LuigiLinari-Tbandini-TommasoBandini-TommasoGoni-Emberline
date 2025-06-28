package dev.emberline.game.world.entities.projectiles.projectile;

import dev.emberline.core.components.Updatable;
import dev.emberline.game.model.EnchantmentInfo;
import dev.emberline.game.model.ProjectileInfo;
import dev.emberline.game.world.World;
import dev.emberline.game.world.entities.enemies.enemy.IEnemy;
import dev.emberline.game.world.entities.projectiles.FlightPathNotFound;
import dev.emberline.utility.Vector2D;

import java.io.Serial;
import java.io.Serializable;
import java.util.function.Function;

/**
 * Represents a projectile in the game world, that includes
 * having a {@link ProjectileInfo}, an {@link EnchantmentInfo},
 * a {@code start position} and a {@code target}.
 */
public class Projectile implements IProjectile {
    @Serial
    private static final long serialVersionUID = 2993223252712274839L;

    private final ProjectileUpdateComponent updateComponent;
    private final ProjectileRenderComponent renderComponent;

    record PositionAndRotation(Vector2D position, Double rotation) implements Serializable {
    }

    interface SerializableFunction<A extends Serializable, B extends Serializable> extends Serializable {
        B apply(A input);
    }

    /**
     * Creates a new Projectile instance with the specified parameters.
     *
     * @param start the starting position of the projectile
     * @param target the target enemy of the projectile
     * @param projInfo the information containing the projectile's size-related properties
     * @param enchInfo the information containing the projectile's enchantment-related properties
     * @param world the game world instance in which the projectile exists
     * @throws FlightPathNotFound if a valid flight path to the target cannot be determined
     */
    public Projectile(final Vector2D start, final IEnemy target,
                      final ProjectileInfo projInfo, final EnchantmentInfo enchInfo,
                      final World world) throws FlightPathNotFound {
        this.updateComponent = new ProjectileUpdateComponent(start, target, projInfo, enchInfo, world, this);
        this.renderComponent = new ProjectileRenderComponent(this);
    }

    /**
     * Updates the projectile.
     * @see ProjectileUpdateComponent#update(long)
     */
    @Override
    public void update(final long elapsed) {
        updateComponent.update(elapsed);
    }

    /**
     * Renders the projectile.
     * @see ProjectileRenderComponent#render()
     */
    @Override
    public void render() {
        renderComponent.render();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean hasHit() {
        return updateComponent.hasHit();
    }

    /**
     * Retrieves the current position and rotation of the projectile.
     * Used to communicate from the updateComponent to the renderComponent.
     *
     * @return an instance of {@link PositionAndRotation} containing the current position
     *         and rotation of the projectile.
     */
    PositionAndRotation getPositionAndRotation() {
        return updateComponent.getPositionAndRotation();
    }

    /**
     * Retrieves the size type of the projectile.
     * Used to communicate from the updateComponent to the renderComponent.
     *
     * @return the size type of the projectile as a {@link ProjectileInfo.Type}.
     */
    ProjectileInfo.Type getSizeType() {
        return updateComponent.getSizeType();
    }

    /**
     * Retrieves the type of the enchantment associated with the projectile.
     * Used to communicate from the updateComponent to the renderComponent.
     *
     * @return the type of the enchantment as an {@link EnchantmentInfo.Type}.
     */
    EnchantmentInfo.Type getEnchantmentType() {
        return updateComponent.getEnchantmentType();
    }

    /**
     * Retrieves the Updatable instance responsible for managing the animation updates
     * of the projectile.
     * Used to communicate from the renderComponent to the updateComponent.
     *
     * @return an Updatable instance used to handle the animation update logic of the projectile
     */
    Updatable getAnimationUpdatable() {
        return renderComponent.getAnimationUpdatable();
    }
}
