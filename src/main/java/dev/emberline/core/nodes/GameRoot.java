package dev.emberline.core.nodes;

import dev.emberline.core.game.components.Inputable;
import dev.emberline.core.game.components.Renderable;
import dev.emberline.core.game.components.Updatable;
import javafx.scene.input.InputEvent;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

public class GameRoot implements Inputable, Renderable, Updatable {

    private final Menu menu = new Menu();
    private final Options options = new Options();
    private final GameSaves gameSaves = new GameSaves();
    private NavigationState navigationState;

    public GameRoot() {
        navigationState = menu;
    }

    @Override
    public void processInput(InputEvent inputEvent) {

        if (inputEvent.getEventType() == KeyEvent.KEY_PRESSED) {
            KeyEvent keyEvent = (KeyEvent)inputEvent;

            if (keyEvent.getCode() == KeyCode.M) {
                navigationState = menu;
            } else if (keyEvent.getCode() == KeyCode.O) {
                navigationState = options;
            } else if (keyEvent.getCode() == KeyCode.G) {
                navigationState = gameSaves;
            } else {
                return;
            }

            inputEvent.consume();
       }
    }

    @Override
    public void render() {
        navigationState.render();
    }

    @Override
    public void update(long elapsed) {
    }

}
