package dev.emberline.core.sounds.event;

import java.io.Serial;

public class ToggleMusicMuteEvent extends SoundEvent  {
    @Serial
    private static final long serialVersionUID = 4340934494495651830L;
    private final boolean isMuted;

    public ToggleMusicMuteEvent(final Object source, final boolean isMuted) {
        super(source);
        this.isMuted = isMuted;
    }

    public boolean isMuted() {
        return isMuted;
    }
}
