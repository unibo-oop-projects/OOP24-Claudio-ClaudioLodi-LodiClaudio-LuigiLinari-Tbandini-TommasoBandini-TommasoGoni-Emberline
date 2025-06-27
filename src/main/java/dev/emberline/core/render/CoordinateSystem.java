package dev.emberline.core.render;

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

    public final synchronized void setRegion(final double regionX1, final double regionY1,
                                             final double regionX2, final double regionY2) {
        this.regionX1 = regionX1;
        this.regionY1 = regionY1;
        this.regionX2 = regionX2;
        this.regionY2 = regionY2;
    }

    // Package private method; should only be called by the renderer
    void update(final double screenWidth, final double screenHeight) {
        final double regionCenterX = getRegionCenterX();
        final double regionCenterY = getRegionCenterY();

        scale = Math.min(screenWidth / (regionX2 - regionX1), screenHeight / (regionY2 - regionY1));
        final double screenCenterX = screenWidth / 2;
        final double screenCenterY = screenHeight / 2;

        screenOriginX = regionCenterX - screenCenterX / scale;
        screenOriginY = regionCenterY - screenCenterY / scale;
    }

    public synchronized double getRegionCenterX() {
        return (regionX1 + regionX2) / 2;
    }

    public synchronized double getRegionCenterY() {
        return (regionY1 + regionY2) / 2;
    }

    public synchronized double getScreenOriginX() {
        return screenOriginX;
    }

    public synchronized double getScreenOriginY() {
        return screenOriginY;
    }

    public synchronized double getPlayableScreenWidth() {
        return (regionX2 - regionX1) * scale;
    }

    public synchronized double getPlayableScreenHeight() {
        return (regionY2 - regionY1) * scale;
    }

    public synchronized double toWorldX(final double screenX) {
        return screenOriginX + screenX / scale;
    }

    public synchronized double toWorldY(final double screenY) {
        return screenOriginY + screenY / scale;
    }

    public synchronized double toScreenX(final double worldX) {
        return (worldX - screenOriginX) * scale;
    }

    public synchronized double toScreenY(final double worldY) {
        return (worldY - screenOriginY) * scale;
    }

    public synchronized double getScale() {
        return scale;
    }
}
