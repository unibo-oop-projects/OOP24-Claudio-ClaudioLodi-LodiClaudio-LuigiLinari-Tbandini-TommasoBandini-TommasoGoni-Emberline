package dev.emberline.core.nodes;

import dev.emberline.core.components.Inputable;
import dev.emberline.core.components.NavigationState;
import dev.emberline.core.components.Renderable;
import dev.emberline.core.components.Updatable;
import dev.emberline.core.gui.GuiLayer;
import dev.emberline.core.gui.GuiLayers;
import dev.emberline.core.input.InputResult;
import dev.emberline.game.world.World;
import javafx.scene.input.InputEvent;

public class GameRoot implements Inputable, Renderable, Updatable {
    // Navigation States
    private final World world = new World();
    private final GuiLayer menu = GuiLayers.MENU.get();
    private final GuiLayer settings = GuiLayers.SETTINGS.get();
    private NavigationState navigationState = menu;

    public GameRoot() {

    }

    @Override
    public InputResult processInput(InputEvent inputEvent) {
        if (!(navigationState instanceof Inputable)) return null;

        InputResult result = ((Inputable) navigationState).processInput(inputEvent);

        if (result == InputResult.GOTO_WORLD)
            navigationState = world;

        if (result == InputResult.GOTO_SETTINGS)
            navigationState = GuiLayers.SETTINGS.get();

        if (result == InputResult.PREVIOUS_GUI && navigationState == settings)
            navigationState = menu;

        return null;
    }

    @Override
    public void render() {
        if (navigationState instanceof Renderable) {
            ((Renderable) navigationState).render();
        }
    }

    @Override
    public void update(long elapsed) {
        if (navigationState instanceof Updatable) {
            ((Updatable) navigationState).update(elapsed);
        }
    }
}
