package dev.emberline.game.world;

import dev.emberline.game.model.EnchantmentInfo;
import dev.emberline.game.model.ProjectileInfo;
import dev.emberline.game.model.TowerInfoProvider;
import dev.emberline.game.model.UpgradableInfo;
import dev.emberline.gui.event.*;

public class Tower implements TowerInfoProvider, GuiEventListener {
    private ProjectileInfo projectileInfo = new ProjectileInfo(ProjectileInfo.Type.BASE, 0);
    private EnchantmentInfo enchantmentInfo = new EnchantmentInfo(EnchantmentInfo.Type.BASE, 0);

    @Override
    public ProjectileInfo getProjectileInfo() {
        return projectileInfo;
    }

    @Override
    public EnchantmentInfo getEnchantmentInfo() {
        return enchantmentInfo;
    }

    @Override
    public void onGuiEvent(GuiEvent event) {
        if (event instanceof UpgradeTowerInfoEvent) {
            handleUpgradeEvent((UpgradeTowerInfoEvent) event);
        } else if (event instanceof ResetTowerInfoEvent) {
            handleResetEvent((ResetTowerInfoEvent) event);
        } else if (event instanceof SetTowerInfoEvent) {
            handleSetEvent((SetTowerInfoEvent) event);
        }
    }

    private void handleUpgradeEvent(UpgradeTowerInfoEvent event) {
        UpgradableInfo<?,?> info = event.getUpgradableInfo();
        if (!info.canUpgrade()) return;
        if (info instanceof ProjectileInfo) {
            projectileInfo = (ProjectileInfo) info.getUpgrade();
        } else if (info instanceof EnchantmentInfo) {
            enchantmentInfo = (EnchantmentInfo) info.getUpgrade();
        }
    }

    private void handleResetEvent(ResetTowerInfoEvent event) {
        UpgradableInfo<?,?> info = event.getUpgradableInfo();
        if (info instanceof ProjectileInfo) {
            projectileInfo = new ProjectileInfo(ProjectileInfo.Type.BASE, 0);
        } else if (info instanceof EnchantmentInfo) {
            enchantmentInfo = new EnchantmentInfo(EnchantmentInfo.Type.BASE, 0);
        }
    }

    private void handleSetEvent(SetTowerInfoEvent event) {
        UpgradableInfo<?,?> info = event.getUpgradableInfo();
        if (!info.canChangeType()) return;
        if (info instanceof ProjectileInfo) {
            projectileInfo = new ProjectileInfo((ProjectileInfo.Type) event.getType(), 0);
        } else if (info instanceof EnchantmentInfo) {
            enchantmentInfo = new EnchantmentInfo((EnchantmentInfo.Type) event.getType(), 0);
        }
    }
}
