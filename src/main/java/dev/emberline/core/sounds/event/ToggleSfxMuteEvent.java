package dev.emberline.core.sounds.event;

import java.io.Serial;

public class ToggleSfxMuteEvent extends SoundEvent {

    @Serial
    private static final long serialVersionUID = 1271668921317526304L;

    public ToggleSfxMuteEvent(final Object source) {
        super(source);
    }
}
