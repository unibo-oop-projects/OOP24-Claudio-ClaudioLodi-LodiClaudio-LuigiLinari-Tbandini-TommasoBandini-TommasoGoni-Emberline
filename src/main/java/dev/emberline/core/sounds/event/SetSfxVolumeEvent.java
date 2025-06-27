package dev.emberline.core.sounds.event;

public class SetSfxVolumeEvent extends SoundEvent {

    private final double volume;

    public SetSfxVolumeEvent(final Object source, final double volume) {
        super(source);
        this.volume = volume;
    }

    public double getVolume() {
        return volume;
    }
}
