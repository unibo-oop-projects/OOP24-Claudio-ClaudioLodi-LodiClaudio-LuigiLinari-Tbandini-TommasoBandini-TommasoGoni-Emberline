package dev.emberline.core.render;

import com.fasterxml.jackson.annotation.JsonProperty;
import dev.emberline.core.ConfigLoader;
import dev.emberline.core.GameLoop;
import dev.emberline.core.components.Updatable;
import dev.emberline.utility.Coordinate2D;
import dev.emberline.utility.Pair;
import dev.emberline.utility.Vector2D;

import java.io.*;

/**
 * This class is used to Zoom in and out areas of the map.
 * It requires a file with the proper indications to be used.
 */
public class Zoom implements Updatable, Serializable {
    //zoom configuration
    private static final String roadsConfigFilename = "roads.json";
    private static class Translation {
        @JsonProperty("fromX")
        private double fromX;
        @JsonProperty("fromY")
        private double fromY;
        @JsonProperty("toX")
        private double toX;
        @JsonProperty("toY")
        private double toY;
    }
    private static class Translations {
        private Translation first;
        private Translation second;
    }
    private final Translations translations;

    private Pair<Vector2D, Vector2D> curr;
    private Pair<Vector2D, Vector2D> to;
    private Pair<Vector2D, Vector2D> step = new Pair<>(Coordinate2D.ZERO, Coordinate2D.ZERO);
    //make sure this number is small enough to let the "zoom" end before the wave ends
    private final Double stepUpperBound = 0.02 ;
    private final Double timePerStep = 50_000_000d;

    private long acc = 0;

    public Zoom(String wavePath) {
        translations = ConfigLoader.loadConfig(wavePath + "cs.json", Translations.class);
        loadCS(wavePath + "cs.txt");
        computeSteps();
    }

    private void updateCS() {
        Renderer renderer = GameLoop.getInstance().getRenderer();
        CoordinateSystem cs = renderer.getWorldCoordinateSystem();

        cs.setRegion(curr.getX().getX(), curr.getX().getY(), curr.getY().getX(), curr.getY().getY());
    }

    private void computeSteps() {
        double d1 = curr.getX().distance(to.getX());
        double d2 = curr.getY().distance(to.getY());
        double maxd = Math.max(d1, d2);
        double mind = Math.min(d1, d2);

        if (maxd != 0d) {
            double speedLowerBound = (mind / maxd) * stepUpperBound;
            //unit directional vectors to scale propely in pair costructor
            Vector2D directionX = curr.getX().directionTo(to.getX());
            Vector2D directionY = curr.getY().directionTo(to.getY());
            if (d1 > d2) {
                step = new Pair<>(directionX.multiply(stepUpperBound), directionY.multiply(speedLowerBound));
            } else {
                step = new Pair<>(directionX.multiply(speedLowerBound), directionY.multiply(stepUpperBound));
            }
        }
    }

    private void loadCS(String file) {
        Vector2D currFirst = new Coordinate2D(translations.first.fromX, translations.first.fromY);
        Vector2D currSecond = new Coordinate2D(translations.first.toX, translations.first.toY);
        Vector2D toFirst = new Coordinate2D(translations.second.fromX, translations.second.fromY);
        Vector2D toSecond = new Coordinate2D(translations.second.toX, translations.second.toY);

        to = new Pair<>(toFirst, toSecond);
        curr = new Pair<>(currFirst, currSecond);
    }

    //checks if the points are no more than 1 step away from the desired coordinates
    private boolean isOver() {
        return to.getX().distance(curr.getX()) <= step.getX().distance(0, 0)
                && to.getY().distance(curr.getY()) <= step.getY().distance(0, 0);
    }

    @Override
    public void update(long elapsed) {
        acc += elapsed;
        while (acc >= timePerStep) {
            acc -= timePerStep;
            if (!isOver()) {
                curr.setX(curr.getX().add(step.getX()));
                curr.setY(curr.getY().add(step.getY()));
            }
        }
        updateCS();
    }
}