package dev.emberline.gui.towerdialog;

import java.text.DecimalFormat;

import javafx.scene.image.Image;

public class PricingGuiButton extends TextGuiButton {

    public PricingGuiButton(double x, double y, double width, double height, Image normalSprite, Image hoverSprite, double price, TextLayoutType layoutType) {
        super(x, y, width, height, normalSprite, hoverSprite, formatPrice(price), layoutType);
    }

    public PricingGuiButton(double x, double y, double width, double height,Image normalSprite, double price, TextLayoutType layoutType) {
        super(x, y, width, height, normalSprite, formatPrice(price), layoutType);
    }

    private static String formatPrice(double price) {
        return new DecimalFormat("+0.##;0.##").format(price) + "$"; // Negative prices won't show a sign, positive prices will show a plus sign
    }
}