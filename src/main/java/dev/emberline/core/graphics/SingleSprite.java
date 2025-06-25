package dev.emberline.core.graphics;

import javafx.scene.image.Image;

/**
 * The {@code SingleSprite} record class provides a straightforward
 * representation of a sprite using a single {@link Image} instance.
 * <p>
 * This implementation is suitable for objects requiring a static image as their visual
 * representation, without the need for animations or frame-based updates.
 *
 * @param image the single static {@link Image} representing the sprite
 */
public record SingleSprite(Image image) implements Sprite {
}
