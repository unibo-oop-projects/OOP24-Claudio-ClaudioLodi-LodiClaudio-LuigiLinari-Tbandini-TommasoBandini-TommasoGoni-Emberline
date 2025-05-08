package dev.emberline.game.model.effects;

/**
 * EnchantmentEffects are effects that can be applied to enemies modifying their behavior.
 * EnchantmentEffects get determined by the enchantment type of an enchantment.
 */
public interface EnchantmentEffect {
    // void updateEffect(Enemy enemy, long elapsed);

    /**
     * Checks if the effect is expired.
     *
     * @return true if the effect is expired, false otherwise.
     */
    boolean isExpired();
}
