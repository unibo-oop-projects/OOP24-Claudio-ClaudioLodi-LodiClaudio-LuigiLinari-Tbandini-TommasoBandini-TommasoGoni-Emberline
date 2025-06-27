package dev.emberline.gui.towerdialog;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import dev.emberline.core.config.ConfigLoader;
import dev.emberline.core.GameLoop;
import dev.emberline.core.graphics.SpriteLoader;
import dev.emberline.core.graphics.spritekeys.StringSpriteKey;
import dev.emberline.core.render.RenderPriority;
import dev.emberline.core.render.RenderTask;
import dev.emberline.core.render.Renderer;
import dev.emberline.gui.GuiButton;
import javafx.scene.image.Image;

import java.util.Locale;

/**
 * Represents a {@link GuiButton} that supports rendering text within its boundaries.
 * This class extends the {@code GuiButton} to integrate a text layout system for displaying a label on the button.
 * The text layout is customizable based on the specified {@code TextLayoutType}.
 */
public class TextGuiButton extends GuiButton {
    private final String labelText;
    private final TextLayout textLayout;

    private record TextLayout(@JsonProperty double textWidthRatio, @JsonProperty double textHeightRatio,
                              @JsonProperty double textXOffset, @JsonProperty double textYPosition) {
    }

    /**
     * Enumerates the possible layout types for positioning text within a graphical user interface button.
     * The text layout determines where the label text should be rendered relative to the button boundaries.
     * <p>
     * Each layout type corresponds to a specific predefined alignment:
     * <ul>
     * <li>LEFT: Aligns the text to the left side of the button.</li>
     * <li>RIGHT: Aligns the text to the right side of the button.</li>
     * <li>TOP: Positions the text at the top of the button.</li>
     * <li>BOTTOM: Positions the text at the bottom of the button.</li>
     * <li>CENTER: Centers the text within the button.</li>
     * </ul>
     */
    public enum TextLayoutType {
        /**
         * Specifies a text alignment option where the text content
         * is positioned on the left side relative to its container.
         */
        LEFT,
        /**
         * Specifies a text alignment option where the text content
         * is positioned on the right side relative to its container.
         */
        RIGHT,
        /**
         * Specifies a text alignment option where the text content
         * is positioned on the top side relative to its container.
         */
        TOP,
        /**
         * Specifies a text alignment option where the text content
         * is positioned on the bottom side relative to its container.
         */
        BOTTOM,
        /**
         * Specifies a text alignment option where the text content
         * is centered in its container.
         */
        CENTER
    }

    /**
     * Loads a {@link TextLayout} configuration based on the specified {@link TextLayoutType}.
     * The method retrieves the layout configuration from a predefined JSON resource file and
     * deserializes it into a {@code TextLayout} instance using the {@link ConfigLoader}.
     *
     * @param type the {@link TextLayoutType} indicating the desired text layout configuration.
     *             This determines which specific subsection of the JSON resource will be used.
     * @return a {@link TextLayout} instance representing the layout configuration for the given type.
     * @throws RuntimeException if the JSON resource or layout subsection cannot be loaded or parsed.
     */
    public static TextLayout loadLayout(final TextLayoutType type) {
        final JsonNode root = ConfigLoader.loadNode("/sprites/ui/buttonTextLayout.json");
        final JsonNode sublayout = root.get(type.name().toLowerCase(Locale.US));
        return ConfigLoader.loadConfig(sublayout, TextLayout.class);
    }

    /**
     * Creates a new {@code TextGuiButton} with specified position, dimensions, and appearance,
     * as well as a label text and a layout type that determines the positioning of the text
     * within the button.
     *
     * @param x           The x-coordinate of the button's top-left corner.
     * @param y           The y-coordinate of the button's top-left corner.
     * @param width       The width of the button.
     * @param height      The height of the button.
     * @param normalSprite The image to display when the button is in its normal state.
     * @param hoverSprite  The image to display when the button is hovered over.
     * @param labelText   The text to be displayed as the button's label.
     * @param textLayout  The {@link TextLayoutType} determining the positioning of the label text.
     * @see TextGuiButton
     */
    public TextGuiButton(
            final double x, final double y, final double width, final double height,
            final Image normalSprite, final Image hoverSprite, final String labelText, final TextLayoutType textLayout
    ) {
        super(x, y, width, height, normalSprite, hoverSprite);
        this.labelText = labelText;
        this.textLayout = loadLayout(textLayout);
    }

    /**
     * Constructs a new TextGuiButton with the specified position, dimensions, appearance,
     * and text layout for the button's label.
     *
     * @param x           The x-coordinate of the button's top-left corner.
     * @param y           The y-coordinate of the button's top-left corner.
     * @param width       The width of the button.
     * @param height      The height of the button.
     * @param normalSprite The image to display when the button is in its normal state.
     * @param labelText   The text to be displayed as the button's label.
     * @param textLayout  The {@link TextLayoutType} determining the positioning of the label text.
     * @see TextGuiButton
     */
    public TextGuiButton(
            final double x, final double y, final double width, final double height,
            final Image normalSprite, final String labelText, final TextLayoutType textLayout
    ) {
        super(x, y, width, height, normalSprite);
        this.labelText = labelText;
        this.textLayout = loadLayout(textLayout);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void render() {
        super.render();
        final Renderer renderer = GameLoop.getInstance().getRenderer();
        renderer.addRenderTask(new RenderTask(RenderPriority.GUI_HIGH, () -> drawText(renderer)));
    }

    private void drawText(final Renderer renderer) {
        if (labelText == null || labelText.isEmpty()) {
            return;
        }

        final Image textImage = SpriteLoader.loadSprite(new StringSpriteKey(labelText)).image();

        final double textWidth = this.width * textLayout.textWidthRatio;
        final double textHeight = this.height * textLayout.textHeightRatio;
        final double textX = this.x + (this.width - textWidth) * textLayout.textXOffset;
        final double textY = this.y + (this.height - textHeight) * textLayout.textYPosition;

        Renderer.drawImageFitCenter(textImage, renderer.getGraphicsContext(),
                                    renderer.getGuiCoordinateSystem(), textX, textY, textWidth, textHeight);
    }

}
