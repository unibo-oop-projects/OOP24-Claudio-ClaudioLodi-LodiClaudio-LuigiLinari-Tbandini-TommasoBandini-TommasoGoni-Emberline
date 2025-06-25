package dev.emberline.core.event;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.EventListener;
import java.util.EventObject;

/**
 * This annotation is used to mark methods declared inside an {@link EventListener} as event handlers.
 * An event handler is a method that listens for a specific type of event and can be invoked when compatible
 * event objects are dispatched.
 * <p>
 * Event handlers must be declared in a class that implements {@code EventListener}.
 * Event handlers must have exactly one parameter and that parameter must be of type {@link EventObject}
 * or a subclass of it. If an invalid event handler is used, an {@link InvalidEventHandlerException}
 * will be thrown.
 * <p>
 * It is suggested to not invoke event handlers directly, but rather use an {@link EventDispatcher}.
 * <h2>Example usage</h2>
 * <pre>{@code
 *     import java.util.EventListener;
 *     import java.util.EventObject;
 *
 *     public class MyEventListener implements EventListener {
 *
 *         @EventHandler
 *         public void handleMyEvent(MyEvent event) {
 *             System.out.println("Event received: " + event.getMessage());
 *         }
 *     }
 *}</pre>
 * In this example, the method `handleMyEvent` is marked with the {@code @EventHandler} annotation, indicating
 * that it will handle events of type `MyEvent` (and subclasses of MyEvent).
 * <p>
 * The `MyEvent` class must extend {@code EventObject} and can contain additional information about the event.
 *
 * @see EventDispatcher
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface EventHandler {
}
