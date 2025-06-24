package dev.emberline.core.input;

import javafx.geometry.Point2D;

/**
 * The MouseLocation class provides a utility to track the state and position of the mouse pointer.
 * It stores whether the mouse is currently inside the window and captures its current coordinates.
 */
public class MouseLocation {
    private static boolean inside = true;
    private static Point2D location = new Point2D(0d, 0d);

    // Package private method
    static void setIsMouseInside(final boolean mouseInside) {
        MouseLocation.inside = mouseInside;
    }

    // Package private method
    static void setLocation(final Point2D location) {
        MouseLocation.location = location;
    }

    /**
     * Indicates whether the mouse pointer is currently inside the window.
     *
     * @return true if the mouse is inside, false otherwise.
     */
    public static boolean isInside() {
        return MouseLocation.inside;
    }

    /**
     * Retrieves the current location of the mouse as a Point2D object.
     *
     * @return the current mouse location stored as a Point2D object
     */
    public static Point2D getLocation() {
        return MouseLocation.location;
    }

    /**
     * Retrieves the x-coordinate of the current mouse position.
     *
     * @return the x-coordinate of the mouse location as a double
     */
    public static double getX() {
        return MouseLocation.location.getX();
    }

    /**
     * Retrieves the y-coordinate of the current mouse location.
     *
     * @return the y-coordinate of the mouse position as a double.
     */
    public static double getY() {
        return MouseLocation.location.getY();
    }
}
