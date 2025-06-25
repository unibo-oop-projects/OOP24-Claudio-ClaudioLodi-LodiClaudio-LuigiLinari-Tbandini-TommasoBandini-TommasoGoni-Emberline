package dev.emberline.core;

import java.text.DecimalFormatSymbols;
import java.util.Locale;

public class Main {
    public static void main(final String[] args) {
        Locale.setDefault(Locale.US);
        DecimalFormatSymbols.getInstance().setDecimalSeparator('.');
        EmberlineApp.launch(EmberlineApp.class, args);
    }
}
