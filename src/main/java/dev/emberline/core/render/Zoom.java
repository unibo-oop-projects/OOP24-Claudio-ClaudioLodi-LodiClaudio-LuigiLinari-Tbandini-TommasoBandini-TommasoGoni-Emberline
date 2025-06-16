package dev.emberline.core.render;

import dev.emberline.core.GameLoop;
import dev.emberline.core.components.Updatable;
import dev.emberline.utility.Coordinate2D;
import dev.emberline.utility.Pair;
import dev.emberline.utility.Vector2D;

import java.io.*;
import java.net.URL;
import java.util.*;

public class Zoom implements Updatable, Serializable {

    private Pair<Vector2D, Vector2D> curr;
    private Pair<Vector2D, Vector2D> to;
    private Pair<Vector2D, Vector2D> step = new Pair<>(Coordinate2D.ZERO, Coordinate2D.ZERO);
    //make sure this number is small enough to let the "zoom" end before the wave ends
    private final Double speedUpperBound = 0.02;

    public Zoom(String wavePath) {
        loadCS(wavePath + "cs.txt");
        computeSteps();
    }

    public void updateCS() {
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
            double speedLowerBound = (mind / maxd) * speedUpperBound;
            //unit directional vectors to scale propely in pair costructor
            Vector2D directionX = curr.getX().directionTo(to.getX());
            Vector2D directionY = curr.getY().directionTo(to.getY());
            if (d1 > d2) {
                step = new Pair<>(directionX.multiply(speedUpperBound), directionY.multiply(speedLowerBound));
            } else {
                step = new Pair<>(directionX.multiply(speedLowerBound), directionY.multiply(speedUpperBound));
            }
        }
    }

    private void loadCS(String file) {
        Vector2D currFirst = Coordinate2D.ZERO;
        Vector2D currSecond = Coordinate2D.ZERO;
        Vector2D toFirst = Coordinate2D.ZERO;
        Vector2D toSecond = Coordinate2D.ZERO;
        try {
            URL fileURL = Objects.requireNonNull(getClass().getResource(file));
            final BufferedReader r = new BufferedReader(new FileReader(fileURL.getPath()));
            String line;
            if ((line = r.readLine()) != null) {
                String[] numbers = line.split(" ");

                currFirst = new Coordinate2D(Double.parseDouble(numbers[0]), Double.parseDouble(numbers[1]));
                toFirst = new Coordinate2D(Double.parseDouble(numbers[2]), Double.parseDouble(numbers[3]));
            }
            if ((line = r.readLine()) != null) {
                String[] numbers = line.split(" ");

                currSecond = new Coordinate2D(Double.parseDouble(numbers[0]), Double.parseDouble(numbers[1]));
                toSecond = new Coordinate2D(Double.parseDouble(numbers[2]), Double.parseDouble(numbers[3]));
            }
            to = new Pair<>(toFirst, toSecond);
            curr = new Pair<>(currFirst, currSecond);

            r.close();
        } catch (IOException e) {
            System.out.println("error loading file: " + file);
        }
    }

    //checks if the points are no more than 1 step away from the desired coordinates
    private boolean isOver() {
        return to.getX().distance(curr.getX()) <= step.getX().distance(0, 0)
                && to.getY().distance(curr.getY()) <= step.getY().distance(0, 0);
    }

    @Override
    public void update(long elapsed) {
        if (!isOver()) {
            curr.setX(curr.getX().add(step.getX()));
            curr.setY(curr.getY().add(step.getY()));
        }
    }
}