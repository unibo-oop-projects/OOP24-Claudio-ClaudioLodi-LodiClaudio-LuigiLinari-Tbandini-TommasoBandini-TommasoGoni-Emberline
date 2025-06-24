package dev.emberline.game.world.graphics;

import com.fasterxml.jackson.annotation.JsonProperty;
import dev.emberline.core.ConfigLoader;
import dev.emberline.core.GameLoop;
import dev.emberline.core.components.Renderable;

/**
 * This class is used to Zoom in and out areas of the map.
 * It requires a file with the proper indications to be used.
 */
public class Zoom implements Renderable {
    //zoom configuration
    private record Translation(
        @JsonProperty double fromX,
        @JsonProperty double fromY,
        @JsonProperty double toX,
        @JsonProperty double toY
    ) {}
    private record Metadata(
        @JsonProperty Translation topLeft,
        @JsonProperty Translation bottomRight,
        @JsonProperty double animationDurationSeconds,
        @JsonProperty double animationDelaySeconds
    ) {}
    private final Metadata metadata;

    private long accumulatorNs = 0;
    private long previousTimeNs = System.nanoTime();

    public Zoom(String wavePath) {
        metadata = ConfigLoader.loadConfig(wavePath + "cs.json", Metadata.class);
        startAnimation();
    }

    private void updateCS(double regionX1, double regionY1, double regionX2, double regionY2) {
        GameLoop.getInstance().getRenderer().getWorldCoordinateSystem().setRegion(regionX1, regionY1, regionX2, regionY2);
    }

    public boolean isOver() {
        return accumulatorNs >= metadata.animationDurationSeconds * 1e9;
    }

    public void startAnimation() {
        previousTimeNs = System.nanoTime();
        accumulatorNs = -(long) (metadata.animationDelaySeconds * 1e9);
    }

    @Override
    public void render() {
        long currentTimeNs = System.nanoTime();
        accumulatorNs += currentTimeNs - previousTimeNs;
        previousTimeNs = currentTimeNs;
        double t = Math.min((accumulatorNs/1e9) / metadata.animationDurationSeconds, 1.0);
        if (accumulatorNs < 0) { // Animation isn't started yet
            updateCS(metadata.topLeft.fromX, metadata.topLeft.fromY, metadata.bottomRight.fromX, metadata.bottomRight.fromY);
            return;
        }
        if (t >= 1) { // Animation is over
            return;
        }
        double easedT = easeInOutExpo(t);
        updateCS(
                lerp(metadata.topLeft.fromX, metadata.topLeft.toX, easedT),
                lerp(metadata.topLeft.fromY, metadata.topLeft.toY, easedT),
                lerp(metadata.bottomRight.fromX, metadata.bottomRight.toX, easedT),
                lerp(metadata.bottomRight.fromY, metadata.bottomRight.toY, easedT)
        );
    }

    private static double lerp(double a, double b, double t) {
        return a + (b - a) * t;
    }

    private static double easeInOutExpo(double x) {
        if (x <= 0 || x >= 1) {
            return Math.clamp(x, 0, 1);
        }
        return x < 0.5 ? Math.pow(2, 20 * x - 10) / 2 : (2 - Math.pow(2, -20 * x + 10)) / 2;
    }
}