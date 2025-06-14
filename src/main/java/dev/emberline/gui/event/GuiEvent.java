package dev.emberline.gui.event;

import java.util.EventObject;

public abstract class GuiEvent extends EventObject {
    protected GuiEvent(Object source) {
        super(source);
    }
}
