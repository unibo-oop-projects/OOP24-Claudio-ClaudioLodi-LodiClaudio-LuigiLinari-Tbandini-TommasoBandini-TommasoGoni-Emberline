package dev.emberline.game.world.entities.player;

import java.io.Serial;
import java.io.Serializable;
import java.util.EventListener;

import com.fasterxml.jackson.annotation.JsonProperty;
import dev.emberline.core.ConfigLoader;
import dev.emberline.core.event.EventDispatcher;
import dev.emberline.core.event.EventHandler;
import dev.emberline.game.model.EnchantmentInfo;
import dev.emberline.game.model.ProjectileInfo;
import dev.emberline.game.model.UpgradableInfo;
import dev.emberline.game.world.World;
import dev.emberline.gui.event.*;


public class Player implements EventListener, Serializable {
    @Serial
    private static final long serialVersionUID = 3553209889364137174L;

    private int health;
    private int gold;
    private final World world;

    Metadata metadata = ConfigLoader.loadConfig("/world/player.json", Metadata.class);

    private record Metadata(
            @JsonProperty int health,
            @JsonProperty int gold
    ) {
    }

    public Player(final World world) {
        this.health = metadata.health;
        this.gold = metadata.gold;
        this.world = world;

        EventDispatcher.getInstance().registerListener(this);
    }

    private boolean spendGold(final int amount) {
        if (this.gold - amount >= 0) {
            this.gold -= amount;
            return true;
        }
        return false;
    }

    public int getHealth() {
        return this.health;
    }

    public int getGold() {
        return this.gold;
    }

    public void earnGold(final int amount) {
        this.gold += amount;
    }

    public void takeDamage() {
        if (this.health - 1 <= 0) {
            EventDispatcher.getInstance().dispatchEvent(new GameOverEvent(this));
        }
        this.health -= 1;
    }

    @EventHandler
    private void handleNewBuildEvent(final NewBuildEvent event) {
        if (!spendGold(event.getTowerPreBuild().getNewBuildCost())) {
            return;
        }
        world.getTowersManager().buildTower(event.getTowerPreBuild());
    }

    @EventHandler
    private void handleUpgradeEvent(final UpgradeTowerInfoEvent event) {
        final UpgradableInfo<?, ?> info = event.getUpgradableInfo();
        if (!spendGold(info.getUpgradeCost()) || !info.canUpgrade()) {
            return;
        }
        event.getTower().setUpgradableInfo(info.getUpgrade());
    }

    @EventHandler
    private void handleResetEvent(final ResetTowerInfoEvent event) {
        final UpgradableInfo<?,?> info = event.getUpgradableInfo();
        earnGold(event.getUpgradableInfo().getRefundValue());
        event.getTower().setUpgradableInfo(info.getDefault());
    }

    @EventHandler
    private void handleSetEvent(final SetTowerInfoEvent event) {
        final UpgradableInfo<?, ?> info = event.getUpgradableInfo();
        if (!info.canChangeType() || !spendGold(info.getUpgradeCost())) {
            return;
        }
        if (info instanceof final ProjectileInfo infoCast) {
            event.getTower().setUpgradableInfo(infoCast.getChangeType((ProjectileInfo.Type) event.getType()));
        } else if (info instanceof final EnchantmentInfo infoCast) {
            event.getTower().setUpgradableInfo(infoCast.getChangeType((EnchantmentInfo.Type) event.getType()));
        }
    }
}
