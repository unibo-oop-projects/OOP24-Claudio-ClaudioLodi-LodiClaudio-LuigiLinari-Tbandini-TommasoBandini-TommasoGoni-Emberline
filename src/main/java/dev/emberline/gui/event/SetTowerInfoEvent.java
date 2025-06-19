package dev.emberline.gui.event;

import dev.emberline.game.model.UpgradableInfo;
import dev.emberline.game.model.UpgradableInfo.InfoType;
import dev.emberline.game.world.towers.tower.Tower;

public class SetTowerInfoEvent extends GuiEvent {

    private final Tower tower;
    private final UpgradableInfo<?, ?> upgradableInfo;
    private final InfoType type;

    public <T extends InfoType, S extends UpgradableInfo<T, S>> SetTowerInfoEvent(Object source, Tower tower, UpgradableInfo<T,S> upgradableInfo, T type) {
        super(source);
        this.tower = tower;
        this.upgradableInfo = upgradableInfo;
        this.type = type;
    }

    public Tower getTower() {
        return tower;
    }

    public UpgradableInfo<?, ?> getUpgradableInfo() {
        return upgradableInfo;
    }

    public InfoType getType() {
        return type;
    }
}
