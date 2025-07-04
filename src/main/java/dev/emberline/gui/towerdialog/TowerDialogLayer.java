package dev.emberline.gui.towerdialog;

import dev.emberline.core.GameLoop;
import dev.emberline.core.config.ConfigLoader;
import dev.emberline.core.graphics.SpriteLoader;
import dev.emberline.core.graphics.spritekeys.SingleSpriteKey;
import dev.emberline.core.graphics.spritekeys.StringSpriteKey;
import dev.emberline.core.render.CoordinateSystem;
import dev.emberline.core.render.RenderPriority;
import dev.emberline.core.render.RenderTask;
import dev.emberline.core.render.Renderer;
import dev.emberline.game.model.EnchantmentInfo;
import dev.emberline.game.model.ProjectileInfo;
import dev.emberline.game.model.UpgradableInfo;
import dev.emberline.game.world.buildings.tower.Tower;
import dev.emberline.gui.GuiButton;
import dev.emberline.gui.GuiLayer;
import dev.emberline.gui.event.ResetTowerInfoEvent;
import dev.emberline.gui.event.SetTowerAimTypeEvent;
import dev.emberline.gui.event.SetTowerAimTypeEvent.AimType;
import dev.emberline.gui.event.SetTowerInfoEvent;
import dev.emberline.gui.event.UpgradeTowerInfoEvent;
import dev.emberline.gui.towerdialog.TextGuiButton.TextLayoutType;
import dev.emberline.gui.towerdialog.stats.TowerStatsProvider;
import dev.emberline.gui.towerdialog.stats.TowerStatsViewsBuilder;
import dev.emberline.gui.towerdialog.stats.TowerStatsViewsBuilder.TowerStatView;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import com.fasterxml.jackson.annotation.JsonProperty;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.Image;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Represents a graphical user interface layer specifically designed to show detailed
 * information, and customization buttons for a {@code Tower} instance.
 * This layer allows players to modify the tower's properties, such as projectiles,
 * enchantments, and aiming modes, as well as view stat comparisons.
 */
public class TowerDialogLayer extends GuiLayer {
    // The Tower linked to this dialog layer
    private final Tower tower;
    // The current state of what is displayed in the dialog
    private EnchantmentInfo displayedEnchantment;
    private ProjectileInfo displayedProjectile;
    private AimType displayedAimType;
    // Tower Stats Views
    private List<TowerStatView> statsViews;
    // Data to display on button hover
    private final Map<GuiButton, TowerStatsProvider> hoverData = new HashMap<>();
    // Layout constants for the GUI elements
    private static final Layout layout = ConfigLoader.loadConfig("/sprites/ui/towerDialogLayerLayout.json", Layout.class);

    /**
     * Defines layout constants used for specifying GUI element dimensions.
     * The numbers are relative to the GUI coordinate system.
     *
     * @see dev.emberline.core.render.CoordinateSystem
     * @see Renderer#getGuiCoordinateSystem()
     */
    private record Layout(
            @JsonProperty        
            double bgWidth, 
            @JsonProperty
            double bgHeight, 
            @JsonProperty
            double bgX, 
            @JsonProperty
            double bgY,
            @JsonProperty
            double statsWidth,
            @JsonProperty
            double statsHeight, 
            @JsonProperty
            double statsX, 
            @JsonProperty
            double statsY,
            @JsonProperty
            double statsOvrWidth, 
            @JsonProperty
            double statsOvrHeight, 
            @JsonProperty
            double statsOvrX, 
            @JsonProperty
            double statsOvrY,
            @JsonProperty
            double statsHMargin, 
            @JsonProperty
            double statsVMargin, 
            @JsonProperty
            int statsColumns, 
            @JsonProperty
            int statsRows,
            @JsonProperty
            double statsSvIconVMarginFactor, 
            @JsonProperty
            double statsSvIconHMarginFactor,
            @JsonProperty
            double statsSvTitleHeightFactor, 
            @JsonProperty
            double statsSvValueWidthFactor,
            @JsonProperty
            double selectorNameHeight, 
            @JsonProperty
            double selectorNameY, 
            @JsonProperty
            double selectorX,
            @JsonProperty
            double selectorNameIconSide, 
            @JsonProperty
            double selectorNameIconX, 
            @JsonProperty
            double selectorNameIconY,
            @JsonProperty
            double selectorNameWidth, 
            @JsonProperty
            double selectorTotalHeight,
            @JsonProperty
            double selectorUpgradeBtnSide, 
            @JsonProperty
            double selectorUpgradeBtnX, 
            @JsonProperty
            double selectorUpgradeBtnY,
            @JsonProperty
            double selectorResetBtnWidth, 
            @JsonProperty
            double selectorResetBtnHeight,
            @JsonProperty
            double selectorResetBtnX, 
            @JsonProperty
            double selectorResetBtnY,
            @JsonProperty
            double selectorUpgradeWidth, 
            @JsonProperty
            double selectorUpgradeHeight,
            @JsonProperty
            double selectorUpgradeX, 
            @JsonProperty
            double selectorUpgradeY,
            @JsonProperty
            double selectorLevelMarkerWidth,
            @JsonProperty
            double selectorTypeBtnHeight, 
            @JsonProperty
            double selectorTypeBtnWidth,
            @JsonProperty
            double selectorTypeBtnX, 
            @JsonProperty
            double selectorTypeBtnY,
            @JsonProperty
            double selectorTypeBtn2X,
            @JsonProperty
            double aimBtnWidth, 
            @JsonProperty
            double aimBtnHeight,
            @JsonProperty
            double aimBtnX, 
            @JsonProperty
            double aimBtnY
    ) {
    }

