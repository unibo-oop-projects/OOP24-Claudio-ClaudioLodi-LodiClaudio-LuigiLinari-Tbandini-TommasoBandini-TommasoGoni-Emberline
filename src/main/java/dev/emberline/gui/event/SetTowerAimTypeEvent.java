package dev.emberline.gui.event;

public class SetTowerAimTypeEvent extends GuiEvent {

    private final AimType aimType;

    public enum AimType {
        FIRST("First"),
        LAST("Last"),
        WEAK("Weakest"),
        STRONG("Strongest"),
        CLOSE("Closest");

        private final String displayName;

        AimType(final String displayName) {
            this.displayName = displayName;
        }

        public String displayName() {
            return displayName;
        }

        public AimType next() {
            return values()[(this.ordinal() + 1) % values().length];
        }
    }

    public SetTowerAimTypeEvent(final Object source, final AimType aimType) {
        super(source);
        this.aimType = aimType;
    }

    public AimType getAimType() {
        return aimType;
    }
}
