package dev.emberline.gui.event;

import dev.emberline.game.model.UpgradableInfo;
import dev.emberline.game.world.buildings.tower.Tower;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import java.io.Serial;

/**
 * The {@code ResetTowerInfoEvent} class represents an event triggered when the upgradable information
 * associated with a tower needs to be reset. This event enables the resetting of a tower's current projectile
 * or enchantment information to its default state.
 */
public class ResetTowerInfoEvent extends GuiEvent {

    @Serial
    private static final long serialVersionUID = -1270204394588734835L;
    private final Tower tower;
    private final UpgradableInfo<?, ?> upgradableInfo;

    /**
     * Constructs a new {@code ResetTowerInfoEvent}.
     *
     * @param source         the source of the event
     * @param tower          the {@code Tower} object associated with this event, whose upgradable information is to be reset
     * @param upgradableInfo the {@code UpgradableInfo} associated with the tower that needs to be reset
     * @see ResetTowerInfoEvent
     */
    @SuppressFBWarnings(
            value = "EI_EXPOSE_REP2",
            justification = "We need the actual reference to the tower for linking the event "
                            + "to the tower itself, making a copy would defeat the purpose."
    )
    public ResetTowerInfoEvent(final Object source, final Tower tower, final UpgradableInfo<?, ?> upgradableInfo) {
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
            justification = "We need to retrive the tower reference attached to this event"
    )
    public Tower getTower() {
        return tower;
    }

    /**
     * Returns the upgradable information associated with the tower.
     *
     * @return the upgradable information associated with the tower.
     */
    public UpgradableInfo<?, ?> getUpgradableInfo() {
        return upgradableInfo;
    }
}
