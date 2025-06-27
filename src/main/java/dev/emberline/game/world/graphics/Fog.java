package dev.emberline.game.world.graphics;

import com.fasterxml.jackson.annotation.JsonProperty;
import dev.emberline.core.ConfigLoader;
import dev.emberline.core.GameLoop;
import dev.emberline.core.components.Renderable;
import dev.emberline.core.graphics.SpriteLoader;
import dev.emberline.core.graphics.spritekeys.SingleSpriteKey;
import dev.emberline.core.render.CoordinateSystem;
import dev.emberline.core.render.RenderPriority;
import dev.emberline.core.render.RenderTask;
import dev.emberline.core.render.Renderer;
import javafx.scene.canvas.GraphicsContext;

import java.io.Serial;
import java.io.Serializable;

/**
 * The Fog class is responsible for rendering a layer of fog in the game environment. It is used to hide parts of the world
 * and guarantees that the player can only see a limited area of the game world at any given time. Fog tiles are rendered
 * in a grid pattern, with each tile being 2x2 units in size.
 * <h2>Fog Animation</h2>
 * The fog begins its animation with a transition between two world regions. The animation is divided into three stages:
 * <ol>
 * <li>The fog is initially visible in the first region.</li>
 * <li>After a delay, it begins to blink, alternating between the two regions.</li>
 * <li>Finally, after the blinking phase, it permanently transitions to the second region.</li>
 * </ol>
 * <h2>Configuration</h2>
 * The JSON file contains the following structure:
 * <pre>
 * {
 *  "topLeft": {
 *    "fromX": 0.0,
 *    "fromY": 0.0,
 *    "toX": 10.0,
 *    "toY": 10.0
 *  },
 *  "bottomRight": {
 *    "fromX": 20.0,
 *    "fromY": 20.0,
 *    "toX": 30.0,
 *    "toY": 30.0
 *  },
 *  "blinkEnabled": true,
 *  "blinkPeriodSeconds": 5.0,
 *  "dutyCycle": 0.5,
 *  "animationDurationSeconds": 10.0,
 *  "animationDelaySeconds": 2.0
 * }
 * </pre>
 * <ul>
 * <li>The `topLeft` and `bottomRight` fields define the coordinates of the two regions between which the fog transitions.</li>
 * <li>The `blinkEnabled` field indicates whether the blinking animation is active.</li>
 * <li>The `blinkPeriodSeconds` field specifies the duration of one blink cycle.</li>
 * <li>The `dutyCycle` field defines the proportion of the blink cycle during which the fog is visible.</li>
 * <li>The `animationDurationSeconds` field specifies the total duration of the blinking animation.</li>
 * <li>The `animationDelaySeconds` field defines the initial delay before the fog transition starts.</li>
 * </ul>
 */
public class Fog implements Renderable, Serializable {

    @Serial
    private static final long serialVersionUID = 4864461952717171977L;

    private static final int FOG_SIDE_LENGTH = 2;
    private final Metadata metadata;

    private long accumulatorNs = 0;
    private long previousTimeNs = System.nanoTime();

