package dev.emberline.game.world.entities.projectiles.events;

import dev.emberline.game.model.EnchantmentInfo;
import dev.emberline.game.model.ProjectileInfo;
import dev.emberline.game.model.effects.EnchantmentEffect;
import dev.emberline.utility.Vector2D;

import java.util.Optional;

/**
 * Represents an event that occurs when a projectile hits a target in the game.
 * The event contains details about the location of the hit, the damage caused,
 * the area affected by the damage, and any enchantment effects applied.
 */
public class ProjectileHitEvent {

    private final Vector2D landingLocation;
    private final double damage;
    private final Optional<Double> damageArea;
    private final Optional<EnchantmentEffect> effect;

    /**
     * Constructs a new ProjectileHitEvent.
     *
     * @param landingLocation the location where the projectile has landed
     * @param projInfo the information containing the projectile's size-related properties
     * @param enchInfo the information containing the projectile's enchantment-related properties
     */
    public ProjectileHitEvent(final Vector2D landingLocation, final ProjectileInfo projInfo, final EnchantmentInfo enchInfo) {
        this.landingLocation = landingLocation;
        this.damage = projInfo.getDamage();
        this.damageArea = projInfo.getDamageArea();
        this.effect = enchInfo.getEffect();
    }

    /**
     * Returns the landing location as a {@code Vector2D} object.
     * @return the landing location as a {@code Vector2D} object
     */
    public Vector2D getLandingLocation() {
        return landingLocation;
    }

    /**
     * Returns the damage value as a {@code double}.
     * @return the damage value as a {@code double}
     */
    public double getDamage() {
        return damage;
    }

    /**
     * Retrieves the optional damage area affected by the projectile.
     * The damage area, if present, specifies the radius within which entities
     *
     * @return the optional damage area affected by the projectile.
     **/
    public Optional<Double> getDamageArea() {
        return damageArea;
    }

    /**
     * Retrieves the optional enchantment effect associated with the projectile.
     * The enchantment effect, if present, defines the behavior or modifications
     * applied to enemies affected by the projectile upon impact.
     *
     * @return an {@code Optional} containing the {@code EnchantmentEffect} if present,
     *         or an empty {@code Optional} if no effect is applied
     */
    public Optional<EnchantmentEffect> getEffect() {
        return effect;
    }
}
