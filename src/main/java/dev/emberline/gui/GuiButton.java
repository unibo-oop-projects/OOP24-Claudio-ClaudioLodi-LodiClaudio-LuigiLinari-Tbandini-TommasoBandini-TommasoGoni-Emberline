package dev.emberline.gui;

import dev.emberline.core.components.Inputable;
import dev.emberline.core.components.Renderable;
import javafx.geometry.Point2D;
import javafx.scene.input.InputEvent;
import javafx.scene.input.MouseEvent;

class GuiButton implements Inputable, Renderable {
    private final Point2D topLeft;
    private final Point2D bottomRight;
    private Runnable onClick;

    GuiButton(Point2D topLeft, Point2D bottomRight) {
        this.topLeft = topLeft;
        this.bottomRight = bottomRight;
    }

    void setOnClick(Runnable onClick) {
        this.onClick = onClick;
    }

    @Override
    public void processInput(InputEvent inputEvent) {
        if (!(inputEvent instanceof MouseEvent mouse)) return;
        if (mouse.getEventType() != MouseEvent.MOUSE_CLICKED) return;

        double x = mouse.getX();
        double y = mouse.getY();
        if (x < topLeft.getX() || x > bottomRight.getX()) return;
        if (y < topLeft.getY() || y > bottomRight.getY()) return;
        onClick.run();
    }

    @Override
    public void render() {

    }
}
