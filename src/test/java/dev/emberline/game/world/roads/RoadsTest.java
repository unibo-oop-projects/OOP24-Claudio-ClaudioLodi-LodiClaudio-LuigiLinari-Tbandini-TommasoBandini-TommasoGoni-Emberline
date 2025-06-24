package dev.emberline.game.world.roads;

import dev.emberline.utility.Coordinate2D;
import dev.emberline.utility.Vector2D;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Optional;

class RoadsTest {

    private final String roadsPath = "/roads/";
    private final Roads roads = new Roads(roadsPath);

    @Test
    void testGetNextNodeDifferentWeights() {
        Vector2D zero_zero = new Coordinate2D(0.5, 0.5);
        Vector2D one_zero = new Coordinate2D(1.5, 0.5);
        Vector2D zero_one = new Coordinate2D(0.5, 1.5);
        Optional<Vector2D> next;

        next = roads.getNextNode(zero_zero);
        Assertions.assertTrue(next.isPresent());
        Assertions.assertEquals(next.get(), one_zero);

        next = roads.getNextNode(zero_zero);
        Assertions.assertTrue(next.isPresent());
        Assertions.assertEquals(next.get(), zero_one);

        next = roads.getNextNode(zero_zero);
        Assertions.assertTrue(next.isPresent());
        Assertions.assertEquals(next.get(), zero_one);

        next = roads.getNextNode(zero_zero);
        Assertions.assertTrue(next.isPresent());
        Assertions.assertEquals(next.get(), one_zero);
    }

    @Test
    void testGetNextNodeWithZeroWeight() {
        Vector2D one_one = new Coordinate2D(1.5, 1.5);
        Vector2D two_one = new Coordinate2D(2.5, 1.5);
        Vector2D one_two = new Coordinate2D(1.5, 2.5);
        Optional<Vector2D> next;

        next = roads.getNextNode(one_one);
        Assertions.assertTrue(next.isPresent());
        Assertions.assertEquals(next.get(), two_one);

        next = roads.getNextNode(one_one);
        Assertions.assertTrue(next.isPresent());
        Assertions.assertEquals(next.get(), two_one);
    }

    @Test
    void testGetNextLastNodeGivesEmpty() {
        Vector2D one_zero = new Coordinate2D(1.5, 0.5);
        Vector2D zero_one = new Coordinate2D(0.5, 1.5);
        Vector2D two_one = new Coordinate2D(2.5, 1.5);
        Vector2D one_two = new Coordinate2D(1.5, 2.5);

        Assertions.assertFalse(roads.getNextNode(one_zero).isPresent());
        Assertions.assertFalse(roads.getNextNode(zero_one).isPresent());
        Assertions.assertFalse(roads.getNextNode(two_one).isPresent());
        Assertions.assertFalse(roads.getNextNode(one_two).isPresent());
    }
}