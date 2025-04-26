package dev.emberline.core.render;

public enum RenderPriority {
    GUI(0),
    BACKGROUND(5),
    ENEMIES(10);

    private final int priority;

    RenderPriority(int priority) {
        this.priority = priority;
    }

    public int getPriority() {
        return priority;
    }
}
