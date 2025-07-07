package dev.emberline.core.sounds.event;

import java.io.Serial;

/**
 * Event representing a request to toggle the mute state of sound effects (SFX).
 * This event is used to signal that the SFX mute state should be toggled.
 */
public class ToggleSfxMuteEvent extends SoundEvent {

    @Serial
    private static final long serialVersionUID = 1271668921317526304L;

    /**
     * Constructs a new ToggleSfxMuteEvent.
     *
     * @param source the source of the event, typically the object that triggered it
     */
    public ToggleSfxMuteEvent(final Object source) {
        super(source);
    }
}
