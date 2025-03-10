package dev.emberline.core.input;

import java.util.concurrent.ConcurrentLinkedQueue;

import dev.emberline.core.game.components.Inputable;
import javafx.scene.input.InputEvent;

public class InputDispatcher {

    // This queue will be filled by JavaFX and polled by the Game Loop
    private static final ConcurrentLinkedQueue<InputEvent> inputs = new ConcurrentLinkedQueue<>();
    private final Inputable root;

    public InputDispatcher(Inputable root) {
        this.root = root;
    }

    public static void sendInput(InputEvent input) {
        inputs.add(input);
    }

    // Maybe other than having a non static root, it may be passed as a parameter to this function
    public void dispatchInputs() {
        while (!inputs.isEmpty()) {
            InputEvent inputEvent = inputs.poll();
            inputEvent.consume();

            root.processInput(inputEvent);
        }
    }
}
