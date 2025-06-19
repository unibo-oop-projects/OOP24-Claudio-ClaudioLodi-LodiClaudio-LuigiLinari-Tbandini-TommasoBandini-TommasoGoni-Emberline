package dev.emberline.game.world.towers.tower;

import com.fasterxml.jackson.annotation.JsonProperty;
import dev.emberline.core.ConfigLoader;
import dev.emberline.game.model.EnchantmentInfo;
import dev.emberline.game.model.ProjectileInfo;
import dev.emberline.game.model.TowerInfoProvider;
import dev.emberline.game.model.UpgradableInfo;
import dev.emberline.game.world.Building;
import dev.emberline.game.world.towers.TowersManager;
import dev.emberline.gui.event.*;
import dev.emberline.utility.Vector2D;

import java.util.Map;

public class Tower extends Building implements TowerInfoProvider, GuiEventListener {
    private static String configsPath = "/sprites/towerAssets/tower.json";
    private static class Metadata {
        @JsonProperty("width")
        private double worldWidth;
        @JsonProperty("height")
        private Map<ProjectileInfo.Type, Double> worldHeight;
    }
    private static Metadata metadata = ConfigLoader.loadConfig(ConfigLoader.loadNode(configsPath).get("worldDimensions"), Metadata.class);

    private ProjectileInfo projectileInfo = new ProjectileInfo(ProjectileInfo.Type.BASE, 0);
    private EnchantmentInfo enchantmentInfo = new EnchantmentInfo(EnchantmentInfo.Type.BASE, 0);

    private final TowersManager towersManager;
    private final Vector2D locationBottomLeft;

    private final TowerRenderComponent towerRenderComponent = new TowerRenderComponent(this);

    public Tower(TowersManager towersManager, Vector2D locationBottomLeft) {
        this.towersManager = towersManager;
        this.locationBottomLeft = locationBottomLeft;
    }

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

    double getWorldWidth() {
        return metadata.worldWidth;
    }

    double getWorldHeight() {
        return metadata.worldHeight.get(projectileInfo.type());
    }

    @Override
    protected Vector2D getWorldTopLeft() {
        return locationBottomLeft.subtract(0, getWorldHeight());
    }

    @Override
    protected Vector2D getWorldBottomRight() {
        return locationBottomLeft.add(getWorldWidth(), 0);
    }

    @Override
    protected void clicked() {
        towersManager.openTowerDialog(this);
        //TODO
    }

    @Override
    public void render() {
        towerRenderComponent.render();
    }

    @Override
    public void update(long elapsed) {
        //TODO
    }
}
