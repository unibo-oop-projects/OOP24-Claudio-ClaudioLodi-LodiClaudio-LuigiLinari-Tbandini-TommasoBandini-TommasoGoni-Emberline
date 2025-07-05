package dev.emberline.gui.event;

import dev.emberline.game.model.UpgradableInfo;
import dev.emberline.game.world.buildings.tower.Tower;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import java.io.Serial;

/**
 * Represents an event triggered during the process of upgrading a tower in the game's user interface.
 * This event provides information about the tower being upgraded and the upgrade details.
 */
public class UpgradeTowerInfoEvent extends GuiEvent {

    @Serial
    private static final long serialVersionUID = 300873193545990018L;
    private final Tower tower;
    private final UpgradableInfo<?, ?> upgradableInfo;

    /**
     * Constructs an {@code UpgradeTowerInfoEvent}.
     *
     * @param source the object on which the event initially occurred
     * @param tower the {@code Tower} instance that is being upgraded
     * @param upgradableInfo the {@code UpgradableInfo} providing details about the upgrade being applied to the tower
     * @see UpgradeTowerInfoEvent
     */
    @SuppressFBWarnings(
            value = "EI_EXPOSE_REP",
            justification = "This is intended behavior as this class needs a reference to the tower it is related to."
    )
    public UpgradeTowerInfoEvent(final Object source, final Tower tower, final UpgradableInfo<?, ?> upgradableInfo) {
        super(source);
        this.tower = tower;
        this.upgradableInfo = upgradableInfo;
    }

    /**
     * Returns the tower associated with this event.
     *
     * @return the tower associated with this event.
     */
    @SuppressFBWarnings(
            value = "EI_EXPOSE_REP",
            justification = "When this method is called, "
                    + "it should return the reference to the Tower this class is related to."
    )
    public Tower getTower() {
        return tower;
    }

    /**
     * Returns the {@code UpgradableInfo} instance associated with this event.
     *
     * @return the {@code UpgradableInfo} instance associated with this event.
     */
    public UpgradableInfo<?, ?> getUpgradableInfo() {
        return upgradableInfo;
    }
}
