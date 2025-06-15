package dev.emberline.core.graphics;

import javafx.scene.image.Image;

public class Sprite {
    private final Image image;

    public Sprite(Image image) {
        this.image = image;
    }

    public Image getImage() {
        return image;
    }
}
