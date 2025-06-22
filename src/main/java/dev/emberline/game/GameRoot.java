package dev.emberline.game;

import dev.emberline.core.components.Inputable;
import dev.emberline.core.components.Renderable;
import dev.emberline.core.components.Updatable;
import dev.emberline.game.world.World;
import dev.emberline.gui.event.GuiEvent;
import dev.emberline.gui.event.GuiEventListener;
import javafx.scene.input.InputEvent;

public class GameRoot implements Inputable, Updatable, Renderable, GuiEventListener {
    // Navigation States
    private final World world = new World();

    public GameRoot() {
    }

    @Override
    public void processInput(InputEvent inputEvent) {
        world.processInput(inputEvent);
    }

    @Override
    public void update(long elapsed) {
        world.update(elapsed);
    }

    @Override
    public void render() {
        world.render();
    }

    @Override
    public void onGuiEvent(GuiEvent event) {}
}
