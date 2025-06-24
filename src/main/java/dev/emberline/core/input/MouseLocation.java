package dev.emberline.core.input;

import javafx.geometry.Point2D;

/**
 * The MouseLocation class provides a utility to track the state and position of the mouse pointer.
 * It stores whether the mouse is currently inside the window and captures its current coordinates.
 */
public class MouseLocation {
    private static boolean mouseInside = true;
    private static Point2D mouseLocation = new Point2D(0d, 0d);

    // Package private method
    static void setIsMouseInside(final boolean mouseInside) {
        MouseLocation.mouseInside = mouseInside;
    }

    // Package private method
    static void setMouseLocation(final Point2D mouseLocation) {
        MouseLocation.mouseLocation = mouseLocation;
    }

    /**
     * Indicates whether the mouse pointer is currently inside the window.
     *
     * @return true if the mouse is inside, false otherwise.
     */
    public static boolean isMouseInside() {
        return MouseLocation.mouseInside;
    }

    /**
     * Retrieves the current location of the mouse as a Point2D object.
     *
     * @return the current mouse location stored as a Point2D object
     */
    public static Point2D getLocation() {
        return MouseLocation.mouseLocation;
    }

    /**
     * Retrieves the x-coordinate of the current mouse position.
     *
     * @return the x-coordinate of the mouse location as a double
     */
    public static double getX() {
        return MouseLocation.mouseLocation.getX();
    }

    /**
     * Retrieves the y-coordinate of the current mouse location.
     *
     * @return the y-coordinate of the mouse position as a double.
     */
    public static double getY() {
        return MouseLocation.mouseLocation.getY();
    }
}
