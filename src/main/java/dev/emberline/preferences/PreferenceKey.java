package dev.emberline.preferences;

/**
 * Enum representing user preference keys and their default values.
 * Each key is associated with either a default {@code Double} or {@code Boolean} value.
 */
public enum PreferenceKey {

    /** 
     * Default music volume (range: 0.0–1.0). 
     */
    MUSIC_VOLUME("musicVolume", 0.5),
    /**
     * Default music mute state.
     */
    MUSIC_MUTE("musicMute", false),
    /**
     * Default sound effects (SFX) volume (range: 0.0–1.0).
     */
    SFX_VOLUME("sfxVolume", 0.5),
    /**
     * Default sound effects (SFX) mute state.
     */
    SFX_MUTE("sfxMute", false),
    /** 
     * Whether the game starts in fullscreen mode by default. 
     */
    FULLSCREEN("fullscreen", false);

    private final String key;
    private final Double doubleDefaultValue;
    private final Boolean booleanDefaultValue;

    PreferenceKey(final String key, final Double doubleDefaultValue) {
        this.key = key;
        this.booleanDefaultValue = null;
        this.doubleDefaultValue = doubleDefaultValue;
    }

    PreferenceKey(final String key, final Boolean booleanDefaultValue) {
        this.key = key;
        this.doubleDefaultValue = null;
        this.booleanDefaultValue = booleanDefaultValue;
    }

    /**
     * Returns the string identifier for this preference.
     * @return the key as a {@code String}
     */
    public String getKey() {
        return key;
    }

    /**
     * Returns the default boolean value for this preference, if applicable.
     * @return the default {@code Boolean}, or {@code null} if not applicable
     */
    // Since we are retrieving a default value, having "getDefaultBooleanValue" seems more appropriate than
    // "isDefaultBooleanValue"
    @SuppressWarnings("PMD.BooleanGetMethodName")
    public Boolean getDefaultBooleanValue() {
        return booleanDefaultValue;
    }

    /**
     * Returns the default double value for this preference, if applicable.
     * @return the default {@code Double}, or {@code null} if not applicable
     */
    public Double getDefaultDoubleValue() {
        return doubleDefaultValue;
    } 

}
