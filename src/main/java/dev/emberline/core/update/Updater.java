package dev.emberline.core.update;

import dev.emberline.core.components.Updatable;

/**
 * The Updater class is responsible for delegating update logic
 * within the application. It operates as a centralized handler for performing
 * time-based updates on an {@link Updatable} root object.
 * <p>
 * The update logic is forwarded from the root object, which is an instance
 * of a class implementing the {@link Updatable} interface. The root object
 * may cascade the update logic to its own components, enabling hierarchical
 * update management.
 */
public class Updater {

    private final Updatable root;

    /**
     * Constructs a new {@code Updater} instance with the specified root {@link Updatable}.
     *
     * @param root the root object implementing {@link Updatable}, which will be updated
     *             when the {@code update} method is called.
     */
    public Updater(final Updatable root) {
        this.root = root;
    }

    /**
     * Updates the root {@link Updatable} object by delegating the update logic.
     *
     * @param elapsed the time elapsed since the previous update in nanoseconds
     */
    public void update(final long elapsed) {
        root.update(elapsed);
    }
}
