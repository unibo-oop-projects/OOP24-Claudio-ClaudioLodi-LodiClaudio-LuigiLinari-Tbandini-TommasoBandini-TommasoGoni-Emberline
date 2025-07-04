package dev.emberline.game.world.entities.player;

import com.fasterxml.jackson.annotation.JsonProperty;
import dev.emberline.core.config.ConfigLoader;
import dev.emberline.core.event.EventDispatcher;
import dev.emberline.core.event.EventHandler;
import dev.emberline.game.model.EnchantmentInfo;
import dev.emberline.game.model.ProjectileInfo;
import dev.emberline.game.model.UpgradableInfo;
import dev.emberline.game.world.World;
import dev.emberline.gui.event.GameOverEvent;
import dev.emberline.gui.event.NewBuildEvent;
import dev.emberline.gui.event.ResetTowerInfoEvent;
import dev.emberline.gui.event.SetTowerAimTypeEvent;
import dev.emberline.gui.event.SetTowerInfoEvent;
import dev.emberline.gui.event.UpgradeTowerInfoEvent;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serial;
import java.io.Serializable;
import java.util.EventListener;

/**
 * Represents a player within the game, keeping track of the health,
 * gold and handling player-related game events such as building a tower.
 */
public class Player implements EventListener, Serializable {
    @Serial
    private static final long serialVersionUID = 3553209889364137174L;

    private int health;
    private int gold;
    private final World world;

    private final Metadata metadata = ConfigLoader.loadConfig("/world/player.json", Metadata.class);

    private record Metadata(
            @JsonProperty int health,
            @JsonProperty int gold
    ) implements Serializable {
    }

    /**
     * Constructs a new {@code Player} instance.
     *
     * @param world the {@code World} instance that this player belongs to
     */
    @SuppressFBWarnings(
            value = "EI_EXPOSE_REP2",   // May expose internal representation by
                                        // storing an externally mutable object
            justification = "This is intended behavior as this class needs a reference to world,"
                    + " both for the game over event and towers."
    )
    public Player(final World world) {
        registerEvents();

        this.health = metadata.health;
        this.gold = metadata.gold;
        this.world = world;
    }

    private void registerEvents() {
        EventDispatcher.getInstance().registerListener(this);
    }

    private boolean spendGold(final int amount) {
        if (this.gold - amount >= 0) {
            this.gold -= amount;
            return true;
        }
        return false;
    }

    /**
     * Retrieves the current health value of the player.
     *
     * @return the player's health as an integer.
     */
    public int getHealth() {
        return this.health;
    }

    /**
     * Retrieves the current gold value of the player.
     *
     * @return the player's gold as an integer.
     */
    public int getGold() {
        return this.gold;
    }

    /**
     * Increases the player's gold by the specified amount.
     *
     * @param amount the amount of gold to add to the player's total.
     */
    public void earnGold(final int amount) {
        this.gold += amount;
    }

    /**
     * Reduces the player's health and triggers a game over event if the player's health
     * goes to zero or below.
     */
    public void takeDamage() {
        if (this.health - 1 <= 0) {
            EventDispatcher.getInstance().dispatchEvent(new GameOverEvent(this, world.getStatistics()));
        }
        this.health -= 1;
    }

    @EventHandler
    // This method is used by the EventDispatcher and should not be removed.
    @SuppressWarnings({"unused", "PMD.AvoidDuplicateLiterals"})
    private void handleNewBuildEvent(final NewBuildEvent event) {
        if (!spendGold(event.getTowerPreBuild().getNewBuildCost())) {
            return;
        }
        world.getTowersManager().buildTower(event.getTowerPreBuild());
    }

    @EventHandler
    // This method is used by the EventDispatcher and should not be removed.
    @SuppressWarnings({"unused", "PMD.AvoidDuplicateLiterals"})
    private void handleUpgradeEvent(final UpgradeTowerInfoEvent event) {
        final UpgradableInfo<?, ?> info = event.getUpgradableInfo();
        if (!spendGold(info.getUpgradeCost()) || !info.canUpgrade()) {
            return;
        }
        event.getTower().setUpgradableInfo(info.getUpgrade());
    }

    @EventHandler
    // This method is used by the EventDispatcher and should not be removed.
    @SuppressWarnings({"unused", "PMD.AvoidDuplicateLiterals"})
    private void handleResetEvent(final ResetTowerInfoEvent event) {
        final UpgradableInfo<?, ?> info = event.getUpgradableInfo();
        earnGold(event.getUpgradableInfo().getRefundValue());
        event.getTower().setUpgradableInfo(info.getDefault());
    }

    @EventHandler
    // This method is used by the EventDispatcher and should not be removed.
    @SuppressWarnings({"unused", "PMD.AvoidDuplicateLiterals"})
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

    @EventHandler
    // This method is used by the EventDispatcher and should not be removed.
    @SuppressWarnings({"unused", "PMD.AvoidDuplicateLiterals"})
    private void handleSetAimTypeEvent(final SetTowerAimTypeEvent event) {
        event.getTower().setAimType(event.getAimType());
    }

    @Serial
    private void readObject(final ObjectInputStream e) throws IOException, ClassNotFoundException {
        e.defaultReadObject();
        registerEvents();
    }
}
