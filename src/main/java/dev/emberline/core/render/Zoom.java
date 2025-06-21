package dev.emberline.core.render;

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
    private record Translations(
        @JsonProperty Translation topLeft,
        @JsonProperty Translation bottomRight,
        @JsonProperty double animationDurationSeconds,
        @JsonProperty double animationDelaySeconds
    ) {}
    private final Translations translations;

    private long accumulatorNs = 0;
    private long previousTimeNs = System.nanoTime();

    public Zoom(String wavePath) {
        translations = ConfigLoader.loadConfig(wavePath + "cs.json", Translations.class);
        startAnimation();
    }

    private void updateCS(double regionX1, double regionY1, double regionX2, double regionY2) {
        GameLoop.getInstance().getRenderer().getWorldCoordinateSystem().setRegion(regionX1, regionY1, regionX2, regionY2);
    }

    public boolean isOver() {
        return accumulatorNs >= translations.animationDurationSeconds * 1e9;
    }

    public void startAnimation() {
        previousTimeNs = System.nanoTime();
        accumulatorNs = -(long) (translations.animationDelaySeconds * 1e9);
    }

    @Override
    public void render() {
        long currentTimeNs = System.nanoTime();
        accumulatorNs += currentTimeNs - previousTimeNs; // Convert nanoseconds to seconds
        previousTimeNs = currentTimeNs;
        double t = Math.min((accumulatorNs/1e9) / translations.animationDurationSeconds, 1.0);
        if (accumulatorNs < 0) { // Animation isn't started yet
            updateCS(translations.topLeft.fromX, translations.topLeft.fromY, translations.bottomRight.fromX, translations.bottomRight.fromY);
            return;
        }
        if (t >= 1) { // Animation is over
            return;
        }
        double easedT = easeInOutExpo(t);
        updateCS(
                lerp(translations.topLeft.fromX, translations.topLeft.toX, easedT),
                lerp(translations.topLeft.fromY, translations.topLeft.toY, easedT),
                lerp(translations.bottomRight.fromX, translations.bottomRight.toX, easedT),
                lerp(translations.bottomRight.fromY, translations.bottomRight.toY, easedT)
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