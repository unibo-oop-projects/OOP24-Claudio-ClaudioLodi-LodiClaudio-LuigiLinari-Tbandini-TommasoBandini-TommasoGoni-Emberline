package dev.emberline.game.world;

import dev.emberline.core.GameLoop;
import dev.emberline.core.components.Inputable;
import dev.emberline.core.components.RenderComponent;
import dev.emberline.core.components.UpdateComponent;
import dev.emberline.core.render.CoordinateSystem;
import dev.emberline.utility.Vector2D;
import javafx.scene.input.InputEvent;
import javafx.scene.input.MouseEvent;

import java.io.Serial;
import java.io.Serializable;

/**
 * An abstract representation of a building within the game world.
 * A building represents an object that can be clicked, updated and rendered.
 * Classes that extend this will have to implement the getters for the bounds and
 * the logic when the building gets clicked.
 */
public abstract class Building implements RenderComponent, UpdateComponent, Inputable, Serializable {

    @Serial
    private static final long serialVersionUID = -4273087203030876279L;

    /**
     * Returns the top-left corner of the building's bounding box in the world
     * coordinate system.
     *
     * @return a {@code Vector2D} representing the world coordinates of the top-left corner
     */
    public abstract Vector2D getWorldTopLeft();

    /**
     * Returns the bottom-right corner of the building's bounding box in the world
     * coordinate system.
     *
     * @return a {@code Vector2D} representing the world coordinates of the bottom-right corner
     */
    public abstract Vector2D getWorldBottomRight();

    /**
     * Defines the behavior that occurs when the building is clicked.
     * Classes extending this class must override this method to
     * provide specific functionality that is executed upon a mouse click.
     * <p>
     * This method is invoked when the building detects a click event
     * within its bounding box in the game world coordinate system.
     */
    protected abstract void clicked();

    /**
     * Processes the input event to determine whether a click occurred within the boundaries
     * of the building in world coordinates. If a click is detected inside the building's
     * bounding box, the event is consumed, and the {@code clicked()} method is invoked.
     *
     * @param inputEvent the input event to process
     */
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
