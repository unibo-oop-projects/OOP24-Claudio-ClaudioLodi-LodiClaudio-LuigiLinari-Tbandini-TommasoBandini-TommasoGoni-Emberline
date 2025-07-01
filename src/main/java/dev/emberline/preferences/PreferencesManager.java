package dev.emberline.preferences;

import java.util.prefs.Preferences;

/**
 * Utility class for accessing and modifying user preferences.
 * <p>
 * Stores and retrieves preference values using {@link java.util.prefs.Preferences}.
 * All preferences are mapped through {@link PreferenceKey}.
 * </p>
 */
public final class PreferencesManager {

    private static final Preferences PREFS = Preferences.userRoot().node("dev.emberline.preferences");

    private PreferencesManager() {
    }
    /**
     * Retrieves the string identifier for a given preference key.
     *
     * @param key the preference key
     * @return the string identifier for the preference
     */
    public static Double getDoublePreference(final PreferenceKey key) {
        final double doubleComparisonTolerance = 0.0001;
        final double value = PREFS.getDouble(key.getKey(), key.getDefaultDoubleValue());
        if (Math.abs(value - key.getDefaultDoubleValue()) < doubleComparisonTolerance) {
            PREFS.putDouble(key.getKey(), value);
        }
        return value;
    }

    /**
     * Retrieves the boolean value for a given preference key.
     *
     * @param key the preference key
     * @return the boolean value for the preference
     */
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

    /**
     * Sets a double preference value.
     *
     * @param key   the preference key to update
     * @param value the new double value (cannot be {@code null})
     * @throws IllegalArgumentException if value is {@code null}
     */
    public static void setDoublePreference(final PreferenceKey key, final Double value) {
        if (value == null) {
            throw new IllegalArgumentException("Value cannot be null");
        }
        PREFS.putDouble(key.getKey(), value);
    }

    /**
     * Sets a boolean preference value.
     *
     * @param key   the preference key to update
     * @param value the new boolean value (cannot be {@code null})
     * @throws IllegalArgumentException if value is {@code null}
     */
    public static void setBooleanPreference(final PreferenceKey key, final Boolean value) {
        if (value == null) {
            throw new IllegalArgumentException("Value cannot be null");
        }
        PREFS.putBoolean(key.getKey(), value);
    }
}
