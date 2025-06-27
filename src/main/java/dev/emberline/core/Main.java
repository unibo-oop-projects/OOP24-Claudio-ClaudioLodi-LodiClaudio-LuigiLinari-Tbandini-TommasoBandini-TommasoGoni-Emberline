package dev.emberline.core;

import java.text.DecimalFormatSymbols;
import java.util.Locale;

/**
 * The class {@code Main} which defines the entry point of the Emberline application.
 */
public final class Main {

    private Main() {

    }

    /**
     * The entry point of the Emberline application.
     * This method initializes the default locale settings and launches the JavaFX application.
     *
     * @param args the command-line arguments passed to the application.
     */
    public static void main(final String[] args) {
        Locale.setDefault(Locale.US);
        DecimalFormatSymbols.getInstance().setDecimalSeparator('.');
        EmberlineApp.launch(EmberlineApp.class, args);
    }
}
