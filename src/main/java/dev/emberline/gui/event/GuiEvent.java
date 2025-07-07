package dev.emberline.gui.event;

import java.io.Serial;
import java.util.EventObject;

/**
 * Represents a generic event in the GUI framework.
 * <p>
 * This class serves as a base for creating specific types of events related to
 * graphical user interface operations and interactions. Subclasses of this event
 * can define more specific behaviors and properties for various GUI events.
 */
public abstract class GuiEvent extends EventObject {

    @Serial
    private static final long serialVersionUID = 3297500278070132241L;

    /**
     * Constructs a new {@code GuiEvent}.
     *
     * @param source the object on which the event initially occurred
     * @see GuiEvent
     */
    protected GuiEvent(final Object source) {
        super(source);
    }
}