    /**
     * Defines colors and effects used for rendering the GUI elements.
     * The colors are defined using the {@link ColorAdjust} class, which allows adjusting the hue, saturation,
     * brightness, and contrast of the color.
     */
    private static final class Colors {
        // To convert hue from 0-360 degrees to jfx range (-1 to 1):
        //hue = (hue > 180 ? hue - 360 : hue) / 180;
        //gc.setEffect(new ColorAdjust(hue,saturation,brightness,contrast));
        private static final ColorAdjust STAT_TITLE = new ColorAdjust(0.15, 0.9, -0.5, 0);
        private static final ColorAdjust STAT_VALUE = new ColorAdjust(0.15, 0.9, -0.3, 0);
        private static final ColorAdjust STAT_COMPARISON = new ColorAdjust(0.9444, 1, -0.3, 0);
        private static final ColorAdjust STAT_NEW_VALUE = new ColorAdjust(0.9444, 1, -0.3, 0);
        private static final ColorAdjust SELECTOR_TITLE = new ColorAdjust(0.15, 0.9, -0.6, 0);
    }

    /**
     * Constructs a new TowerDialogLayer.
     *
     * @param tower the tower instance for which the dialog layer is being created
     * @see TowerDialogLayer
     */
    @SuppressFBWarnings(
            value = "EI_EXPOSE_REP2",
            justification = "The tower reference is needed to link the dialog layer to the tower instance."
    )
    public TowerDialogLayer(final Tower tower) {
        super(layout.bgX, layout.bgY, layout.bgWidth, layout.bgHeight);
        this.tower = tower;
        updateLayout();
    }

    /**
     * Returns the tower instance associated with this dialog layer.
     *
     * @return the tower instance associated with this dialog layer.
     */
    @SuppressFBWarnings(
            value = "EI_EXPOSE_REP",
            justification = "We need to retrieve the tower reference attached to this dialog layer."
    )
    public Tower getTower() {
        return tower;
    }

    private void updateLayout() {
        // Clearing previous layout //
        super.getButtons().clear();
        hoverData.clear();
        displayedEnchantment = Objects.requireNonNull(tower.getEnchantmentInfo(), "EnchantmentInfo cannot be null");
        displayedProjectile = Objects.requireNonNull(tower.getProjectileInfo(), "ProjectileInfo cannot be null");
        displayedAimType = Objects.requireNonNull(tower.getAimType(), "AimType cannot be null");

        // Building stats
        rebuildStats();
        // Building buttons
        addAimButton();
        addSelectorButtons(
                displayedEnchantment,
                new Image[]{SpriteLoader.loadSprite(SingleSpriteKey.ICE_BUTTON).image(),
                            SpriteLoader.loadSprite(SingleSpriteKey.FIRE_BUTTON).image()},
                new EnchantmentInfo.Type[]{EnchantmentInfo.Type.ICE, EnchantmentInfo.Type.FIRE},
                0
        );
        addSelectorButtons(
                displayedProjectile,
                new Image[]{SpriteLoader.loadSprite(SingleSpriteKey.SMALL_BUTTON).image(),
                            SpriteLoader.loadSprite(SingleSpriteKey.BIG_BUTTON).image()},
                new ProjectileInfo.Type[]{ProjectileInfo.Type.SMALL, ProjectileInfo.Type.BIG},
                layout.selectorTotalHeight
        );
        for (final GuiButton button : super.getButtons()) {
            button.setOnMouseEnter(this::refreshHoverData);
            button.setOnMouseLeave(this::refreshHoverData);
        }
    }

