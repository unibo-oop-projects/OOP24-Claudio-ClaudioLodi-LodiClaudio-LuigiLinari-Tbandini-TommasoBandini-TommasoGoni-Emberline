package dev.emberline.core.sounds.event;

public class ToggleSfxMuteEvent extends SoundEvent {
    private final boolean muteState;

    public ToggleSfxMuteEvent(final Object source, final boolean muteState) {
        super(source);
        this.muteState = muteState;
    }

    public boolean getMuteState() {
        return muteState;
    }
}
