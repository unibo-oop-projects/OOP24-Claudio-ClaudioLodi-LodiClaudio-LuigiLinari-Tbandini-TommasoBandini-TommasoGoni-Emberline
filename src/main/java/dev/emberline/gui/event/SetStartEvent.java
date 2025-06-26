package dev.emberline.gui.event;

/**
 * Represents an event that signals the transition to the effective game.
 */
public class SetStartEvent extends GuiEvent {

    /**
     * Constructs a new {@code SetStartEvent}.
     *
     * @param source the object on which the event initially occurred
     * @see SetStartEvent
     */
    public SetStartEvent(final Object source) {
        super(source);
    }
}
