package dev.emberline.core.nodes;

import dev.emberline.core.game.components.Inputable;
import dev.emberline.core.game.components.Renderable;
import dev.emberline.core.game.components.Updatable;
import javafx.scene.input.InputEvent;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

public class Game implements Inputable, Renderable, Updatable {
    private final Walking walking = new Walking();
    private final Running running = new Running();
    private State state;

    public Game() {
        state = walking;
    }

    @Override
    public void processInput(InputEvent inputEvent) {
        if (state == running) {
            running.processInput(inputEvent);
        }

        if (inputEvent.isConsumed()) return;

        if (inputEvent.getEventType() == KeyEvent.KEY_PRESSED) {
            KeyEvent keyEvent = (KeyEvent)inputEvent;

            if (keyEvent.getCode() == KeyCode.W) {
                inputEvent.consume();
                state = walking;
            } else if (keyEvent.getCode() == KeyCode.R) {
                inputEvent.consume();
                running.goSlow();
                state = running;
            }
        }
    }

    @Override
    public void render() {
        state.render();
    }

    @Override
    public void update(long elapsed)  {

    }
}
