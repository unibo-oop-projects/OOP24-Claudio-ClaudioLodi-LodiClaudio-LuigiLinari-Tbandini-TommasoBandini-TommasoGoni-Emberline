package dev.emberline.core.graphics.spritekeys;

/**
 * An enumeration of unique keys used for identifying and caching single sprite assets.
 * <p>
 * Using an enum ensures that the {@code hashCode()} and {@code equals(Object)} methods
 * are correctly implemented, making it suitable as a key in a hashing mechanism.
 */
public enum SingleSpriteKey implements SpriteKey {
    /**
     * Represents the key used to identify and cache the sprite asset for
     * the background of the stats menu.
     */
    STATS_BACKGROUND,
    /**
     * Represents the key used to identify and cache the sprite asset for
     * the new tower dialog background.
     */
    NTDL_BACKGROUND,
    /**
     * Represents the key used to identify and cache the sprite asset for
     * the tower dialog background.
     */
    TDL_BACKGROUND,
    /**
     * Represents the key used to identify and cache the sprite asset for
     * the cancel button.
     */
    CANCEL_BUTTON,
    /**
     * Represents the key used to identify and cache the sprite asset for
     * the generic button.
     */
    GENERIC_BUTTON,
    /**
     * Represents the key used to identify and cache the sprite asset for
     * the empty upgrade level.
     */
    EMPTY_UPGRADE_LEVEL,
    /**
     * Represents the key used to identify and cache the sprite asset for
     * the full upgrade level.
     */
    FULL_UPGRADE_LEVEL,
    /**
     * Represents the key used to identify and cache the sprite asset for
     * the add button.
     */
    ADD_BUTTON,
    /**
     * Represents the key used to identify and cache the sprite asset for
     * the char atlas.
     */
    CHARS_ATLAS,
    /**
     * Represents the key used to identify and cache the sprite asset for
     * the ice button.
     */
    ICE_BUTTON,
    /**
     * Represents the key used to identify and cache the sprite asset for
     * the fire button.
     */
    FIRE_BUTTON,
    /**
     * Represents the key used to identify and cache the sprite asset for
     * the small button.
     */
    SMALL_BUTTON,
    /**
     * Represents the key used to identify and cache the sprite asset for
     * the big button.
     */
    BIG_BUTTON,
    /**
     * Represents the key used to identify and cache the sprite asset for
     * the upgrade button.
     */
    UPGRADE_BUTTON,
    /**
     * Represents the key used to identify and cache the sprite asset for
     * the aim button.
     */
    AIM_BUTTON,
    /**
     * Represents the key used to identify and cache the sprite asset for
     * the disabled upgrade button.
     */
    DISABLED_UPGRADE_BUTTON,
    /**
     * Represents the key used to identify and cache the sprite asset for
     * the small icon.
     */
    SMALL_ICON,
    /**
     * Represents the key used to identify and cache the sprite asset for
     * the big icon.
     */
    BIG_ICON,
    /**
     * Represents the key used to identify and cache the sprite asset for
     * the fire icon.
     */
    FIRE_ICON,
    /**
     * Represents the key used to identify and cache the sprite asset for
     * the ice icon.
     */
    ICE_ICON,

    // Tower dialog assets
    /**
     * Represents the key used to identify and cache the sprite asset for
     * the fire rate.
     */
    FIRE_RATE,
    /**
     * Represents the key used to identify and cache the sprite asset for
     * the damage.
     */
    DAMAGE,
    /**
     * Represents the key used to identify and cache the sprite asset for
     * the damage area.
     */
    DAMAGE_AREA,
    /**
     * Represents the key used to identify and cache the sprite asset for
     * the projectile speed.
     */
    PROJECTILE_SPEED,
    /**
     * Represents the key used to identify and cache the sprite asset for
     * the tower range.
     */
    TOWER_RANGE,
    /**
     * Represents the key used to identify and cache the sprite asset for
     * the effect duration.
     */
    EFFECT_DURATION,
    /**
     * Represents the key used to identify and cache the sprite asset for
     * the burn effect.
     */
    BURN_EFFECT,
    /**
     * Represents the key used to identify and cache the sprite asset for
     * the slow effect.
     */
    SLOW_EFFECT,

    // Sign Buttons
    /**
     * Represents the key used to identify and cache the sprite asset for
     * the start sign button.
     */
    START_SIGN_BUTTON,
    /**
     * Represents the key used to identify and cache the sprite asset for
     * the start sign button hover.
     */
    START_SIGN_BUTTON_HOVER,
    /**
     * Represents the key used to identify and cache the sprite asset for
     * the options sign button.
     */
    OPTIONS_SIGN_BUTTON,
    /**
     * Represents the key used to identify and cache the sprite asset for
     * the options sign button hover.
     */
    OPTIONS_SIGN_BUTTON_HOVER,
    /**
     * Represents the key used to identify and cache the sprite asset for
     * the exit sign button.
     */
    EXIT_SIGN_BUTTON,
    /**
     * Represents the key used to identify and cache the sprite asset for
     * the exit sign button hover.
     */
    EXIT_SIGN_BUTTON_HOVER,
    /**
     * Represents the key used to identify and cache the sprite asset for
     * the menu sign button.
     */
    MENU_SIGN_BUTTON,
    /**
     * Represents the key used to identify and cache the sprite asset for
     * the menu sign button hover.
     */
    MENU_SIGN_BUTTON_HOVER,
    /**
     * Represents the key used to identify and cache the sprite asset for
     * the back sign button.
     */
    BACK_SIGN_BUTTON,
    /**
     * Represents the key used to identify and cache the sprite asset for
     * the back sign button hover.
     */
    BACK_SIGN_BUTTON_HOVER,
    /**
     * Represents the key used to identify and cache the sprite asset for
     * the default sign button.
     */
    DEFAULT_SIGN_BUTTON,

