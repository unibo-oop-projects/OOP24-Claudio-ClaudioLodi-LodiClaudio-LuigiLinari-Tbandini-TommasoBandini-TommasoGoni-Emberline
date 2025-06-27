package dev.emberline.core.sounds.event;

public class ToggleMusicMuteEvent extends SoundEvent  {
    private final boolean muteState;

    public ToggleMusicMuteEvent(final Object source, final boolean muteState) {
        super(source);
        this.muteState = muteState;
    }

    public boolean getMuteState() {
        return muteState;
    }
}
