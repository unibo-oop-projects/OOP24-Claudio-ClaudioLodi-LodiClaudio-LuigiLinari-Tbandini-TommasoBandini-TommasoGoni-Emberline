package dev.emberline.core.render;

import java.lang.Comparable;

public class RenderTask implements Comparable<RenderTask>, Runnable {
    private final RenderPriority renderPriority;
    private final Runnable runnable;

    public RenderTask(RenderPriority renderPriority, Runnable runnable) {
        this.renderPriority = renderPriority;
        this.runnable = runnable;
    }

    @Override
    public void run() {
        runnable.run();
    }

    /**
     * Compares this object with the specified object for order.
     * @return Returns a negative integer, zero, or a positive integer as this object is less than, equal to, or greater than the specified object.
     */
    @Override
    public int compareTo(RenderTask t1) {
        return this.renderPriority.getPriority() - t1.renderPriority.getPriority();
    }
}
