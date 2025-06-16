package dev.emberline.core.animations;

import java.util.List;

import dev.emberline.core.components.Updatable;
import javafx.scene.image.Image;

@Deprecated
public class Animation implements Updatable {
    
    private final List<Image> animationStates;
    private int currState = 0;

    private long interval;
    private long intervalAcc = 0;

    public Animation(List<Image> animationStates, long interval) {
        this.animationStates = animationStates;
        this.interval = interval;
    }

    public Image getAnimationState() {
        return animationStates.get(currState);
    }

    @Override
    public void update(long elapsed) {
        intervalAcc += elapsed;

        while (intervalAcc >= interval) {
            currState = (currState + 1) % animationStates.size();
            intervalAcc -= interval;
        }
    }
}
