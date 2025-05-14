package dev.emberline.core.graphics;

import java.util.ArrayList;
import java.util.List;

import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritableImage;

public final class SpriteLoader {
    private static Image ENEMY_ATLAS = new Image(SpriteLoader.class.getResourceAsStream("/enemy_assets/EnemyAtlas.png"));
    private static Image PROJECTILES_ATLAS = new Image(SpriteLoader.class.getResourceAsStream("/enemy_assets/ProjectilesAtlas.png"));

    private static final int TILE_WIDTH = 16;
    private static final int TILE_HEIGHT = 16;

    public record EnemySprites(
        List<Image> WALK_FRONT,
        List<Image> WALK_BACK,
        List<Image> WALK_LEFT,
        List<Image> WALK_RIGHT,

        List<Image> FIRE_FRONT,
        List<Image> FIRE_BACK, 
        List<Image> FIRE_LEFT,
        List<Image> FIRE_RIGHT,
        
        List<Image> ICE_FRONT,
        List<Image> ICE_BACK, 
        List<Image> ICE_LEFT,
        List<Image> ICE_RIGHT,

        List<Image> DYING_FRONT,
        List<Image> DYING_BACK,
        List<Image> DYING_LEFT,
        List<Image> DYING_RIGHT
    ) {};

    public record ProjectilesSprites(
        List<Image> SMALL_BASE,
        List<Image> BASE_BASE,
        List<Image> BIG_BASE,

        List<Image> SMALL_FIRE,
        List<Image> BASE_FIRE,
        List<Image> BIG_FIRE,

        List<Image> SMALL_ICE,
        List<Image> BASE_ICE,
        List<Image> BIG_ICE
    ) {};

    public static EnemySprites ENEMY_SPRITES = loadEnemySprites();
    public static ProjectilesSprites PROJECTILES_SPRITES = loadProjectilesSprites();

    private SpriteLoader() {};

    private static EnemySprites loadEnemySprites() {
        List<Image> walkFront = loadAnimation(ENEMY_ATLAS, 8, 0, 0, TILE_WIDTH, TILE_HEIGHT);
        List<Image> walkBack = loadAnimation(ENEMY_ATLAS, 8, 16, 0, TILE_WIDTH, TILE_HEIGHT);
        List<Image> walkLeft = loadAnimation(ENEMY_ATLAS, 8, 32, 0, TILE_WIDTH, TILE_HEIGHT);
        List<Image> walkRight = loadAnimation(ENEMY_ATLAS, 8, 48, 0, TILE_WIDTH, TILE_HEIGHT);

        List<Image> fireFront = loadAnimation(ENEMY_ATLAS, 8, 0, 16, TILE_WIDTH, TILE_HEIGHT);
        List<Image> fireBack = loadAnimation(ENEMY_ATLAS, 8, 16, 16, TILE_WIDTH, TILE_HEIGHT);
        List<Image> fireLeft = loadAnimation(ENEMY_ATLAS, 8, 32, 16, TILE_WIDTH, TILE_HEIGHT);
        List<Image> fireRight = loadAnimation(ENEMY_ATLAS, 8, 48, 16, TILE_WIDTH, TILE_HEIGHT);

        List<Image> iceFront = loadAnimation(ENEMY_ATLAS, 8, 0, 32, TILE_WIDTH, TILE_HEIGHT);
        List<Image> iceBack = loadAnimation(ENEMY_ATLAS, 8, 16, 32, TILE_WIDTH, TILE_HEIGHT);
        List<Image> iceLeft = loadAnimation(ENEMY_ATLAS, 8, 32, 32, TILE_WIDTH, TILE_HEIGHT);
        List<Image> iceRight = loadAnimation(ENEMY_ATLAS, 8, 48, 32, TILE_WIDTH, TILE_HEIGHT);

        List<Image> dyingFront = loadAnimation(ENEMY_ATLAS, 8, 0, 48, TILE_WIDTH, TILE_HEIGHT);
        List<Image> dyingBack = loadAnimation(ENEMY_ATLAS, 8, 16, 48, TILE_WIDTH, TILE_HEIGHT);
        List<Image> dyingLeft = loadAnimation(ENEMY_ATLAS, 8, 32, 48, TILE_WIDTH, TILE_HEIGHT);
        List<Image> dyingRight = loadAnimation(ENEMY_ATLAS, 8, 48, 48, TILE_WIDTH, TILE_HEIGHT);

        return ENEMY_SPRITES = new EnemySprites(
            walkFront,
            walkBack,
            walkLeft,
            walkRight,

            fireFront,
            fireBack,
            fireLeft,
            fireRight,

            iceFront,
            iceBack,
            iceLeft,
            iceRight,

            dyingFront,
            dyingBack,
            dyingLeft,
            dyingRight
        );
    }

    private static ProjectilesSprites loadProjectilesSprites() {
        List<Image> smallBase = loadAnimation(PROJECTILES_ATLAS, 8, 0, 0, TILE_WIDTH, TILE_HEIGHT);
        List<Image> baseBase = loadAnimation(PROJECTILES_ATLAS, 8, 16, 0, TILE_WIDTH, TILE_HEIGHT);
        List<Image> bigBase = loadAnimation(PROJECTILES_ATLAS, 8, 32, 0, TILE_WIDTH, TILE_HEIGHT);

        List<Image> smallFire = loadAnimation(PROJECTILES_ATLAS, 8, 0, 16, TILE_WIDTH, TILE_HEIGHT);
        List<Image> baseFire = loadAnimation(PROJECTILES_ATLAS, 8, 16, 16, TILE_WIDTH, TILE_HEIGHT);
        List<Image> bigFire = loadAnimation(PROJECTILES_ATLAS, 8, 32, 16, TILE_WIDTH, TILE_HEIGHT);

        List<Image> smallIce = loadAnimation(PROJECTILES_ATLAS, 8, 0, 32, TILE_WIDTH, TILE_HEIGHT);
        List<Image> baseIce = loadAnimation(PROJECTILES_ATLAS, 8, 16, 32, TILE_WIDTH, TILE_HEIGHT);
        List<Image> bigIce = loadAnimation(PROJECTILES_ATLAS, 8, 32, 32, TILE_WIDTH, TILE_HEIGHT);


        return PROJECTILES_SPRITES = new ProjectilesSprites(
            smallBase,
            baseBase,
            bigBase,

            smallFire,
            baseFire,
            bigFire,

            smallIce,
            baseIce,
            bigIce
        );
    }

    /**
     * Extracts a sequence of frames from a sprite atlas.
     * 
     * @param atlas      The full sprite sheet containing the animation frames.
     * @param frameCount The total number of frames in the animation.
     * @param startX     The x-coordinate of the first frame in the atlas.
     * @param startY     The y-coordinate of the first frame in the atlas.
     * @param width      The width of each frame.
     * @param height     The height of each frame.
     * @return           A list of individual frames as Image objects.
     */
    private static List<Image> loadAnimation(Image atlas, int frameCount, int startX, int startY, int width, int height) {
        List<Image> frames = new ArrayList<>();
        int singleBatchWidth = (int) atlas.getWidth() / frameCount;

        for (int i = 0; i < frameCount; ++i) {
            int x = startX + (i * singleBatchWidth);
            int y = startY;
            frames.add(loadTexture(atlas, x, y, width, height));
        }
        
        return frames;
    }

    private static Image loadTexture(Image atlas, int startX, int startY, int width, int height) {
        PixelReader reader = atlas.getPixelReader();
        return new WritableImage(
            reader,
            startX,
            startY,
            width,
            height
        );
    }
}

