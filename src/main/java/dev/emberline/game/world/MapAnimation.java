package dev.emberline.game.world;

import dev.emberline.core.components.Updatable;
import dev.emberline.core.graphics.AnimatedSprite;
import dev.emberline.core.graphics.SpriteLoader;
import dev.emberline.core.graphics.spritekeys.MapSpriteKey;
import dev.emberline.game.world.waves.IWaveManager;
import javafx.scene.image.Image;

public class MapAnimation implements Updatable {

    private final IWaveManager waveManager;
    private AnimatedSprite animatedSprite;

    private int frameIndex = 0;
    private long accumulatedTimeNs = 0;
    private int currentWaveIndex = -1;

    public MapAnimation(IWaveManager waveManager) {
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

    public Image getImage() {
        return animatedSprite.image(frameIndex);
    }

    @Override
    public void update(long elapsed) {
        if (isAnimationOver()) return;

        updateAnimatedSprite();

        long frameTimeNs = animatedSprite.getFrameTimeNs();
        accumulatedTimeNs += elapsed;
        while(!isAnimationOver() && accumulatedTimeNs >= frameTimeNs) {
            accumulatedTimeNs -= frameTimeNs;
            frameIndex++;
        }
    }
}
