package dev.emberline.core.render;

import java.util.Objects;

/**
 * The RenderTask class encapsulates a {@link Runnable} intended for rendering in a
 * graphical or game application. It associates a priority and optional
 * z-order with the task to ensure proper rendering order.
 * <p>
 * RenderTask implements the Comparable interface for ordering.
 */
public class RenderTask implements Comparable<RenderTask>, Runnable {
    private final RenderPriority renderPriority;
    private boolean zOrderEnabled;
    private double zOrder;
    private long secondaryPriority; //lower values get rendered first
    private final Runnable runnable;

    /**
     * Constructs a {@code RenderTask} with the specified rendering priority and runnable.
     *
     * @param renderPriority the rendering priority associated with this task. Determines the
     *                       render sequence based on its priority value.
     * @param runnable       the {@code Runnable} to be executed for this render task.
     */
    public RenderTask(final RenderPriority renderPriority, final Runnable runnable) {
        this.renderPriority = renderPriority;
        this.runnable = runnable;
    }

    /**
     * Executes the encapsulated {@link Runnable} associated with this {@code RenderTask}.
     */
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

    /**
     * Enables the z-ordering for the {@link RenderTask} to ensure a
     * rendering sequence based on the provided z-order value.
     *
     * @param lowestUnder the z-order value to be associated with this task.
     *                    Tasks with lower z-order values will be rendered underneath tasks
     *                    with higher z-order values.
     * @return the current {@code RenderTask} instance with z-ordering enabled
     *         and the z-order value updated.
     */
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
        //  Generally, the value of compareTo should return zero IF AND ONLY IF equals returns true.
        if (this.equals(t1)) {
            return 0;
        }

        final int comparison = this.renderPriority.getPriority() - t1.renderPriority.getPriority();
        if (comparison != 0) {
            return comparison;
        }
        final int secondaryComparison = comparisonToInt(this.secondaryPriority - t1.secondaryPriority);
        final int zOrderComparison = comparisonToInt(this.zOrder - t1.zOrder);
        if (zOrderEnabled && zOrderComparison != 0) {
            return zOrderComparison;
        }
        if (secondaryComparison != 0) {
            return secondaryComparison;
        }
        // This is the last resort, we compare the hash codes of the runnables.
        // This is not a perfect solution, but it should be extremely rare that two RenderTasks
        // have the same render priority, secondary priority, z-order and runnable hashCode but are not equal.
        return this.runnable.hashCode() - t1.runnable.hashCode();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(final Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final RenderTask that = (RenderTask) o;
        return zOrderEnabled == that.zOrderEnabled
                && Double.compare(zOrder, that.zOrder) == 0
                && secondaryPriority == that.secondaryPriority
                && renderPriority == that.renderPriority
                && Objects.equals(runnable, that.runnable);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return Objects.hash(renderPriority, zOrderEnabled, zOrder, secondaryPriority, runnable);
    }

    private int comparisonToInt(final double comparison) {
        return comparison < 0 ? -1 : comparison > 0 ? 1 : 0;
    }

    private int comparisonToInt(final long comparison) {
        return comparison < 0 ? -1 : comparison > 0 ? 1 : 0;
    }
}
