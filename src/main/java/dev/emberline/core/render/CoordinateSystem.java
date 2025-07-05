package dev.emberline.core.render;

/**
 * The {@code CoordinateSystem} class manages the transformation between
 * screen coordinates and world coordinates. It is used to map positions
 * and sizes from one coordinate system to the other.
 * <p>
 * The coordinate system is defined by a rectangular region in the world space,
 * which is scaled and fitted to the screen. This class ensures that:
 * <ul>
 *   <li>Every point within the defined world region is bijectively mapped to a visible point on the screen.</li>
 *   <li>The aspect ratio of the world region is always preserved.</li>
 *   <li>At least one of the two dimensions (width or height) of the world region will always be fully visible on the screen.</li>
 * </ul>
 * <p>
 * The transformation applied is an affine linear transformation in 2D.
 * <p>
 * This class is thread-safe.
 */
public final class CoordinateSystem {
    // Screen coordinates:  [SCREEN]
    // World coordinates:   [WORLD]

    // World region bounded by two points (TL & BR) [WORLD]
    private double regionX1;
    private double regionY1;
    private double regionX2;
    private double regionY2;

    // Scaling factor
    private double scale;

    // Screen origin (TL)   [WORLD]
    private double screenOriginX;
    private double screenOriginY;

    // Package private; should only be constructed by Renderer
    CoordinateSystem(final double regionX1, final double regionY1, final double regionX2, final double regionY2) {
        setRegion(regionX1, regionY1, regionX2, regionY2);
    }

    /**
     * Sets the region boundaries for the coordinate system.
     * This method updates the coordinates of the region's corners.
     *
     * @param regionX1 the x-coordinate of the top left corner.
     * @param regionY1 the y-coordinate of the top left corner.
     * @param regionX2 the x-coordinate of the bottom right corner.
     * @param regionY2 the y-coordinate of the bottom right corner.
     */
    public synchronized void setRegion(final double regionX1, final double regionY1,
                                             final double regionX2, final double regionY2) {
        this.regionX1 = regionX1;
        this.regionY1 = regionY1;
        this.regionX2 = regionX2;
        this.regionY2 = regionY2;
    }

    // Package private method; should only be called by the renderer
    synchronized void update(final double screenWidth, final double screenHeight) {
        final double regionCenterX = getRegionCenterX();
        final double regionCenterY = getRegionCenterY();

        scale = Math.min(screenWidth / (regionX2 - regionX1), screenHeight / (regionY2 - regionY1));
        final double screenCenterX = screenWidth / 2;
        final double screenCenterY = screenHeight / 2;

        screenOriginX = regionCenterX - screenCenterX / scale;
        screenOriginY = regionCenterY - screenCenterY / scale;
    }

    /**
     * Computes and returns the x-coordinate of the center of the region.
     *
     * @return the x-coordinate of the center of the defined region as a double.
     */
    public synchronized double getRegionCenterX() {
        return (regionX1 + regionX2) / 2;
    }

    /**
     * Computes and returns the y-coordinate of the center of the region.
     *
     * @return the y-coordinate of the center of the defined region as a double.
     */
    public synchronized double getRegionCenterY() {
        return (regionY1 + regionY2) / 2;
    }

    /**
     * Converts an x-coordinate from screen space to world space.
     *
     * @param screenX the x-coordinate in screen space to be converted.
     * @return the corresponding x-coordinate in world space as a double.
     */
    public synchronized double toWorldX(final double screenX) {
        return screenOriginX + screenX / scale;
    }

    /**
     * Converts a y-coordinate from screen space to world space.
     *
     * @param screenY the y-coordinate in screen space to be converted.
     * @return the corresponding y-coordinate in world space as a double.
     */
    public synchronized double toWorldY(final double screenY) {
        return screenOriginY + screenY / scale;
    }

    /**
     * Converts an x-coordinate from world space to screen space.
     *
     * @param worldX the x-coordinate in world space to be converted.
     * @return the corresponding x-coordinate in screen space as a double.
     */
    public synchronized double toScreenX(final double worldX) {
        return (worldX - screenOriginX) * scale;
    }

    /**
     * Converts a y-coordinate from world space to screen space.
     *
     * @param worldY the y-coordinate in world space to be converted.
     * @return the corresponding y-coordinate in screen space as a double.
     */
    public synchronized double toScreenY(final double worldY) {
        return (worldY - screenOriginY) * scale;
    }

    /**
     * Retrieves the current scale factor from world coordinates to screen coordinates.
     * <p>
     * This scale factor is used to convert lengths and distances from world space to screen space.
     *
     * @return the scale factor as a double.
     */
    public synchronized double getScale() {
        return scale;
    }
}
