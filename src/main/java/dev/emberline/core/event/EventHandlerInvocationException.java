package dev.emberline.core.event;

public class EventHandlerInvocationException extends RuntimeException {
    public EventHandlerInvocationException(String message, Throwable cause) {
        super(message, cause);
    }
}
