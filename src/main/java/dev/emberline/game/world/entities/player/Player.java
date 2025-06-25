package dev.emberline.game.world.entities.player;

import com.fasterxml.jackson.annotation.JsonProperty;
import dev.emberline.core.ConfigLoader;
import dev.emberline.game.model.EnchantmentInfo;
import dev.emberline.game.model.ProjectileInfo;
import dev.emberline.game.model.UpgradableInfo;
import dev.emberline.game.world.World;
import dev.emberline.gui.event.*;


public class Player implements GuiEventListener {
    private int health;
    private int gold;
    private final World world;
    private GameEventListener gameEventListener;

    private record Metadata(
            @JsonProperty int health,
            @JsonProperty int gold
    ) {
    }

    Metadata metadata = ConfigLoader.loadConfig("/world/player.json", Metadata.class);

    public Player(final World world) {
        this.health = metadata.health;
        this.gold = metadata.gold;
        this.world = world;
    }

    public final void setListener(final GameEventListener gameEventListener) {
        this.gameEventListener = gameEventListener;
    }

    protected final void throwGameEvent(final GameEvent event) {
        if (gameEventListener != null) {
            gameEventListener.onGameEvent(event);
        }
    }

    private boolean spendGold(final int amount) {
        if (this.gold - amount >= 0) {
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

    public void earnGold(final int amount) {
        this.gold += amount;
    }

    public void takeDamage() {
        if (this.health - 200 <= 0) {
            throwGameEvent(new GameOverEvent(this));
        }
        this.health -= 1;
    }

    @Override
    public void onGuiEvent(GuiEvent event) {
        if (event instanceof UpgradeTowerInfoEvent upgradeEvent && spendGold(upgradeEvent.getUpgradableInfo().getUpgradeCost())) {
            handleUpgradeEvent(upgradeEvent);
        } else if (event instanceof ResetTowerInfoEvent resetEvent) {
            handleResetEvent(resetEvent);
        } else if (event instanceof SetTowerInfoEvent setTowerEvent) {
            handleSetEvent(setTowerEvent);
        } else if (event instanceof NewBuildEvent newBuildEvent && spendGold(newBuildEvent.getTowerPreBuild().getNewBuildCost())) {
            handleNewBuildEvent(newBuildEvent);
        }
    }

    private void handleNewBuildEvent(final NewBuildEvent event) {
        world.getTowersManager().buildTower(event.getTowerPreBuild());
    }

    private void handleUpgradeEvent(final UpgradeTowerInfoEvent event) {
        final UpgradableInfo<?, ?> info = event.getUpgradableInfo();
        if (!info.canUpgrade()) {
            return;
        }
        event.getTower().setUpgradableInfo(info.getUpgrade());
    }

    private void handleResetEvent(final ResetTowerInfoEvent event) {
        final UpgradableInfo<?,?> info = event.getUpgradableInfo();
        earnGold(event.getUpgradableInfo().getRefundValue());
        event.getTower().setUpgradableInfo(info.getDefault());
    }

    private void handleSetEvent(final SetTowerInfoEvent event) {
        final UpgradableInfo<?, ?> info = event.getUpgradableInfo();
        if (!info.canChangeType()) {
            return;
        }
        if (info instanceof final ProjectileInfo infoCast) {
            event.getTower().setUpgradableInfo(infoCast.getChangeType((ProjectileInfo.Type) event.getType()));
        } else if (info instanceof final EnchantmentInfo infoCast) {
            event.getTower().setUpgradableInfo(infoCast.getChangeType((EnchantmentInfo.Type) event.getType()));
        }
    }
}