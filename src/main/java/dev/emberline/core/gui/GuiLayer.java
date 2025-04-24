package dev.emberline.core.gui;

import dev.emberline.core.GameLoop;
import dev.emberline.core.components.Inputable;
import dev.emberline.core.components.Renderable;
import dev.emberline.core.input.InputResult;
import dev.emberline.core.components.NavigationState;
import dev.emberline.core.render.CoordinateSystem;
import dev.emberline.core.render.RenderPriority;
import dev.emberline.core.render.RenderTask;
import dev.emberline.core.render.Renderer;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.InputEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;

import java.util.Collections;
import java.util.List;

public class GuiLayer implements Renderable, Inputable, NavigationState {
    private final List<GuiButton> buttons;
    private final List<Renderable> decorations;

    // Package private constructor
    GuiLayer(List<GuiButton> buttons, List<Renderable> decorations) {
        this.buttons = (buttons == null) ? Collections.emptyList() : buttons;
        this.decorations = (decorations == null) ? Collections.emptyList() : decorations;
    }

    @Override
    public InputResult processInput(InputEvent inputEvent) {
        if (inputEvent.isConsumed()) return null;
        if (!inputEvent.getEventType().equals(MouseEvent.MOUSE_CLICKED)) return null;

        InputResult result = null;
        for (GuiButton button : buttons) {
           if (inputEvent.isConsumed()) break;
           result = button.processInput(inputEvent);
        }
        return result;
    }

    @Override
    public void render() {
        Renderer gameRenderer = GameLoop.getInstance().getRenderer();
        GraphicsContext gc = gameRenderer.getGraphicsContext();
        CoordinateSystem guics = gameRenderer.getGuiCoordinateSystem();

        gameRenderer.addRenderTask(new RenderTask(RenderPriority.BACKGROUND, () -> {
            gc.setStroke(Color.YELLOW);

            //drawing grid vertical lines
            for (int x = 0; x <= Renderer.GUICS_WIDTH; ++x) {
                gc.strokeLine(guics.toScreenX(x), guics.toScreenY(0), guics.toScreenX(x), guics.toScreenY(Renderer.GUICS_HEIGHT));
                gc.setFill(Color.AQUAMARINE);
                gc.fillText(String.valueOf(x), guics.toScreenX(x), guics.toScreenY(Renderer.GUICS_HEIGHT) - 10);
            }
            //drawing grid horizontal lines
            for (int y = 0; y <= Renderer.GUICS_HEIGHT; ++y) {
                gc.strokeLine(guics.toScreenX(0), guics.toScreenY(y), guics.toScreenX(Renderer.GUICS_WIDTH), guics.toScreenY(y));
                gc.setFill(Color.FLORALWHITE);
                gc.fillText(String.valueOf(y), 10, guics.toScreenY(y));
            }
        }));


        for (GuiButton button : buttons) {
            button.render();
        }

        for (Renderable decoration : decorations) {
            decoration.render();
        }
    }
}
