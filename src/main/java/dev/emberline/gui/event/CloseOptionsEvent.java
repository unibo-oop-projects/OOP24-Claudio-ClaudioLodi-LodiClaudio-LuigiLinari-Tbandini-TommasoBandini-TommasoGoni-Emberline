package dev.emberline.gui.event;


import java.io.Serial;

/**
 * Represents an event that signals the closure of the options menu in the GUI.
 * <p>
 * This event is typically used to transition the application's state back
 * to the previous state before the options menu was opened.
 */
public class CloseOptionsEvent extends GuiEvent {

    @Serial
    private static final long serialVersionUID = -1813619923053971563L;

    /**
     * Creates a new {@code CloseOptionsEvent}.
     *
     * @param source the object on which the event initially occurred.
     * @see CloseOptionsEvent
     */
    public CloseOptionsEvent(final Object source) {
        super(source);
    }
}
