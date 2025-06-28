package dev.emberline.core.sounds.event;

import java.io.Serial;

public class SfxSoundEvent extends SoundEvent {

    @Serial
    private static final long serialVersionUID = -7223011141027816914L;
    private final SoundType soundType;

    public enum SoundType {
        CLICK,
        BUILD,
        PROJECTILE_LANDED,
        ENEMY_ENTERED_CASTLE,
        OPEN_DIALOG_CHAINS
    }

    public SfxSoundEvent(final Object source, final SoundType soundType) {
        super(source);
        this.soundType = soundType;
    }

    /**
     * Retrieves the sound type attached to the {@code SfxSoundEvent}.
     * @return a double representing the sound type attached to the {@code SfxSoundEvent}.
     */
    public SoundType getSoundType() {
        return soundType;
    }

}
