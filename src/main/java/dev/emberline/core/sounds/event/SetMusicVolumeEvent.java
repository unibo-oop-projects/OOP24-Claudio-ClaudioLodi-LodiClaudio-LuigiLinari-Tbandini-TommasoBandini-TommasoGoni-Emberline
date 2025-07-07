package dev.emberline.core.sounds.event;

import java.io.Serial;

/**
 * Event used to set the music volume in the game.
 * This event is typically dispatched when the user changes the music volume settings.
 */
public class SetMusicVolumeEvent extends SoundEvent {

    @Serial
    private static final long serialVersionUID = -8564457291124220708L;
    private final double volume;

    /**
     * Constructs a new {@code SetMusicVolumeEvent} with the specified source and volume.
     *
     * @param source the source of the event, typically the object that triggered the volume change
     * @param volume the new music volume to be set
     */
    public SetMusicVolumeEvent(final Object source, final double volume) {
        super(source);
        this.volume = volume;
    }

    /**
     * Retrieves the volume attached to the {@code SetMusicVolumeEvent}.
     * @return a double representing the volume attached to the {@code SetMusicVolumeEvent}
     */
    public double getVolume() {
        return volume;
    }
}
