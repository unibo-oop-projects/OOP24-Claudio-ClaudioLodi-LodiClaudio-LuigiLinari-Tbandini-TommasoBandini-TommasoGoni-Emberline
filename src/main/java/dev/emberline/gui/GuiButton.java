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
    private final Image normalSprite;
    private final Image hoverSprite; // Can be null
    private final double x;
    private final double y;
    private final double width;
    private final double height;
    private Runnable onClick;
    private Runnable onMouseEnter;
    private Runnable onMouseLeave;
    private boolean hovered = false;

    /**
     * Constructs a new GuiButton with the specified coordinates and sprites.
     *
     * @param x  The top-left x coordinate of the button in GUI coordinates.
     * @param y  The top-left y coordinate of the button in GUI coordinates.
     * @param width  The width of the button in GUI coordinates.
     * @param height The height of the button in GUI coordinates.
     * @param normalSprite The image to be displayed when the button is in its normal state.
     * @param hoverSprite  The image to be displayed when the button is hovered over.
     *
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
     * @param x  The top-left x coordinate of the button in GUI coordinates.
     * @param y  The top-left y coordinate of the button in GUI coordinates.
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
        if (inputEvent.isConsumed()) return;
        if (!(inputEvent instanceof MouseEvent mouse)) return;
        if (mouse.getEventType() != MouseEvent.MOUSE_CLICKED) return;

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
        // Positioning
        double x = guics.toScreenX(this.x);
        double y = guics.toScreenY(this.y);
        double width = guics.getScale() * this.width;
        double height = guics.getScale() * this.height;
        // Mouse hovering
        double mx = guics.toWorldX(MouseLocation.getX());
        double my = guics.toWorldY(MouseLocation.getY());
        boolean nowHovered = isInside(mx, my);
        if (nowHovered && !hovered && onMouseEnter != null) onMouseEnter.run();
        if (hovered && !nowHovered && onMouseLeave != null) onMouseLeave.run();
        hovered = nowHovered;

        // Render task
        renderer.addRenderTask(new RenderTask(RenderPriority.GUI_HIGH, () -> {
            if (hovered && hoverSprite == null) {
                gc.drawImage(normalSprite, x, y, width, height);
                Paint previousFill = gc.getFill();
                gc.setFill(Color.rgb(10,10,10,0.2));
                gc.fillRect(x, y, width, height);
                gc.setFill(previousFill);
            } else {
                gc.drawImage(hovered ? hoverSprite : normalSprite, x, y, width, height);
            }
        }));
    }

    private boolean isInside(double x, double y) {
        if (x < this.x || x > this.x + width) return false;
        if (y < this.y || y > this.y + height) return false;
        return true;
    }
}