    private void addAimButton() {
        final GuiButton aimButton = new TextGuiButton(
                layout.aimBtnX, layout.aimBtnY,
                layout.aimBtnWidth, layout.aimBtnHeight,
                SpriteLoader.loadSprite(SingleSpriteKey.AIM_BUTTON).image(),
                displayedAimType.displayName(), TextLayoutType.CENTER
        );
        aimButton.setOnClick(() -> {
            throwEvent(new SetTowerAimTypeEvent(aimButton, tower, displayedAimType.next()));
        });
        super.getButtons().add(aimButton);
    }

    private <T extends UpgradableInfo.InfoType, S extends UpgradableInfo<T, S>> void addSelectorButtons(
            final UpgradableInfo<T, S> element, final Image[] typeImages, final T[] typeValues, final double yOffset
    ) {
        // if the current element can change type, we add the two type buttons
        if (element.canChangeType()) {
            final double[] x = {layout.selectorTypeBtnX, layout.selectorTypeBtn2X};
            final double y = layout.selectorTypeBtnY + yOffset;
            for (int t = 0; t < typeValues.length; ++t) {
                final T typeValue = typeValues[t];
                final GuiButton button = new PricingGuiButton(
                        x[t], y, layout.selectorTypeBtnWidth, layout.selectorTypeBtnHeight,
                        typeImages[t], -element.getUpgradeCost(), TextLayoutType.LEFT
                );
                button.setOnClick(() -> throwEvent(new SetTowerInfoEvent(this, tower, element, typeValue)));
                hoverData.put(button, (TowerStatsProvider) element.getChangeType(typeValue));
                super.getButtons().add(button);
            }
        } else { // otherwise, we add the upgrade and reset button
            GuiButton upgradeButton = new PricingGuiButton(
                    layout.selectorUpgradeBtnX, layout.selectorUpgradeBtnY + yOffset,
                    layout.selectorUpgradeBtnSide, layout.selectorUpgradeBtnSide,
                    SpriteLoader.loadSprite(SingleSpriteKey.UPGRADE_BUTTON).image(),
                    -element.getUpgradeCost(), TextLayoutType.BOTTOM
            );
            // On the last level, disable the upgrade button
            if (!element.canUpgrade()) {
                upgradeButton = new TextGuiButton(
                        layout.selectorUpgradeBtnX, layout.selectorUpgradeBtnY + yOffset,
                        layout.selectorUpgradeBtnSide, layout.selectorUpgradeBtnSide,
                        SpriteLoader.loadSprite(SingleSpriteKey.DISABLED_UPGRADE_BUTTON).image(),
                        SpriteLoader.loadSprite(SingleSpriteKey.DISABLED_UPGRADE_BUTTON).image(),
                        "MAX", TextLayoutType.CENTER
                );
            }
            final GuiButton resetButton = new PricingGuiButton(
                    layout.selectorResetBtnX, layout.selectorResetBtnY + yOffset,
                    layout.selectorResetBtnWidth, layout.selectorResetBtnHeight,
                    SpriteLoader.loadSprite(SingleSpriteKey.CANCEL_BUTTON).image(),
                    element.getRefundValue(), TextLayoutType.BOTTOM
            );
            upgradeButton.setOnClick(() -> throwEvent(new UpgradeTowerInfoEvent(this, tower, element)));
            resetButton.setOnClick(() -> throwEvent(new ResetTowerInfoEvent(this, tower, element)));
            if (element.canUpgrade()) {
                hoverData.put(upgradeButton, (TowerStatsProvider) element.getUpgrade());
            }
            super.getButtons().add(upgradeButton);
            super.getButtons().add(resetButton);
        }
    }

