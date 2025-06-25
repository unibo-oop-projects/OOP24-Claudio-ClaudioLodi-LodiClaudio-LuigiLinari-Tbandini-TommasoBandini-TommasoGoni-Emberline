package dev.emberline.core.graphics;

import javafx.scene.image.Image;

public class AnimatedSprite implements Sprite {
    private final Image[] images;
    private final int frameTimeNs;

    public AnimatedSprite(final Image[] images, final int frameTimeNs) {
        if (images == null || images.length == 0) {
            throw new IllegalArgumentException("Image array cannot be null or empty");
        }
        this.images = images;
        this.frameTimeNs = frameTimeNs;
    }

    @Override
    public Image image() {
        return images[0];
    }

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
}
