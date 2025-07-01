package dev.emberline.gui.menu;

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
import dev.emberline.core.sounds.AudioController;
import dev.emberline.game.GameState;
import dev.emberline.gui.GuiButton;
import dev.emberline.gui.GuiLayer;
import dev.emberline.gui.event.CloseOptionsEvent;
import dev.emberline.gui.event.SetMainMenuEvent;
import dev.emberline.preferences.PreferenceKey;
import dev.emberline.preferences.PreferencesManager;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.Image;

/**
 * Represents the "Options" menu in the game, providing functionality for rendering
 * and interacting with the options interface of the GUI layer. This class controls
 * the layout of buttons and the display of the background and window for the options menu.
 */
public class Options extends GuiLayer implements GameState {
    private final OptionsBounds optionsBounds;
    private final Layout layout;
    private final boolean showMenuButton;

    private static final class Colors {
        private static final ColorAdjust OPTIONS_WRITINGS = new ColorAdjust(0.15, 0.9, -0.3, 0);
    }

    private record OptionsBounds(
            @JsonProperty
            int topLeftX,
            @JsonProperty
            int topLeftY,
            @JsonProperty
            int bottomRightX,
            @JsonProperty
            int bottomRightY
    ) {
        // Data validation
        private OptionsBounds {
            if (topLeftX >= bottomRightX || topLeftY >= bottomRightY) {
                throw new IllegalArgumentException("Invalid options optionsBounds: " + this);
            }
        }
    }

    private record Layout(
            @JsonProperty 
            double bgWidth,
            @JsonProperty 
            double bgHeight,
            @JsonProperty 
            double windowBgWidth,
            @JsonProperty 
            double windowBgHeight,
            @JsonProperty 
            double windowBgX,
            @JsonProperty 
            double windowBgY,
            @JsonProperty 
            double btnScale,
            @JsonProperty 
            double btnWidth,
            @JsonProperty 
            double btnHeight,
            @JsonProperty 
            double rowHeight,
            @JsonProperty 
            double rowPadding,
            @JsonProperty 
            double rowStartX,
            @JsonProperty 
            double rowWidth,
            @JsonProperty 
            double verticalSpacing,
            @JsonProperty 
            double firstRowOffsetY,
            @JsonProperty 
            double labelWidth,
            @JsonProperty 
            double controlsWidth,
            @JsonProperty 
            double controlsStartX,
            @JsonProperty 
            double percentageWidth,
            @JsonProperty 
            double buttonSpacing,
            @JsonProperty 
            double minusOffsetX,
            @JsonProperty 
            double plusOffsetX,
            @JsonProperty 
            double checkboxOffsetX,
            @JsonProperty 
            double musicVolumeY,
            @JsonProperty 
            double musicCheckboxY,
            @JsonProperty 
            double sfxVolumeY,
            @JsonProperty 
            double sfxCheckboxY,
            @JsonProperty 
            double fullscreenCheckboxY,
            @JsonProperty 
            double navScaleFactor,
            @JsonProperty 
            double btnBackHeight,
            @JsonProperty 
            double btnBackWidth,
            @JsonProperty 
            double btnBackX,
            @JsonProperty 
            double btnBackY,
            @JsonProperty 
            double btnMenuHeight,
            @JsonProperty 
            double btnMenuWidth,
            @JsonProperty 
            double btnMenuX,
            @JsonProperty 
            double btnMenuY
    ) {
    }

    /**
     * Constructs an {@code Options} object by initializing it with the configuration
     * data loaded from a predefined JSON resource file. The configuration provides
     * the optionsBounds necessary for setting up the {@code Options} screen in the GUI.
     *
     * @param showMenuButton a boolean representing whether the options menu should or should not
     *                       have a menu button
     * @throws RuntimeException if the configuration file cannot be loaded or parsed.
     * @see Options
     */
    public Options(final boolean showMenuButton) {
        this(ConfigLoader.loadConfig("/gui/guiBounds.json", OptionsBounds.class),
             ConfigLoader.loadConfig("/gui/options/options.json", Layout.class), showMenuButton);
    }

    private Options(final OptionsBounds optionsBounds, final Layout layout, final boolean showMenuButton) {
        super(optionsBounds.topLeftX,
                optionsBounds.topLeftY,
                optionsBounds.bottomRightX - optionsBounds.topLeftX,
                optionsBounds.bottomRightY - optionsBounds.topLeftY);
        this.optionsBounds = optionsBounds;
        this.showMenuButton = showMenuButton;
        this.layout = layout;
    }

