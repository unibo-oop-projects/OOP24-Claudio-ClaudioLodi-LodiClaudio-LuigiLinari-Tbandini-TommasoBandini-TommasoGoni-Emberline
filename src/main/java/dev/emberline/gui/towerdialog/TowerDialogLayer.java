package dev.emberline.gui.towerdialog;

import dev.emberline.core.GameLoop;
import dev.emberline.core.render.CoordinateSystem;
import dev.emberline.core.render.RenderPriority;
import dev.emberline.core.render.RenderTask;
import dev.emberline.core.render.Renderer;
import dev.emberline.game.model.EnchantmentInfo;
import dev.emberline.game.model.ProjectileInfo;
import dev.emberline.game.model.TowerInfoProvider;
import dev.emberline.game.model.UpgradableInfo;
import dev.emberline.gui.GuiButton;
import dev.emberline.gui.GuiLayer;
import dev.emberline.gui._TemporarySpriteProvider;
import dev.emberline.gui.event.ResetTowerInfoEvent;
import dev.emberline.gui.event.SetTowerInfoEvent;
import dev.emberline.gui.event.UpgradeTowerInfoEvent;
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
            private static final double Y = TITLE_Y + TITLE_HEIGHT + BG_HEIGHT * 0.03;
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
        private static final double BG_WIDTH = 8 * 1.15;
        private static final double BG_HEIGHT = 15 * 1.023;
        private static final double BG_X = Renderer.GUICS_WIDTH * 0.98 - BG_WIDTH;
        private static final double BG_Y = 0;
        // Title
        private static final double TITLE_WIDTH = 8 * 0.77;
        private static final double TITLE_HEIGHT = 2 * 0.67;
        private static final double TITLE_X = BG_X + (BG_WIDTH - TITLE_WIDTH) / 2;
        private static final double TITLE_Y = BG_Y + 2;
        // Selectors
        private static class Selector {
            private static final double TOP_MARGIN = 0.5;
            private static final double INNER_MARGIN = 0.1;
            private static final double WIDTH = 7.3;
            private static final double HEIGHT = 1.5;
            private static final double NAME_HEIGHT = 0.5;
            private static final double NAME_Y = Stats.Y + Stats.HEIGHT + TOP_MARGIN;
            private static final double X = Stats.X + (Stats.WIDTH - WIDTH) / 2;
            private static final double Y = NAME_Y + NAME_HEIGHT + INNER_MARGIN;
            private static final double NAME_ICON_SIDE = NAME_HEIGHT * 1.7;
            private static final double NAME_ICON_X = X + WIDTH - NAME_ICON_SIDE;
            private static final double NAME_ICON_Y = NAME_Y + (NAME_HEIGHT - NAME_ICON_SIDE) / 2;
            private static final double NAME_WIDTH = WIDTH - NAME_ICON_SIDE - INNER_MARGIN;
            private static final double TOTAL_HEIGHT = NAME_HEIGHT + HEIGHT + INNER_MARGIN + TOP_MARGIN;
            private static final double UPGRADE_BTN_MARGIN = 0.4;
            private static final double UPGRADE_BTN_SIDE = HEIGHT - 2 * INNER_MARGIN;
            private static final double UPGRADE_BTN_X = X;
            private static final double UPGRADE_BTN_Y = Y + (HEIGHT - UPGRADE_BTN_SIDE) / 2;
            private static final double RESET_BTN_MARGIN = 0.8;
            private static final double RESET_BTN_WIDTH = UPGRADE_BTN_SIDE;
            private static final double RESET_BTN_HEIGHT = UPGRADE_BTN_SIDE;
            private static final double RESET_BTN_X = X + WIDTH - RESET_BTN_WIDTH;
            private static final double RESET_BTN_Y = Y + (HEIGHT - RESET_BTN_HEIGHT) / 2;
            private static final double UPGRADE_WIDTH = WIDTH - UPGRADE_BTN_MARGIN - UPGRADE_BTN_SIDE - RESET_BTN_WIDTH - RESET_BTN_MARGIN;
            private static final double UPGRADE_HEIGHT = HEIGHT - 2 * INNER_MARGIN;
            private static final double UPGRADE_X = UPGRADE_BTN_X + UPGRADE_BTN_SIDE + UPGRADE_BTN_MARGIN;
            private static final double UPGRADE_Y = Y + (HEIGHT - UPGRADE_HEIGHT) / 2;
            private static final double LEVEL_MARKER_WIDTH = UPGRADE_WIDTH / 5;
            private static final double TYPE_BTN_HEIGHT = HEIGHT - 2 * INNER_MARGIN;
            private static final double TYPE_BTN_WIDTH = WIDTH / 2 - INNER_MARGIN;
            private static final double TYPE_BTN_X = X + INNER_MARGIN;
            private static final double TYPE_BTN_Y = Y + (HEIGHT - TYPE_BTN_HEIGHT) / 2;
            private static final double TYPE_BTN_2_X = TYPE_BTN_X + TYPE_BTN_WIDTH + INNER_MARGIN;
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

    // The TowerInfoProvider linked to this dialog layer
    private final TowerInfoProvider towerInfoProvider;
    // The current state of what is displayed in the dialog
    private EnchantmentInfo displayedEnchantment = null;
    private ProjectileInfo displayedProjectile = null;
    // Tower Stats Views
    private List<TowerStatView> statsViews = null;
    // Data to display on button hover
    private final Map<GuiButton, TowerStatsProvider> hoverData = new HashMap<>();

    public TowerDialogLayer(TowerInfoProvider towerInfoProvider) {
        this.towerInfoProvider = towerInfoProvider;
        updateLayout();
    }

    private void updateLayout() {
        // Clearing previous layout //
        super.buttons.clear();
        hoverData.clear();
        displayedEnchantment = Objects.requireNonNull(towerInfoProvider.getEnchantmentInfo(), "EnchantmentInfo cannot be null");
        displayedProjectile = Objects.requireNonNull(towerInfoProvider.getProjectileInfo(), "ProjectileInfo cannot be null");

        // Building stats
        rebuildStats();
        // Building buttons
        addSelectorButtons(
                displayedEnchantment,
                new Image[]{_TemporarySpriteProvider.ICE_BUTTON.getImage(), _TemporarySpriteProvider.FIRE_BUTTON.getImage()},
                new EnchantmentInfo.Type[]{EnchantmentInfo.Type.ICE, EnchantmentInfo.Type.FIRE},
                0
        );
        addSelectorButtons(
                displayedProjectile,
                new Image[]{_TemporarySpriteProvider.SMALL_BUTTON.getImage(), _TemporarySpriteProvider.BIG_BUTTON.getImage()},
                new ProjectileInfo.Type[]{ProjectileInfo.Type.SMALL, ProjectileInfo.Type.BIG},
                Layout.Selector.TOTAL_HEIGHT
        );
        for (GuiButton button : buttons) {
            button.setOnMouseEnter(this::refreshHoverData);
            button.setOnMouseLeave(this::refreshHoverData);
        }
    }

    private <T extends UpgradableInfo.InfoType, S extends UpgradableInfo<T, S>> void addSelectorButtons(UpgradableInfo<T,S> element, Image[] typeImages, T[] typeValues, double yOffset) {
        // if the current element can change type, we add the two type buttons
        if (element.canChangeType()) {
            double[] x = {Layout.Selector.TYPE_BTN_X, Layout.Selector.TYPE_BTN_2_X};
            double y = Layout.Selector.TYPE_BTN_Y + yOffset;
            for (int t = 0; t < typeValues.length; ++t) {
                T typeValue = typeValues[t];
                GuiButton button = new PricingGuiButton(
                        x[t], y, Layout.Selector.TYPE_BTN_WIDTH, Layout.Selector.TYPE_BTN_HEIGHT,
                        typeImages[t], -element.getUpgradeCost()
                );
                button.setOnClick(() -> throwEvent(new SetTowerInfoEvent(this, null, element, typeValue)));
                hoverData.put(button, (TowerStatsProvider) element.getChangeType(typeValue));
                buttons.add(button);
            }
        } else { // otherwise, we add the upgrade and reset button
            GuiButton upgradeButton = new PricingGuiButton(
                    Layout.Selector.UPGRADE_BTN_X, Layout.Selector.UPGRADE_BTN_Y + yOffset,
                    Layout.Selector.UPGRADE_BTN_SIDE, Layout.Selector.UPGRADE_BTN_SIDE,
                    _TemporarySpriteProvider.UPGRADE_BUTTON.getImage(), -element.getUpgradeCost()
            );
            // On the last level, disable the upgrade button
            if (!element.canUpgrade()) {
                upgradeButton = new GuiButton(
                        Layout.Selector.UPGRADE_BTN_X, Layout.Selector.UPGRADE_BTN_Y + yOffset,
                        Layout.Selector.UPGRADE_BTN_SIDE, Layout.Selector.UPGRADE_BTN_SIDE,
                        _TemporarySpriteProvider.DISABLED_UPGRADE_BUTTON.getImage()
                );
            }
            GuiButton resetButton = new GuiButton(
                    Layout.Selector.RESET_BTN_X, Layout.Selector.RESET_BTN_Y + yOffset,
                    Layout.Selector.RESET_BTN_WIDTH, Layout.Selector.RESET_BTN_HEIGHT,
                    _TemporarySpriteProvider.CANCEL_BUTTON.getImage()
            );
            upgradeButton.setOnClick(() -> throwEvent(new UpgradeTowerInfoEvent(this, null, element)));
            resetButton.setOnClick(() -> throwEvent(new ResetTowerInfoEvent(this, null, element)));
            if (element.canUpgrade()) hoverData.put(upgradeButton, (TowerStatsProvider) element.getUpgrade());
            buttons.add(upgradeButton);
            buttons.add(resetButton);
        }
    }

    private void refreshHoverData() {
        for (GuiButton button : buttons) {
            TowerStatsProvider hoverStats = hoverData.get(button);
            if (button.isHovered()) {
                if (hoverStats != null) rebuildStatsWithCompared(hoverStats);
                return;
            }
        }
        rebuildStats();
    }

    @Override
    public void render() {
        if (displayedEnchantment != towerInfoProvider.getEnchantmentInfo()) updateLayout();
        if (displayedProjectile != towerInfoProvider.getProjectileInfo()) updateLayout();

        Renderer renderer = GameLoop.getInstance().getRenderer();
        GraphicsContext gc = renderer.getGraphicsContext();
        CoordinateSystem guics = renderer.getGuiCoordinateSystem();

        renderer.addRenderTask(new RenderTask(RenderPriority.GUI, () -> {
            // Background
            Renderer.drawImage(_TemporarySpriteProvider.TDL_BACKGROUND.getImage(), gc, guics, Layout.BG_X, Layout.BG_Y, Layout.BG_WIDTH, Layout.BG_HEIGHT);
            // Title
            Renderer.drawImageFit(_TemporarySpriteProvider.TOWER_TEXT.getImage(), gc, guics, Layout.TITLE_X, Layout.TITLE_Y, Layout.TITLE_WIDTH, Layout.TITLE_HEIGHT);
            // Stats Background
            Renderer.drawImage(_TemporarySpriteProvider.STATS_BACKGROUND.getImage(), gc, guics, Layout.Stats.X, Layout.Stats.Y, Layout.Stats.WIDTH, Layout.Stats.HEIGHT);
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

    private void rebuildStatsWithCompared(TowerStatsProvider... comparedStatsProviders) {
        TowerStatsViewsBuilder builder = new TowerStatsViewsBuilder();
        builder.addStats(displayedEnchantment).addStats(displayedProjectile);
        for (TowerStatsProvider comparedStats : comparedStatsProviders) builder.addComparedStats(comparedStats);
        statsViews = builder.build();
    }

    // STATS DRAWING //
    private static void drawStatView(TowerStatView statView, GraphicsContext gc, CoordinateSystem cs, double x, double y, double width, double height) {
        gc.save();
        // Title and value strings
        String displayName = statView.getStat().type().getDisplayName();
        double statValue = statView.getStat().value();
        String statValueStr = new DecimalFormat("0.##").format(statValue);
        // Value color effect
        ColorAdjust valueColor = (statView.getType() == TowerStatView.Type.NEW) ? Colors.STAT_NEW_VALUE : Colors.STAT_VALUE;
        // Layout
        double iconVMargin = width * Layout.Stats.SV_ICON_V_MARGIN_FACTOR;
        double iconHMargin = iconVMargin * Layout.Stats.SV_ICON_H_MARGIN_FACTOR;
        double iconSide = height - 2*iconVMargin;
        double titleX = x + iconSide + iconHMargin;
        double titleWidth = width - iconSide - iconHMargin;
        double titleHeight = height * Layout.Stats.SV_TITLE_HEIGHT_FACTOR;
        double valueY = y + titleHeight;
        double valueWidth = titleWidth * Layout.Stats.SV_VALUE_WIDTH_FACTOR;
        double valueHeight = height - titleHeight;
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
            double comparedValue = statView.getComparedStat().value();
            String comparisonStr = new DecimalFormat("+0.##;-0.##").format(comparedValue - statValue);
            gc.setEffect(Colors.STAT_COMPARISON);
            Renderer.drawText(comparisonStr, gc, cs, titleX + valueWidth, valueY, titleWidth - valueWidth, valueHeight);
        }
        gc.restore();
    }

    private static void drawStatsOverlay(List<TowerStatView> statsViews, GraphicsContext gc, CoordinateSystem cs) {
        int nStatsViews = statsViews.size();
        int statsRows = Math.max(Layout.Stats.ROWS, (int) Math.ceil((double) nStatsViews / Layout.Stats.COLUMNS));
        double totVPadding = (statsRows - 1) * Layout.Stats.V_MARGIN;
        double totHPadding = (Layout.Stats.COLUMNS - 1) * Layout.Stats.H_MARGIN;
        double statHeight = (Layout.Stats.OVR_HEIGHT - totVPadding) / statsRows;
        double statWidth = (Layout.Stats.OVR_WIDTH - totHPadding) / Layout.Stats.COLUMNS;

        for (int i = 0; i < nStatsViews; i++) {
            int row = i / Layout.Stats.COLUMNS;
            int col = i % Layout.Stats.COLUMNS;
            double x = Layout.Stats.OVR_X + col * (statWidth + Layout.Stats.H_MARGIN);
            double y = Layout.Stats.OVR_Y + row * (statHeight + Layout.Stats.V_MARGIN);
            drawStatView(statsViews.get(i), gc, cs, x, y, statWidth, statHeight);
        }
    }

    // SELECTORS DRAWING //
    private static void drawSelector(GraphicsContext gc, CoordinateSystem cs, String title, UpgradableInfo<?, ?> info, double verticalOffset) {
        // Title
        gc.save();
        gc.setEffect(Colors.SELECTOR_TITLE);
        Renderer.drawText(title, gc, cs, Layout.Selector.X, Layout.Selector.NAME_Y + verticalOffset, Layout.Selector.NAME_WIDTH, Layout.Selector.NAME_HEIGHT);
        gc.restore();
        // Should we draw the selector?
        if (info.canChangeType()) return;
        // Name icon
        Renderer.drawImage(getIcon(info), gc, cs, Layout.Selector.NAME_ICON_X, Layout.Selector.NAME_ICON_Y + verticalOffset, Layout.Selector.NAME_ICON_SIDE, Layout.Selector.NAME_ICON_SIDE);
        // Upgrade selector
        double emptySpace = Layout.Selector.UPGRADE_WIDTH - Layout.Selector.LEVEL_MARKER_WIDTH * info.getMaxLevel();
        for (int i = 0; i < info.getMaxLevel(); ++i) {
            double x = Layout.Selector.UPGRADE_X + i * (emptySpace / (info.getMaxLevel() - 1) + Layout.Selector.LEVEL_MARKER_WIDTH);
            double y = Layout.Selector.UPGRADE_Y + verticalOffset;
            double width = Layout.Selector.LEVEL_MARKER_WIDTH;
            double height = Layout.Selector.UPGRADE_HEIGHT;
            Image sprite = (i < info.level()) ? _TemporarySpriteProvider.FULL_UPGRADE_LEVEL.getImage() : _TemporarySpriteProvider.EMPTY_UPGRADE_LEVEL.getImage();
            Renderer.drawImage(sprite, gc, cs, x, y, width, height);
        }
    }

    // Utility method to get the icon for the given UpgradableInfo, does not cover the BASE types.
    // If an icon cannot be found, an empty image is returned (the space character).
    private static Image getIcon(UpgradableInfo<?, ?> info) {
        Image empty = _TemporarySpriteProvider.getCharImage(' ');
        return switch (info) {
            case EnchantmentInfo e -> {
                if (e.type() == EnchantmentInfo.Type.FIRE) yield _TemporarySpriteProvider.FIRE_ICON.getImage();
                if (e.type() == EnchantmentInfo.Type.ICE) yield _TemporarySpriteProvider.ICE_ICON.getImage();
                yield empty;
            }
            case ProjectileInfo p -> {
                if (p.type() == ProjectileInfo.Type.SMALL) yield _TemporarySpriteProvider.SMALL_ICON.getImage();
                if (p.type() == ProjectileInfo.Type.BIG) yield _TemporarySpriteProvider.BIG_ICON.getImage();
                yield empty;
            }
            default -> empty;
        };
    }
}
