package dev.emberline.gui.event;

import dev.emberline.game.model.UpgradableInfo;
import dev.emberline.game.model.UpgradableInfo.InfoType;
import dev.emberline.game.world.buildings.tower.Tower;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import java.io.Serial;

/**
 * The {@code SetTowerInfoEvent} class represents an event triggered when
 * information about a tower and its upgradable attributes needs to be set or updated.
 * <p>
 * It also contains information about the {@link UpgradableInfo} associated with this event
 */
public class SetTowerInfoEvent extends GuiEvent {

    @Serial
    private static final long serialVersionUID = 519985352478449773L;
    private final Tower tower;
    private final UpgradableInfo<?, ?> upgradableInfo;
    private final InfoType type;

    /**
     * Constructs a new {@code SetTowerInfoEvent} with the specified source, tower, upgradable information, and type.
     *
     * @param <T>           the type of the {@link InfoType} associated with the {@code UpgradableInfo}.
     * @param <S>           the type of the {@link UpgradableInfo} implementation, which is linked to the {@code InfoType}.
     * @param source        the source of the event.
     * @param tower         the {@link Tower} related to this event,
     *                      representing the tower whose information is being set or updated.
     * @param upgradableInfo the {@link UpgradableInfo} instance containing the upgradable attributes of the tower.
     * @param type          the specific {@link InfoType} associated with the {@code UpgradableInfo},
     *                      identifying the type of the upgradable information.
     * @see SetTowerInfoEvent
     */
    @SuppressFBWarnings(
            value = "EI_EXPOSE_REP2",
            justification = "This is intended behavior as this class needs a reference to the tower it is related to."
    )
    public <T extends InfoType, S extends UpgradableInfo<T, S>> SetTowerInfoEvent(final Object source,
    final Tower tower, final UpgradableInfo<T, S> upgradableInfo, final T type) {
        super(source);
        this.tower = tower;
        this.upgradableInfo = upgradableInfo;
        this.type = type;
    }

    /**
     * Returns the {@code Tower} associated with this event.
     *
     * @return the {@code Tower} associated with this event.
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
     * Returns the {@link UpgradableInfo} instance associated with this event.
     *
     * @return the {@link UpgradableInfo} instance associated with this event.
     */
    public UpgradableInfo<?, ?> getUpgradableInfo() {
        return upgradableInfo;
    }

    /**
     * Returns the specific {@link InfoType} associated with the event.
     *
     * @return the specific {@link InfoType} associated with the event.
     */
    public InfoType getType() {
        return type;
    }
}