    private static final double SECOND_IN_NS = 1e9;

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
            @JsonProperty boolean blinkEnabled,
            @JsonProperty double blinkPeriodSeconds,
            @JsonProperty double dutyCycle,
            @JsonProperty double animationDurationSeconds,
            @JsonProperty double animationDelaySeconds
    ) implements Serializable {
    }

    /**
     * Constructs a Fog instance by loading the metadata configuration file and
     * initializing the fog animation.
     *
     * @param configDirectory the path to the directory containing the {@code fog.json} configuration file.
     */
    public Fog(final String configDirectory) {
        metadata = ConfigLoader.loadConfig(configDirectory + "fog.json", Metadata.class);
        startAnimation();
    }

    /**
     * Initializes the fog animation timings accounting for the initial delay.
     * This method should be called when the fog life cycle starts, such as when a new wave begins.
     */
    public void startAnimation() {
        previousTimeNs = System.nanoTime();
        accumulatorNs = -(long) (metadata.animationDelaySeconds * SECOND_IN_NS);
    }

    /**
     * Renders the fog element with animation effects, such as blinking and positional transitions,
     * based on the current animation time and configuration metadata.
     * <p>
     * The animation uses a duty cycle to handle blinking behavior.
     */
    @Override
    public void render() {
        final long currentTimeNs = System.nanoTime();
        accumulatorNs += currentTimeNs - previousTimeNs;
        previousTimeNs = currentTimeNs;

        // Duty cycle to make the fog blink between two states
        final double cycleDurationNs = metadata.blinkPeriodSeconds * SECOND_IN_NS;
        final boolean blinkState = accumulatorNs % cycleDurationNs < cycleDurationNs * metadata.dutyCycle;
        // Determine if the fog is currently blinking
        final boolean isBlinking = accumulatorNs >= 0
                && accumulatorNs < metadata.animationDurationSeconds * SECOND_IN_NS && metadata.blinkEnabled;
        // Draw inner or outer fog
        if (accumulatorNs < 0 || isBlinking && blinkState) {
            renderFog(metadata.topLeft.fromX, metadata.topLeft.fromY,
                    metadata.bottomRight.fromX, metadata.bottomRight.fromY);
        } else {
            renderFog(metadata.topLeft.toX, metadata.topLeft.toY,
                    metadata.bottomRight.toX, metadata.bottomRight.toY);
        }
    }

    private static void renderFog(final double topLeftX, final double topLeftY,
                                  final double bottomRightX, final double bottomRightY) {
        final Renderer renderer = GameLoop.getInstance().getRenderer();
        final GraphicsContext gc = renderer.getGraphicsContext();
        final CoordinateSystem cs = renderer.getWorldCoordinateSystem();

        final double viewWorldWidth = bottomRightX - topLeftX;
        final double viewWorldHeight = bottomRightY - topLeftY;
        final double screenWidth = renderer.getScreenWidth();
        final double screenHeight = renderer.getScreenHeight();

        renderer.addRenderTask(new RenderTask(RenderPriority.FOG, () -> {
            for (double fogWorldX = topLeftX; fogWorldX < topLeftX + viewWorldWidth; fogWorldX += FOG_SIDE_LENGTH) {
                drawFogTile(gc, cs, SingleSpriteKey.FOG_TOP, fogWorldX, topLeftY);
                drawFogTile(gc, cs, SingleSpriteKey.FOG_BOTTOM,
                        fogWorldX, topLeftY + viewWorldHeight - FOG_SIDE_LENGTH);
            }
            for (double fogWorldY = topLeftY; fogWorldY < topLeftY + viewWorldHeight; fogWorldY += FOG_SIDE_LENGTH) {
                drawFogTile(gc, cs, SingleSpriteKey.FOG_LEFT, topLeftX, fogWorldY);
                drawFogTile(gc, cs, SingleSpriteKey.FOG_RIGHT,
                        topLeftX + viewWorldWidth - FOG_SIDE_LENGTH, fogWorldY);
            }
            // Draw fog corners
            drawFogTile(gc, cs, SingleSpriteKey.FOG_TOP_LEFT, topLeftX, topLeftY);
            drawFogTile(gc, cs, SingleSpriteKey.FOG_TOP_RIGHT,
                    topLeftX + viewWorldWidth - FOG_SIDE_LENGTH, topLeftY);
            drawFogTile(gc, cs, SingleSpriteKey.FOG_BOTTOM_LEFT,
                    topLeftX, topLeftY + viewWorldHeight - FOG_SIDE_LENGTH);
            drawFogTile(gc, cs, SingleSpriteKey.FOG_BOTTOM_RIGHT,
                    topLeftX + viewWorldWidth - FOG_SIDE_LENGTH, topLeftY + viewWorldHeight - FOG_SIDE_LENGTH);
            // Convert screen corners to world coordinates and align to tile grid
            final double screenWorldLeft = Math.floor(cs.toWorldX(0));
            final double screenWorldTop = Math.floor(cs.toWorldY(0));
            final double screenWorldRight = Math.ceil(cs.toWorldX(screenWidth));
            final double screenWorldBottom = Math.ceil(cs.toWorldY(screenHeight));
            // Compute fog tile offset within the visible region
            final double fogOffsetX = (screenWorldLeft - topLeftX) % FOG_SIDE_LENGTH;
            final double fogOffsetY = (screenWorldTop - topLeftY) % FOG_SIDE_LENGTH;
            // Iterate through the fog tiles that intersect the screen area
            for (double fogX = screenWorldLeft + fogOffsetX - FOG_SIDE_LENGTH;
                 fogX < screenWorldRight; fogX += FOG_SIDE_LENGTH) {
                for (double fogY = screenWorldTop + fogOffsetY - FOG_SIDE_LENGTH;
                     fogY < screenWorldBottom; fogY += FOG_SIDE_LENGTH) {
                    // Draw fog only outside the current visible world area
                    final boolean isOutsideView = fogX < topLeftX
                            || fogX >= topLeftX + viewWorldWidth
                            || fogY < topLeftY
                            || fogY >= topLeftY + viewWorldHeight;
                    if (isOutsideView) {
                        drawFogTile(gc, cs, SingleSpriteKey.FOG, fogX, fogY);
                    }
                }
            }
        }));
    }

    private static void drawFogTile(final GraphicsContext gc, final CoordinateSystem cs,
                                    final SingleSpriteKey fogKey, final double x, final double y) {
        Renderer.drawImage(SpriteLoader.loadSprite(fogKey).image(), gc, cs, x, y, FOG_SIDE_LENGTH, FOG_SIDE_LENGTH);
    }
}
