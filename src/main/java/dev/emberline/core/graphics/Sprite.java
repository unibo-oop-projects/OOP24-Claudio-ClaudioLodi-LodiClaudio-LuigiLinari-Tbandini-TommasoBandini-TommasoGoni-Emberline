package dev.emberline.core.graphics;

import javafx.scene.image.Image;

/**
 * Represents a graphical entity capable of providing an image instance.
 * This interface serves as a common abstraction for various sprite
 * implementations, such as single-image sprites or animated multi-frame sprites.
 */
public interface Sprite {
    /**
     * Retrieves the current image associated with this sprite.
     *
     * @return the current image of the sprite
     */
    Image getImage();
}
