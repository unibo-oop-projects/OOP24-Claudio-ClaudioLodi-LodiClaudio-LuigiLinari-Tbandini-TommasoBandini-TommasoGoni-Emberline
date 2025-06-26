package dev.emberline.core.sounds.event;

public class SetVolumeEvent extends SoundEvent {

    private final double volume;

    public SetVolumeEvent(final Object source, final double volume) {
        super(source);
        this.volume = volume;
    }

    public double getVolume() {
        return volume;
    }
}
