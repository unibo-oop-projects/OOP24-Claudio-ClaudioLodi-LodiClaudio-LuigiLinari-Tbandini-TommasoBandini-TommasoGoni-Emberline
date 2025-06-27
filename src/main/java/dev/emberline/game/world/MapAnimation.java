package dev.emberline.game.world;

import dev.emberline.core.components.Updatable;
import dev.emberline.core.graphics.AnimatedSprite;
import dev.emberline.core.graphics.SpriteLoader;
import dev.emberline.core.graphics.spritekeys.MapSpriteKey;
import dev.emberline.game.world.waves.IWaveManager;
import javafx.scene.image.Image;

import java.io.Serial;
import java.io.Serializable;

/**
 * Represents an animated map that updates its animation based on the current wave
 * managed by an external {@link IWaveManager}. This class controls the animation's
 * frame updates and sprite transitions as the wave index changes.
 */
public class MapAnimation implements Updatable, Serializable {

    @Serial
    private static final long serialVersionUID = 556620506149444855L;

    private final IWaveManager waveManager;
    private AnimatedSprite animatedSprite;

    private int frameIndex = 0;
    private long accumulatedTimeNs = 0;
    private int currentWaveIndex = -1;

    /**
     * Constructs a new instance of the {@code MapAnimation} class using the provided {@code IWaveManager}.
     *
     * @param waveManager an instance of {@code IWaveManager} that manages the wave index and provides
     *                    the current wave used to control animation updates.
     */
    public MapAnimation(final IWaveManager waveManager) {
        this.waveManager = waveManager;
        updateAnimatedSprite();
    }

    private boolean isAnimationOver() {
        return frameIndex + 1 >= animatedSprite.getFrameCount();
    }

    private void updateAnimatedSprite() {
        if (waveManager.getCurrentWaveIndex() > currentWaveIndex) {
            currentWaveIndex = waveManager.getCurrentWaveIndex();
            this.animatedSprite = (AnimatedSprite) SpriteLoader.loadSprite(
                    new MapSpriteKey(currentWaveIndex)
            );
        }
    }

    /**
     * Retrieves the current frame of the animation as an {@code Image} instance.
     *
     * @return the {@code Image} representing the current frame of the animation.
     */
    public Image getImage() {
        return animatedSprite.image(frameIndex);
    }

    @Override
    public void update(final long elapsed) {
        if (isAnimationOver()) {
            return;
        }

        updateAnimatedSprite();

        final long frameTimeNs = animatedSprite.getFrameTimeNs();
        accumulatedTimeNs += elapsed;
        while (!isAnimationOver() && accumulatedTimeNs >= frameTimeNs) {
            accumulatedTimeNs -= frameTimeNs;
            frameIndex++;
        }
    }
}
