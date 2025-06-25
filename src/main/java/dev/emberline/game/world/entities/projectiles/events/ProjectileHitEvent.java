package dev.emberline.game.world.entities.projectiles.events;

import dev.emberline.game.model.EnchantmentInfo;
import dev.emberline.game.model.ProjectileInfo;
import dev.emberline.game.model.effects.EnchantmentEffect;
import dev.emberline.utility.Vector2D;

import java.util.Optional;

public class ProjectileHitEvent {

    private final Vector2D landingLocation;
    private final double damage;
    private final Optional<Double> damageArea;
    private final Optional<EnchantmentEffect> effect;

    public ProjectileHitEvent(final Vector2D landingLocation, final ProjectileInfo projInfo, final EnchantmentInfo enchInfo) {
        this.landingLocation = landingLocation;
        this.damage = projInfo.getDamage();
        this.damageArea = projInfo.getDamageArea();
        this.effect = enchInfo.getEffect();
    }

    public Vector2D getLandingLocation() {
        return landingLocation;
    }

    public double getDamage() {
        return damage;
    }

    public Optional<Double> getDamageArea() {
        return damageArea;
    }

    public Optional<EnchantmentEffect> getEffect() {
        return effect;
    }
}
