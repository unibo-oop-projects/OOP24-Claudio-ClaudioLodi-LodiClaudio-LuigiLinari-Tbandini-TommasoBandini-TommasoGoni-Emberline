package dev.emberline.preferences;

import java.util.prefs.Preferences;

public final class PreferencesManager {

    private static final Preferences PREFS = Preferences.userRoot().node("dev.emberline.preferences");

    private PreferencesManager() {
    }

    public static Double getDoublePreference(final PreferenceKey key) {
        final double value = PREFS.getDouble(key.getKey(), key.getDefaultDoubleValue());
        if (value == key.getDefaultDoubleValue()) {
            PREFS.putDouble(key.getKey(), value);
        }
        return value;
    }

    // Since we are retrieving a preference, having "getBooleanPreference" seems more appropriate than
    // "isBooleanPreference"
    @SuppressWarnings("PMD.BooleanGetMethodName")
    public static Boolean getBooleanPreference(final PreferenceKey key) {
        final boolean value = PREFS.getBoolean(key.getKey(), key.getDefaultBooleanValue());
        if (value == key.getDefaultBooleanValue()) {
            PREFS.putBoolean(key.getKey(), value);
        }
        return value;
    }

    public static void setDoublePreference(final PreferenceKey key, final Double value) {
        if (value == null) {
            throw new IllegalArgumentException("Value cannot be null");
        }
        PREFS.putDouble(key.getKey(), value);
    }

    public static void setBooleanPreference(final PreferenceKey key, final Boolean value) {
        if (value == null) {
            throw new IllegalArgumentException("Value cannot be null");
        }
        PREFS.putBoolean(key.getKey(), value);
    }
}
