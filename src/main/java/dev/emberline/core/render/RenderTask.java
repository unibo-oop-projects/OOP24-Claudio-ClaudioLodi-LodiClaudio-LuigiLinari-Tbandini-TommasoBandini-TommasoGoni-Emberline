package dev.emberline.core.render;

import java.lang.Comparable;

public class RenderTask implements Comparable<RenderTask>, Runnable {
    private final RenderPriority renderPriority;
    private long secondaryPriority = 0; //lower values get rendered first
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
     * Sets the secondary priority value for this render task.
     * This value is used as a criterion to sort equal priority tasks
     * where the task with the lower secondary priority value will be rendered first.
     *
     * @param secondaryPriority the secondary priority value to set
     */
    public void setSecondaryPriority(long secondaryPriority) {
        this.secondaryPriority = secondaryPriority;
    }

    /**
     * Compares this object with the specified object for order.
     * @return Returns a negative integer, zero, or a positive integer as this object is less than, equal to, or greater than the specified object.
     */
    @Override
    public int compareTo(RenderTask t1) {
        int comparison = this.renderPriority.getPriority() - t1.renderPriority.getPriority();
        if (comparison == 0) return (int) (this.secondaryPriority - t1.secondaryPriority);
        else return comparison;
    }
}
