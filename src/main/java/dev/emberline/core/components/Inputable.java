package dev.emberline.core.components;

import dev.emberline.core.input.InputResult;
import javafx.scene.input.InputEvent;

@FunctionalInterface
public interface Inputable {

    InputResult processInput(InputEvent inputEvent);
}
