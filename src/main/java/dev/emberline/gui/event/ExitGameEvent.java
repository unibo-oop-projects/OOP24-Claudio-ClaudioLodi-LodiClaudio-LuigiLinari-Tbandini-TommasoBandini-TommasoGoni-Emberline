package dev.emberline.gui.event;

/**
 * Represents an event that signals the intention to exit the game.
 * <p>
 * This event is typically used to notify the application to gracefully terminate
 * or close the game, ensuring proper resource cleanup and shutdown processes.
 */
public class ExitGameEvent extends GuiEvent {

    /**
     * Creates a new {@code ExitGameEvent}.
     *
     * @param source the object on which the event initially occurred.
     * @see ExitGameEvent
     */
    public ExitGameEvent(final Object source) {
        super(source);
    }
}
