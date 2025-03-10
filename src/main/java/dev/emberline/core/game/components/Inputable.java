package dev.emberline.core.game.components;

import javafx.scene.input.InputEvent;

@FunctionalInterface
public interface Inputable {
    
    void processInput(InputEvent inputEvent);
}
