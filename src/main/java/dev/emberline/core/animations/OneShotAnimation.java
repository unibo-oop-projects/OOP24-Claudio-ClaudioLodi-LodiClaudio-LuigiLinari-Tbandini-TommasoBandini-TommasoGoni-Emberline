package dev.emberline.core.animations;

import dev.emberline.core.components.Updatable;
import javafx.scene.image.Image;

import java.util.List;

public class OneShotAnimation implements Updatable {

    private final List<Image> animationStates;
    private int currState = 0;

    private long interval;
    private long intervalAcc = 0;

    public OneShotAnimation(List<Image> animationStates, long interval) {
        this.animationStates = animationStates;
        this.interval = interval;
    }

    public Image getAnimationState() {
        return animationStates.get(currState);
    }

    public boolean hasEnded() {
        return currState + 1 >= animationStates.size();
    }

    @Override
    public void update(long elapsed) {
        if (!hasEnded()) {
            intervalAcc += elapsed;

            while (intervalAcc >= interval && !hasEnded()) {
                currState++;
                intervalAcc -= interval;
            }
        }
    }
}
