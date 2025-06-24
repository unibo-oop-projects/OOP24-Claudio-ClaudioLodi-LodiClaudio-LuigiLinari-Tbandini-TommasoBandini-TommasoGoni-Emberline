package dev.emberline.game.world;

import dev.emberline.core.GameLoop;
import dev.emberline.core.components.Inputable;
import dev.emberline.core.components.Renderable;
import dev.emberline.core.components.Updatable;
import dev.emberline.core.render.CoordinateSystem;
import dev.emberline.utility.Vector2D;
import javafx.scene.input.InputEvent;
import javafx.scene.input.MouseEvent;

public abstract class Building implements Renderable, Updatable, Inputable {

    public abstract Vector2D getWorldTopLeft();

    public abstract Vector2D getWorldBottomRight();

    protected abstract void clicked();

    @Override
    public void processInput(InputEvent inputEvent) {
        if (inputEvent.isConsumed()) {
            return;
        }
        if (!(inputEvent instanceof MouseEvent mouse)) {
            return;
        }
        if (mouse.getEventType() != MouseEvent.MOUSE_CLICKED) {
            return;
        }

        CoordinateSystem worldCS = GameLoop.getInstance().getRenderer().getWorldCoordinateSystem();
        double worldX = worldCS.toWorldX(mouse.getX());
        double worldY = worldCS.toWorldY(mouse.getY());
        if (isInside(worldX, worldY)) {
            clicked();
            inputEvent.consume();
        }
    }

    private boolean isInside(double worldX, double worldY) {
        if (worldX < getWorldTopLeft().getX() || worldX > getWorldBottomRight().getX()) {
            return false;
        }
        if (worldY < getWorldTopLeft().getY() || worldY > getWorldBottomRight().getY()) {
            return false;
        }
        return true;
    }
}
