package dev.emberline.core.nodes;

import dev.emberline.core.game.GeneralParent;
import javafx.scene.input.InputEvent;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

public class Running extends GeneralParent {

    private final Slow slow = new Slow();
    private final Fast fast = new Fast();

    public Running() {
        super();
        super.addActiveUpdatable(slow);
        super.addActiveRenderable(slow);
    }

    @Override
    public void processInput(InputEvent inputEvent) {
        if (inputEvent.getEventType() == KeyEvent.KEY_PRESSED) {
            KeyEvent keyEvent = (KeyEvent)inputEvent;

            if (keyEvent.getCode() == KeyCode.F) {
                goFast();
            } else if (keyEvent.getCode() == KeyCode.S) {
                goSlow();
            }
        }
    }

    public void goFast() {
        super.removeActiveUpdatable(slow);
        super.removeActiveRenderable(slow);

        super.addActiveUpdatable(fast);
        super.addActiveRenderable(fast);

    }

    public void goSlow() {
        super.removeActiveUpdatable(fast);
        super.removeActiveRenderable(fast);

        super.addActiveUpdatable(slow);
        super.addActiveRenderable(slow);
    }
}
