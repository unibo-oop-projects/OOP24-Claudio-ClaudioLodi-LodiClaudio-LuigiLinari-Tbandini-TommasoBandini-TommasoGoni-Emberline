package dev.emberline.game.model.effects;

// TODO change this to a class when implementing the effect behaviour, duration must not be immutable
/**
 * Represents a burn effect that deals damage over time.
 * The effect is associated with fire enchantments in the game.
 *
 * @param duration The total duration in seconds over which the burn effect persists.
 * @param damagePerSecond The amount of damage in hp dealt to the affected entity each second.
 *
 * @see dev.emberline.game.model.EnchantmentInfo.Type#FIRE
 */
public record BurnEffect(double damagePerSecond, double duration) implements EnchantmentEffect {
    @Override
    public boolean isExpired() {
        return false;
    }
}
