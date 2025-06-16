package dev.emberline.core.animations;

import dev.emberline.core.components.Updatable;
import javafx.scene.image.Image;

public interface Animation extends Updatable {

    Image getAnimationState();

    boolean hasEnded();
}
