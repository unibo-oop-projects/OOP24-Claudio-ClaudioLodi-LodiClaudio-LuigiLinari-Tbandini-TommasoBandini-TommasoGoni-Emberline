package dev.emberline.core.components;

import dev.emberline.core.GameLoop;
import dev.emberline.core.render.RenderTask;
import dev.emberline.core.render.Renderer;

/**
 * This interface defines a general rule for handling rendering calls to
 * the {@link dev.emberline.core.render.Renderer}.
 * <p>
 * Classes implementing this interface might delegate the rendering to other
 * implementing classes.
 * <p>
 * This interface should be implemented by any component that requires
 * rendering capabilities.
 *
 * @see dev.emberline.core.render.Renderer
 */
@FunctionalInterface
public interface RenderComponent {
    /**
     * Executes the rendering logic defined by the implementing class.
     * <p>
     * This method must only be called in cascade from other {@code render} methods;
     * the only class that can call this method directly is the {@link Renderer}.
     * This guarantees that every render call is paced correctly by render pulses, and
     * that it is being processed on the {@link GameLoop Game Thread}.
     * <p>
     * This method should be the only source of rendering calls to the {@link Renderer}.
     *
     * @see Renderer#addRenderTask(RenderTask)
     */
    void render();
}
