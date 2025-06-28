package dev.emberline.core.sounds.event;

import java.io.Serial;

public class RefreshSfxVolumeEvent extends SoundEvent {

    @Serial
    private static final long serialVersionUID = 7318126071849935354L;

    public RefreshSfxVolumeEvent(final Object source) {
        super(source);
    }
}
