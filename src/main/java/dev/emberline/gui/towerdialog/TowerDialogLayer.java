package dev.emberline.gui.towerdialog;

import com.fasterxml.jackson.annotation.JsonProperty;
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
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.effect.Bloom;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.RadialGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.transform.Rotate;

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
    private static final Layout LAYOUT = ConfigLoader.loadConfig("/sprites/ui/towerDialogLayerLayout.json", Layout.class);
    private final long creationTime;

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
        super(LAYOUT.bgX, LAYOUT.bgY, LAYOUT.bgWidth, LAYOUT.bgHeight);
        this.tower = tower;
        this.creationTime = System.nanoTime();
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
        // Clearing previous LAYOUT //
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
                LAYOUT.selectorTotalHeight
        );
        for (final GuiButton button : super.getButtons()) {
            button.setOnMouseEnter(this::refreshHoverData);
            button.setOnMouseLeave(this::refreshHoverData);
        }
    }

    private void addAimButton() {
        final GuiButton aimButton = new TextGuiButton(
                LAYOUT.aimBtnX, LAYOUT.aimBtnY,
                LAYOUT.aimBtnWidth, LAYOUT.aimBtnHeight,
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
            final double[] x = {LAYOUT.selectorTypeBtnX, LAYOUT.selectorTypeBtn2X};
            final double y = LAYOUT.selectorTypeBtnY + yOffset;
            for (int t = 0; t < typeValues.length; ++t) {
                final T typeValue = typeValues[t];
                final GuiButton button = new PricingGuiButton(
                        x[t], y, LAYOUT.selectorTypeBtnWidth, LAYOUT.selectorTypeBtnHeight,
                        typeImages[t], -element.getUpgradeCost(), TextLayoutType.LEFT
                );
                button.setOnClick(() -> throwEvent(new SetTowerInfoEvent(this, tower, element, typeValue)));
                hoverData.put(button, (TowerStatsProvider) element.getChangeType(typeValue));
                super.getButtons().add(button);
            }
        } else { // otherwise, we add the upgrade and reset button
            GuiButton upgradeButton = new PricingGuiButton(
                    LAYOUT.selectorUpgradeBtnX, LAYOUT.selectorUpgradeBtnY + yOffset,
                    LAYOUT.selectorUpgradeBtnSide, LAYOUT.selectorUpgradeBtnSide,
                    SpriteLoader.loadSprite(SingleSpriteKey.UPGRADE_BUTTON).image(),
                    -element.getUpgradeCost(), TextLayoutType.BOTTOM
            );
            // On the last level, disable the upgrade button
            if (!element.canUpgrade()) {
                upgradeButton = new TextGuiButton(
                        LAYOUT.selectorUpgradeBtnX, LAYOUT.selectorUpgradeBtnY + yOffset,
                        LAYOUT.selectorUpgradeBtnSide, LAYOUT.selectorUpgradeBtnSide,
                        SpriteLoader.loadSprite(SingleSpriteKey.DISABLED_UPGRADE_BUTTON).image(),
                        SpriteLoader.loadSprite(SingleSpriteKey.DISABLED_UPGRADE_BUTTON).image(),
                        "MAX", TextLayoutType.CENTER
                );
            }
            final GuiButton resetButton = new PricingGuiButton(
                    LAYOUT.selectorResetBtnX, LAYOUT.selectorResetBtnY + yOffset,
                    LAYOUT.selectorResetBtnWidth, LAYOUT.selectorResetBtnHeight,
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
        final CoordinateSystem worldcs = renderer.getWorldCoordinateSystem();

        renderer.addRenderTask(new RenderTask(RenderPriority.GUI, () -> {
            // Background
            Renderer.drawImage(SpriteLoader.loadSprite(SingleSpriteKey.TDL_BACKGROUND).image(),
                                gc, guics, LAYOUT.bgX, LAYOUT.bgY, LAYOUT.bgWidth, LAYOUT.bgHeight);
            // Stats Background
            Renderer.drawImage(SpriteLoader.loadSprite(SingleSpriteKey.STATS_BACKGROUND).image(),
                                gc, guics, LAYOUT.statsX, LAYOUT.statsY, LAYOUT.statsWidth, LAYOUT.statsHeight);
            // Stats Overlay
            drawStatsOverlay(statsViews, gc, guics);

            drawSelector(gc, guics, "Enchantment:", displayedEnchantment, 0);
            drawSelector(gc, guics, "Projectile:", displayedProjectile, LAYOUT.selectorTotalHeight);
        }));

        // Draw tower radius
        final double currentTimeNs = System.nanoTime() - creationTime;
        final double animationDurationNs = 1e9 / 2.;
        final double t = easeInOutExpo(Math.min(currentTimeNs / animationDurationNs, 1.0));
        final double worldTowerRange = tower.getProjectileInfo().getTowerRange() * t;
        final double ovalScreenX = worldcs.toScreenX(tower.getPosition().getX() - worldTowerRange);
        final double ovalScreenY = worldcs.toScreenY(tower.getPosition().getY() - worldTowerRange);
        final double ovalCenterScreenX = worldcs.toScreenX(tower.getPosition().getX());
        final double ovalCenterScreenY = worldcs.toScreenY(tower.getPosition().getY());


        final double lineWorldWidth = 0.08; // era 0.08
        final double lineDashes1 = 0.2 * worldcs.getScale();
        final double lineDashes2 = 0.4 * worldcs.getScale();
        final double lineAlpha = 0.5;
        final double lineBloomThreshold = 0.1;
        final double rotationAngle = System.nanoTime() / 2e8;

        final double strokeWorldRadius = worldTowerRange - lineWorldWidth / 1.9;
        final double strokeScreenX = worldcs.toScreenX(tower.getPosition().getX() - strokeWorldRadius);
        final double strokeScreenY = worldcs.toScreenY(tower.getPosition().getY() - strokeWorldRadius);

        renderer.addRenderTask(new RenderTask(RenderPriority.TOWER_RADIUS, () -> {
            gc.save();
            gc.setEffect(new Bloom(lineBloomThreshold));
            gc.setGlobalAlpha(lineAlpha);
            final Rotate r = new Rotate(rotationAngle,
                    ovalCenterScreenX,
                    ovalCenterScreenY);
            gc.setTransform(r.getMxx(), r.getMyx(), r.getMxy(), r.getMyy(), r.getTx(), r.getTy());
            gc.setLineWidth(lineWorldWidth * worldcs.getScale());
            gc.setLineDashes(lineDashes1, lineDashes2);
            gc.setLineCap(StrokeLineCap.ROUND);
            gc.setStroke(Color.WHITE);
            gc.strokeOval(strokeScreenX, strokeScreenY, strokeWorldRadius * 2 * worldcs.getScale(),
                    strokeWorldRadius * 2 * worldcs.getScale());
            // Draw radial gradient
            final double secondStopOffset = 0.9;
            final double secondStopAlpha = 0.1;
            final int maxByteValue = 255;
            final RadialGradient radialGradient = new RadialGradient(
                    0, 0, ovalCenterScreenX, ovalCenterScreenY,
                    worldTowerRange * worldcs.getScale(),
                    false, CycleMethod.NO_CYCLE,
                    new Stop(0, Color.TRANSPARENT),
                    new Stop(secondStopOffset, Color.rgb(maxByteValue, maxByteValue, maxByteValue, secondStopAlpha)),
                    new Stop(1, Color.WHITE)
            );
            gc.setFill(radialGradient);
            gc.fillOval(ovalScreenX, ovalScreenY, worldTowerRange * worldcs.getScale() * 2,
                    worldTowerRange * worldcs.getScale() * 2);
            gc.restore();
        }));

        super.render();
    }

    private static double easeInOutExpo(final double x) {
        if (x <= 0 || x >= 1) {
            return Math.clamp(x, 0, 1);
        }
        final double beforeHalfFactor = 20, afterHalfFactor = -20;
        return x < 0.5 ? Math.pow(2, beforeHalfFactor * x - 10) / 2 : (2 - Math.pow(2, afterHalfFactor * x + 10)) / 2;
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
        // Layout
        final double iconVMargin = width * LAYOUT.statsSvIconVMarginFactor;
        final double iconHMargin = iconVMargin * LAYOUT.statsSvIconHMarginFactor;
        final double iconSide = height - 2 * iconVMargin;
        final double titleX = x + iconSide + iconHMargin;
        final double titleWidth = width - iconSide - iconHMargin;
        final double titleHeight = height * LAYOUT.statsSvTitleHeightFactor;
        final double valueY = y + titleHeight;
        final double valueWidth = titleWidth * LAYOUT.statsSvValueWidthFactor;
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
        final int statsRows = Math.max(LAYOUT.statsRows, (int) Math.ceil((double) nStatsViews / LAYOUT.statsColumns));
        final double totVPadding = (statsRows - 1) * LAYOUT.statsVMargin;
        final double totHPadding = (LAYOUT.statsColumns - 1) * LAYOUT.statsHMargin;
        final double statHeight = (LAYOUT.statsOvrHeight - totVPadding) / statsRows;
        final double statWidth = (LAYOUT.statsOvrWidth - totHPadding) / LAYOUT.statsColumns;

        for (int i = 0; i < nStatsViews; i++) {
            final int row = i / LAYOUT.statsColumns;
            final int col = i % LAYOUT.statsColumns;
            final double x = LAYOUT.statsOvrX + col * (statWidth + LAYOUT.statsHMargin);
            final double y = LAYOUT.statsOvrY + row * (statHeight + LAYOUT.statsVMargin);
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
        Renderer.drawText(title, gc, cs, LAYOUT.selectorX, LAYOUT.selectorNameY + verticalOffset,
                LAYOUT.selectorNameWidth, LAYOUT.selectorNameHeight);
        gc.restore();
        // Should we draw the selector?
        if (info.canChangeType()) {
            return;
        }
        // Name icon
        Renderer.drawImage(getIcon(info), gc, cs, LAYOUT.selectorNameIconX,
                LAYOUT.selectorNameIconY + verticalOffset,
                LAYOUT.selectorNameIconSide, LAYOUT.selectorNameIconSide);
        // Upgrade selector
        final double emptySpace = LAYOUT.selectorUpgradeWidth - LAYOUT.selectorLevelMarkerWidth * info.getMaxLevel();
        for (int i = 0; i < info.getMaxLevel(); ++i) {
            final double x = LAYOUT.selectorUpgradeX
                    + i * (emptySpace / (info.getMaxLevel() - 1) + LAYOUT.selectorLevelMarkerWidth);
            final double y = LAYOUT.selectorUpgradeY + verticalOffset;
            final double width = LAYOUT.selectorLevelMarkerWidth;
            final double height = LAYOUT.selectorUpgradeHeight;
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
