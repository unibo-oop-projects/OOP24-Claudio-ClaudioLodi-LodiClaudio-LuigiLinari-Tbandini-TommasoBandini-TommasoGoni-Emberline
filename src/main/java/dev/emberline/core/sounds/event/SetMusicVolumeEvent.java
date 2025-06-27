package dev.emberline.core.sounds.event;

public class SetMusicVolumeEvent extends SoundEvent {

    private final double volume;

    public SetMusicVolumeEvent(final Object source, final double volume) {
        super(source);
        this.volume = volume;
    }

    public double getVolume() {
        return volume;
    }
}
