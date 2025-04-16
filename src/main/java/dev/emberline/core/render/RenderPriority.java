package dev.emberline.core.render;

public enum RenderPriority {
    GUI(2),
    BACKGROUND(1),
    LETTERBOXING(0);

    private final int priority;

    RenderPriority(int priority) {
        this.priority = priority;
    }

    public int getPriority() {
        return priority;
    }
}
