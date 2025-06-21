package dev.emberline.game.world.entities.player;

import java.io.ObjectInputFilter.Config;

import com.fasterxml.jackson.annotation.JsonProperty;

import dev.emberline.core.ConfigLoader;
import dev.emberline.game.model.EnchantmentInfo;
import dev.emberline.game.model.ProjectileInfo;
import dev.emberline.game.model.UpgradableInfo;
import dev.emberline.game.world.World;
import dev.emberline.gui.event.GuiEvent;
import dev.emberline.gui.event.GuiEventListener;
import dev.emberline.gui.event.ResetTowerInfoEvent;
import dev.emberline.gui.event.SetTowerInfoEvent;
import dev.emberline.gui.event.UpgradeTowerInfoEvent;


public class Player implements GuiEventListener {
    private int health;
    private int gold;
    private World world;

    private record Metadata (
        @JsonProperty int health,
        @JsonProperty int gold
    ) {}

    Metadata metadata = ConfigLoader.loadConfig("/world/player.json", Metadata.class);

    public Player(World world) {
        this.health = metadata.health;
        this.gold = metadata.gold;
        this.world = world;
    }

    private boolean spendGold(int amount) {
        if(this.gold - amount >= 0) {
            this.gold -= amount;
            return true;
        }
        return false;
    }

    public int getHealt() {
        return this.health;
    }

    public int getGold() {
        return this.gold;
    }

    public void earnGold(int amount) {
        this.gold += amount;
    }

    public void takeDamage() {
        if (this.health - 1 <= 0) {
            // TODO gui game over
        }
        this.health -= 1;
    }

    // TODO add spending gold for all methods below

    @Override
    public void onGuiEvent(GuiEvent event) {
        if (event instanceof UpgradeTowerInfoEvent) {
            handleUpgradeEvent((UpgradeTowerInfoEvent) event);
        } else if (event instanceof ResetTowerInfoEvent) {
            handleResetEvent((ResetTowerInfoEvent) event);
        } else if (event instanceof SetTowerInfoEvent) {
            handleSetEvent((SetTowerInfoEvent) event);
        } /* else if (event instanceof NewBuildEvent) {
            handleNewBuildEvent((NewBuildEvent) event);
        } */
    }

    /* private void handleNewBuildEvent(NewBuildEvent event) {
        // TODO: add tower to the world
    } */

    private void handleUpgradeEvent(UpgradeTowerInfoEvent event) {
        UpgradableInfo<?,?> info = event.getUpgradableInfo();
        if (!info.canUpgrade()) return;
        event.getTower().setUpgradableInfo(info.getUpgrade());
    }

    private void handleResetEvent(ResetTowerInfoEvent event) {
        UpgradableInfo<?,?> info = event.getUpgradableInfo();
        event.getTower().setUpgradableInfo(info.getDefault());
    }

    private void handleSetEvent(SetTowerInfoEvent event) {
        UpgradableInfo<?,?> info = event.getUpgradableInfo();
        if (!info.canChangeType()) return;
        if (info instanceof ProjectileInfo infoCast) {
            event.getTower().setUpgradableInfo(infoCast.getChangeType((ProjectileInfo.Type) event.getType()));
        } else if (info instanceof EnchantmentInfo infoCast) {
            event.getTower().setUpgradableInfo(infoCast.getChangeType((EnchantmentInfo.Type) event.getType()));
        }
    }
}