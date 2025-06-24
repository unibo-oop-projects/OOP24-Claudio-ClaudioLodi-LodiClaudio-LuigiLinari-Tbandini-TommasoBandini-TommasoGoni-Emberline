package dev.emberline.gui.event;

import dev.emberline.game.model.UpgradableInfo;
import dev.emberline.game.world.buildings.tower.Tower;

public class UpgradeTowerInfoEvent extends GuiEvent {

    private final Tower tower;
    private final UpgradableInfo<?, ?> upgradableInfo;

    public UpgradeTowerInfoEvent(final Object source, final Tower tower, final UpgradableInfo<?, ?> upgradableInfo) {
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
