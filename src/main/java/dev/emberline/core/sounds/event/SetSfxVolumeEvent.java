package dev.emberline.core.sounds.event;

import java.io.Serial;

public class SetSfxVolumeEvent extends SoundEvent {

    @Serial
    private static final long serialVersionUID = 7318126071849935354L;
    private final double volume;

    public SetSfxVolumeEvent(final Object source, final double volume) {
        super(source);
        this.volume = volume;
    }

    public double getVolume() {
        return volume;
    }
}
