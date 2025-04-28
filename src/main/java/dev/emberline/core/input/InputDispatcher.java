package dev.emberline.core.input;

import java.util.concurrent.ConcurrentLinkedQueue;

import dev.emberline.core.components.Inputable;
import javafx.event.EventType;
import javafx.geometry.Point2D;
import javafx.scene.input.InputEvent;
import javafx.scene.input.MouseEvent;

public class InputDispatcher {

    // This queue will be filled by JavaFX and polled by the Game Loop
    private static final ConcurrentLinkedQueue<InputEvent> inputs = new ConcurrentLinkedQueue<>();
    private final Inputable root;

    public InputDispatcher(Inputable root) {
        this.root = root;
    }

    public static void sendInput(InputEvent input) {
        // MouseLocation events
        if (input instanceof MouseEvent mouseEvent) {
            EventType<? extends MouseEvent> type = mouseEvent.getEventType();

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
            InputEvent inputEvent = inputs.poll();

            root.processInput(inputEvent);
        }
    }
}
