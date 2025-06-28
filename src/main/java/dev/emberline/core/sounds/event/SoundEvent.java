package dev.emberline.core.sounds.event;

import java.io.Serial;
import java.util.EventObject;

public abstract class SoundEvent extends EventObject {
    @Serial
    private static final long serialVersionUID = 3350257963807412369L;

    protected SoundEvent(final Object source) {
        super(source);
    }
}
