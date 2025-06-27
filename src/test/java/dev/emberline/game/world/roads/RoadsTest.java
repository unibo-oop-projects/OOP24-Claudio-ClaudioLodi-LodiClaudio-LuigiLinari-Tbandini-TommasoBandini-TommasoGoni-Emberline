package dev.emberline.game.world.roads;

import dev.emberline.utility.Coordinate2D;
import dev.emberline.utility.Vector2D;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Optional;

class RoadsTest {

    private static final String ROADS_PATH = "/roads/";
    private final Roads roads = new Roads(ROADS_PATH);

    @Test
    void testGetNextNodeDifferentWeights() {
        final Vector2D zeroZero = new Coordinate2D(0.5, 0.5);
        final Vector2D oneZero = new Coordinate2D(1.5, 0.5);
        final Vector2D zeroOne = new Coordinate2D(0.5, 1.5);
        Optional<Vector2D> next;

        next = roads.getNextNode(zeroZero);
        Assertions.assertTrue(next.isPresent());
        Assertions.assertEquals(next.get(), oneZero);

        next = roads.getNextNode(zeroZero);
        Assertions.assertTrue(next.isPresent());
        Assertions.assertEquals(next.get(), zeroOne);

        next = roads.getNextNode(zeroZero);
        Assertions.assertTrue(next.isPresent());
        Assertions.assertEquals(next.get(), zeroOne);

        next = roads.getNextNode(zeroZero);
        Assertions.assertTrue(next.isPresent());
        Assertions.assertEquals(next.get(), oneZero);
    }

    @Test
    void testGetNextNodeWithZeroWeight() {
        final Vector2D oneOne = new Coordinate2D(1.5, 1.5);
        final Vector2D twoOne = new Coordinate2D(2.5, 1.5);
        Optional<Vector2D> next;

        next = roads.getNextNode(oneOne);
        Assertions.assertTrue(next.isPresent());
        Assertions.assertEquals(next.get(), twoOne);

        next = roads.getNextNode(oneOne);
        Assertions.assertTrue(next.isPresent());
        Assertions.assertEquals(next.get(), twoOne);
    }

    @Test
    void testGetNextLastNodeGivesEmpty() {
        final Vector2D oneZero = new Coordinate2D(1.5, 0.5);
        final Vector2D zeroOne = new Coordinate2D(0.5, 1.5);
        final Vector2D twoOne = new Coordinate2D(2.5, 1.5);
        final Vector2D oneTwo = new Coordinate2D(1.5, 2.5);

        Assertions.assertFalse(roads.getNextNode(oneZero).isPresent());
        Assertions.assertFalse(roads.getNextNode(zeroOne).isPresent());
        Assertions.assertFalse(roads.getNextNode(twoOne).isPresent());
        Assertions.assertFalse(roads.getNextNode(oneTwo).isPresent());
    }
}
