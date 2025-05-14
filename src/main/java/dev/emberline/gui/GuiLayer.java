package dev.emberline.gui;

import dev.emberline.core.components.Inputable;
import dev.emberline.core.components.Renderable;
import dev.emberline.gui.event.GuiEventListener;
import javafx.scene.input.InputEvent;

import java.util.List;

public abstract class GuiLayer implements Renderable, Inputable {
    protected List<GuiButton> buttons;
    protected List<Renderable> decorations;
    protected GuiEventListener listener;

    public void setListener(GuiEventListener listener) {
        this.listener = listener;
    }

    @Override
    public void processInput(InputEvent input) {
        for (GuiButton button : buttons) button.processInput(input);
    }

    @Override
    public void render() {
        for (GuiButton button : buttons) button.render();
        for (Renderable decoration : decorations) decoration.render();
    }
}
