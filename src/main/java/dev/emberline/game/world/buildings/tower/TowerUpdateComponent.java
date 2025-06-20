package dev.emberline.game.world.buildings.tower;

import dev.emberline.core.components.Updatable;
import dev.emberline.game.model.EnchantmentInfo;
import dev.emberline.game.model.ProjectileInfo;
import dev.emberline.game.model.TowerInfoProvider;
import dev.emberline.game.model.UpgradableInfo;
import dev.emberline.game.world.World;
import dev.emberline.game.world.buildings.TowersManager;
import dev.emberline.game.world.entities.enemies.IEnemiesManager;
import dev.emberline.game.world.entities.enemies.enemy.IEnemy;
import dev.emberline.gui.event.GuiEvent;
import dev.emberline.gui.event.GuiEventListener;
import dev.emberline.gui.event.ResetTowerInfoEvent;
import dev.emberline.gui.event.SetTowerInfoEvent;
import dev.emberline.gui.event.UpgradeTowerInfoEvent;
import dev.emberline.utility.Vector2D;

import java.util.List;

class TowerUpdateComponent implements Updatable, GuiEventListener, TowerInfoProvider {

    private ProjectileInfo projectileInfo = new ProjectileInfo(ProjectileInfo.Type.BASE, 0);
    private EnchantmentInfo enchantmentInfo = new EnchantmentInfo(EnchantmentInfo.Type.BASE, 0);
    private final Vector2D locationBottomLeft;

    private long accumulatedTimeNs = 0;

    private final World world;
    private final Tower tower;

    TowerUpdateComponent(Vector2D locationBottomLeft, World world, Tower tower) {
        this.locationBottomLeft = locationBottomLeft;
        this.world = world;
        this.tower = tower;
    }

    @Override
    public void update(long elapsed) {
        long shootingInterval = (long) (1e9 / projectileInfo.getFireRate());

        accumulatedTimeNs += elapsed;
        if (accumulatedTimeNs < shootingInterval) {
            return;
        }

        // Shooting
        IEnemiesManager enemiesManager = world.getEnemiesManager();

        List<IEnemy> toShoot = enemiesManager.getNear(locationBottomLeft, projectileInfo.getTowerRange());
        for (final IEnemy enemy : toShoot) {
            boolean creationSucceeded = world.getProjectilesManager().addProjectile(
                    getLocationBottomLeft(), enemy, projectileInfo, enchantmentInfo
            );

            if (creationSucceeded) {
                accumulatedTimeNs = 0;
                break;
            }
        }
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
    
    @Override
    public ProjectileInfo getProjectileInfo() {
        return projectileInfo;
    }

    @Override
    public EnchantmentInfo getEnchantmentInfo() {
        return enchantmentInfo;
    }

    void clicked() {
        world.getTowersManager().openTowerDialog(tower);
    }

    Vector2D getLocationBottomLeft() {
        return locationBottomLeft;
    }
}
