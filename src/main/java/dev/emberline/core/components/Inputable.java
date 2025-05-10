package dev.emberline.core.components;

import javafx.scene.input.InputEvent;

@FunctionalInterface
public interface Inputable {
    void processInput(InputEvent inputEvent);
}
