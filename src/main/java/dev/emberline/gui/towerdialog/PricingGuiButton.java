package dev.emberline.gui.towerdialog;

import javafx.scene.image.Image;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

public class PricingGuiButton extends TextGuiButton {

    public PricingGuiButton(final double x, final double y, final double width, final double height, final Image normalSprite, final Image hoverSprite, final double price, final TextLayoutType layoutType) {
        super(x, y, width, height, normalSprite, hoverSprite, formatPrice(price), layoutType);
    }

    public PricingGuiButton(final double x, final double y, final double width, final double height, final Image normalSprite, final double price, final TextLayoutType layoutType) {
        super(x, y, width, height, normalSprite, formatPrice(price), layoutType);
    }

    private static String formatPrice(final double price) {
        return new DecimalFormat("+0.##;0.##", DecimalFormatSymbols.getInstance()).format(price) + "$"; // Negative prices won't show a sign, positive prices will show a plus sign
    }
}