    private void updateLayout() {
        super.getButtons().clear();

        addMusicVolumeControl();
        addMusicCheckbox();
        addSfxVolumeControl();
        addSfxCheckbox();
        addFullScreenCheckbox();

        addCloseOptionsButton();
        if (showMenuButton) {
            addMenuOptionsButton();
        }
    }

    private void addMusicVolumeControl() {
        final int musicVolume = (int) (PreferencesManager.getDoublePreference(PreferenceKey.MUSIC_VOLUME) * 100);

        Image minusButton = SpriteLoader.loadSprite(SingleSpriteKey.OPTIONS_MINUS_BUTTON).image();
        Image minusButtonHover = SpriteLoader.loadSprite(SingleSpriteKey.OPTIONS_MINUS_BUTTON_HOVER).image();
        if (musicVolume <= 0) {
            minusButton = SpriteLoader.loadSprite(SingleSpriteKey.OPTIONS_MINUS_BUTTON_DISABLED).image();
            minusButtonHover = SpriteLoader.loadSprite(SingleSpriteKey.OPTIONS_MINUS_BUTTON_DISABLED).image();
        }

        Image plusButton = SpriteLoader.loadSprite(SingleSpriteKey.OPTIONS_PLUS_BUTTON).image();
        Image plusButtonHover = SpriteLoader.loadSprite(SingleSpriteKey.OPTIONS_PLUS_BUTTON_HOVER).image();
        if (musicVolume >= 100) {
            plusButton = SpriteLoader.loadSprite(SingleSpriteKey.OPTIONS_PLUS_BUTTON_DISABLED).image();
            plusButtonHover = SpriteLoader.loadSprite(SingleSpriteKey.OPTIONS_PLUS_BUTTON_DISABLED).image();
        }

        final GuiButton musicMinusVolumeControl = createMusicVolumeControlButton(layout.minusOffsetX,
                minusButton, minusButtonHover, musicVolume - 10 < 0, musicVolume - 10);
        super.getButtons().add(musicMinusVolumeControl);

        final GuiButton musicPlusVolumeControl = createMusicVolumeControlButton(layout.plusOffsetX,
                plusButton, plusButtonHover, musicVolume + 10 > 100, musicVolume + 10);
        super.getButtons().add(musicPlusVolumeControl);
    }

    private GuiButton createMusicVolumeControlButton(
            final double xOffset, final Image buttonImage, final Image buttonHoverImage,
            final boolean volumeBound, final int volumeToSet) {
        final GuiButton musicVolumeControl = new GuiButton(
                layout.controlsStartX + xOffset,
                layout.musicVolumeY + (layout.rowHeight - layout.btnHeight) / 2,
                layout.btnWidth,
                layout.btnHeight,
                buttonImage,
                buttonHoverImage
        );
        musicVolumeControl.setOnClick(() -> {
            if (volumeBound) {
                return; // Do not change volume if already at the limit
            }
            final double volumeToSetDouble = volumeToSet / 100.0;
            PreferencesManager.setDoublePreference(PreferenceKey.MUSIC_VOLUME, volumeToSetDouble);
            AudioController.requestSetMusicVolume(this, volumeToSetDouble);
        });
        return musicVolumeControl;
    }

    private void addMusicCheckbox() {
        final boolean isMusicMuted = PreferencesManager.getBooleanPreference(PreferenceKey.MUSIC_MUTE);

        final Image checkboxImage = isMusicMuted
                ? SpriteLoader.loadSprite(SingleSpriteKey.OPTIONS_CHECKBOX_FULL).image()
                : SpriteLoader.loadSprite(SingleSpriteKey.OPTIONS_CHECKBOX_EMPTY).image();
        final Image checkboxHoverImage = isMusicMuted
                ? SpriteLoader.loadSprite(SingleSpriteKey.OPTIONS_CHECKBOX_FULL_HOVER).image()
                : SpriteLoader.loadSprite(SingleSpriteKey.OPTIONS_CHECKBOX_EMPTY_HOVER).image();

        final GuiButton musicMuteCheckbox = new GuiButton(
            layout.controlsStartX + layout.checkboxOffsetX,
            layout.musicCheckboxY + (layout.rowHeight - layout.btnHeight) / 2,
            layout.btnWidth,
            layout.btnHeight,
            checkboxImage,
            checkboxHoverImage
        );
        musicMuteCheckbox.setOnClick(() -> {
            final boolean newMuteState = !isMusicMuted;
            PreferencesManager.setBooleanPreference(PreferenceKey.MUSIC_MUTE, newMuteState);
            AudioController.requestToggleMusicMute(this, newMuteState);
        });
        super.getButtons().add(musicMuteCheckbox);
    }

