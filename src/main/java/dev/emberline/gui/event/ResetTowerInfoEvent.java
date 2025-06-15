package dev.emberline.gui.event;

import dev.emberline.game.model.UpgradableInfo;
import dev.emberline.game.world.Tower;

public class ResetTowerInfoEvent extends GuiEvent {

    private final Tower tower;
    private final UpgradableInfo<?, ?> upgradableInfo;

    public ResetTowerInfoEvent(Object source, Tower tower, UpgradableInfo<?, ?> upgradableInfo) {
        super(source);
        this.tower = tower;
        this.upgradableInfo = upgradableInfo;
    }

    public Tower getTower() {
        return tower;
    }

    public UpgradableInfo<?, ?> getUpgradableInfo() {
        return upgradableInfo;
    }
}
