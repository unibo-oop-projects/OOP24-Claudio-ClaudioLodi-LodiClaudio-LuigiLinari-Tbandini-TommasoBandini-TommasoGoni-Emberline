package dev.emberline.core.event;

import java.lang.reflect.Method;
import java.util.*;

/**
 * TODO
 */
public class EventDispatcher {
    private static EventDispatcher instance;

    // Map to hold event handler methods and their corresponding listeners
    private final Map<EventHandlerMethod, List<EventListener>> eventHandlers = new HashMap<>();
    
    private record EventHandlerMethod(Class<? extends EventObject> eventType, Method method) {
        private EventHandlerMethod {
            if (eventType == null || method == null) {
                throw new IllegalArgumentException("Event type and method cannot be null");
            }
        }
        
        private EventHandlerMethod(Method method) {
            this(method.getParameterTypes()[0].asSubclass(EventObject.class), method);
        }
    }
    
    private EventDispatcher() {
    }

    public static EventDispatcher getInstance() {
        if (instance == null) {
            instance = new EventDispatcher();
        }
        return instance;
    }

    /**
     * TODO
     */
    public void registerListener(EventListener listener) {
        if (listener == null) {
            throw new IllegalArgumentException("Listener cannot be null");
        }
        for (Method method : getEventHandlerMethods(listener)) {
            eventHandlers.computeIfAbsent(new EventHandlerMethod(method), k -> new ArrayList<>()).add(listener);
        }
    }

    /**
     * TODO
     */
    public void unregisterListener(EventListener listener) {
        if (listener == null) {
            throw new IllegalArgumentException("Listener cannot be null");
        }
        for (Method method : getEventHandlerMethods(listener)) {
            EventHandlerMethod handlerMethod = new EventHandlerMethod(method);
            List<EventListener> listeners = eventHandlers.get(handlerMethod);
            if (listeners != null) {
                listeners.remove(listener);
                if (listeners.isEmpty()) {
                    eventHandlers.remove(handlerMethod);
                }
            }
        }
    }

    /**
     * Dispatches the specified event to all registered event listeners that have
     * handlers for the type of event being dispatched. There is no guarantee on the
     * order in which the event handlers are called.
     *
     * @param event the event to dispatch, which must be an instance of {@link EventObject}
     *              or one of its subclasses
     * @throws IllegalArgumentException if the event parameter is {@code null}
     */
    public void dispatchEvent(EventObject event) {
        if (event == null) {
            throw new IllegalArgumentException("Event cannot be null");
        }

        for (var entry : eventHandlers.entrySet()) {
            if (!entry.getKey().eventType.isAssignableFrom(event.getClass())) {
                continue;
            }
            Method method = entry.getKey().method;
            method.setAccessible(true);
            List<EventListener> listeners = entry.getValue();
            listeners.forEach(listener -> {
                try {
                    method.invoke(listener, event);
                } catch (Exception e) {
                    throw new RuntimeException("Failed to invoke event handler method: " + method.getName(), e);
                }
            });
        }
    }

    /**
     * Retrieves all valid event handler methods from the given listener.
     * @param listener the event listener from which to retrieve event handler methods
     * @return an array of valid event handler methods
     */
    private Method[] getEventHandlerMethods(EventListener listener) {
        return Arrays.stream(listener.getClass().getDeclaredMethods())
                .filter(this::isEventHandlerMethod)
                .toArray(Method[]::new);
    }

    /**
     * Determines whether the given method qualifies as a valid event handler method.
     * A method is considered an event handler method if it is annotated with {@link EventHandler}
     * and passes the validation checks defined in {@link EventDispatcher#validateEventHandler}.
     *
     * @param method the method to check for event handler validity
     * @return {@code true} if the method is a valid event handler, {@code false} otherwise
     */
    private boolean isEventHandlerMethod(Method method) {
        if (!method.isAnnotationPresent(EventHandler.class)) {
            return false;
        }
        // If an invalid event handler method is found, it will throw an exception
        validateEventHandler(method);
        return true;
    }

    /**
     * Validates that a method annotated with {@link EventHandlerMethod} satisfies the required signature
     * for an event handler.
     * <p>
     * Event handlers must have exactly one parameter and that parameter must be of type
     * {@link EventObject} or a subclass of it. Event handlers must also be declared in a
     * class that implements {@link EventListener}.
     * <p>
     * If the validation fails, an {@link InvalidEventHandlerException} is thrown.
     *
     * @param method the method to validate as a potential event handler
     * @throws InvalidEventHandlerException if the method does not comply with the expected
     *                                      signature for event handlers
     */
    private void validateEventHandler(Method method) {
        Class<?>[] parameterTypes = method.getParameterTypes();
        if (parameterTypes.length != 1) {
            throw new InvalidEventHandlerException("Event handler methods must have exactly one parameter, but found: " + parameterTypes.length);
        }
        if (!EventObject.class.isAssignableFrom(parameterTypes[0])) {
            throw new InvalidEventHandlerException("Event handler methods must accept a parameter of type EventObject or a subclass, but found: " + parameterTypes[0].getName());
        }
        if (!EventListener.class.isAssignableFrom(method.getDeclaringClass())) {
            throw new InvalidEventHandlerException("Event handler methods must be declared in a class that implements EventListener, but found: " + method.getDeclaringClass().getName());
        }
    }
}
