package dev.emberline.gui.event;

import java.io.Serial;

/**
 * Represents an event that triggers the transition to the main menu state.
 */
public class SetMainMenuEvent extends GuiEvent {

    @Serial
    private static final long serialVersionUID = 5755784783299744853L;

    /**
     * Creates a new {@code SetMainMenuEvent}.
     *
     * @param source the object on which the event initially occurred
     * @see SetMainMenuEvent
     */
    public SetMainMenuEvent(final Object source) {
        super(source);
    }
}
