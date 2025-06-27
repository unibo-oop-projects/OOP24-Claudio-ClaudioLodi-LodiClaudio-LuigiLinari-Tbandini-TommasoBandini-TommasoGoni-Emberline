package dev.emberline.gui.menu;

import java.util.prefs.Preferences;

import com.fasterxml.jackson.annotation.JsonProperty;
import dev.emberline.core.ConfigLoader;
import dev.emberline.core.GameLoop;
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
    private final OptionsBounds bounds;
    private final boolean showMenuButton;

    private static class Layout {
        // Background
        private static final double BG_WIDTH = 32;
        private static final double BG_HEIGHT = 18;
        
        // Title and window
        private static final double SCALE = 1.8;
        private static final double WINDOW_BG_WIDTH = 7 * SCALE;
        private static final double WINDOW_BG_HEIGHT = 5 * SCALE;
        private static final double WINDOW_BG_X = (BG_WIDTH - WINDOW_BG_WIDTH) / 2;
        private static final double WINDOW_BG_Y = (BG_HEIGHT - WINDOW_BG_HEIGHT) / 2 - 2;

        // Button scaling
        private static final double BTN_SCALE = 0.45;
        private static final double BTN_WIDTH = 1.3 * BTN_SCALE;
        private static final double BTN_HEIGHT = 1.3 * BTN_SCALE;

        // Row dimensions
        private static final double ROW_HEIGHT = BTN_HEIGHT;
        private static final double ROW_PADDING = 4.5 * BTN_SCALE;
        private static final double ROW_START_X = WINDOW_BG_X + ROW_PADDING;
        private static final double ROW_WIDTH = WINDOW_BG_WIDTH - 1 * ROW_PADDING;

        // Vertical spacing
        private static final double VERTICAL_SPACING = 1 * BTN_SCALE;
        private static final double FIRST_ROW_OFFSET_Y = 1.65 * SCALE;

        // Layout elementi per riga
        private static final double LABEL_WIDTH = ROW_WIDTH * 0.45;
        private static final double CONTROLS_WIDTH = ROW_WIDTH * 0.55;
        private static final double CONTROLS_START_X = ROW_START_X + LABEL_WIDTH;

        // Controlli volume
        private static final double PERCENTAGE_WIDTH = CONTROLS_WIDTH * 0.4;
        private static final double BUTTON_SPACING = 0.1 * BTN_SCALE;
        private static final double MINUS_OFFSET_X = PERCENTAGE_WIDTH + BUTTON_SPACING;
        private static final double PLUS_OFFSET_X = MINUS_OFFSET_X + BTN_WIDTH + BUTTON_SPACING;

        // Controlli checkbox
        private static final double CHECKBOX_OFFSET_X = (CONTROLS_WIDTH - BTN_WIDTH) / 2;

        // Posizioni Y delle righe
        private static final double MUSIC_VOLUME_Y = WINDOW_BG_Y + FIRST_ROW_OFFSET_Y;
        private static final double MUSIC_CHECKBOX_Y = MUSIC_VOLUME_Y + ROW_HEIGHT + VERTICAL_SPACING;
        private static final double SFX_VOLUME_Y = MUSIC_CHECKBOX_Y + ROW_HEIGHT + VERTICAL_SPACING;
        private static final double SFX_CHECKBOX_Y = SFX_VOLUME_Y + ROW_HEIGHT + VERTICAL_SPACING;
        private static final double FULLSCREEN_CHECKBOX_Y = SFX_CHECKBOX_Y + ROW_HEIGHT + VERTICAL_SPACING;

        // Navigation buttons
        private static final double NAV_SCALE_FACTOR = 1.7;
        private static final double BTN_BACK_HEIGHT = 1.5 * NAV_SCALE_FACTOR;
        private static final double BTN_BACK_WIDTH = 3.5 * NAV_SCALE_FACTOR;
        private static final double BTN_BACK_X = (BG_WIDTH - BTN_BACK_WIDTH) / 2;
        private static final double BTN_BACK_Y = WINDOW_BG_Y + WINDOW_BG_HEIGHT - 0.1;
        
        // Menu button
        private static final double BTN_MENU_HEIGHT = 1.5 * NAV_SCALE_FACTOR;
        private static final double BTN_MENU_WIDTH = 3.5 * NAV_SCALE_FACTOR;
        private static final double BTN_MENU_X = (BG_WIDTH - BTN_MENU_WIDTH) / 2;
        private static final double BTN_MENU_Y = BTN_BACK_Y + BTN_BACK_HEIGHT - 0.2;
    }

    private static final class Colors {
        private static final ColorAdjust OPTIONS_WRITINGS = new ColorAdjust(0.15, 0.9, -0.3, 0);
    }

    private record Coordinate(
        @JsonProperty int x,
        @JsonProperty int y
    ) {}

    private record OptionsBounds(
        @JsonProperty Coordinate topLeftBound,
        @JsonProperty Coordinate bottomRightBound
    ) {}

    /**
     * Constructs an {@code Options} object by initializing it with the configuration
     * data loaded from a predefined JSON resource file. The configuration provides
     * the bounds necessary for setting up the {@code Options} screen in the GUI.
     *
     * @param showMenuButton a boolean representing whether the options menu should or should not
     *                       have a menu button
     * @throws RuntimeException if the configuration file cannot be loaded or parsed.
     * @see Options
     */
    public Options(final boolean showMenuButton) {
        this(ConfigLoader.loadConfig("/gui/options/optionsBounds.json", OptionsBounds.class), showMenuButton);
    }

    private Options(final OptionsBounds bounds, final boolean showMenuButton) {
        super(bounds.topLeftBound.x, bounds.topLeftBound.y, bounds.bottomRightBound.x - bounds.topLeftBound.x, bounds.bottomRightBound.y - bounds.topLeftBound.y);
        this.bounds = bounds;
        this.showMenuButton = showMenuButton;
    }

    private void updateLayout(final GraphicsContext gc, final CoordinateSystem cs) {
        super.buttons.clear();

        addMusicVolumeControl(gc, cs);
        addMusicCheckbox(gc, cs);
        addSfxVolumeControl(gc, cs);
        addSfxCheckbox(gc, cs);
        addFullScreenCheckbox(gc, cs);

        addCloseOptionsButton();
        if (showMenuButton) {
            addMenuOptionsButton();
        }
    }

    private void addMusicVolumeControl(final GraphicsContext gc, final CoordinateSystem cs) {
        int musicVolume = (int) (PreferencesManager.getDoublePreference(PreferenceKey.MUSIC_VOLUME) * 100);

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

        final GuiButton musicMinusVolumeControl = new GuiButton(
            Layout.CONTROLS_START_X + Layout.MINUS_OFFSET_X, 
            Layout.MUSIC_VOLUME_Y + (Layout.ROW_HEIGHT - Layout.BTN_HEIGHT) / 2, 
            Layout.BTN_WIDTH, 
            Layout.BTN_HEIGHT,
            minusButton,
            minusButtonHover
        );
        musicMinusVolumeControl.setOnClick(() -> {
            if (musicVolume - 10 < 0) {
                return; // Do not decrease volume if already at minimum
            }
            double musicDoubleVolume = (musicVolume - 10) / 100.0;
            PreferencesManager.setDoublePreference(PreferenceKey.MUSIC_VOLUME, musicDoubleVolume);
            AudioController.requestSetMusicVolume(this, musicDoubleVolume);
        });
        super.buttons.add(musicMinusVolumeControl);
        
        final GuiButton musicPlusVolumeControl = new GuiButton(
            Layout.CONTROLS_START_X + Layout.PLUS_OFFSET_X, 
            Layout.MUSIC_VOLUME_Y + (Layout.ROW_HEIGHT - Layout.BTN_HEIGHT) / 2, 
            Layout.BTN_WIDTH, 
            Layout.BTN_HEIGHT,
            plusButton,
            plusButtonHover
        );
        musicPlusVolumeControl.setOnClick(() -> {
            if (musicVolume + 10 > 100) {
                return; // Do not increase volume if already at maximum
            }
            double newMusicVolume = (musicVolume + 10) / 100.0;
            PreferencesManager.setDoublePreference(PreferenceKey.MUSIC_VOLUME, newMusicVolume);
            AudioController.requestSetMusicVolume(this, newMusicVolume);
        });
        super.buttons.add(musicPlusVolumeControl);
    }

    private void addMusicCheckbox(final GraphicsContext gc, final CoordinateSystem cs) {  
        final boolean isMusicMuted = PreferencesManager.getBooleanPreference(PreferenceKey.MUSIC_MUTE);

        final Image checkboxImage = isMusicMuted ? 
            SpriteLoader.loadSprite(SingleSpriteKey.OPTIONS_CHECKBOX_FULL).image() :
            SpriteLoader.loadSprite(SingleSpriteKey.OPTIONS_CHECKBOX_EMPTY).image();
        final Image checkboxHoverImage = isMusicMuted ?
            SpriteLoader.loadSprite(SingleSpriteKey.OPTIONS_CHECKBOX_FULL_HOVER).image() :
            SpriteLoader.loadSprite(SingleSpriteKey.OPTIONS_CHECKBOX_EMPTY_HOVER).image();
            
        final GuiButton musicMuteCheckbox = new GuiButton(
            Layout.CONTROLS_START_X + Layout.CHECKBOX_OFFSET_X,
            Layout.MUSIC_CHECKBOX_Y + (Layout.ROW_HEIGHT - Layout.BTN_HEIGHT) / 2,
            Layout.BTN_WIDTH,
            Layout.BTN_HEIGHT,
            checkboxImage,
            checkboxHoverImage
        );
        musicMuteCheckbox.setOnClick(() -> {
            boolean newMuteState = !isMusicMuted;
            PreferencesManager.setBooleanPreference(PreferenceKey.MUSIC_MUTE, newMuteState);
            AudioController.requestToggleMusicMute(this, newMuteState);
        });
        super.buttons.add(musicMuteCheckbox);
    }

    private void addSfxVolumeControl(final GraphicsContext gc, final CoordinateSystem cs) {
        int sfxVolume = (int) (PreferencesManager.getDoublePreference(PreferenceKey.SFX_VOLUME) * 100);

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
            Layout.CONTROLS_START_X + Layout.MINUS_OFFSET_X, 
            Layout.SFX_VOLUME_Y + (Layout.ROW_HEIGHT - Layout.BTN_HEIGHT) / 2, 
            Layout.BTN_WIDTH, 
            Layout.BTN_HEIGHT,
            minusButton,
            minusButtonHover
        );
        sfxMinusVolumeControl.setOnClick(() -> {
            if (sfxVolume - 10 < 0) {
                return; // Do not decrease volume if already at minimum
            }
            double newSfxVolume = (sfxVolume - 10) / 100.0;
            PreferencesManager.setDoublePreference(PreferenceKey.SFX_VOLUME, newSfxVolume);
            AudioController.requestSetSfxVolume(this, newSfxVolume);
        });
        super.buttons.add(sfxMinusVolumeControl);
        
        final GuiButton sfxPlusVolumeControl = new GuiButton(
            Layout.CONTROLS_START_X + Layout.PLUS_OFFSET_X, 
            Layout.SFX_VOLUME_Y + (Layout.ROW_HEIGHT - Layout.BTN_HEIGHT) / 2, 
            Layout.BTN_WIDTH, 
            Layout.BTN_HEIGHT,
            plusButton,
            plusButtonHover
        );
        sfxPlusVolumeControl.setOnClick(() -> {
            if (sfxVolume + 10 > 100) {
                return; // Do not increase volume if already at maximum
            }
            double newSfxVolume = (sfxVolume + 10) / 100.0;
            PreferencesManager.setDoublePreference(PreferenceKey.SFX_VOLUME, newSfxVolume);
            AudioController.requestSetSfxVolume(this, newSfxVolume);
        });
        super.buttons.add(sfxPlusVolumeControl);
    }

    private void addSfxCheckbox(final GraphicsContext gc, final CoordinateSystem cs) {
        final boolean isSfxMuted = PreferencesManager.getBooleanPreference(PreferenceKey.SFX_MUTE);

        final Image checkboxImage = isSfxMuted ? 
            SpriteLoader.loadSprite(SingleSpriteKey.OPTIONS_CHECKBOX_FULL).image() :
            SpriteLoader.loadSprite(SingleSpriteKey.OPTIONS_CHECKBOX_EMPTY).image();
        final Image checkboxHoverImage = isSfxMuted ?
            SpriteLoader.loadSprite(SingleSpriteKey.OPTIONS_CHECKBOX_FULL_HOVER).image() :
            SpriteLoader.loadSprite(SingleSpriteKey.OPTIONS_CHECKBOX_EMPTY_HOVER).image();
            
        final GuiButton sfxMuteCheckbox = new GuiButton(
            Layout.CONTROLS_START_X + Layout.CHECKBOX_OFFSET_X,
            Layout.SFX_CHECKBOX_Y + (Layout.ROW_HEIGHT - Layout.BTN_HEIGHT) / 2,
            Layout.BTN_WIDTH,
            Layout.BTN_HEIGHT,
            checkboxImage,
            checkboxHoverImage
        );
        sfxMuteCheckbox.setOnClick(() -> {
            boolean newMuteState = !isSfxMuted;
            PreferencesManager.setBooleanPreference(PreferenceKey.SFX_MUTE, newMuteState);
            AudioController.requestToggleSfxMute(this, newMuteState);
        });
        super.buttons.add(sfxMuteCheckbox);
    }

    private void addFullScreenCheckbox(final GraphicsContext gc, final CoordinateSystem cs) {
        final boolean isFullscreen = PreferencesManager.getBooleanPreference(PreferenceKey.FULLSCREEN);
        final Image checkboxImage = isFullscreen ? 
            SpriteLoader.loadSprite(SingleSpriteKey.OPTIONS_CHECKBOX_FULL).image() :
            SpriteLoader.loadSprite(SingleSpriteKey.OPTIONS_CHECKBOX_EMPTY).image();
        final Image checkboxHoverImage = isFullscreen ?
            SpriteLoader.loadSprite(SingleSpriteKey.OPTIONS_CHECKBOX_FULL_HOVER).image() :
            SpriteLoader.loadSprite(SingleSpriteKey.OPTIONS_CHECKBOX_EMPTY_HOVER).image();
            
        final GuiButton fullscreenCheckbox = new GuiButton(
            Layout.CONTROLS_START_X + Layout.CHECKBOX_OFFSET_X,
            Layout.FULLSCREEN_CHECKBOX_Y + (Layout.ROW_HEIGHT - Layout.BTN_HEIGHT) / 2,
            Layout.BTN_WIDTH,
            Layout.BTN_HEIGHT,
            checkboxImage,
            checkboxHoverImage
        );
        fullscreenCheckbox.setOnClick(() -> {
            boolean newFullscreenState = !isFullscreen;
            PreferencesManager.setBooleanPreference(PreferenceKey.FULLSCREEN, newFullscreenState);
            GameLoop.getInstance().setFullscreen(newFullscreenState);
        });
        super.buttons.add(fullscreenCheckbox);
    }

    private void addCloseOptionsButton() {
        final GuiButton backButton = new GuiButton(
            Layout.BTN_BACK_X,
            Layout.BTN_BACK_Y, 
            Layout.BTN_BACK_WIDTH,
            Layout.BTN_BACK_HEIGHT, 
            SpriteLoader.loadSprite(SingleSpriteKey.BACK_SIGN_BUTTON).image(), 
            SpriteLoader.loadSprite(SingleSpriteKey.BACK_SIGN_BUTTON_HOVER).image()
        );
        backButton.setOnClick(() -> throwEvent(new CloseOptionsEvent(this)));
        super.buttons.add(backButton);
    }

    private void addMenuOptionsButton() {
        final GuiButton menuButton = new GuiButton(
            Layout.BTN_MENU_X,
            Layout.BTN_MENU_Y, 
            Layout.BTN_MENU_WIDTH,
            Layout.BTN_MENU_HEIGHT, 
            SpriteLoader.loadSprite(SingleSpriteKey.MENU_SIGN_BUTTON).image(), 
            SpriteLoader.loadSprite(SingleSpriteKey.MENU_SIGN_BUTTON_HOVER).image()
        );
        menuButton.setOnClick(() -> throwEvent(new SetMainMenuEvent(this)));
        super.buttons.add(menuButton);
    }

    private void drawStringImage(final GraphicsContext gc, final CoordinateSystem cs, final Image img, 
                               final double x, final double y, final double maxWidth, final double maxHeight,
                               final boolean centerHorizontally, final boolean centerVertically) {
        if (img == null) return;
        
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
        drawStringImage(gc, cs, musicVolumeLabel, Layout.ROW_START_X, Layout.MUSIC_VOLUME_Y, Layout.LABEL_WIDTH, Layout.ROW_HEIGHT, false, true);
        
        final Integer musicVolumeValue = (int) (PreferencesManager.getDoublePreference(PreferenceKey.MUSIC_VOLUME) * 100);
        final Image musicVolume = SpriteLoader.loadSprite(new StringSpriteKey(musicVolumeValue.toString() + "%")).image();
        drawStringImage(gc, cs, musicVolume, Layout.CONTROLS_START_X, Layout.MUSIC_VOLUME_Y, Layout.PERCENTAGE_WIDTH, Layout.ROW_HEIGHT, true, true);
    
        final Image musicMuteLabel = SpriteLoader.loadSprite(new StringSpriteKey("Mute music")).image();
        drawStringImage(gc, cs, musicMuteLabel, Layout.ROW_START_X, Layout.MUSIC_CHECKBOX_Y, Layout.LABEL_WIDTH, Layout.ROW_HEIGHT, false, true);
        
        final Image sfxVolumeLabel = SpriteLoader.loadSprite(new StringSpriteKey("SFX")).image();
        drawStringImage(gc, cs, sfxVolumeLabel, Layout.ROW_START_X, Layout.SFX_VOLUME_Y, Layout.LABEL_WIDTH, Layout.ROW_HEIGHT, false, true);
        
        final Integer sfxVolumeValue = (int) (PreferencesManager.getDoublePreference(PreferenceKey.SFX_VOLUME) * 100);
        final Image sfxVolume = SpriteLoader.loadSprite(new StringSpriteKey(sfxVolumeValue.toString() + "%")).image();
        drawStringImage(gc, cs, sfxVolume, Layout.CONTROLS_START_X, Layout.SFX_VOLUME_Y, Layout.PERCENTAGE_WIDTH, Layout.ROW_HEIGHT, true, true);

        final Image sfxMuteLabel = SpriteLoader.loadSprite(new StringSpriteKey("Mute SFX")).image();
        drawStringImage(gc, cs, sfxMuteLabel, Layout.ROW_START_X, Layout.SFX_CHECKBOX_Y, Layout.LABEL_WIDTH, Layout.ROW_HEIGHT, false, true);
    
        final Image fullscreenLabel = SpriteLoader.loadSprite(new StringSpriteKey("Fullscreen")).image();
        drawStringImage(gc, cs, fullscreenLabel, Layout.ROW_START_X, Layout.FULLSCREEN_CHECKBOX_Y, Layout.LABEL_WIDTH, Layout.ROW_HEIGHT, false, true);
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

        final double menuScreenWidth = bounds.bottomRightBound.x * cs.getScale();
        final double menuScreenHeight = bounds.bottomRightBound.y * cs.getScale();
        final double menuScreenX = cs.toScreenX(bounds.topLeftBound.x);
        final double menuScreenY = cs.toScreenY(bounds.topLeftBound.y);

        updateLayout(gc, cs);

        final Image menuBackground = SpriteLoader.loadSprite(SingleSpriteKey.MENU_BACKGROUND).image();
        final Image windowBackground = SpriteLoader.loadSprite(SingleSpriteKey.OPTIONS_WINDOW_BACKGROUND).image();

        renderer.addRenderTask(new RenderTask(RenderPriority.BACKGROUND, () -> {
            gc.drawImage(menuBackground, menuScreenX, menuScreenY, menuScreenWidth, menuScreenHeight);
            gc.drawImage(windowBackground, cs.toScreenX(Layout.WINDOW_BG_X), cs.toScreenY(Layout.WINDOW_BG_Y), 
                        Layout.WINDOW_BG_WIDTH * cs.getScale(), Layout.WINDOW_BG_HEIGHT * cs.getScale());
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