package dev.emberline.preferences;

public enum PreferenceKey {

    MUSIC_VOLUME("musicVolume", 0.5),
    MUSIC_MUTE("musicMute", false),
    SFX_VOLUME("sfxVolume", 0.5),
    SFX_MUTE("sfxMute", false),
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

    public String getKey() {
        return key;
    }

    public Boolean getDefaultBooleanValue() {
        return booleanDefaultValue;
    } 

    public Double getDefaultDoubleValue() {
        return doubleDefaultValue;
    } 

}
