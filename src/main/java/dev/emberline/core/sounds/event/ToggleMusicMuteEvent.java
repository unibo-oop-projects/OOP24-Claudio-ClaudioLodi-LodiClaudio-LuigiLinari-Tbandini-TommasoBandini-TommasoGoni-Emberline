package dev.emberline.core.sounds.event;

import java.io.Serial;

/**
 * Event representing a request to toggle the mute state of the music.
 * Carries a boolean flag indicating whether the music is muted or not.
 */
public class ToggleMusicMuteEvent extends SoundEvent  {
    @Serial
    private static final long serialVersionUID = 4340934494495651830L;
    private final boolean isMuted;

    /**
     * Constructs a new ToggleMusicMuteEvent with the specified source and mute state.
     * @param source the source of the event, typically the object that triggered it
     * @param isMuted the boolean flag indicating whether the music is muted or not
     */
    public ToggleMusicMuteEvent(final Object source, final boolean isMuted) {
        super(source);
        this.isMuted = isMuted;
    }

    /**
     * Retrieves the {@code isMuted} flag attached to the {@code SfxSoundEvent}.
     * @return the {@code isMuted} flag attached to the {@code SfxSoundEvent}.
     */
    public boolean isMuted() {
        return isMuted;
    }
}
