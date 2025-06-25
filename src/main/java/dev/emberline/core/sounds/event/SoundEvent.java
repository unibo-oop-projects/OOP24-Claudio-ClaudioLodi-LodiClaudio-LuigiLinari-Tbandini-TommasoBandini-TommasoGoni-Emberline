package dev.emberline.core.sounds.event;

import java.util.EventObject;

public abstract class SoundEvent extends EventObject {
    protected SoundEvent(final Object source) {
        super(source);
    }
}
