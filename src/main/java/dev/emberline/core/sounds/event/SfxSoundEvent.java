package dev.emberline.core.sounds.event;

import java.io.Serial;

/**
 * Event representing a request to play a specific sound effect (SFX).
 * Carries the {@link SoundType} indicating which sound to play.
 */
public class SfxSoundEvent extends SoundEvent {

    @Serial
    private static final long serialVersionUID = -7223011141027816914L;
    private final SoundType soundType;

    /**
     * Enum representing the different types of sound effects that can be played.
     */
    public enum SoundType {
        /**
         * Sound effect for every button click.
         */
        CLICK,
        /**
         * Sound effect for building a tower.
         */
        BUILD,
        /**
         * Sound effect for a projectile landing on the ground.
         */
        PROJECTILE_LANDED,
        /**
         * Sound effect when an enemy enters the castle.
         */
        ENEMY_ENTERED_CASTLE,
        /**
         * Sound effect for opening a tower dialog whether it is new or existing.
         */
        OPEN_DIALOG_CHAINS
    }

    /**
     * Constructs a new SfxSoundEvent with the specified source and sound type.
     * @param source the source of the event, typically the object that triggered it
     * @param soundType the type of sound effect to be played
     */
    public SfxSoundEvent(final Object source, final SoundType soundType) {
        super(source);
        this.soundType = soundType;
    }

    /**
     * Returns the type of sound effect associated with this event.
     * @return the {@link SoundType} of the sound effect.
     */
    public SoundType getSoundType() {
        return soundType;
    }

}
