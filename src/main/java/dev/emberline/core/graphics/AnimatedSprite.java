package dev.emberline.core.graphics;

import dev.emberline.core.graphics.spritekeys.SpriteKey;
import javafx.scene.image.Image;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serial;
import java.io.Serializable;

/**
 * The {@code AnimatedSprite} class represents a sprite composed of multiple frames,
 * enabling animation by cycling through these frames. Each frame is represented by
 * an {@link Image} and displayed for a fixed duration.
 * The class provides access to individual frames and general animation properties,
 * such as the number of frames and the duration per frame.
 *
 * @see Sprite
 */
public class AnimatedSprite implements Sprite, Serializable {
    @Serial
    private static final long serialVersionUID = -6515408676254063528L;

    private final transient Image[] images;
    private final SpriteKey key;
    private final int frameTimeNs;

    /**
     * Constructs an {@code AnimatedSprite} using the provided array of images for animation frames
     * and the duration each frame should be displayed.
     *
     * @param images      an array of {@link Image} objects representing the frames of the animation;
     *                    must not be null or empty
     * @param frameTimeNs the duration in nanoseconds each frame is displayed; must be a positive integer
     * @throws IllegalArgumentException if the {@code images} array is null or empty
     */
    public AnimatedSprite(final Image[] images, final SpriteKey spriteKey, final int frameTimeNs) {
        if (images == null || images.length == 0) {
            throw new IllegalArgumentException("Image array cannot be null or empty");
        }
        this.images = images;
        this.key = spriteKey;
        this.frameTimeNs = frameTimeNs;
    }

    /**
     * Retrieves the first frame of the animated sprite.
     * <p>
     * Note: this method is added just for interface adherence purposes,
     * the expected usage of this class is by the {@link AnimatedSprite#image(int)} method.
     *
     * @return the first {@link Image} in the sequence of animation frames
     */
    @Override
    public Image image() {
        return images[0];
    }

    /**
     * Retrieves the image corresponding to the specified frame index in the animation sequence.
     *
     * @param frameIndex the index of the frame whose image is to be retrieved; must be within the bounds of the animation frames
     * @return the {@link Image} at the specified frame index
     * @throws ArrayIndexOutOfBoundsException if the provided frameIndex is out of bounds
     */
    public Image image(final int frameIndex) {
        return images[frameIndex];
    }

    /**
     * Returns the number of frames in the animated sprite.
     *
     * @return the total number of frames available in the animation
     */
    public int getFrameCount() {
        return images.length;
    }

    /**
     * Returns the duration of each frame in the animated sprite.
     *
     * @return the time (in nanoseconds) each frame is displayed
     */
    public int getFrameTimeNs() {
        return frameTimeNs;
    }

    @Serial
    private void readObject(ObjectInputStream e) throws IOException, ClassNotFoundException {
        e.defaultReadObject();

        AnimatedSprite animatedSprite = (AnimatedSprite) SpriteLoader.loadSpriteAfterSerialization(key);

        System.arraycopy(animatedSprite.images, 0, this.images, 0, animatedSprite.images.length);
    }
}