    private void addSfxVolumeControl() {
        final int sfxVolume = (int) (PreferencesManager.getDoublePreference(PreferenceKey.SFX_VOLUME) * 100);

        Image minusButton = SpriteLoader.loadSprite(SingleSpriteKey.OPTIONS_MINUS_BUTTON).image();
        Image minusButtonHover = SpriteLoader.loadSprite(SingleSpriteKey.OPTIONS_MINUS_BUTTON_HOVER).image();
        if (sfxVolume <= 0) {
            minusButton = SpriteLoader.loadSprite(SingleSpriteKey.OPTIONS_MINUS_BUTTON_DISABLED).image();
            minusButtonHover = SpriteLoader.loadSprite(SingleSpriteKey.OPTIONS_MINUS_BUTTON_DISABLED).image();
        }

        Image plusButton = SpriteLoader.loadSprite(SingleSpriteKey.OPTIONS_PLUS_BUTTON).image();
        Image plusButtonHover = SpriteLoader.loadSprite(SingleSpriteKey.OPTIONS_PLUS_BUTTON_HOVER).image();
        if (sfxVolume >= 100) {
            plusButton = SpriteLoader.loadSprite(SingleSpriteKey.OPTIONS_PLUS_BUTTON_DISABLED).image();
            plusButtonHover = SpriteLoader.loadSprite(SingleSpriteKey.OPTIONS_PLUS_BUTTON_DISABLED).image();
        }

        final GuiButton sfxMinusVolumeControl = new GuiButton(
            layout.controlsStartX + layout.minusOffsetX, 
            layout.sfxVolumeY + (layout.rowHeight - layout.btnHeight) / 2, 
            layout.btnWidth, 
            layout.btnHeight,
            minusButton,
            minusButtonHover
        );
        sfxMinusVolumeControl.setOnClick(() -> {
            if (sfxVolume - 10 < 0) {
                return; // Do not decrease volume if already at minimum
            }
            final double newSfxVolume = (sfxVolume - 10) / 100.0;
            PreferencesManager.setDoublePreference(PreferenceKey.SFX_VOLUME, newSfxVolume);
            AudioController.requestSetSfxVolume(this);
        });
        super.getButtons().add(sfxMinusVolumeControl);

        final GuiButton sfxPlusVolumeControl = new GuiButton(
            layout.controlsStartX + layout.plusOffsetX, 
            layout.sfxVolumeY + (layout.rowHeight - layout.btnHeight) / 2, 
            layout.btnWidth, 
            layout.btnHeight,
            plusButton,
            plusButtonHover
        );
        sfxPlusVolumeControl.setOnClick(() -> {
            if (sfxVolume + 10 > 100) {
                return; // Do not increase volume if already at maximum
            }
            final double newSfxVolume = (sfxVolume + 10) / 100.0;
            PreferencesManager.setDoublePreference(PreferenceKey.SFX_VOLUME, newSfxVolume);
            AudioController.requestSetSfxVolume(this);
        });
        super.getButtons().add(sfxPlusVolumeControl);
    }

