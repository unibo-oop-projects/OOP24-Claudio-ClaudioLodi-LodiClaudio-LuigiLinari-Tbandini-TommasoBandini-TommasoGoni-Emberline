package dev.emberline.game.world.buildings.towerloader;

import com.fasterxml.jackson.annotation.JsonProperty;
import dev.emberline.core.config.ConfigLoader;

import java.io.Serial;
import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

/**
 * This class in used as a loader for the Towers in the current wave.
 */
public class TowerLoader implements Serializable {

    @Serial
    private static final long serialVersionUID = 4377537553748556262L;

    private final static String TOWERLOADER_CONFIG_NAME = "towers.json";

    private final TowerToLoad[] METADATA;

    /**
     * Record class that encapsulates the informations about where to spawn the towers.
     *
     * @param x coordinate.
     * @param y ccordinate.
     */
    public record TowerToLoad(
            @JsonProperty double x,
            @JsonProperty double y
    ) implements Serializable { }

    /**
     * Creates an instance of {@code TowerLoader} based on the configuration file passed as a parameter.
     *
     * @param wavePath the path of the directory containing the wave files
     */
    public TowerLoader(final String wavePath) {
        METADATA = ConfigLoader.loadConfig(wavePath + TOWERLOADER_CONFIG_NAME, TowerToLoad[].class);
    }

    /**
     * This method is a getter for the towers to build in the current wave.
     *
     * @return the list of towers to build in the current wave
     */
    public List<TowerToLoad> getTowers() {
        return List.copyOf(Arrays.asList(METADATA));
    }
}
