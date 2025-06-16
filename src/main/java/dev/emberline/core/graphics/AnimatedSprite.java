package dev.emberline.core.graphics;

import javafx.scene.image.Image;

public class AnimatedSprite implements Sprite {
    private final Image[] images;
    private int frameIndex = 0;

    public AnimatedSprite(Image[] images) {
        if (images == null || images.length == 0) {
            throw new IllegalArgumentException("Image array cannot be null or empty");
        }
        this.images = images;
    }

    @Override
    public Image getImage() {
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
     * Sets the frame index for the animated sprite. The specified index determines
     * which frame in the animation sequence is currently active. If the index exceeds
     * the total number of frames, it will wrap around using a modulo operation.
     *
     * @param frameIndex the index of the frame to set; must be non-negative
     * @throws IllegalArgumentException if the frame index is negative
     */
    public void setFrame(int frameIndex) {
        if (frameIndex < 0) {
            throw new IllegalArgumentException("Frame index cannot be negative");
        }
        this.frameIndex = frameIndex % images.length;
    }
}
