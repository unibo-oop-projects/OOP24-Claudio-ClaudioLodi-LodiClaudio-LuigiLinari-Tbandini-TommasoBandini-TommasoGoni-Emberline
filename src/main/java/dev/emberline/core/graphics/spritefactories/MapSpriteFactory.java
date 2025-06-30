package dev.emberline.core.graphics.spritefactories;

import com.fasterxml.jackson.annotation.JsonProperty;
import dev.emberline.core.config.ConfigLoader;
import dev.emberline.core.graphics.AnimatedSprite;
import dev.emberline.core.graphics.Sprite;
import dev.emberline.core.graphics.spritekeys.MapSpriteKey;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;

import java.util.Locale;
import java.util.Objects;

/**
 * A factory class for creating animated map sprites based on a provided {@link MapSpriteKey}.
 * The map sprite atlas is a single image containing all sprite frames,
 * and the individual frames are extracted during sprite creation based on metadata values.
 */
public final class MapSpriteFactory implements SpriteFactory<MapSpriteKey> {

    private static final Metadata METADATA = ConfigLoader.loadConfig("/sprites/map/map.json", Metadata.class);

    private record Waves(@JsonProperty int wave, @JsonProperty int startFrame, 
                         @JsonProperty int endFrame, @JsonProperty int frames) {
    }

    private record Metadata(@JsonProperty String fileName, @JsonProperty int mapLenght, 
                            @JsonProperty int mapHeight, @JsonProperty int frameTimeNs,
                            @JsonProperty Waves[] waves) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Sprite loadSprite(final MapSpriteKey key) {
        final int frameNumber = METADATA.waves[key.waveNumber()].frames;
        final int startFrame = METADATA.waves[key.waveNumber()].startFrame;
        final int endFrame = METADATA.waves[key.waveNumber()].endFrame;
        
        final String mapAtlasPath = String.format(METADATA.fileName.toLowerCase(Locale.US));
        final Image mapAtlas = getMapAtlas(mapAtlasPath);
        
        final Image[] frames = new Image[frameNumber];
        for (int i = startFrame; i <= endFrame; ++i) {
            final int x = i * METADATA.mapLenght;
            final int y = 0;
            frames[i - startFrame] = new WritableImage(mapAtlas.getPixelReader(), x, y, METADATA.mapLenght, METADATA.mapHeight);
        }
        return new AnimatedSprite(frames, key, METADATA.frameTimeNs);
    }

    private static Image getMapAtlas(final String mapAtlasPath) {
        return new Image(Objects.requireNonNull(MapSpriteFactory.class.getResourceAsStream(mapAtlasPath)));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Class<MapSpriteKey> getKeyType() {
        return MapSpriteKey.class;
    }
}
