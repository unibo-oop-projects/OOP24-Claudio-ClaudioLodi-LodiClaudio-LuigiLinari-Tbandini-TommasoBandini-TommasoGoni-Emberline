package dev.emberline.gui.towerdialog.stats;

import java.util.List;

/**
 * An interface for providing tower statistics.
 * <p>
 * Implementations of this interface return a list of {@link TowerStat} objects,
 * each representing a specific stat of a tower.
 * These stats are used by {@link TowerStatsViewsBuilder} to build visual representations
 * of the tower's statistics in {@link dev.emberline.gui.towerdialog.TowerDialogLayer}.
 */
public interface TowerStatsProvider {
    List<TowerStat> getTowerStats();
}
