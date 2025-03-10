package dev.emberline.core.nodes;

import dev.emberline.core.game.GeneralParent;
import javafx.scene.input.InputEvent;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

public class Game extends GeneralParent {

    private Walking walking = new Walking();
    private Running running = new Running();

    public Game() {
        super();
        super.addActiveUpdatable(walking);
        super.addActiveRenderable(walking);
    }

    @Override
    public void processInput(InputEvent inputEvent) {
        if (inputEvent.getEventType() == KeyEvent.KEY_PRESSED) {
            KeyEvent keyEvent = (KeyEvent)inputEvent;

            if (keyEvent.getCode() == KeyCode.W) {
                startWalking();
            } else if (keyEvent.getCode() == KeyCode.R) {
                running.goSlow();
                startRunning();
            }
        }
        super.processInput(inputEvent);
    }

    public void startWalking() {
        super.removeActiveInputable(running);
        super.removeActiveUpdatable(running);
        super.removeActiveRenderable(running);

        super.addActiveUpdatable(walking);
        super.addActiveRenderable(walking);
    }

    public void startRunning() {
        super.removeActiveUpdatable(walking);
        super.removeActiveRenderable(walking);

        super.addActiveInputable(running);
        super.addActiveUpdatable(running);
        super.addActiveRenderable(running);
    }
}
