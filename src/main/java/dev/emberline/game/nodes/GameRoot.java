package dev.emberline.game.nodes;

import dev.emberline.core.components.Inputable;
import dev.emberline.core.components.Renderable;
import dev.emberline.core.components.Updatable;
import dev.emberline.game.world.Tower;
import dev.emberline.gui.GuiLayer;
import dev.emberline.gui.towerdialog.TowerDialogLayer;
import dev.emberline.gui.event.GuiEvent;
import dev.emberline.gui.event.GuiEventListener;
import dev.emberline.game.world.World;
import javafx.scene.input.InputEvent;

import java.io.*;

public class GameRoot implements Inputable, Updatable, Renderable, GuiEventListener {
    // Navigation States
    private final World world = new World();

    private long acc = 0;
    private final GuiLayer gl;

    public GameRoot() {
        Tower t = new Tower();
        gl = new TowerDialogLayer(t);
        gl.setListener(t);
    }

    @Override
    public void processInput(InputEvent inputEvent) {
        gl.processInput(inputEvent);
    }

    @Override
    public void update(long elapsed) {
        world.update(elapsed);
    }

    @Override
    public void render() {
        world.render();
        gl.render();
    }

    @Override
    public void onGuiEvent(GuiEvent event) {

    }
}
