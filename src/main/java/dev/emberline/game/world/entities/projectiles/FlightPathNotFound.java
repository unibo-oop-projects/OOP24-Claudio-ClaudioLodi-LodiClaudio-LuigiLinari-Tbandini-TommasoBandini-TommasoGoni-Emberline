package dev.emberline.game.world.entities.projectiles;

/**
 * Exception thrown to indicate that a flight path could not be determined for a projectile.
 */
public class FlightPathNotFound extends Exception {

    /**
     * Constructs a new {@link FlightPathNotFound} exception with the specified detail message.
     *
     * @param message the detail message explaining the reason the flight path could not be determined
     */
    public FlightPathNotFound(final String message) {
        super(message);
    }
}
