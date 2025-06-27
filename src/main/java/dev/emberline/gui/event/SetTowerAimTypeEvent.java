package dev.emberline.gui.event;

/**
 * Represents an event triggered to set the aim preference of a tower in the game.
 * <p>
 * This event is used within the GUI framework to modify the targeting behavior
 * of a specific tower. It encapsulates the desired aiming type, allowing for
 * precise control over how a tower selects its targets during gameplay.
 */
public class SetTowerAimTypeEvent extends GuiEvent {

    private final AimType aimType;

    /**
     * Represents the different targeting modes a tower can have for selecting its targets.
     * Each mode defines a distinct strategy for determining which enemy target to prioritize.
     */
    public enum AimType {
        /**
         * Represents the "First" targeting mode in the tower's targeting strategy.
         * The "First" mode prioritizes selecting the enemy closest to its endpoint.
         */
        FIRST("First"),
        /**
         * Represents the "Last" targeting mode in the tower's targeting strategy.
         * The "Last" mode prioritizes selecting the enemy furthest from its endpoint.
         */
        LAST("Last"),
        /**
         * Represents the "Weak" targeting mode in the tower's targeting strategy.
         * The "Weak" mode prioritizes selecting the enemy with the lowest health value.
         */
        WEAK("Weakest"),
        /**
         * Represents the "Strong" targeting mode in the tower's targeting strategy.
         * The "Strong" mode prioritizes selecting the enemy with the highest health value.
         */
        STRONG("Strongest"),
        /**
         * Represents the "Closest" targeting mode in the tower's targeting strategy.
         * The "Closest" mode prioritizes selecting the enemy closest to the tower.
         */
        CLOSE("Closest");

        private final String displayName;

        AimType(final String displayName) {
            this.displayName = displayName;
        }

        /**
         * Returns the display name associated with this instance.
         *
         * @return the display name associated with this instance.
         */
        public String displayName() {
            return displayName;
        }

        /**
         * Cycles to the next targeting mode in the enum in the defined order.
         *
         * @return the next {@code AimType} in the enumeration order.
         */
        public AimType next() {
            return values()[(this.ordinal() + 1) % values().length];
        }
    }

    /**
     * Constructs a new {@code SetTowerAimTypeEvent}.
     *
     * @param source  the object on which the event initially occurred
     * @param aimType the desired targeting mode for the tower
     * @see SetTowerInfoEvent
     */
    public SetTowerAimTypeEvent(final Object source, final AimType aimType) {
        super(source);
        this.aimType = aimType;
    }

    /**
     * Returns the aim type associated with this event.
     *
     * @return the aim type associated with this event.
     */
    public AimType getAimType() {
        return aimType;
    }
}
