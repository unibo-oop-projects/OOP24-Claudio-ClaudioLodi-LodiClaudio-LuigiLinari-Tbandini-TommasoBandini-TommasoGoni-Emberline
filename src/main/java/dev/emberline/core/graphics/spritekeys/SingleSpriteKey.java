package dev.emberline.core.graphics.spritekeys;

/**
 * An enumeration of unique keys used for identifying and caching single sprite assets.
 * <p>
 * Using an enum ensures that the {@code hashCode()} and {@code equals(Object)} methods
 * are correctly implemented, making it suitable as a key in a hashing mechanism.
 */
public enum SingleSpriteKey implements SpriteKey {
    STATS_BACKGROUND,
    NTDL_BACKGROUND,
    TDL_BACKGROUND,
    CANCEL_BUTTON,
    GENERIC_BUTTON,
    EMPTY_UPGRADE_LEVEL,
    FULL_UPGRADE_LEVEL,
    ADD_BUTTON,
    CHARS_ATLAS,
    ICE_BUTTON,
    FIRE_BUTTON,
    SMALL_BUTTON,
    BIG_BUTTON,
    UPGRADE_BUTTON,
    AIM_BUTTON,
    DISABLED_UPGRADE_BUTTON,
    SMALL_ICON,
    BIG_ICON,
    FIRE_ICON,
    ICE_ICON,
    // Tower dialog assets
    FIRE_RATE,
    DAMAGE,
    DAMAGE_AREA,
    PROJECTILE_SPEED,
    TOWER_RANGE,
    EFFECT_DURATION,
    BURN_EFFECT,
    SLOW_EFFECT,
    // Sign Buttons
    START_SIGN_BUTTON,
    START_SIGN_BUTTON_HOVER,
    OPTIONS_SIGN_BUTTON,
    OPTIONS_SIGN_BUTTON_HOVER,
    EXIT_SIGN_BUTTON,
    EXIT_SIGN_BUTTON_HOVER,
    MENU_SIGN_BUTTON,
    MENU_SIGN_BUTTON_HOVER,
    BACK_SIGN_BUTTON,
    BACK_SIGN_BUTTON_HOVER,
    DEFAULT_SIGN_BUTTON,
    // Main Menu
    MENU_BACKGROUND,
    EMBERLINE_TITLE,
    // TowerPreBuild
    TOWER_PRE_BUILD,
    // Fog
    FOG,
    FOG_LEFT,
    FOG_RIGHT,
    FOG_TOP,
    FOG_BOTTOM,
    FOG_TOP_LEFT,
    FOG_TOP_RIGHT,
    FOG_BOTTOM_LEFT,
    FOG_BOTTOM_RIGHT,
    // In game gui
    TOPBAR_BACKGROUND,
    TOPBAR_OPTIONS_BUTTON,
    TOPBAR_OPTIONS_BUTTON_HOVER,
    // Game Over
    GAME_OVER_BACKGROUND,
    GAME_OVER,
    // Options
    OPTIONS_WINDOW_BACKGROUND,
    OPTIONS_MINUS_BUTTON,
    OPTIONS_MINUS_BUTTON_HOVER,
    OPTIONS_MINUS_BUTTON_DISABLED,
    OPTIONS_PLUS_BUTTON,
    OPTIONS_PLUS_BUTTON_HOVER,
    OPTIONS_PLUS_BUTTON_DISABLED,
    OPTIONS_CHECKBOX_FULL,
    OPTIONS_CHECKBOX_FULL_HOVER,
    OPTIONS_CHECKBOX_EMPTY,
    OPTIONS_CHECKBOX_EMPTY_HOVER
}
