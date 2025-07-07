package dev.emberline.game.serialization;

import java.io.Serial;

/**
 * This exception is thrown to indicate that there was some issue loading a save of the game.
 * The {@code SavesLoadingException} is intended for use
 * within the context of the serializer package.
 */
class GameSavesLoadingException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 1759292897703915209L;

    /**
     * Constructs a new {@code SavesLoadingException} with the specified
     * exception.
     *
     * @param ex the Exception that was originally thrown.
     */
    GameSavesLoadingException(final Exception ex) {
        super(ex);
    }
}
