package dev.emberline.core.render;

public enum RenderPriority {
    BACKGROUND(1),
    GUI(2),
    ENEMIES(10);

    private final int priority;

    RenderPriority(int priority) {
        this.priority = priority;
    }

    public int getPriority() {
        return priority;
    }
}
