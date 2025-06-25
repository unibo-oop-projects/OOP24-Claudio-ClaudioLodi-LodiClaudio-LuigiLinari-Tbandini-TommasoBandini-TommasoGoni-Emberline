package dev.emberline.gui.towerdialog;

import javafx.scene.image.Image;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

/**
 * Represents a GUI button specifically designed to display a price in its label.
 * This class extends the {@code TextGuiButton} and adds functionality to format
 * and display a price as part of the button text.
 */
public class PricingGuiButton extends TextGuiButton {

    /**
     * Constructs a new PricingGuiButton object.
     *
     * @param x the x-coordinate of the button's upper-left corner.
     * @param y the y-coordinate of the button's upper-left corner.
     * @param width the width of the button in pixels.
     * @param height the height of the button in pixels.
     * @param normalSprite the image to be displayed for the button in its normal state.
     * @param hoverSprite the image to be displayed for the button in its hover state.
     * @param price the price value to be formatted and displayed as part of the button label.
     * @param layoutType the layout type for positioning the text on the button (e.g. CENTER, LEFT).
     * @see PricingGuiButton
     */
    public PricingGuiButton(final double x, final double y, final double width, final double height, final Image normalSprite, final Image hoverSprite, final double price, final TextLayoutType layoutType) {
        super(x, y, width, height, normalSprite, hoverSprite, formatPrice(price), layoutType);
    }

    /**
     * Constructs a new PricingGuiButton object.
     * <p>
     * This constructor does not include a hover state for the button.
     *
     * @param x the x-coordinate of the button's upper-left corner.
     * @param y the y-coordinate of the button's upper-left corner.
     * @param width the width of the button in pixels.
     * @param height the height of the button in pixels.
     * @param normalSprite the image to be displayed for the button in its normal state.
     * @param price the price value to be formatted and displayed as part of the button label.
     * @param layoutType the layout type for positioning the text on the button (e.g. CENTER, LEFT).
     * @see PricingGuiButton
     */
    public PricingGuiButton(final double x, final double y, final double width, final double height, final Image normalSprite, final double price, final TextLayoutType layoutType) {
        super(x, y, width, height, normalSprite, formatPrice(price), layoutType);
    }

    private static String formatPrice(final double price) {
        return new DecimalFormat("+0.##;0.##", DecimalFormatSymbols.getInstance()).format(price) + "$"; // Negative prices won't show a sign, positive prices will show a plus sign
    }
}