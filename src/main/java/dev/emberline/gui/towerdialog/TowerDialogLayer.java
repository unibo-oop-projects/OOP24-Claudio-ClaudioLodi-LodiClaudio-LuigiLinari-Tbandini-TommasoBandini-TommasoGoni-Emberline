package dev.emberline.gui.towerdialog;

import dev.emberline.core.GameLoop;
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
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.Image;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class TowerDialogLayer extends GuiLayer {
    /**
     * Defines layout constants used for specifying GUI element dimensions.
     * The numbers are relative to the GUI coordinate system.
     *
     * @see dev.emberline.core.render.CoordinateSystem
     * @see Renderer#getGuiCoordinateSystem()
     */
    private static class Layout {
        // stats
        private static class Stats {
            // stats total area (with background image)
            private static final double WIDTH = 79. / 10.4;
            private static final double HEIGHT = 51. / 9.5;
            private static final double X = Layout.BG_X + (BG_WIDTH - WIDTH) / 2;
            private static final double Y = TITLE_Y + TITLE_HEIGHT + BG_HEIGHT * 0.008;
            // stats effective overlay area
            private static final double OVR_WIDTH = WIDTH * 0.9;
            private static final double OVR_HEIGHT = HEIGHT * 0.75;
            private static final double OVR_X = X + (WIDTH - OVR_WIDTH) / 2;
            private static final double OVR_Y = Y + (HEIGHT - OVR_HEIGHT) / 3.1;
            // stats overlay padding
            private static final double H_MARGIN = OVR_WIDTH * 0.055;
            private static final double V_MARGIN = OVR_HEIGHT * 0.04;
            // stats columns and rows
            private static final int COLUMNS = 2;
            private static final int ROWS = 3;
            // Single stat view layout
            private static final double SV_ICON_V_MARGIN_FACTOR = 0.05; // vertical margin relative to the width
            private static final double SV_ICON_H_MARGIN_FACTOR = 0.5; // horizontal margin relative to the vertical margin
            private static final double SV_TITLE_HEIGHT_FACTOR = 0.475; // title height relative to the height of the stat view
            private static final double SV_VALUE_WIDTH_FACTOR = 0.5; // value width relative to the title width
        }

        // Background
        private static final double BG_WIDTH = 9.2;
        private static final double BG_HEIGHT = 15.34;
        private static final double BG_X = Renderer.GUICS_WIDTH * 0.98 - BG_WIDTH;
        private static final double BG_Y = 0;
        // Title
        private static final double TITLE_WIDTH = 8 * 0.77;
        private static final double TITLE_HEIGHT = 2 * 0.52;
        private static final double TITLE_X = BG_X + (BG_WIDTH - TITLE_WIDTH) / 2;
        private static final double TITLE_Y = BG_Y + 2;

        // Selectors
        private static class Selector {
            private static final double TOP_MARGIN = 0.2;
            private static final double INNER_MARGIN = 0.1;
            private static final double WIDTH = 7.3;
            private static final double HEIGHT = 2;
            private static final double NAME_HEIGHT = 0.5;
            private static final double NAME_Y = Stats.Y + Stats.HEIGHT + AimButton.BTN_HEIGHT + TOP_MARGIN + AimButton.TOP_MARGIN;
            private static final double X = Stats.X + (Stats.WIDTH - WIDTH) / 2;
            private static final double Y = NAME_Y + NAME_HEIGHT + INNER_MARGIN;
            private static final double NAME_ICON_SIDE = NAME_HEIGHT * 1.7;
            private static final double NAME_ICON_X = X + WIDTH - NAME_ICON_SIDE;
            private static final double NAME_ICON_Y = NAME_Y + (NAME_HEIGHT - NAME_ICON_SIDE) / 2;
            private static final double NAME_WIDTH = WIDTH - NAME_ICON_SIDE - INNER_MARGIN;
            private static final double TOTAL_HEIGHT = NAME_HEIGHT + HEIGHT + INNER_MARGIN + TOP_MARGIN;
            private static final double UPGRADE_BTN_MARGIN = 0.1;
            private static final double UPGRADE_BTN_SIDE = HEIGHT - 2 * INNER_MARGIN;
            private static final double UPGRADE_BTN_X = X;
            private static final double UPGRADE_BTN_Y = Y + (HEIGHT - UPGRADE_BTN_SIDE) / 2;
            private static final double RESET_BTN_MARGIN = 0.1;
            private static final double RESET_BTN_WIDTH = UPGRADE_BTN_SIDE;
            private static final double RESET_BTN_HEIGHT = UPGRADE_BTN_SIDE;
            private static final double RESET_BTN_X = X + WIDTH - RESET_BTN_WIDTH;
            private static final double RESET_BTN_Y = Y + (HEIGHT - RESET_BTN_HEIGHT) / 2;
            private static final double UPGRADE_WIDTH = WIDTH - UPGRADE_BTN_MARGIN - UPGRADE_BTN_SIDE - RESET_BTN_WIDTH - RESET_BTN_MARGIN;
            private static final double UPGRADE_HEIGHT = HEIGHT - 2 * INNER_MARGIN;
            private static final double UPGRADE_X = UPGRADE_BTN_X + UPGRADE_BTN_SIDE + UPGRADE_BTN_MARGIN;
            private static final double UPGRADE_Y = Y + (HEIGHT - UPGRADE_HEIGHT) / 2;
            private static final double LEVEL_MARKER_WIDTH = UPGRADE_WIDTH / 4.3;
            private static final double TYPE_BTN_HEIGHT = HEIGHT - 2 * INNER_MARGIN;
            private static final double TYPE_BTN_WIDTH = WIDTH / 2 - INNER_MARGIN;
            private static final double TYPE_BTN_X = X + INNER_MARGIN;
            private static final double TYPE_BTN_Y = Y + (HEIGHT - TYPE_BTN_HEIGHT) / 2;
            private static final double TYPE_BTN_2_X = TYPE_BTN_X + TYPE_BTN_WIDTH + INNER_MARGIN;
        }

        private static class AimButton {
            private static final double TOP_MARGIN = -0.2;
            private static final double BTN_WIDTH = 4;
            private static final double BTN_HEIGHT = 1;
            private static final double BTN_X = Stats.X + (Stats.WIDTH - BTN_WIDTH) / 2;
            private static final double BTN_Y = Stats.Y + Stats.HEIGHT + TOP_MARGIN;
        }
    }

    /**
     * Defines colors and effects used for rendering the GUI elements.
     * The colors are defined using the {@link ColorAdjust} class, which allows adjusting the hue, saturation,
     * brightness, and contrast of the color.
     */
    private static class Colors {
        // To convert hue from 0-360 degrees to jfx range (-1 to 1):
        //hue = (hue > 180 ? hue - 360 : hue) / 180;
        //gc.setEffect(new ColorAdjust(hue,saturation,brightness,contrast));
        private static final ColorAdjust STAT_TITLE = new ColorAdjust(0.15, 0.9, -0.5, 0);
        private static final ColorAdjust STAT_VALUE = new ColorAdjust(0.15, 0.9, -0.3, 0);
        private static final ColorAdjust STAT_COMPARISON = new ColorAdjust(0.9444, 1, -0.3, 0);
        private static final ColorAdjust STAT_NEW_VALUE = new ColorAdjust(0.9444, 1, -0.3, 0);
        private static final ColorAdjust SELECTOR_TITLE = new ColorAdjust(0.15, 0.9, -0.6, 0);
    }

    // The Tower linked to this dialog layer
    private final Tower tower;
    // The current state of what is displayed in the dialog
    private EnchantmentInfo displayedEnchantment = null;
    private ProjectileInfo displayedProjectile = null;
    // Initial aim type is set to FIRST
    private AimType displayedAimType = AimType.FIRST;
    private AimType currentAimType = displayedAimType;
    // Tower Stats Views
    private List<TowerStatView> statsViews = null;
    // Data to display on button hover
    private final Map<GuiButton, TowerStatsProvider> hoverData = new HashMap<>();

    public TowerDialogLayer(final Tower tower) {
        super(Layout.BG_X, Layout.BG_Y, Layout.BG_WIDTH, Layout.BG_HEIGHT);
        this.tower = tower;
        updateLayout();
    }

    public Tower getTower() {
        return tower;
    }

    private void updateLayout() {
        // Clearing previous layout //
        super.buttons.clear();
        hoverData.clear();
        displayedEnchantment = Objects.requireNonNull(tower.getEnchantmentInfo(), "EnchantmentInfo cannot be null");
        displayedProjectile = Objects.requireNonNull(tower.getProjectileInfo(), "ProjectileInfo cannot be null");
        displayedAimType = currentAimType;

        // Building stats
        rebuildStats();
        // Building buttons
        addAimButton();
        addSelectorButtons(
                displayedEnchantment,
                new Image[]{SpriteLoader.loadSprite(SingleSpriteKey.ICE_BUTTON).image(), SpriteLoader.loadSprite(SingleSpriteKey.FIRE_BUTTON).image()},
                new EnchantmentInfo.Type[]{EnchantmentInfo.Type.ICE, EnchantmentInfo.Type.FIRE},
                0
        );
        addSelectorButtons(
                displayedProjectile,
                new Image[]{SpriteLoader.loadSprite(SingleSpriteKey.SMALL_BUTTON).image(), SpriteLoader.loadSprite(SingleSpriteKey.BIG_BUTTON).image()},
                new ProjectileInfo.Type[]{ProjectileInfo.Type.SMALL, ProjectileInfo.Type.BIG},
                Layout.Selector.TOTAL_HEIGHT
        );
        for (final GuiButton button : buttons) {
            button.setOnMouseEnter(this::refreshHoverData);
            button.setOnMouseLeave(this::refreshHoverData);
        }
    }

    private void addAimButton() {
        final GuiButton aimButton = new TextGuiButton(
                Layout.AimButton.BTN_X, Layout.AimButton.BTN_Y,
                Layout.AimButton.BTN_WIDTH, Layout.AimButton.BTN_HEIGHT,
                SpriteLoader.loadSprite(SingleSpriteKey.AIM_BUTTON).image(),
                currentAimType.displayName(), TextLayoutType.CENTER
        );
        aimButton.setOnClick(() -> {
            currentAimType = currentAimType.next();

            throwEvent(new SetTowerAimTypeEvent(aimButton, currentAimType));
        });
        buttons.add(aimButton);
    }

    private <T extends UpgradableInfo.InfoType, S extends UpgradableInfo<T, S>> void addSelectorButtons(final UpgradableInfo<T, S> element, final Image[] typeImages, final T[] typeValues, final double yOffset) {
        // if the current element can change type, we add the two type buttons
        if (element.canChangeType()) {
            final double[] x = {Layout.Selector.TYPE_BTN_X, Layout.Selector.TYPE_BTN_2_X};
            final double y = Layout.Selector.TYPE_BTN_Y + yOffset;
            for (int t = 0; t < typeValues.length; ++t) {
                final T typeValue = typeValues[t];
                final GuiButton button = new PricingGuiButton(
                        x[t], y, Layout.Selector.TYPE_BTN_WIDTH, Layout.Selector.TYPE_BTN_HEIGHT,
                        typeImages[t], -element.getUpgradeCost(), TextLayoutType.LEFT
                );
                button.setOnClick(() -> throwEvent(new SetTowerInfoEvent(this, tower, element, typeValue)));
                hoverData.put(button, (TowerStatsProvider) element.getChangeType(typeValue));
                buttons.add(button);
            }
        } else { // otherwise, we add the upgrade and reset button
            GuiButton upgradeButton = new PricingGuiButton(
                    Layout.Selector.UPGRADE_BTN_X, Layout.Selector.UPGRADE_BTN_Y + yOffset,
                    Layout.Selector.UPGRADE_BTN_SIDE, Layout.Selector.UPGRADE_BTN_SIDE,
                    SpriteLoader.loadSprite(SingleSpriteKey.UPGRADE_BUTTON).image(),
                    -element.getUpgradeCost(), TextLayoutType.BOTTOM
            );
            // On the last level, disable the upgrade button
            if (!element.canUpgrade()) {
                upgradeButton = new TextGuiButton(
                        Layout.Selector.UPGRADE_BTN_X, Layout.Selector.UPGRADE_BTN_Y + yOffset,
                        Layout.Selector.UPGRADE_BTN_SIDE, Layout.Selector.UPGRADE_BTN_SIDE,
                        SpriteLoader.loadSprite(SingleSpriteKey.DISABLED_UPGRADE_BUTTON).image(),
                        SpriteLoader.loadSprite(SingleSpriteKey.DISABLED_UPGRADE_BUTTON).image(),
                        "MAX", TextLayoutType.CENTER
                );
            }
            final GuiButton resetButton = new PricingGuiButton(
                    Layout.Selector.RESET_BTN_X, Layout.Selector.RESET_BTN_Y + yOffset,
                    Layout.Selector.RESET_BTN_WIDTH, Layout.Selector.RESET_BTN_HEIGHT,
                    SpriteLoader.loadSprite(SingleSpriteKey.CANCEL_BUTTON).image(),
                    element.getRefundValue(), TextLayoutType.BOTTOM
            );
            upgradeButton.setOnClick(() -> throwEvent(new UpgradeTowerInfoEvent(this, tower, element)));
            resetButton.setOnClick(() -> throwEvent(new ResetTowerInfoEvent(this, tower, element)));
            if (element.canUpgrade()) {
                hoverData.put(upgradeButton, (TowerStatsProvider) element.getUpgrade());
            }
            buttons.add(upgradeButton);
            buttons.add(resetButton);
        }
    }

    private void refreshHoverData() {
        for (final GuiButton button : buttons) {
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

    @Override
    public void render() {
        if (displayedEnchantment != tower.getEnchantmentInfo()) {
            updateLayout();
        }
        if (displayedProjectile != tower.getProjectileInfo()) {
            updateLayout();
        }
        if (displayedAimType != currentAimType) {
            updateLayout();
        }

        final Renderer renderer = GameLoop.getInstance().getRenderer();
        final GraphicsContext gc = renderer.getGraphicsContext();
        final CoordinateSystem guics = renderer.getGuiCoordinateSystem();

        renderer.addRenderTask(new RenderTask(RenderPriority.GUI, () -> {
            // Background
            Renderer.drawImage(SpriteLoader.loadSprite(SingleSpriteKey.TDL_BACKGROUND).image(), gc, guics, Layout.BG_X, Layout.BG_Y, Layout.BG_WIDTH, Layout.BG_HEIGHT);
            // Stats Background
            Renderer.drawImage(SpriteLoader.loadSprite(SingleSpriteKey.STATS_BACKGROUND).image(), gc, guics, Layout.Stats.X, Layout.Stats.Y, Layout.Stats.WIDTH, Layout.Stats.HEIGHT);
            // Stats Overlay
            drawStatsOverlay(statsViews, gc, guics);

            drawSelector(gc, guics, "Enchantment:", displayedEnchantment, 0);
            drawSelector(gc, guics, "Projectile:", displayedProjectile, Layout.Selector.TOTAL_HEIGHT);
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
    private static void drawStatView(final TowerStatView statView, final GraphicsContext gc, final CoordinateSystem cs, final double x, final double y, final double width, final double height) {
        gc.save();
        // Title and value strings
        final String displayName = statView.getStat().type().getDisplayName();
        final double statValue = statView.getStat().value();
        final String statValueStr = new DecimalFormat("0.##").format(statValue);
        // Value color effect
        final ColorAdjust valueColor = statView.getType() == TowerStatView.Type.NEW ? Colors.STAT_NEW_VALUE : Colors.STAT_VALUE;
        // Layout
        final double iconVMargin = width * Layout.Stats.SV_ICON_V_MARGIN_FACTOR;
        final double iconHMargin = iconVMargin * Layout.Stats.SV_ICON_H_MARGIN_FACTOR;
        final double iconSide = height - 2 * iconVMargin;
        final double titleX = x + iconSide + iconHMargin;
        final double titleWidth = width - iconSide - iconHMargin;
        final double titleHeight = height * Layout.Stats.SV_TITLE_HEIGHT_FACTOR;
        final double valueY = y + titleHeight;
        final double valueWidth = titleWidth * Layout.Stats.SV_VALUE_WIDTH_FACTOR;
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
            Renderer.drawText(comparisonStr, gc, cs, titleX + valueWidth, valueY, titleWidth - valueWidth, valueHeight);
        }
        gc.restore();
    }

    private static void drawStatsOverlay(final List<TowerStatView> statsViews, final GraphicsContext gc, final CoordinateSystem cs) {
        final int nStatsViews = statsViews.size();
        final int statsRows = Math.max(Layout.Stats.ROWS, (int) Math.ceil((double) nStatsViews / Layout.Stats.COLUMNS));
        final double totVPadding = (statsRows - 1) * Layout.Stats.V_MARGIN;
        final double totHPadding = (Layout.Stats.COLUMNS - 1) * Layout.Stats.H_MARGIN;
        final double statHeight = (Layout.Stats.OVR_HEIGHT - totVPadding) / statsRows;
        final double statWidth = (Layout.Stats.OVR_WIDTH - totHPadding) / Layout.Stats.COLUMNS;

        for (int i = 0; i < nStatsViews; i++) {
            final int row = i / Layout.Stats.COLUMNS;
            final int col = i % Layout.Stats.COLUMNS;
            final double x = Layout.Stats.OVR_X + col * (statWidth + Layout.Stats.H_MARGIN);
            final double y = Layout.Stats.OVR_Y + row * (statHeight + Layout.Stats.V_MARGIN);
            drawStatView(statsViews.get(i), gc, cs, x, y, statWidth, statHeight);
        }
    }

    // SELECTORS DRAWING //
    private static void drawSelector(final GraphicsContext gc, final CoordinateSystem cs, final String title, final UpgradableInfo<?, ?> info, final double verticalOffset) {
        // Title
        gc.save();
        gc.setEffect(Colors.SELECTOR_TITLE);
        Renderer.drawText(title, gc, cs, Layout.Selector.X, Layout.Selector.NAME_Y + verticalOffset, Layout.Selector.NAME_WIDTH, Layout.Selector.NAME_HEIGHT);
        gc.restore();
        // Should we draw the selector?
        if (info.canChangeType()) {
            return;
        }
        // Name icon
        Renderer.drawImage(getIcon(info), gc, cs, Layout.Selector.NAME_ICON_X, Layout.Selector.NAME_ICON_Y + verticalOffset, Layout.Selector.NAME_ICON_SIDE, Layout.Selector.NAME_ICON_SIDE);
        // Upgrade selector
        final double emptySpace = Layout.Selector.UPGRADE_WIDTH - Layout.Selector.LEVEL_MARKER_WIDTH * info.getMaxLevel();
        for (int i = 0; i < info.getMaxLevel(); ++i) {
            final double x = Layout.Selector.UPGRADE_X + i * (emptySpace / (info.getMaxLevel() - 1) + Layout.Selector.LEVEL_MARKER_WIDTH);
            final double y = Layout.Selector.UPGRADE_Y + verticalOffset;
            final double width = Layout.Selector.LEVEL_MARKER_WIDTH;
            final double height = Layout.Selector.UPGRADE_HEIGHT;
            final Image sprite = i < info.level() ? SpriteLoader.loadSprite(SingleSpriteKey.FULL_UPGRADE_LEVEL).image() : SpriteLoader.loadSprite(SingleSpriteKey.EMPTY_UPGRADE_LEVEL).image();
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
