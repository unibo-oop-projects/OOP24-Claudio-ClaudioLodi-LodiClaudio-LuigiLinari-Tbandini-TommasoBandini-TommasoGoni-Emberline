package dev.emberline.core.sounds.event;

import java.io.Serial;

public class SetMusicVolumeEvent extends SoundEvent {

    @Serial
    private static final long serialVersionUID = -8564457291124220708L;
    private final double volume;

    public SetMusicVolumeEvent(final Object source, final double volume) {
        super(source);
        this.volume = volume;
    }

    public double getVolume() {
        return volume;
    }
}
