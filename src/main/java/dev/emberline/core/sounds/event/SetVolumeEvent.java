package dev.emberline.core.sounds.event;

public class SetVolumeEvent extends SoundEvent {

    private final float volume;

    public SetVolumeEvent(final Object source, final float volume) {
        super(source);
        this.volume = volume;
    }

    public float getVolume() {
        return volume;
    }
}
