package dev.emberline.core.config;

import java.io.Serial;

/**
 * An exception thrown to indicate that an error occurred during the
 * loading process when using the {@code ConfigLoader}.
 * <p>
 * The {@code ConfigLoaderLoadingException} is intended for use
 * within the context of the {@code ConfigLoader}.
 */
class ConfigLoaderLoadingException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 9027251156916662870L;

    /**
     * Constructs a new {@code ConfigLoaderLoadingException} with the specified detail message
     * and cause.
     *
     * @param message the detail message, providing additional information about the error.
     * @param cause the cause of the exception, typically something that happened during the loading process.
     */
    ConfigLoaderLoadingException(final String message, final Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a new {@code ConfigLoaderLoadingException} with the specified detail message.
     *
     * @param message the detail message, providing additional information about the error.
     */
    public ConfigLoaderLoadingException(String message) {
        super(message);
    }
}
