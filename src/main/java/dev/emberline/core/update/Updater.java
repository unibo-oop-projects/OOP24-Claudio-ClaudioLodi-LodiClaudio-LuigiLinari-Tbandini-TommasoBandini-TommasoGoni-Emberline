package dev.emberline.core.update;

import dev.emberline.core.components.UpdateComponent;

/**
 * The Updater class is responsible for delegating update logic
 * within the application. It operates as a centralized handler for performing
 * time-based updates on an {@link UpdateComponent} root object.
 * <p>
 * The update logic is forwarded from the root object, which is an instance
 * of a class implementing the {@link UpdateComponent} interface. The root object
 * may cascade the update logic to its own components, enabling hierarchical
 * update management.
 */
public class Updater {

    private final UpdateComponent root;

    /**
     * Constructs a new {@code Updater} instance with the specified root {@link UpdateComponent}.
     *
     * @param root the root object implementing {@link UpdateComponent}, which will be updated
     *             when the {@code update} method is called.
     */
    public Updater(final UpdateComponent root) {
        this.root = root;
    }

    /**
     * Updates the root {@link UpdateComponent} object by delegating the update logic.
     *
     * @param elapsed the time elapsed since the previous update in nanoseconds
     */
    public void update(final long elapsed) {
        root.update(elapsed);
    }
}
