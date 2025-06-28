package dev.emberline.game.world.graphics;

import com.fasterxml.jackson.annotation.JsonProperty;
import dev.emberline.core.GameLoop;
import dev.emberline.core.components.Renderable;
import dev.emberline.core.config.ConfigLoader;

import java.io.Serial;
import java.io.Serializable;

/**
 * The {@code Zoom} class represents an animation effect that interpolates between two defined
 * coordinate regions over a specified duration, applying easing for a smooth transition.
 * <p>
 * The animation is configured using {@code Metadata}, which defines the starting and
 * target coordinates, as well as the duration and delay of the animation sequence.
 * The configuration is loaded from a JSON file using the {@code ConfigLoader}.
 */
public final class Zoom implements Renderable, Serializable {

    @Serial
    private static final long serialVersionUID = -7769530860574511173L;

    private final Metadata metadata;

    private long accumulatorNs;
    private long previousTimeNs = System.nanoTime();

    private static final double SECOND_IN_NS = 1e9;

    //zoom configuration
    private record Translation(
            @JsonProperty double fromX,
            @JsonProperty double fromY,
            @JsonProperty double toX,
            @JsonProperty double toY
    ) implements Serializable {
    }

    private record Metadata(
            @JsonProperty Translation topLeft,
            @JsonProperty Translation bottomRight,
            @JsonProperty double animationDurationSeconds,
            @JsonProperty double animationDelaySeconds
    ) implements Serializable {
    }

    /**
     * Constructs a new {@code Zoom} instance by loading metadata from a configuration file
     * and initializing the animation sequence.
     *
     * @param wavePath the base file path used to locate the configuration file.
     * @throws RuntimeException if the configuration file cannot be read or parsed.
     */
    public Zoom(final String wavePath) {
        metadata = ConfigLoader.loadConfig(wavePath + "cs.json", Metadata.class);
        startAnimation();
    }

    private void updateCS(final double regionX1, final double regionY1, final double regionX2, final double regionY2) {
        GameLoop.getInstance().getRenderer().getWorldCoordinateSystem().setRegion(regionX1, regionY1, regionX2, regionY2);
    }

    /**
     * Returns whether the animation sequence is complete.
     *
     * @return whether the animation sequence is complete.
     */
    public boolean isOver() {
        return accumulatorNs >= metadata.animationDurationSeconds * SECOND_IN_NS;
    }

    /**
     * Starts the animation sequence by initializing the time tracking variables.
     */
    public void startAnimation() {
        previousTimeNs = System.nanoTime();
        accumulatorNs = -(long) (metadata.animationDelaySeconds * SECOND_IN_NS);
    }

    /**
     * Updates the world coordinate system based on the ongoing state of the zoom animation.
     */
    @Override
    public void render() {
        final long currentTimeNs = System.nanoTime();
        accumulatorNs += currentTimeNs - previousTimeNs;
        previousTimeNs = currentTimeNs;
        // todo: check finals
        double t = Math.min(accumulatorNs / SECOND_IN_NS / metadata.animationDurationSeconds, 1.0);
        if (accumulatorNs < 0) { // Animation isn't started yet
            updateCS(metadata.topLeft.fromX, metadata.topLeft.fromY, metadata.bottomRight.fromX, metadata.bottomRight.fromY);
            return;
        }
        if (t >= 1) { // Animation is over
            t = 1;
        }
        final double easedT = easeInOutExpo(t);
        updateCS(
                lerp(metadata.topLeft.fromX, metadata.topLeft.toX, easedT),
                lerp(metadata.topLeft.fromY, metadata.topLeft.toY, easedT),
                lerp(metadata.bottomRight.fromX, metadata.bottomRight.toX, easedT),
                lerp(metadata.bottomRight.fromY, metadata.bottomRight.toY, easedT)
        );
    }

    private static double lerp(final double a, final double b, final double t) {
        return a + (b - a) * t;
    }

    private static double easeInOutExpo(final double x) {
        if (x <= 0 || x >= 1) {
            return Math.clamp(x, 0, 1);
        }
        final double beforeHalfFactor = 20, afterHalfFactor = -20;
        return x < 0.5 ? Math.pow(2, beforeHalfFactor * x - 10) / 2 : (2 - Math.pow(2, -afterHalfFactor * x + 10)) / 2;
    }
}
