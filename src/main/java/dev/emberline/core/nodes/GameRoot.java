package dev.emberline.core.nodes;

import dev.emberline.core.game.GeneralParent;
import javafx.scene.input.InputEvent;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

public class GameRoot extends GeneralParent {

    private Menu menu = new Menu();
    private Options options = new Options();
    private GameSaves gameSaves = new GameSaves();

    public GameRoot() {
        super();
        goToMenu();   // default
    }

    @Override
    public void processInput(InputEvent inputEvent) {
        if (inputEvent.getEventType() == KeyEvent.KEY_PRESSED) {
            KeyEvent keyEvent = (KeyEvent)inputEvent;

            switch (keyEvent.getCode()) {
                case KeyCode.M -> goToMenu();
                case KeyCode.O -> goToOptions();
                case KeyCode.G -> goToGameSaves();
                default -> {break;}
            }
       }
        super.processInput(inputEvent);
    }

    public void goToMenu() {
        super.removeAllActives(gameSaves.asInputable(), gameSaves.asUpdatable(), gameSaves.asRenderable());
        super.removeAllActives(options.asInputable(), options.asUpdatable(), options.asRenderable());

        super.addAllActives(menu.asInputable(), menu.asUpdatable(), menu.asRenderable());
    }

    public void goToOptions() {
        super.removeAllActives(gameSaves.asInputable(), gameSaves.asUpdatable(), gameSaves.asRenderable());
        super.removeAllActives(menu.asInputable(), menu.asUpdatable(), menu.asRenderable());
        
        super.addAllActives(options.asInputable(), options.asUpdatable(), options.asRenderable());
    }

    public void goToGameSaves() {
        super.removeAllActives(menu.asInputable(), menu.asUpdatable(), menu.asRenderable());
        super.removeAllActives(options.asInputable(), options.asUpdatable(), options.asRenderable());
        
        super.addAllActives(gameSaves.asInputable(), gameSaves.asUpdatable(), gameSaves.asRenderable());
    }

}
