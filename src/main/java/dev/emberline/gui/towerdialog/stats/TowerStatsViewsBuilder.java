package dev.emberline.gui.towerdialog.stats;

import dev.emberline.gui.towerdialog.stats.TowerStat.TowerStatType;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

/**
 * A builder class for creating a list of {@link TowerStatView}.
 * <p>
 * This class is used to build a list of tower stat views based on the provided stats and compared stats.
 * It allows adding stats from different providers and building the final list of views.
 * Duplicate stats (same type) are ignored and only the first occurrence is kept.
 * If a stat is being compared but has the same value as the original stat, it will be kept as a normal stat.
 */
public final class TowerStatsViewsBuilder {

    private final Map<TowerStatType, TowerStat> statsMap = new EnumMap<>(TowerStatType.class);
    private final Map<TowerStatType, TowerStat> comparedStatsMap = new EnumMap<>(TowerStatType.class);

    /**
     * Represents a view of a single {@link TowerStat} for display in the GUI.
     * <p>
     * A {@code TowerStatView} can be in one of three {@link Type states}:
     * <ul>
     *   <li><b>NORMAL:</b> A stat that is not being compared to another stat, or is being compared but has the same value.</li>
     *   <li><b>COMPARED:</b> A stat that is being compared to another stat of the same type and has a different value
     *       (for example, damage is 100 but compared to 200 will show +100).</li>
     *   <li><b>NEW:</b> A stat that is being compared but has no corresponding stat to be compared to
     *       (for example, an effect is being compared but is actually a new stat that wasn't already present in the tower).</li>
     * </ul>
     * <p>
     * This class is immutable and comparable by the type of the stat it represents.
     */
    public static final class TowerStatView implements Comparable<TowerStatView> {
        /**
         * The type of {@code TowerStatView}, can be in one of three {@link Type states}:
         * <ul>
         *   <li><b>NORMAL:</b> A stat that is not being compared to another stat,
         *       or is being compared but has the same value.</li>
         *   <li><b>COMPARED:</b> A stat that is being compared to another stat of the same type and has a different value
         *       (for example, damage is 100 but compared to 200 will show +100).</li>
         *   <li><b>NEW:</b> A stat that is being compared but has no corresponding stat to be compared to
         *       (for example, an effect is being compared but is actually a new stat
         *       that wasn't already present in the tower).</li>
         * </ul>
         */
        public enum Type {
            /**
             * Represents the "NORMAL" state of a {@link TowerStatView} type: a stat that is not being
             * compared to another stat, or is being compared but has the same value.
             */
            NORMAL,
            /**
             * Represents the "COMPARED" state of a {@link TowerStatView} type: a stat that is being compared to another
             * stat of the same type and has a different value (for example, damage is 100 but compared to 200
             * will show +100).
             */
            COMPARED,
            /**
             * Represents the "NEW" state of a {@link TowerStatView} type: a stat that is being compared but has no
             * corresponding stat to be compared to (for example, an effect is being compared but is actually
             * a new stat that wasn't already present in the tower).
             */
            NEW
        }

        private final TowerStat stat;
        private final TowerStat comparedStat;
        private final Type type;

        // Constructor for normal or new stat view
        private TowerStatView(final TowerStat stat, final Type type) {
            if (type == Type.COMPARED) {
                throw new IllegalArgumentException("Cannot create a compared stat view without a compared stat");
            }
            this.stat = Objects.requireNonNull(stat, "TowerStat cannot be null");
            this.type = Objects.requireNonNull(type, "Type cannot be null");
            this.comparedStat = null;
        }

        // Constructor for compared stat view
        private TowerStatView(final TowerStat stat, final TowerStat comparedStat) {
            this.stat = Objects.requireNonNull(stat, "TowerStat cannot be null");
            this.comparedStat = Objects.requireNonNull(comparedStat, "Compared TowerStat cannot be null");
            this.type = Type.COMPARED;
        }

        /**
         * Returns the tower stat represented by this view.
         *
         * @return the tower stat
         */
        public TowerStat getStat() {
            return stat;
        }

        /**
         * Returns the compared stat if this is a compared stat view, otherwise null.
         *
         * @return the compared stat or null if this is not a compared stat view
         */
        public TowerStat getComparedStat() {
            return comparedStat;
        }

        /**
         * Returns the type of this stat view.
         *
         * @return the type of this stat view
         */
        public Type getType() {
            return type;
        }

        // TowerStatView is comparable based on the stat type
        @Override
        public int compareTo(final TowerStatView compared) {
            return this.stat.type().compareTo(compared.stat.type());
        }
    }

    /**
     * Adds stats from the given provider to the builder.
     *
     * @param provider the provider containing tower stats
     * @return this builder instance
     */
    public TowerStatsViewsBuilder addStats(final TowerStatsProvider provider) {
        for (final TowerStat stat : provider.getTowerStats()) {
            statsMap.putIfAbsent(stat.type(), stat);
        }
        return this;
    }

    /**
     * Adds stats from the given provider to the builder for comparison with the existing
     * stats added with {@link #addStats(TowerStatsProvider)}.
     *
     * @param provider the provider containing tower stats
     * @return this builder instance
     */
    public TowerStatsViewsBuilder addComparedStats(final TowerStatsProvider provider) {
        for (final TowerStat stat : provider.getTowerStats()) {
            comparedStatsMap.putIfAbsent(stat.type(), stat);
        }
        return this;
    }

    /**
     * Builds the list of tower stat views based on the provided stats and compared stats.
     *
     * @return an unmodifiable sorted List of tower stat views
     */
    public List<TowerStatView> build() {
        final List<TowerStatView> views = new ArrayList<>();

        // Add NORMAL stat views
        // (stats that are in the stat map but not in the compared stats map or are in both but have the same value)
        final Stream<TowerStatView> normalStatViews = statsMap.values().stream()
                .filter(stat -> !comparedStatsMap.containsKey(stat.type())
                        || stat.value() == comparedStatsMap.get(stat.type()).value())
                .map(stat -> new TowerStatView(stat, TowerStatView.Type.NORMAL));
        // Add COMPARED stat views (stats that are in both maps but have different values)
        final Stream<TowerStatView> comparedStatViews = statsMap.values().stream()
                .filter(stat -> comparedStatsMap.containsKey(stat.type())
                        && stat.value() != comparedStatsMap.get(stat.type()).value())
                .map(stat -> new TowerStatView(stat, comparedStatsMap.get(stat.type())));
        // Add NEW stat views (compared stats that are not in the stats map)
        final Stream<TowerStatView> newStatViews = comparedStatsMap.values().stream()
                .filter(stat -> !statsMap.containsKey(stat.type()))
                .map(stat -> new TowerStatView(stat, TowerStatView.Type.NEW));
        // Combine all views into a single unmodifiable sorted list
        return Stream.of(normalStatViews, comparedStatViews, newStatViews)
                .flatMap(stream -> stream)
                .sorted()
                .toList();
    }
}
