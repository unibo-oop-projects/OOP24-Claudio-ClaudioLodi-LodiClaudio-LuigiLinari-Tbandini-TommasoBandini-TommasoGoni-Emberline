package dev.emberline.gui;

import dev.emberline.core.components.Inputable;
import dev.emberline.core.components.Renderable;
import dev.emberline.gui.event.GuiEvent;
import dev.emberline.gui.event.GuiEventListener;
import javafx.scene.input.InputEvent;

import java.util.ArrayList;
import java.util.List;

public class GuiLayer implements Renderable, Inputable {
    protected final List<GuiButton> buttons = new ArrayList<>();
    private GuiEventListener listener;

    protected GuiLayer() {}

    public final void setListener(GuiEventListener listener) {
        this.listener = listener;
    }

    protected final void throwEvent(GuiEvent event) {
        if (listener != null) {
            listener.onGuiEvent(event);
        }
    }

    @Override
    public void processInput(InputEvent input) {
        for (GuiButton button : buttons) {
            button.processInput(input);
        }
    }

    @Override
    public void render() {
        for (GuiButton button : buttons) {
            button.render();
        }
    }
}
