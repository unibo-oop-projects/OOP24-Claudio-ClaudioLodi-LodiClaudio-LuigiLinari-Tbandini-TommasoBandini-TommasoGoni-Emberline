package dev.emberline.core.graphics;

import javafx.scene.image.Image;

public class SingleSprite implements Sprite {
    private final Image image;

    public SingleSprite(Image image) {
        this.image = image;
    }

    @Override
    public Image getImage() {
        return image;
    }
}
