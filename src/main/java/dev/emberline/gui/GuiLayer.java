package dev.emberline.gui;

import dev.emberline.core.GameLoop;
import dev.emberline.core.components.Inputable;
import dev.emberline.core.components.Renderable;
import dev.emberline.core.render.CoordinateSystem;
import dev.emberline.gui.event.GuiEvent;
import dev.emberline.gui.event.GuiEventListener;
import javafx.scene.input.InputEvent;
import javafx.scene.input.MouseEvent;

import java.util.ArrayList;
import java.util.List;

public class GuiLayer implements Renderable, Inputable {
    protected final List<GuiButton> buttons = new ArrayList<>();
    private GuiEventListener listener;

    protected double x, y, width, height;

    protected GuiLayer(double x, double y, double width, double height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public final void setListener(GuiEventListener listener) {
        this.listener = listener;
    }

    protected final void throwEvent(GuiEvent event) {
        if (listener != null) {
            listener.onGuiEvent(event);
        }
    }

    @Override
    public final void processInput(InputEvent input) {
        CoordinateSystem guiCS = GameLoop.getInstance().getRenderer().getGuiCoordinateSystem();
        if (input instanceof MouseEvent mouse && mouse.getEventType() == MouseEvent.MOUSE_CLICKED) {
            double guiX = guiCS.toWorldX(mouse.getX());
            double guiY = guiCS.toWorldY(mouse.getY());

            // Check if the mouse is within the bounds of this layer
            if (guiX < x || guiX > x + width || guiY < y || guiY > y + height) {
                return;
            }
        }

        for (GuiButton button : buttons) {
            button.processInput(input);
        }

        input.consume(); // Consume the input event to prevent closing the active dialog
    }

    @Override
    public void render() {
        for (GuiButton button : buttons) {
            button.render();
        }
    }
}
