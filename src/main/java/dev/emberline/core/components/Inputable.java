package dev.emberline.core.components;

import dev.emberline.core.GameLoop;
import dev.emberline.core.input.InputDispatcher;
import javafx.scene.input.InputEvent;

/**
 * This interface defines a general rule for handling raw input events.
 * This should not be used for high-level input processing, but rather
 * for low-level input handling that is specific to a component or layer.
 * <p>
 * Classes implementing this interface might delegate the processing input
 * to other implementing classes of this interface, allowing for a modular
 * approach to input handling.
 *
 * @see dev.emberline.core.input.InputDispatcher
 */
@FunctionalInterface
public interface Inputable {
    /**
     * Processes a given input event. Implementations should not use this method
     * to handle business logic or high-level input processing.
     * <p>
     * This method must only be called in cascade from other {@code processInput} methods;
     * the only class that can call this method directly is the {@link InputDispatcher}.
     * This guarantees that every input event is valid, filtered, and that it is being
     * processed on the {@link GameLoop Game Thread}.
     * <p>
     * Implementations should `consume` the input event if they handle it successfully.
     * Implementations must not continue processing the input if it has already been consumed.
     *
     * @param inputEvent the input event to process.
     */
    void processInput(InputEvent inputEvent);
}
