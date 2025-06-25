package dev.emberline.core.sounds.event;

public class ToggleMuteEvent extends SoundEvent  {

    private final boolean muteState;
    
    public ToggleMuteEvent(final Object source, final boolean muteState) {
        super(source);
        this.muteState = muteState;
    }

    public boolean getMuteState() {
        return muteState;
    }
}
