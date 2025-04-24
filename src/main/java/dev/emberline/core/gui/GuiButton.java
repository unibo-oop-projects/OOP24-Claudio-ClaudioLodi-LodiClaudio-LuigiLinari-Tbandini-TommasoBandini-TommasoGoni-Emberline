package dev.emberline.core.gui;

import dev.emberline.core.GameLoop;
import dev.emberline.core.components.Inputable;
import dev.emberline.core.components.Renderable;
import dev.emberline.core.input.InputResult;
import dev.emberline.core.render.CoordinateSystem;
import dev.emberline.core.render.RenderPriority;
import dev.emberline.core.render.RenderTask;
import dev.emberline.core.render.Renderer;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.InputEvent;
import javafx.geometry.Point2D;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;

public class GuiButton implements Renderable, Inputable {
    private String text; //TODO SUBSTITUTE THIS WITH A SPRITE
    private Point2D topLeft;
    private Point2D bottomRight;
    private InputResult inputResult;

    // Package private constructor
    GuiButton(String text, Point2D topLeft, Point2D bottomRight, InputResult inputResult) {
        this.text = text;
        this.topLeft = topLeft;
        this.bottomRight = bottomRight;
        this.inputResult = inputResult;
    }

    @Override
    public InputResult processInput(InputEvent inputEvent) {
        if (inputEvent.isConsumed() || !inputEvent.getEventType().equals(MouseEvent.MOUSE_CLICKED)) {
            return null;
        }

        MouseEvent mouseEvent = (MouseEvent) inputEvent;
        CoordinateSystem guics = GameLoop.getInstance().getRenderer().getGuiCoordinateSystem();
        double x = guics.toWorldX(mouseEvent.getSceneX());
        double y = guics.toWorldY(mouseEvent.getSceneY());

        if (x >= topLeft.getX() && x <= bottomRight.getX() && y >= topLeft.getY() && y <= bottomRight.getY()) {
            inputEvent.consume();
            return inputResult;
        }
        return null;
    }

    @Override
    public void render() {
        // TODO
        Renderer gameRenderer = GameLoop.getInstance().getRenderer();
        GraphicsContext gc = gameRenderer.getGraphicsContext();
        CoordinateSystem guics = gameRenderer.getGuiCoordinateSystem();

        double x = guics.toScreenX(topLeft.getX());
        double y = guics.toScreenY(topLeft.getY());
        double width = guics.toScreenX(bottomRight.getX()) - x;
        double height = guics.toScreenY(bottomRight.getY()) - y;

        gameRenderer.addRenderTask(new RenderTask(RenderPriority.GUI, () -> {
            gc.setFill(Color.RED);
            gc.fillRect(x, y, width, height);
            gc.setFill(Color.WHITE);
            gc.fillText(text, x, y + height/2);
        }));
    }
}
