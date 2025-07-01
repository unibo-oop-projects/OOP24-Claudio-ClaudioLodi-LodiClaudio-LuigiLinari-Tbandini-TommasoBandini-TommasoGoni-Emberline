package dev.emberline.game.serialization;

import java.io.IOException;
import java.io.Serial;

/**
 * This exception is thrown to indicate that there was some issue saving an instance of the game.
 * The {@code SavesSavingException} is intended for use
 * within the context of the serializer package.
 */
public class GameSavesSavingException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = -4023633403542710524L;

    /**
     * Constructs a new {@code GameSavesSavingException} with the specified
     * exception.
     *
     * @param ex the IO exception that was originally thrown.
     */
    public GameSavesSavingException(final IOException ex) {
        super(ex);
    }
}
