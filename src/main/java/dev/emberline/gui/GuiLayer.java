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

/**
 * Represents a graphical user interface layer that can handle rendering
 * and input events. This class is designed to encapsulate a collection of
 * {@code GuiButton} elements, provide spatial boundaries, and propagate
 * interactions within its area.
 * <p>
 * A {@code GuiLayer} defines a rectangular region on the GUI canvas and manages
 * input events and rendering within its bounds. It is intended to be extended by
 * specialized GUI layers that implement additional functionality.
 */
public class GuiLayer implements Renderable, Inputable {
    /**
     * This list is intended to store all buttons associated with the current
     * {@code GuiLayer}, allowing centralized management of their input handling
     * and rendering logic.
     */
    protected final List<GuiButton> buttons = new ArrayList<>();
    /**
     * The x-y coordinates of the top-left corner and dimensions of this GUI layer in the GUI
     * coordinate system.
     */
    protected double x, y, width, height;

    /**
     * Constructs a new {@code GuiLayer} with the specified position and dimensions.
     *
     * @param x the x-coordinate of the top-left corner of the GUI layer
     * @param y the y-coordinate of the top-left corner of the GUI layer
     * @param width the width of the GUI layer
     * @param height the height of the GUI layer
     */
    protected GuiLayer(final double x, final double y, final double width, final double height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    /**
     * Dispatches the specified {@code GuiEvent} to the {@code EventDispatcher} for further processing.
     *
     * @param event the {@code GuiEvent} to be dispatched
     * @throws IllegalArgumentException if {@code event} is {@code null}
     */
    protected final void throwEvent(final GuiEvent event) {
        EventDispatcher.getInstance().dispatchEvent(event);
    }

    /**
     * Processes the given input event, which may include mouse or other input types,
     * and delegates input handling to buttons within the GUI layer. It also ensures
     * that the input events are consumed if appropriate.
     *
     * @param input the {@link InputEvent} to be handled, which could include mouse
     *              events or other types of input events. This parameter must not be {@code null}.
     */
    @Override
    public final void processInput(final InputEvent input) {
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

        input.consume(); // Consume the input event to prevent closing the active dialog
    }

    /**
     * Renders all GUI buttons contained within the current layer and the layer itself.
     * @see Renderable#render()
     */
    @Override
    public void render() {
        for (final GuiButton button : buttons) {
            button.render();
        }
    }
}
