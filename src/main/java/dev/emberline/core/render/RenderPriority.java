package dev.emberline.core.render;

public enum RenderPriority {
    GUI_HIGH(3),
    GUI(2),
    BACKGROUND(1);

    private final int priority;

    RenderPriority(int priority) {
        this.priority = priority;
    }

    public int getPriority() {
        return priority;
    }
}
