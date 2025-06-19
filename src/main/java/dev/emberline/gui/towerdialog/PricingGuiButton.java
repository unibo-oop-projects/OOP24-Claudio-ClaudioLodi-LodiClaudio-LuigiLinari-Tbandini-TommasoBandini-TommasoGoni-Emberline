package dev.emberline.gui.towerdialog;

import dev.emberline.core.GameLoop;
import dev.emberline.core.graphics.SpriteLoader;
import dev.emberline.core.graphics.spritekeys.StringSpriteKey;
import dev.emberline.core.render.RenderPriority;
import dev.emberline.core.render.RenderTask;
import dev.emberline.core.render.Renderer;
import dev.emberline.gui.GuiButton;
import javafx.scene.image.Image;

import java.text.DecimalFormat;

public class PricingGuiButton extends GuiButton {
    private final double price;
    private static class Layout {
        private static final double PRICE_WIDTH_RATIO = 0.8;
        private static final double PRICE_HEIGHT_RATIO = 0.4;
        private static final double PRICE_X_OFFSET = 0.5;
        private static final double PRICE_Y_POSITION = 0.5;
    }

    public PricingGuiButton(double x, double y, double width, double height, Image normalSprite, Image hoverSprite, double price) {
        super(x, y, width, height, normalSprite, hoverSprite);
        this.price = price;
    }

    public PricingGuiButton(double x, double y, double width, double height, Image sprite, double price) {
        super(x, y, width, height, sprite);
        this.price = price;
    }

    @Override
    public void render() {
        super.render();
        Renderer renderer = GameLoop.getInstance().getRenderer();
        renderer.addRenderTask(new RenderTask(RenderPriority.GUI_HIGH, () -> drawPrice(renderer)));
    }

    private void drawPrice(Renderer renderer) {
        // Draw price
        String priceString = new DecimalFormat("+0.##;0.##").format(price); // Negative prices won't show a sign, positive prices will show a plus sign
        Image pricingImage = SpriteLoader.loadSprite(new StringSpriteKey(priceString + "$")).image();
        double priceWidth = this.width * Layout.PRICE_WIDTH_RATIO;
        double priceHeight = this.height * Layout.PRICE_HEIGHT_RATIO;
        double priceX = this.x + (this.width-priceWidth)*Layout.PRICE_X_OFFSET;
        double priceY = this.y + (this.height-priceHeight)*Layout.PRICE_Y_POSITION;
        Renderer.drawImageFitCenter(pricingImage,renderer.getGraphicsContext(),renderer.getGuiCoordinateSystem(),priceX, priceY, priceWidth, priceHeight);
    }
}
