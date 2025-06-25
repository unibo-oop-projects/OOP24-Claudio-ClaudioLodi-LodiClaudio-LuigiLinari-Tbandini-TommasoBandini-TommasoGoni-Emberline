package dev.emberline.gui.event;

/**
 * Represents an event that triggers the transition to the main menu state.
 */
public class SetMainMenuEvent extends GuiEvent {

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