package dev.emberline.core.components;

import dev.emberline.core.GameLoop;
import dev.emberline.core.update.Updater;

/**
 * This interface defines a general rule for handling update logic
 * within the game loop. This should be used for components that
 * need to perform time-based updates or continuous calculations.
 * <p>
 * Classes implementing this interface might delegate the update logic
 * to other implementing classes of this interface, allowing for a modular
 * approach to game state updates.
 * <p>
 * This interface should be implemented by any component that requires
 * time-based update capabilities.
 *
 * @see dev.emberline.core.GameLoop
 */

@FunctionalInterface
public interface UpdateComponent {
    /**
     * Executes the update logic defined by the implementing class.
     * <p>
     * This method must only be called in cascade from other {@code update} methods;
     * the only class that can call this method directly is the {@link Updater}.
     * This guarantees that every update call is aligned with game loop ticks and that
     * it is being processed on the {@link GameLoop Game Thread}.
     * <p>
     * This method should be used to perform state changes to the game components.
     *
     * @param elapsed the time elapsed since the last update in nanoseconds
     */
    void update(long elapsed);
}
