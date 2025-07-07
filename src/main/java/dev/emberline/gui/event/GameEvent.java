package dev.emberline.gui.event;

import java.io.Serial;
import java.util.EventObject;

/**
 * Represents a generic event specific to the game's lifecycle or state changes.
 *
 * Subclasses of this event can define specific game actions or triggers allowing
 * for fine-grained event handling within the game logic.
 */
public abstract class GameEvent extends EventObject {

    @Serial
    private static final long serialVersionUID = 342144865823116108L;

    /**
     * Constructs a new {@code GameEvent}.
     *
     * @param source the object on which the event initially occurred.
     * @see GameEvent
     */
    protected GameEvent(final Object source) {
        super(source);
    }
}
