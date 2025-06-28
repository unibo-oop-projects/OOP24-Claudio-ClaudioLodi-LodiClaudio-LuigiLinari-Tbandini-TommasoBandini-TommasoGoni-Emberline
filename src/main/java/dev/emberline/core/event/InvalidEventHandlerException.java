package dev.emberline.core.event;

import java.io.Serial;

/**
 * This exception is thrown to indicate that an event handler method is invalid
 * or does not comply with the required structure or constraints expected by
 * the event dispatching system, described in the {@link EventHandler} documentation.
 * <p>
 * The {@code EventHandlerInvocationException} is intended for use
 * within the context of the event dispatching system, such as in the
 * {@code EventDispatcher#registerListener(java.util.EventListener)} method.
 *
 * @see EventDispatcher#registerListener(java.util.EventListener)
 * @see EventDispatcher
 */
class InvalidEventHandlerException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = -5988139178342694937L;

    /**
     * Constructs a new {@code EventHandlerInvocationException} with the specified
     * detail message.
     *
     * @param message the detail message explaining the reason for the exception.
     */
    InvalidEventHandlerException(final String message) {
        super(message);
    }
}
