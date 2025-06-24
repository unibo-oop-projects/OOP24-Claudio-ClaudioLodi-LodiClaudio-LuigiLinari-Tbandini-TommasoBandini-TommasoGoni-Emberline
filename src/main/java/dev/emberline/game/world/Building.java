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
    public void processInput(final InputEvent inputEvent) {
        if (inputEvent.isConsumed()) {
            return;
        }
        if (!(inputEvent instanceof final MouseEvent mouse)) {
            return;
        }
        if (mouse.getEventType() != MouseEvent.MOUSE_CLICKED) {
            return;
        }

        final CoordinateSystem worldCS = GameLoop.getInstance().getRenderer().getWorldCoordinateSystem();
        final double worldX = worldCS.toWorldX(mouse.getX());
        final double worldY = worldCS.toWorldY(mouse.getY());
        if (isInside(worldX, worldY)) {
            clicked();
            inputEvent.consume();
        }
    }

    private boolean isInside(final double worldX, final double worldY) {
        if (worldX < getWorldTopLeft().getX() || worldX > getWorldBottomRight().getX()) {
            return false;
        }
        return worldY >= getWorldTopLeft().getY() && worldY <= getWorldBottomRight().getY();
    }
}