    private void addSfxCheckbox() {
        final boolean isSfxMuted = PreferencesManager.getBooleanPreference(PreferenceKey.SFX_MUTE);

        final Image checkboxImage = isSfxMuted
                ? SpriteLoader.loadSprite(SingleSpriteKey.OPTIONS_CHECKBOX_FULL).image()
                : SpriteLoader.loadSprite(SingleSpriteKey.OPTIONS_CHECKBOX_EMPTY).image();
        final Image checkboxHoverImage = isSfxMuted
                ? SpriteLoader.loadSprite(SingleSpriteKey.OPTIONS_CHECKBOX_FULL_HOVER).image()
                : SpriteLoader.loadSprite(SingleSpriteKey.OPTIONS_CHECKBOX_EMPTY_HOVER).image();

        final GuiButton sfxMuteCheckbox = new GuiButton(
            layout.controlsStartX + layout.checkboxOffsetX,
            layout.sfxCheckboxY + (layout.rowHeight - layout.btnHeight) / 2,
            layout.btnWidth,
            layout.btnHeight,
            checkboxImage,
            checkboxHoverImage
        );
        sfxMuteCheckbox.setOnClick(() -> {
            final boolean newMuteState = !isSfxMuted;
            PreferencesManager.setBooleanPreference(PreferenceKey.SFX_MUTE, newMuteState);
            AudioController.requestToggleSfxMute(this);
        });
        super.getButtons().add(sfxMuteCheckbox);
    }

    private void addFullScreenCheckbox() {
        final boolean isFullscreen = PreferencesManager.getBooleanPreference(PreferenceKey.FULLSCREEN);
        final Image checkboxImage = isFullscreen
                ? SpriteLoader.loadSprite(SingleSpriteKey.OPTIONS_CHECKBOX_FULL).image()
                : SpriteLoader.loadSprite(SingleSpriteKey.OPTIONS_CHECKBOX_EMPTY).image();
        final Image checkboxHoverImage = isFullscreen
                ? SpriteLoader.loadSprite(SingleSpriteKey.OPTIONS_CHECKBOX_FULL_HOVER).image()
                : SpriteLoader.loadSprite(SingleSpriteKey.OPTIONS_CHECKBOX_EMPTY_HOVER).image();

        final GuiButton fullscreenCheckbox = new GuiButton(
            layout.controlsStartX + layout.checkboxOffsetX,
            layout.fullscreenCheckboxY + (layout.rowHeight - layout.btnHeight) / 2,
            layout.btnWidth,
            layout.btnHeight,
            checkboxImage,
            checkboxHoverImage
        );
        fullscreenCheckbox.setOnClick(() -> {
            final boolean newFullscreenState = !isFullscreen;
            PreferencesManager.setBooleanPreference(PreferenceKey.FULLSCREEN, newFullscreenState);
            GameLoop.getInstance().setFullscreen(newFullscreenState);
        });
        super.getButtons().add(fullscreenCheckbox);
    }

    private void addCloseOptionsButton() {
        final GuiButton backButton = new GuiButton(
            layout.btnBackX,
            layout.btnBackY, 
            layout.btnBackWidth,
            layout.btnBackHeight, 
            SpriteLoader.loadSprite(SingleSpriteKey.BACK_SIGN_BUTTON).image(), 
            SpriteLoader.loadSprite(SingleSpriteKey.BACK_SIGN_BUTTON_HOVER).image()
        );
        backButton.setOnClick(() -> throwEvent(new CloseOptionsEvent(this)));
        super.getButtons().add(backButton);
    }

    private void addMenuOptionsButton() {
        final GuiButton menuButton = new GuiButton(
            layout.btnMenuX,
            layout.btnMenuY, 
            layout.btnMenuWidth,
            layout.btnMenuHeight, 
            SpriteLoader.loadSprite(SingleSpriteKey.MENU_SIGN_BUTTON).image(), 
            SpriteLoader.loadSprite(SingleSpriteKey.MENU_SIGN_BUTTON_HOVER).image()
        );
        menuButton.setOnClick(() -> throwEvent(new SetMainMenuEvent(this)));
        super.getButtons().add(menuButton);
    }

    private void drawStringImage(final GraphicsContext gc, final CoordinateSystem cs, final Image img, 
                               final double x, final double y, final double maxWidth, final double maxHeight,
                               final boolean centerHorizontally, final boolean centerVertically) {
        if (img == null) {
            return;
        }

        final double ratio = img.getWidth() / img.getHeight();
        double targetWidth = maxWidth;
        double targetHeight = maxHeight;

        if (targetWidth / targetHeight > ratio) {
            targetWidth = targetHeight * ratio;
        } else {
            targetHeight = targetWidth / ratio;
        }

        double finalX = x;
        double finalY = y;

        if (centerHorizontally) {
            finalX = x + (maxWidth - targetWidth) / 2;
        }

        if (centerVertically) {
            finalY = y + (maxHeight - targetHeight) / 2;
        }

        Renderer.drawImage(img, gc, cs, finalX, finalY, targetWidth, targetHeight);
    }

