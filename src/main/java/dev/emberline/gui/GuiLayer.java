package dev.emberline.gui;

import dev.emberline.core.GameLoop;
import dev.emberline.core.components.Inputable;
import dev.emberline.core.components.Renderable;
import dev.emberline.core.event.EventDispatcher;
import dev.emberline.core.render.CoordinateSystem;
import dev.emberline.gui.event.GuiEvent;
import javafx.scene.input.InputEvent;
import javafx.scene.input.MouseEvent;

import java.util.ArrayList;
import java.util.List;

public class GuiLayer implements Renderable, Inputable {
    protected final List<GuiButton> buttons = new ArrayList<>();
    protected double x, y, width, height;

    protected GuiLayer(final double x, final double y, final double width, final double height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    protected final void throwEvent(final GuiEvent event) {
        EventDispatcher.getInstance().dispatchEvent(event);
    }

    @Override
    public final void processInput(final InputEvent input) {
        System.out.println(input.getEventType());
        final CoordinateSystem guiCS = GameLoop.getInstance().getRenderer().getGuiCoordinateSystem();
        if (input instanceof final MouseEvent mouse && mouse.getEventType() == MouseEvent.MOUSE_CLICKED) {
            final double guiX = guiCS.toWorldX(mouse.getX());
            final double guiY = guiCS.toWorldY(mouse.getY());

            // Check if the mouse is within the bounds of this layer
            if (guiX < x || guiX > x + width || guiY < y || guiY > y + height) {
                return;
            }
        }

        for (final GuiButton button : buttons) {
            button.processInput(input);
        }

        System.out.println("Click cosumed");
        input.consume(); // Consume the input event to prevent closing the active dialog
    }

    @Override
    public void render() {
        for (final GuiButton button : buttons) {
            button.render();
        }
    }
}
