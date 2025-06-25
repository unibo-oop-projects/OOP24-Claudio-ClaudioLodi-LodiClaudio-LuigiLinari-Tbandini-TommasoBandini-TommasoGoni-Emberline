package dev.emberline.gui.event;

import java.util.EventObject;

public abstract class GameEvent extends EventObject {
    protected GameEvent(final Object source) {
        super(source);
    }
}
