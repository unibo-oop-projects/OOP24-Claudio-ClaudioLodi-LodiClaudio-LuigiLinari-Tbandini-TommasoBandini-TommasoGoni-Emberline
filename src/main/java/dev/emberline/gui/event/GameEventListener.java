package dev.emberline.gui.event;

import java.util.EventListener;

/**
 * The GuiEventListener interface should be implemented by any class
 * that wishes to respond to GUI-based events.
 * Classes implementing this interface must provide an implementation
 * for the onGuiEvent method, which will be invoked whenever a GUI event occurs.
 */
public interface GameEventListener extends EventListener {
    void onGameEvent(GameEvent event);
}
