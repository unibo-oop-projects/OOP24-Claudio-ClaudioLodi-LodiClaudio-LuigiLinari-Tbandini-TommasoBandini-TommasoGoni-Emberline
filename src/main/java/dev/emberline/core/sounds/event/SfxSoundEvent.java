package dev.emberline.core.sounds.event;

public class SfxSoundEvent extends SoundEvent {

    private final SoundType soundType;

    public enum SoundType {
        CLICK,
        BUILD,
        PROJECTILE_LANDED,
        ENEMY_ENTERED_CASTLE
    }

    public SfxSoundEvent(final Object source, SoundType soundType) {
        super(source);
        this.soundType = soundType;
    }

    public SoundType getSoundType() {
        return soundType;
    }

}
