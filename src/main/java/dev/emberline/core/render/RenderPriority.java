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
    GUI_HIGH(21),
    GUI(20),
    // Enemies and Buildings have the same priority, enable z-ordering
    ENEMIES(10),
    BUILDINGS(10),
    BACKGROUND(1);

    private final int priority;

    RenderPriority(int priority) {
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
