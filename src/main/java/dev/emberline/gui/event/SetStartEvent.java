package dev.emberline.gui.event;

import java.io.Serial;

/**
 * Represents an event that signals the transition to the effective game.
 */
public class SetStartEvent extends GuiEvent {

    @Serial
    private static final long serialVersionUID = 7338925458970275592L;

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
