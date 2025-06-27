package dev.emberline.gui.event;

import dev.emberline.game.world.statistics.Statistics;

/**
 * Represents an event that signals the game has ended.
 * <p>
 * This event is typically used to notify the application of a game-over state,
 * which may be triggered by scenarios such as the player losing all lives.
 */
public class GameOverEvent extends GameEvent {

    private final Statistics statistics;

    /**
     * Constructs a new {@code GameOverEvent}.
     *
     * @param source the object on which the event initially occurred.
     * @param statistics the statistics to be shown in the game over screen.
     * @see GameOverEvent
     */
    public GameOverEvent(final Object source, final Statistics statistics) {
        super(source);
        this.statistics = statistics;
    }

    /**
     * Returns the statistics associated with this game over event.
     *
     * @return the {@link Statistics} object containing game statistics.
     */
    public Statistics getStatistics() {
        return statistics;
    }

}
