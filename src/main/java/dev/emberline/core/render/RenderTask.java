package dev.emberline.core.render;

public class RenderTask implements Comparable<RenderTask>, Runnable {
    private final RenderPriority renderPriority;
    private boolean zOrderEnabled = false;
    private double zOrder = 0;
    private long secondaryPriority = 0; //lower values get rendered first
    private final Runnable runnable;

    public RenderTask(final RenderPriority renderPriority, final Runnable runnable) {
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
     * where the task with the lower secondary priority value will be rendered first (under).
     *
     * @param secondaryPriority the secondary priority value to set
     */
    public void setSecondaryPriority(final long secondaryPriority) {
        this.secondaryPriority = secondaryPriority;
    }

    public RenderTask enableZOrder(final double lowestUnder) {
        this.zOrderEnabled = true;
        this.zOrder = lowestUnder;
        return this;
    }

    /**
     * Compares this object with the specified object for order.
     *
     * @return Returns a negative integer, zero, or a positive integer
     * as this object is less than, equal to, or greater than the specified object.
     */
    @Override
    public int compareTo(final RenderTask t1) {
        final int comparison = this.renderPriority.getPriority() - t1.renderPriority.getPriority();
        if (comparison != 0) {
            return comparison;
        }
        final int secondaryComparison = comparisonToInt(this.secondaryPriority - t1.secondaryPriority);
        final int zOrderComparison = comparisonToInt(this.zOrder - t1.zOrder);
        if (zOrderEnabled && zOrderComparison != 0) {
            return zOrderComparison;
        }
        return secondaryComparison;
    }

    private int comparisonToInt(final double comparison) {
        return comparison < 0 ? -1 : comparison > 0 ? 1 : 0;
    }

    private int comparisonToInt(final long comparison) {
        return comparison < 0 ? -1 : comparison > 0 ? 1 : 0;
    }
}
