package dev.emberline.preferences;

import java.util.prefs.Preferences;

public final class PreferencesManager {

    private static Preferences prefs = Preferences.userRoot().node("dev.emberline.preferences");

    private PreferencesManager() {
    }

    public static Double getDoublePreference(final PreferenceKey key) {
        double value = prefs.getDouble(key.getKey(), -1);
        if (value == -1) {
            prefs.putDouble(key.getKey(), value = key.getDefaultDoubleValue());
        }
        return value;
    }

    public static Boolean getBooleanPreference(final PreferenceKey key) {
        boolean value = prefs.getBoolean(key.getKey(), key.getDefaultBooleanValue());
        if (value == key.getDefaultBooleanValue()) {
            prefs.putBoolean(key.getKey(), value = key.getDefaultBooleanValue());

        }
        System.out.println(prefs.get(key.getKey(), "ciao"));
        return value;
    }

    public static void setDoublePreference(final PreferenceKey key, final Double value) {
//        setDoublePreference(key, value);
    }

    public static void setBooleanPreference(final PreferenceKey key, final Boolean value) {
//        setBooleanPreference(key, value);
    }
}
