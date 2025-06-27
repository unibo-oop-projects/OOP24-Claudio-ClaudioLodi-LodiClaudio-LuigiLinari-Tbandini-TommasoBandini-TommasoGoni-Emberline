package dev.emberline.core.event;

import java.io.Serial;

/**
 * Exception thrown when the invocation of an event handler fails.
 * This exception is used when an error occurs during the reflective
 * invocation of a method annotated with {@code @EventHandler}.
 * <p>
 * The {@code EventHandlerInvocationException} is intended for use
 * within the context of the event dispatching system, such as in the
 * {@code EventDispatcher#dispatchEvent(EventObject)} method.
 *
 * @see EventHandler
 * @see EventDispatcher#dispatchEvent(java.util.EventObject)
 */
class EventHandlerInvocationException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = -1092545022971684428L;

    /**
     * Constructs a new {@code EventHandlerInvocationException} with the specified
     * detail message and cause.
     *
     * @param message the detail message explaining the reason for the exception.
     * @param cause   the underlying cause of the exception, usually an exception
     *                thrown during the invocation process.
     */
    public EventHandlerInvocationException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
