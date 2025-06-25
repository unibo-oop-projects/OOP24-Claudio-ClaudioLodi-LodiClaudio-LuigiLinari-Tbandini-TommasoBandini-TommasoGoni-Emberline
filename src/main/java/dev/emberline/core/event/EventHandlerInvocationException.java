package dev.emberline.core.event;

public class EventHandlerInvocationException extends RuntimeException {
    public EventHandlerInvocationException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
