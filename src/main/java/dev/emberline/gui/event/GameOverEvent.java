package dev.emberline.gui.event;

/**
 * Represents an event that signals the game has ended.
 * <p>
 * This event is typically used to notify the application of a game-over state,
 * which may be triggered by scenarios such as the player losing all lives.
 */
public class GameOverEvent extends GameEvent {

    /**
     * Constructs a new {@code GameOverEvent}.
     *
     * @param source the object on which the event initially occurred.
     * @see GameOverEvent
     */
    public GameOverEvent(final Object source) {
        super(source);
    }
}
