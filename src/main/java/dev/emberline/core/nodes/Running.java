package dev.emberline.core.nodes;

import dev.emberline.core.game.components.Inputable;
import javafx.scene.input.InputEvent;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

public class Running implements State, Inputable {
    private final Slow slow = new Slow();
    private final Fast fast = new Fast();
    private RunningState runningState;

    public Running() {
        runningState = slow;
    }

    @Override
    public void processInput(InputEvent inputEvent) {
        if (inputEvent.getEventType() == KeyEvent.KEY_PRESSED) {
            KeyEvent keyEvent = (KeyEvent)inputEvent;

            if (keyEvent.getCode() == KeyCode.F) {
                inputEvent.consume();
                goFast();
            } else if (keyEvent.getCode() == KeyCode.S) {
                inputEvent.consume();
                goSlow();
            }
        }
    }

    public void goFast() {
        runningState = fast;
    }

    public void goSlow() {
        runningState = slow;
    }

    @Override
    public void render() {
        runningState.render();
    }
}
