package dev.emberline.gui;

import dev.emberline.core.GameLoop;
import dev.emberline.core.components.Inputable;
import dev.emberline.core.components.Renderable;
import dev.emberline.core.input.MouseLocation;
import dev.emberline.core.render.CoordinateSystem;
import dev.emberline.core.render.RenderPriority;
import dev.emberline.core.render.RenderTask;
import dev.emberline.core.render.Renderer;
import dev.emberline.core.sounds.AudioController;
import dev.emberline.core.sounds.event.SfxSoundEvent.SoundType;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.InputEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

import java.util.Objects;

/**
 * Represents a graphical user interface button that can respond to mouse
 * input events and render itself on the screen. A GUI button can have a normal
 * sprite, a hover sprite (optional), and user-defined behavior such as click,
 * mouse-enter, and mouse-leave actions.
 * <p>
 * The position and size of the button are defined in GUI coordinates. The button
 * is interactive and can track the hover state of the mouse. It also provides
 * methods for registering event handlers for various mouse interactions.
 */
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

    /**
     * Indicates whether the button was previously in a hovered state.
     * <p>
     * Note: use this only for holding the previous hovered state, use hovered to communicate to the outside world.
     */
    private boolean wasHovered;

    /**
     * Indicates whether the GUI button is currently being hovered over by the mouse.
     * This is needed if isHovered() is called from onMouseEnter or onMouseLeave, because the wasHovered state
     * is not updated yet.
     *
     * @see GuiButton#isHovered()
     * @see GuiButton#computeHoverState(double, double)
     */
    private boolean hovered;

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
    @SuppressFBWarnings(
            value = "EI_EXPOSE_REP2",
            justification = "This is intended behaviour as Image is an immutable container."
    )
    public GuiButton(final double x, final double y, final double width, final double height,
                     final Image normalSprite, final Image hoverSprite) {
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
    public GuiButton(final double x, final double y, final double width, final double height, final Image sprite) {
        this(x, y, width, height, sprite, null);
    }

    /**
     * Sets the action to be performed when this button is clicked.
     *
     * @param onClick A Runnable containing the code to execute when the button is clicked.
     */
    public final void setOnClick(final Runnable onClick) {
        this.onClick = onClick;
    }

    /**
     * Sets the action to be performed when the mouse leaves the bounds of this button.
     *
     * @param onMouseLeave A Runnable containing the code to execute when the mouse leaves the button.
     */
    public void setOnMouseLeave(final Runnable onMouseLeave) {
        this.onMouseLeave = onMouseLeave;
    }

    /**
     * Sets the action to be performed when the mouse enters the bounds of this button.
     *
     * @param onMouseEnter A Runnable containing the code to execute when the mouse enters the button.
     */
    public void setOnMouseEnter(final Runnable onMouseEnter) {
        this.onMouseEnter = onMouseEnter;
    }

    /**
     * Returns whether the GUI button is currently in a hovered state.
     *
     * @return whether the GUI button is currently in a hovered state.
     */
    public boolean isHovered() {
        return hovered;
    }

    /**
     * Processes input events to handle mouse interactions with the GUI button.
     *
     * @param inputEvent The input event to process.
     * @see Inputable#processInput(InputEvent)
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

        final CoordinateSystem guics = GameLoop.getInstance().getRenderer().getGuiCoordinateSystem();
        final double x = guics.toWorldX(mouse.getX());
        final double y = guics.toWorldY(mouse.getY());
        if (isInside(x, y) && onClick != null) {
            AudioController.requestSfxSound(this, SoundType.CLICK);
            onClick.run();
            inputEvent.consume();
        }
    }

    /**
     * Renders the GUI button based on its visual representation, such as rendering the normal or hover state.
     * @see Renderable#render()
     */
    @Override
    public void render() {
        // Rendering
        final Renderer renderer = GameLoop.getInstance().getRenderer();
        final GraphicsContext gc = renderer.getGraphicsContext();
        final CoordinateSystem guics = renderer.getGuiCoordinateSystem();
        // Mouse hovering
        computeHoverState(guics.toWorldX(MouseLocation.getX()), guics.toWorldY(MouseLocation.getY()));

        // Positioning
        final double screenX = guics.toScreenX(this.x);
        final double screenY = guics.toScreenY(this.y);
        final double screenWidth = guics.getScale() * this.width;
        final double screenHeight = guics.getScale() * this.height;

        // Render task
        renderer.addRenderTask(new RenderTask(RenderPriority.GUI_HIGH, () -> {
            if (hovered && hoverSprite == null) {
                gc.drawImage(normalSprite, screenX, screenY, screenWidth, screenHeight);
                final Paint previousFill = gc.getFill();
                final double alpha = 0.2;
                gc.setFill(Color.rgb(10, 10, 10, alpha));
                gc.fillRect(screenX, screenY, screenWidth, screenHeight);
                gc.setFill(previousFill);
            } else {
                gc.drawImage(hovered ? hoverSprite : normalSprite, screenX, screenY, screenWidth, screenHeight);
            }
        }));
    }

    /**
     * Determines if a given point is inside the bounds of the object.
     *
     * @param x The x-coordinate of the point to be checked.
     * @param y The y-coordinate of the point to be checked.
     * @return true if the point specified by the x and y coordinates is within the bounds; false otherwise.
     */
    protected boolean isInside(final double x, final double y) {
        if (x < this.x || x > this.x + width) {
            return false;
        }
        return y >= this.y && y <= this.y + height;
    }

    /**
     * Updates the hover state of the button based on the specified mouse coordinates.
     * If the mouse enters the button's area, the onMouseEnter action is executed.
     * If the mouse leaves the button's area, the onMouseLeave action is executed.
     * The state of "hovered" and "wasHovered" is updated accordingly.
     *
     * @param mouseGuiX The x-coordinate of the mouse in GUI coordinates.
     * @param mouseGuiY The y-coordinate of the mouse in GUI coordinates.
     */
    protected void computeHoverState(final double mouseGuiX, final double mouseGuiY) {
        hovered = isInside(mouseGuiX, mouseGuiY);
        if (hovered && !wasHovered && onMouseEnter != null) {
            onMouseEnter.run();
        }
        if (wasHovered && !hovered && onMouseLeave != null) {
            onMouseLeave.run();
        }
        wasHovered = hovered;
    }

    /**
     * Returns the x-coordinate of the top-left corner of the graphical button in the GUI coordinate system.
     * @return the x-coordinate of the top-left corner of the graphical button in the GUI coordinate system.
     */
    public final double getX() {
        return x;
    }

    /**
     * Returns the top-left y-coordinate of the button in the GUI coordinate system.
     * @return the top-left y-coordinate of the button in the GUI coordinate system.
     */
    public final double getY() {
        return y;
    }

    /**
     * Returns the width of the GUI button in the GUI coordinate system.
     * @return the width of the GUI button in the GUI coordinate system.
     */
    public final double getWidth() {
        return width;
    }

    /**
     * Returns the height of the {@code GuiButton} in the GUI coordinate system.
     * @return the height of the {@code GuiButton} in the GUI coordinate system.
     */
    public final double getHeight() {
        return height;
    }

    /**
     * Returns the action to be executed when the button is clicked.
     * @return the action to be executed when the button is clicked.
     */
    public final Runnable getOnClick() {
        return onClick;
    }

    /**
     * Returns the action to be executed when the button enters the bounds of the {@code GuiButton}.
     * @return the action to be executed when the button enters the bounds of the {@code GuiButton}.
     */
    public final Runnable getOnMouseEnter() {
        return onMouseEnter;
    }

    /**
     * Returns the action to be executed when the mouse leaves the bounds of the {@code GuiButton}.
     * @return the action to be executed when the mouse leaves the bounds of the {@code GuiButton}.
     */
    public final Runnable getOnMouseLeave() {
        return onMouseLeave;
    }
}