    private void refreshHoverData() {
        for (final GuiButton button : super.getButtons()) {
            final TowerStatsProvider hoverStats = hoverData.get(button);
            if (button.isHovered()) {
                if (hoverStats != null) {
                    rebuildStatsWithCompared(hoverStats);
                }
                return;
            }
        }
        rebuildStats();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void render() {
        if (!displayedEnchantment.equals(tower.getEnchantmentInfo())) {
            updateLayout();
        }
        if (!displayedProjectile.equals(tower.getProjectileInfo())) {
            updateLayout();
        }
        if (!displayedAimType.equals(tower.getAimType())) {
            updateLayout();
        }

        final Renderer renderer = GameLoop.getInstance().getRenderer();
        final GraphicsContext gc = renderer.getGraphicsContext();
        final CoordinateSystem guics = renderer.getGuiCoordinateSystem();

        renderer.addRenderTask(new RenderTask(RenderPriority.GUI, () -> {
            // Background
            Renderer.drawImage(SpriteLoader.loadSprite(SingleSpriteKey.TDL_BACKGROUND).image(),
                                gc, guics, layout.bgX, layout.bgY, layout.bgWidth, layout.bgHeight);
            // Stats Background
            Renderer.drawImage(SpriteLoader.loadSprite(SingleSpriteKey.STATS_BACKGROUND).image(),
                                gc, guics, layout.statsX, layout.statsY, layout.statsWidth, layout.statsHeight);
            // Stats Overlay
            drawStatsOverlay(statsViews, gc, guics);

            drawSelector(gc, guics, "Enchantment:", displayedEnchantment, 0);
            drawSelector(gc, guics, "Projectile:", displayedProjectile, layout.selectorTotalHeight);
        }));

        super.render();
    }

    // STATS BUILDER HELPERS //
    private void rebuildStats() {
        statsViews = new TowerStatsViewsBuilder()
                .addStats(displayedEnchantment)
                .addStats(displayedProjectile)
                .build();
    }

    private void rebuildStatsWithCompared(final TowerStatsProvider... comparedStatsProviders) {
        final TowerStatsViewsBuilder builder = new TowerStatsViewsBuilder();
        builder.addStats(displayedEnchantment).addStats(displayedProjectile);
        for (final TowerStatsProvider comparedStats : comparedStatsProviders) {
            builder.addComparedStats(comparedStats);
        }
        statsViews = builder.build();
    }

    // STATS DRAWING //
    private static void drawStatView(
            final TowerStatView statView, final GraphicsContext gc, final CoordinateSystem cs,
            final double x, final double y, final double width, final double height
    ) {
        gc.save();
        // Title and value strings
        final String displayName = statView.getStat().type().getDisplayName();
        final double statValue = statView.getStat().value();
        final String statValueStr = new DecimalFormat("0.##").format(statValue);
        // Value color effect
        final ColorAdjust valueColor = statView.getType() == TowerStatView.Type.NEW ? Colors.STAT_NEW_VALUE : Colors.STAT_VALUE;
        // layout
        final double iconVMargin = width * layout.statsSvIconVMarginFactor;
        final double iconHMargin = iconVMargin * layout.statsSvIconHMarginFactor;
        final double iconSide = height - 2 * iconVMargin;
        final double titleX = x + iconSide + iconHMargin;
        final double titleWidth = width - iconSide - iconHMargin;
        final double titleHeight = height * layout.statsSvTitleHeightFactor;
        final double valueY = y + titleHeight;
        final double valueWidth = titleWidth * layout.statsSvValueWidthFactor;
        final double valueHeight = height - titleHeight;
        // Icon
        Renderer.drawImage(statView.getStat().type().getIcon(), gc, cs, x, y + iconVMargin, iconSide, iconSide);
        // Title
        gc.setEffect(Colors.STAT_TITLE);
        Renderer.drawText(displayName, gc, cs, titleX, y, titleWidth, titleHeight);
        // Value
        gc.setEffect(valueColor);
        Renderer.drawText(statValueStr, gc, cs, titleX, valueY, valueWidth, valueHeight);
        // Comparison
        if (statView.getType() == TowerStatView.Type.COMPARED) {
            final double comparedValue = statView.getComparedStat().value();
            final String comparisonStr = new DecimalFormat("+0.##;-0.##").format(comparedValue - statValue);
            gc.setEffect(Colors.STAT_COMPARISON);
            Renderer.drawText(comparisonStr, gc, cs,
                    titleX + valueWidth, valueY, titleWidth - valueWidth, valueHeight);
        }
        gc.restore();
    }

    private static void drawStatsOverlay(
            final List<TowerStatView> statsViews,
            final GraphicsContext gc, final CoordinateSystem cs
    ) {
        final int nStatsViews = statsViews.size();
        final int statsRows = Math.max(layout.statsRows, (int) Math.ceil((double) nStatsViews / layout.statsColumns));
        final double totVPadding = (statsRows - 1) * layout.statsVMargin;
        final double totHPadding = (layout.statsColumns - 1) * layout.statsHMargin;
        final double statHeight = (layout.statsOvrHeight - totVPadding) / statsRows;
        final double statWidth = (layout.statsOvrWidth - totHPadding) / layout.statsColumns;

        for (int i = 0; i < nStatsViews; i++) {
            final int row = i / layout.statsColumns;
            final int col = i % layout.statsColumns;
            final double x = layout.statsOvrX + col * (statWidth + layout.statsHMargin);
            final double y = layout.statsOvrY + row * (statHeight + layout.statsVMargin);
            drawStatView(statsViews.get(i), gc, cs, x, y, statWidth, statHeight);
        }
    }

    // SELECTORS DRAWING //
    private static void drawSelector(
            final GraphicsContext gc, final CoordinateSystem cs, final String title,
            final UpgradableInfo<?, ?> info, final double verticalOffset
    ) {
        // Title
        gc.save();
        gc.setEffect(Colors.SELECTOR_TITLE);
        Renderer.drawText(title, gc, cs, layout.selectorX, layout.selectorNameY + verticalOffset,
                layout.selectorNameWidth, layout.selectorNameHeight);
        gc.restore();
        // Should we draw the selector?
        if (info.canChangeType()) {
            return;
        }
        // Name icon
        Renderer.drawImage(getIcon(info), gc, cs, layout.selectorNameIconX,
                layout.selectorNameIconY + verticalOffset,
                layout.selectorNameIconSide, layout.selectorNameIconSide);
        // Upgrade selector
        final double emptySpace = layout.selectorUpgradeWidth - layout.selectorLevelMarkerWidth * info.getMaxLevel();
        for (int i = 0; i < info.getMaxLevel(); ++i) {
            final double x = layout.selectorUpgradeX
                    + i * (emptySpace / (info.getMaxLevel() - 1) + layout.selectorLevelMarkerWidth);
            final double y = layout.selectorUpgradeY + verticalOffset;
            final double width = layout.selectorLevelMarkerWidth;
            final double height = layout.selectorUpgradeHeight;
            final Image sprite = i < info.level() ? SpriteLoader.loadSprite(SingleSpriteKey.FULL_UPGRADE_LEVEL).image()
                    : SpriteLoader.loadSprite(SingleSpriteKey.EMPTY_UPGRADE_LEVEL).image();
            Renderer.drawImage(sprite, gc, cs, x, y, width, height);
        }
    }

    // Utility method to get the icon for the given UpgradableInfo, does not cover the BASE types.
    // If an icon cannot be found, an empty image is returned (the space character).
    private static Image getIcon(final UpgradableInfo<?, ?> info) {
        final Image empty = SpriteLoader.loadSprite(new StringSpriteKey(" ")).image();
        return switch (info) {
            case final EnchantmentInfo e -> {
                if (e.type() == EnchantmentInfo.Type.FIRE) {
                    yield SpriteLoader.loadSprite(SingleSpriteKey.FIRE_ICON).image();
                }
                if (e.type() == EnchantmentInfo.Type.ICE) {
                    yield SpriteLoader.loadSprite(SingleSpriteKey.ICE_ICON).image();
                }
                yield empty;
            }
            case final ProjectileInfo p -> {
                if (p.type() == ProjectileInfo.Type.SMALL) {
                    yield SpriteLoader.loadSprite(SingleSpriteKey.SMALL_ICON).image();
                }
                if (p.type() == ProjectileInfo.Type.BIG) {
                    yield SpriteLoader.loadSprite(SingleSpriteKey.BIG_ICON).image();
                }
                yield empty;
            }
            default -> empty;
        };
    }
}
