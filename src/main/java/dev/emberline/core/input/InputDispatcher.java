package dev.emberline.core.input;

import dev.emberline.core.components.Inputable;
import javafx.event.EventType;
import javafx.geometry.Point2D;
import javafx.scene.input.InputEvent;
import javafx.scene.input.MouseEvent;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class InputDispatcher {

    // This queue will be filled by JavaFX and polled by the Game Loop
    private static final Queue<InputEvent> inputs = new ConcurrentLinkedQueue<>();
    private final Inputable root;

    public InputDispatcher(final Inputable root) {
        this.root = root;
    }

    public static void sendInput(final InputEvent input) {
        // MouseLocation events
        if (input instanceof final MouseEvent mouseEvent) {
            final EventType<? extends MouseEvent> type = mouseEvent.getEventType();

            if (type == MouseEvent.MOUSE_MOVED) {
                MouseLocation.setMouseLocation(new Point2D(mouseEvent.getX(), mouseEvent.getY()));
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
        inputs.add(input);
    }

    // Maybe other than having a non-static root, it may be passed as a parameter to this function
    public void dispatchInputs() {
        while (!inputs.isEmpty()) {
            final InputEvent inputEvent = inputs.poll();

            root.processInput(inputEvent);
        }
    }
}
