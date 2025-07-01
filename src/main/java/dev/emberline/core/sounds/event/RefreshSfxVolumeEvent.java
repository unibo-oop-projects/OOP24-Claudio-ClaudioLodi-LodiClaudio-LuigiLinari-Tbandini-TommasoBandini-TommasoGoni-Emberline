package dev.emberline.core.sounds.event;

import java.io.Serial;

/**
 * Event used to trigger a refresh of the SFX volume settings.
 * Typically dispatched when volume preferences are updated.
 */
public class RefreshSfxVolumeEvent extends SoundEvent {

    @Serial
    private static final long serialVersionUID = 7318126071849935354L;

    /**
     * Constructs a new RefreshSfxVolumeEvent.
     *
     * @param source the source of the event, typically the object that triggered the volume refresh.
     */
    public RefreshSfxVolumeEvent(final Object source) {
        super(source);
    }
}
