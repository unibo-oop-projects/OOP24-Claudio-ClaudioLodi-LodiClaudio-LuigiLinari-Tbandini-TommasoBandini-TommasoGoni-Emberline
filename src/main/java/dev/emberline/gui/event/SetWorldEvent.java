package dev.emberline.gui.event;

/**
 * Represents an event that signals the transition to the effective game.
 */
public class SetWorldEvent extends GuiEvent {

    /**
     * Constructs a new {@code SetWorldEvent}.
     *
     * @param source the object on which the event initially occurred
     * @see SetWorldEvent
     */
    public SetWorldEvent(final Object source) {
        super(source);
    }
}
