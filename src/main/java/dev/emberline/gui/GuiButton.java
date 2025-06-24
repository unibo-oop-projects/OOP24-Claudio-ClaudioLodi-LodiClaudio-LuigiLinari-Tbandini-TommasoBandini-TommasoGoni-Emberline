package dev.emberline.gui;

import dev.emberline.core.GameLoop;
import dev.emberline.core.components.Inputable;
import dev.emberline.core.components.Renderable;
import dev.emberline.core.input.MouseLocation;
import dev.emberline.core.render.CoordinateSystem;
import dev.emberline.core.render.RenderPriority;
import dev.emberline.core.render.RenderTask;
import dev.emberline.core.render.Renderer;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.InputEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

import java.util.Objects;

public class GuiButton implements Inputable, Renderable {
    protected final Image normalSprite;
    protected final Image hoverSprite; // Can be null
    protected final double x;
    protected final double y;
    protected final double width;
    protected final double height;
    protected Runnable onClick;
    protected Runnable onMouseEnter;
    protected Runnable onMouseLeave;
    // use this only for holding the previous hovered state, use hovered to communicate to the outside world
    private boolean wasHovered = false;
    // this is needed if isHovered() is called from onMouseEnter or onMouseLeave, because the wasHovered state is not updated yet.
    private boolean hovered = false;

    /**
     * Constructs a new GuiButton with the specified coordinates and sprites.
     *
     * @param x            The top-left x coordinate of the button in GUI coordinates.
     * @param y            The top-left y coordinate of the button in GUI coordinates.
     * @param width        The width of the button in GUI coordinates.
     * @param height       The height of the button in GUI coordinates.
     * @param normalSprite The image to be displayed when the button is in its normal state.
     * @param hoverSprite  The image to be displayed when the button is hovered over.
     * @see GuiButton#GuiButton(double, double, double, double, Image)
     */
    public GuiButton(double x, double y, double width, double height, Image normalSprite, Image hoverSprite) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.normalSprite = Objects.requireNonNull(normalSprite, "normalSprite cannot be null");
        this.hoverSprite = hoverSprite;
    }

    /**
     * Constructs a new GuiButton with the specified coordinates and sprite, a default hover effect is applied.
     * If you want to use a different hover effect, use {@link #GuiButton(double, double, double, double, Image, Image)}.
     *
     * @param x      The top-left x coordinate of the button in GUI coordinates.
     * @param y      The top-left y coordinate of the button in GUI coordinates.
     * @param width  The width of the button in GUI coordinates.
     * @param height The height of the button in GUI coordinates.
     * @param sprite The image to be displayed when the button is in its normal state.
     */
    public GuiButton(double x, double y, double width, double height, Image sprite) {
        this(x, y, width, height, sprite, null);
    }

    public void setOnClick(Runnable onClick) {
        this.onClick = onClick;
    }

    public void setOnMouseLeave(Runnable onMouseLeave) {
        this.onMouseLeave = onMouseLeave;
    }

    public void setOnMouseEnter(Runnable onMouseEnter) {
        this.onMouseEnter = onMouseEnter;
    }

    public boolean isHovered() {
        return hovered;
    }

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

        CoordinateSystem guics = GameLoop.getInstance().getRenderer().getGuiCoordinateSystem();
        double x = guics.toWorldX(mouse.getX());
        double y = guics.toWorldY(mouse.getY());
        if (isInside(x, y) && onClick != null) {
            onClick.run();
            inputEvent.consume();
        }
    }

    @Override
    public void render() {
        // Rendering
        Renderer renderer = GameLoop.getInstance().getRenderer();
        GraphicsContext gc = renderer.getGraphicsContext();
        CoordinateSystem guics = renderer.getGuiCoordinateSystem();
        // Mouse hovering
        computeHoverState(guics.toWorldX(MouseLocation.getX()), guics.toWorldY(MouseLocation.getY()));

        // Positioning
        double screenX = guics.toScreenX(this.x);
        double screenY = guics.toScreenY(this.y);
        double screenWidth = guics.getScale() * this.width;
        double screenHeight = guics.getScale() * this.height;

        // Render task
        renderer.addRenderTask(new RenderTask(RenderPriority.GUI_HIGH, () -> {
            if (hovered && hoverSprite == null) {
                gc.drawImage(normalSprite, screenX, screenY, screenWidth, screenHeight);
                Paint previousFill = gc.getFill();
                gc.setFill(Color.rgb(10, 10, 10, 0.2));
                gc.fillRect(screenX, screenY, screenWidth, screenHeight);
                gc.setFill(previousFill);
            }
            else {
                gc.drawImage(hovered ? hoverSprite : normalSprite, screenX, screenY, screenWidth, screenHeight);
            }
        }));
    }

    // In GUI coordinates
    protected boolean isInside(double x, double y) {
        if (x < this.x || x > this.x + width) {
            return false;
        }
        return !(y < this.y) && !(y > this.y + height);
    }

    protected void computeHoverState(double mouseGuiX, double mouseGuiY) {
        hovered = isInside(mouseGuiX, mouseGuiY);
        if (hovered && !wasHovered && onMouseEnter != null) {
            onMouseEnter.run();
        }
        if (wasHovered && !hovered && onMouseLeave != null) {
            onMouseLeave.run();
        }
        wasHovered = hovered;
    }
}