    // Main Menu
    /**
     * Represents the key used to identify and cache the sprite asset for
     * the menu background.
     */
    MENU_BACKGROUND,
    /**
     * Represents the key used to identify and cache the sprite asset for
     * the emberline title.
     */
    EMBERLINE_TITLE,

    // TowerPreBuild
    /**
     * Represents the key used to identify and cache the sprite asset for
     * the tower pre-build.
     */
    TOWER_PRE_BUILD,

    // Fog
    /**
     * Represents the key used to identify and cache the sprite asset for
     * the fog.
     */
    FOG,
    /**
     * Represents the key used to identify and cache the sprite asset for
     * the fog left.
     */
    FOG_LEFT,
    /**
     * Represents the key used to identify and cache the sprite asset for
     * the fog right.
     */
    FOG_RIGHT,
    /**
     * Represents the key used to identify and cache the sprite asset for
     * the fog top.
     */
    FOG_TOP,
    /**
     * Represents the key used to identify and cache the sprite asset for
     * the fog bottom.
     */
    FOG_BOTTOM,
    /**
     * Represents the key used to identify and cache the sprite asset for
     * the fog top left.
     */
    FOG_TOP_LEFT,
    /**
     * Represents the key used to identify and cache the sprite asset for
     * the fog top right.
     */
    FOG_TOP_RIGHT,
    /**
     * Represents the key used to identify and cache the sprite asset for
     * the fog bottom left.
     */
    FOG_BOTTOM_LEFT,
    /**
     * Represents the key used to identify and cache the sprite asset for
     * the fog bottom right.
     */
    FOG_BOTTOM_RIGHT,

    // In game gui
    /**
     * Represents the key used to identify and cache the sprite asset for
     * the topbar background.
     */
    TOPBAR_BACKGROUND,
    /**
     * Represents the key used to identify and cache the sprite asset for
     * the options button.
     */
    TOPBAR_OPTIONS_BUTTON,
    /**
     * Represents the key used to identify and cache the sprite asset for
     * the topbar options button hover.
     */
    TOPBAR_OPTIONS_BUTTON_HOVER,

    // Game Over
    /**
     * Represents the key used to identify and cache the sprite asset for
     * the game over background.
     */
    GAME_OVER_BACKGROUND,
    /**
     * Represents the key used to identify and cache the sprite asset for
     * the game over.
     */
    GAME_OVER,
    /**
     * Represents the key used to identify and cache the sprite asset for
     * the statistics window to be shown in the game over game state.
     */
    STATISTICS,

    // Options
    /**
     * Represents the key used to identify and cache the sprite asset for
     * the options window background.
     */
    OPTIONS_WINDOW_BACKGROUND,
    /**
     * Represents the key used to identify and cache the sprite asset for
     * the options minus button.
     */
    OPTIONS_MINUS_BUTTON,
    /**
     * Represents the key used to identify and cache the sprite asset for
     * the options minus button hover.
     */
    OPTIONS_MINUS_BUTTON_HOVER,
    /**
     * Represents the key used to identify and cache the sprite asset for
     * the options minus button disabled.
     */
    OPTIONS_MINUS_BUTTON_DISABLED,
    /**
     * Represents the key used to identify and cache the sprite asset for
     * the options plus button.
     */
    OPTIONS_PLUS_BUTTON,
    /**
     * Represents the key used to identify and cache the sprite asset for
     * the options plus button hover.
     */
    OPTIONS_PLUS_BUTTON_HOVER,
    /**
     * Represents the key used to identify and cache the sprite asset for
     * the options plus button disabled.
     */
    OPTIONS_PLUS_BUTTON_DISABLED,
    /**
     * Represents the key used to identify and cache the sprite asset for
     * the options checkbox full.
     */
    OPTIONS_CHECKBOX_FULL,
    /**
     * Represents the key used to identify and cache the sprite asset for
     * the options checkbox full hover.
     */
    OPTIONS_CHECKBOX_FULL_HOVER,
    /**
     * Represents the key used to identify and cache the sprite asset for
     * the options checkbox empty.
     */
    OPTIONS_CHECKBOX_EMPTY,
    /**
     * Represents the key used to identify and cache the sprite asset for
     * the options checkbox empty hover.
     */
    OPTIONS_CHECKBOX_EMPTY_HOVER
}
