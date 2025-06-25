package dev.emberline.core.input;

import dev.emberline.core.components.Inputable;
import javafx.event.EventType;
import javafx.geometry.Point2D;
import javafx.scene.input.InputEvent;
import javafx.scene.input.MouseEvent;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * The InputDispatcher class is responsible for keeping track of all the inputs received
 * from the JavaFX Application Thread and dispatching them to the GameLoop thread.
 * For that it maintains a thread-safe queue of input events.
 * <p>
 * Dispatching of events is cascaded to a root object of type {@code Inputable}, which
 * processes input recursively, delegating further handling as required.
 */
public class InputDispatcher {

    // This queue will be filled by JavaFX and polled by the Game Loop
    private static final Queue<InputEvent> INPUTS = new ConcurrentLinkedQueue<>();
    private final Inputable root;

    /**
     * Constructs an InputDispatcher object.
     *
     * @param root the root {@code Inputable} object that serves as the primary handler
     *             for input events. This object processes the inputs and may delegate
     *             further handling recursively.
     * @see InputDispatcher
     */
    public InputDispatcher(final Inputable root) {
        this.root = root;
    }

    /**
     * Sends the given input event to the {@code InputDispatcher}.
     * Depending on its type, the event is either used to update mouse-related states or
     * added to a thread-safe queue for further processing.
     *
     * @param input the input event to be processed. It can be of various types, including
     *              {@code MouseEvent} or other types extending {@code InputEvent}.
     */
    public static void sendInput(final InputEvent input) {
        // MouseLocation events
        if (input instanceof final MouseEvent mouseEvent) {
            final EventType<? extends MouseEvent> type = mouseEvent.getEventType();

            if (type == MouseEvent.MOUSE_MOVED) {
                MouseLocation.setLocation(new Point2D(mouseEvent.getX(), mouseEvent.getY()));
                mouseEvent.consume();
                return;
            }
            if (type == MouseEvent.MOUSE_ENTERED) {
                MouseLocation.setIsMouseInside(true);
                mouseEvent.consume();
                return;
            }
            if (type == MouseEvent.MOUSE_EXITED) {
                MouseLocation.setIsMouseInside(false);
                mouseEvent.consume();
                return;
            }
        }

        // Other events get enqueued
        INPUTS.add(input);
    }

    /**
     * Dispatches all pending input events from the queue to the root {@code Inputable} object
     * for processing.
     * <p>
     * This method must be executed within the game loop thread to guarantee that input events
     * are handled in sync with the game update and render cycle.
     * <p>
     * Handlers in the input processing chain are expected to call {@code consume} on events
     * they fully handle.
     */
    public void dispatchInputs() {
        while (!INPUTS.isEmpty()) {
            final InputEvent inputEvent = INPUTS.poll();

            root.processInput(inputEvent);
        }
    }
}