    private void drawOptionsText(final GraphicsContext gc, final CoordinateSystem cs) {
        gc.save();
        gc.setEffect(Colors.OPTIONS_WRITINGS);
        final Image musicVolumeLabel = SpriteLoader.loadSprite(new StringSpriteKey("Music")).image();
        drawStringImage(gc, cs, musicVolumeLabel, layout.rowStartX, layout.musicVolumeY,
                layout.labelWidth, layout.rowHeight, false, true);

        final Integer musicVolumeValue = (int) (PreferencesManager.getDoublePreference(PreferenceKey.MUSIC_VOLUME) * 100);
        final Image musicVolume = SpriteLoader.loadSprite(new StringSpriteKey(musicVolumeValue.toString() + "%")).image();
        drawStringImage(gc, cs, musicVolume, layout.controlsStartX, layout.musicVolumeY,
                layout.percentageWidth, layout.rowHeight, true, true);

        final Image musicMuteLabel = SpriteLoader.loadSprite(new StringSpriteKey("Mute music")).image();
        drawStringImage(gc, cs, musicMuteLabel, layout.rowStartX, layout.musicCheckboxY,
                layout.labelWidth, layout.rowHeight, false, true);

        final Image sfxVolumeLabel = SpriteLoader.loadSprite(new StringSpriteKey("SFX")).image();
        drawStringImage(gc, cs, sfxVolumeLabel, layout.rowStartX, layout.sfxVolumeY,
                layout.labelWidth, layout.rowHeight, false, true);

        final Integer sfxVolumeValue = (int) (PreferencesManager.getDoublePreference(PreferenceKey.SFX_VOLUME) * 100);
        final Image sfxVolume = SpriteLoader.loadSprite(new StringSpriteKey(sfxVolumeValue.toString() + "%")).image();
        drawStringImage(gc, cs, sfxVolume, layout.controlsStartX, layout.sfxVolumeY,
                layout.percentageWidth, layout.rowHeight, true, true);

        final Image sfxMuteLabel = SpriteLoader.loadSprite(new StringSpriteKey("Mute SFX")).image();
        drawStringImage(gc, cs, sfxMuteLabel, layout.rowStartX, layout.sfxCheckboxY,
                layout.labelWidth, layout.rowHeight, false, true);

        final Image fullscreenLabel = SpriteLoader.loadSprite(new StringSpriteKey("Fullscreen")).image();
        drawStringImage(gc, cs, fullscreenLabel, layout.rowStartX, layout.fullscreenCheckboxY,
                layout.labelWidth, layout.rowHeight, false, true);
        gc.restore();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void render() {
        final Renderer renderer = GameLoop.getInstance().getRenderer();
        final GraphicsContext gc = renderer.getGraphicsContext();
        final CoordinateSystem cs = renderer.getGuiCoordinateSystem();

        final double optionsScreenWidth = (optionsBounds.bottomRightX - optionsBounds.topLeftX) * cs.getScale();
        final double optionsScreenHeight = (optionsBounds.bottomRightY - optionsBounds.topLeftY) * cs.getScale();
        final double optionsScreenX = cs.toScreenX(optionsBounds.topLeftX);
        final double optionsScreenY = cs.toScreenY(optionsBounds.topLeftY);

        updateLayout();

        final Image optionsBackground = SpriteLoader.loadSprite(SingleSpriteKey.GUI_BACKGROUND).image();
        final Image windowBackground = SpriteLoader.loadSprite(SingleSpriteKey.OPTIONS_WINDOW_BACKGROUND).image();

        renderer.addRenderTask(new RenderTask(RenderPriority.BACKGROUND, () -> {
            gc.drawImage(optionsBackground, optionsScreenX, optionsScreenY, optionsScreenWidth, optionsScreenHeight);
            gc.drawImage(windowBackground, cs.toScreenX(layout.windowBgX), cs.toScreenY(layout.windowBgY), 
                        layout.windowBgWidth * cs.getScale(), layout.windowBgHeight * cs.getScale());
        }));

        renderer.addRenderTask(new RenderTask(RenderPriority.GUI, () -> {
            drawOptionsText(gc, cs);
        }));

        super.render();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void update(final long elapsed) {
    }
}
