package dev.emberline.core.sounds.event;

import java.io.Serial;
import java.util.EventObject;

/**
 * Abstract base class for sound-related events in the Emberline game.
 */
public abstract class SoundEvent extends EventObject {
    @Serial
    private static final long serialVersionUID = 3350257963807412369L;

    /**
     * Constructs a new SoundEvent with the specified source.
     *
     * @param source the source of the event, typically the object that triggered it
     */
    protected SoundEvent(final Object source) {
        super(source);
    }
}
