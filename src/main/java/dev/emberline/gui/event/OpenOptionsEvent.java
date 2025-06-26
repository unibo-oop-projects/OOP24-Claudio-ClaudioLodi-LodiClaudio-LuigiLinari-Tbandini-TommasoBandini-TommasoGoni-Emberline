package dev.emberline.gui.event;

/**
 * Represents an event that signals the opening of the options menu in the GUI.
 * <p>
 * This event is typically dispatched to notify the application about transitioning
 * to the options menu state.
 */
public class OpenOptionsEvent extends GuiEvent {

    /**
     * Constructs a new {@code OpenOptionsEvent}.
     *
     * @param source the object on which the event initially occurred
     * @see OpenOptionsEvent
     */
    public OpenOptionsEvent(final Object source) {
        super(source);
    }
}
