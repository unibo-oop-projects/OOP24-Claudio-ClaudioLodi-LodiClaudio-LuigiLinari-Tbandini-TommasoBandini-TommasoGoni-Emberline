package dev.emberline.core.render;

/**
 * The {@code RenderPriority} enum defines rendering priorities for various components
 * in a graphical application. Each priority is associated with an integer value that
 * determines the rendering order, where lower numbers represent higher rendering precedence.
 * <p>
 * The priorities are used to control the drawing sequence of graphical elements such as
 * enemies, GUI elements, and background layers, ensuring proper visual stacking and
 * layering in the rendered output.
 * <p>
 * Each constant is associated with a specific integer priority value, which can be
 * retrieved using the {@link #getPriority()} method.
 */
public enum RenderPriority {
    /**
     * Represents the rendering priority for GUI elements that require a high priority
     * in the drawing sequence. This priority level ensures that these GUI components
     * are rendered above standard GUI components.
     * <p>
     * The associated priority value is {@code 21}.
     */
    GUI_HIGH(21),
    /**
     * Represents the rendering priority for standard GUI elements.
     * <p>
     * The associated priority value is {@code 20}.
     */
    GUI(20),
    /**
     * Represents the rendering priority for fog elements.
     * <p>
     * The associated priority value is {@code 19}.
     */
    FOG(19),
    /**
     * Represents the rendering priority for projectile elements.
     * <p>
     * The associated priority value is {@code 15}.
     */
    PROJECTILES(15),
    /**
     * Represents the rendering priority for enemy entities in the application.
     * <p>
     * Note: Enemies share the same priority value as buildings, allowing z-ordering
     * is mandatory to determine their correct visual stacking.
     * <p>
     * The associated priority value is {@code 10}.
     */
    ENEMIES(10),
    /**
     * Represents the rendering priority for buildings entities in the application.
     * <p>
     * Note: Buildings share the same priority value as enemis, allowing z-ordering
     * is mandatory to determine their correct visual stacking.
     * <p>
     * The associated priority value is {@code 10}.
     */
    BUILDINGS(10),
    /**
     * Represents the rendering priority for background layers in the application.
     * This is the lowest priority, ensuring that background elements are rendered
     * beneath all other graphical components.
     * <p>
     * The associated priority value is {@code 1}.
     */
    BACKGROUND(1);

    private final int priority;

    RenderPriority(final int priority) {
        this.priority = priority;
    }

    /**
     * Retrieves the integer value representing the priority level of a
     * {@code RenderPriority} instance. Lower values indicate higher rendering
     * precedence.
     *
     * @return the priority level associated with this {@code RenderPriority}.
     */
    public int getPriority() {
        return priority;
    }
}
