package dev.emberline.gui.event;

import dev.emberline.game.world.buildings.tower.Tower;
import dev.emberline.game.world.buildings.tower.aimstrategy.AimStrategy;
import dev.emberline.game.world.buildings.tower.aimstrategy.concrete.CloseAimStrategy;
import dev.emberline.game.world.buildings.tower.aimstrategy.concrete.FirstAimStrategy;
import dev.emberline.game.world.buildings.tower.aimstrategy.concrete.LastAimStrategy;
import dev.emberline.game.world.buildings.tower.aimstrategy.concrete.StrongAimStrategy;
import dev.emberline.game.world.buildings.tower.aimstrategy.concrete.WeakAimStrategy;

import java.io.Serial;

/**
 * Represents an event triggered to set the aim preference of a tower in the game.
 * <p>
 * This event is used within the GUI framework to modify the targeting behavior
 * of a specific tower. It encapsulates the desired aiming type, allowing for
 * precise control over how a tower selects its targets during gameplay.
 */
public class SetTowerAimTypeEvent extends GuiEvent {
    @Serial
    private static final long serialVersionUID = -4695684923846091262L;

    private final AimType aimType;
    private final Tower tower;

    /**
     * Represents the different targeting modes a tower can have for selecting its targets.
     * Each mode defines a distinct strategy for determining which enemy target to prioritize.
     */
    public enum AimType {
        FIRST("First", new FirstAimStrategy()),
        LAST("Last", new LastAimStrategy()),
        WEAK("Weakest", new WeakAimStrategy()),
        STRONG("Strongest", new StrongAimStrategy()),
        CLOSE("Closest", new CloseAimStrategy()),;

        private final String displayName;
        private final AimStrategy aimStrategy;

        AimType(final String displayName, AimStrategy aimStrategy) {
            this.displayName = displayName;
            this.aimStrategy = aimStrategy;
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

        /**
         * Returns an instance of the appropriate {@link AimStrategy}.
         *
         * @return the instance of the appropriate {@link AimStrategy}.
         */
        public AimStrategy getAimStrategy() {
            return aimStrategy;
        }
    }

    /**
     * Constructs a new {@code SetTowerAimTypeEvent}.
     *
     * @param source  the object on which the event initially occurred
     * @param aimType the desired targeting mode for the tower
     * @see SetTowerInfoEvent
     */
    public SetTowerAimTypeEvent(final Object source, final Tower tower, final AimType aimType) {
        super(source);
        this.tower = tower;
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

    /**
     * Returns the tower associated with this event.
     *
     * @return the tower associated with this event.
     */
    public Tower getTower() {
        return tower;
    }
}
