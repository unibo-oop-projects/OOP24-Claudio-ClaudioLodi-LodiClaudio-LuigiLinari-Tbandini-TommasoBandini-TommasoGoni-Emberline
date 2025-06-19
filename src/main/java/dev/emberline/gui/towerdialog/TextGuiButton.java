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

public class TextGuiButton extends GuiButton {
    private final String labelText;
    private final TextLayout textLayout;

    private static class TextLayout {
        @JsonProperty("textWidthRatio")
        private double textWidthRatio;
        @JsonProperty("textHeightRatio")
        private double textHeightRatio;
        @JsonProperty("textXOffset")
        private double textXOffset;
        @JsonProperty("textYPosition")
        private double textYPosition;
    }

    public enum TextLayoutType {
        LEFT,
        RIGHT,
        TOP,
        BOTTOM,
        CENTER;
    }

    public static TextLayout loadLayout(TextLayoutType type) {
        JsonNode root = ConfigLoader.loadConfig("/sprites/ui/buttonTextLayout.json");
        JsonNode sublayout = root.get(type.name().toLowerCase());
        return ConfigLoader.loadConfig(sublayout, TextLayout.class);
    }

    public TextGuiButton(double x, double y, double width, double height, Image normalSprite, Image hoverSprite, String labelText, TextLayoutType textLayout) {
        super(x, y, width, height, normalSprite, hoverSprite);
        this.labelText = labelText;
        this.textLayout = loadLayout(textLayout);
    }

    public TextGuiButton(double x, double y, double width, double height, Image normalSprite, String labelText, TextLayoutType textLayout) {
        super(x, y, width, height, normalSprite);
        this.labelText = labelText;
        this.textLayout = loadLayout(textLayout);
    }

    @Override
    public void render() {
        super.render();
        Renderer renderer = GameLoop.getInstance().getRenderer();
        renderer.addRenderTask(new RenderTask(RenderPriority.GUI_HIGH, () -> drawText(renderer)));
    }

    private void drawText(Renderer renderer) {
        if (labelText == null || labelText.isEmpty()) return;

        Image textImage = SpriteLoader.loadSprite(new StringSpriteKey(labelText)).getImage();

        double textWidth = this.width * textLayout.textWidthRatio;
        double textHeight = this.height * textLayout.textHeightRatio;
        double textX = this.x + (this.width - textWidth) * textLayout.textXOffset;
        double textY = this.y + (this.height - textHeight) * textLayout.textYPosition;

        Renderer.drawImageFitCenter(textImage, renderer.getGraphicsContext(), renderer.getGuiCoordinateSystem(), textX, textY, textWidth, textHeight);
    }

}
