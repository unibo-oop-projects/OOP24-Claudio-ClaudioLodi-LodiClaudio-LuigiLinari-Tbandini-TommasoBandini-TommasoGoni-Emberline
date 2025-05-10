package dev.emberline.game.model.effects;

// TODO change this to a class when implementing the effect behaviour, duration must not be immutable
/**
 * Represents a slow effect that slows down an enemy by a certain factor over a specified duration.
 * The effect is associated with ice enchantments in the game.
 *
 * @param duration The total duration in seconds over which the slow effect persists.
 * @param slowingFactor The multiplicative factor by which the speed of the affected entity is reduced.
 *
 * @see dev.emberline.game.model.EnchantmentInfo.Type#ICE
 */
public record SlowEffect(double slowingFactor, double duration) implements EnchantmentEffect {

    @Override
    public boolean isExpired() {
        return false;
    }
}
