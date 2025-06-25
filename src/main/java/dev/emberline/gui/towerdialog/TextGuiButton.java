package dev.emberline.gui.towerdialog;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import dev.emberline.core.ConfigLoader;
import dev.emberline.core.GameLoop;
import dev.emberline.core.graphics.SpriteLoader;
import dev.emberline.core.graphics.spritekeys.StringSpriteKey;
import dev.emberline.core.render.RenderPriority;
import dev.emberline.core.render.RenderTask;
import dev.emberline.core.render.Renderer;
import dev.emberline.gui.GuiButton;
import javafx.scene.image.Image;

import java.util.Locale;

public class TextGuiButton extends GuiButton {
    private final String labelText;
    private final TextLayout textLayout;

    private static class TextLayout {
        @JsonProperty
        double textWidthRatio;
        @JsonProperty
        double textHeightRatio;
        @JsonProperty
        double textXOffset;
        @JsonProperty
        double textYPosition;
    }

    public enum TextLayoutType {
        LEFT,
        RIGHT,
        TOP,
        BOTTOM,
        CENTER
    }

    public static TextLayout loadLayout(final TextLayoutType type) {
        final JsonNode root = ConfigLoader.loadNode("/sprites/ui/buttonTextLayout.json");
        final JsonNode sublayout = root.get(type.name().toLowerCase(Locale.US));
        return ConfigLoader.loadConfig(sublayout, TextLayout.class);
    }

    public TextGuiButton(final double x, final double y, final double width, final double height, final Image normalSprite, final Image hoverSprite, final String labelText, final TextLayoutType textLayout) {
        super(x, y, width, height, normalSprite, hoverSprite);
        this.labelText = labelText;
        this.textLayout = loadLayout(textLayout);
    }

    public TextGuiButton(final double x, final double y, final double width, final double height, final Image normalSprite, final String labelText, final TextLayoutType textLayout) {
        super(x, y, width, height, normalSprite);
        this.labelText = labelText;
        this.textLayout = loadLayout(textLayout);
    }

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

        Renderer.drawImageFitCenter(textImage, renderer.getGraphicsContext(), renderer.getGuiCoordinateSystem(), textX, textY, textWidth, textHeight);
    }

}
