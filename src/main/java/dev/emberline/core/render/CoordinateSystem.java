package dev.emberline.core.render;

public class CoordinateSystem {
    // Screen coordinates:  [SCREEN]
    // World coordinates:   [WORLD]

    // World region bounded by two points (TL & BR) [WORLD]
    private double regionX1;
    private double regionY1;
    private double regionX2;
    private double regionY2;

    private final double tolerance = 10;

    // Screen Dimensions [SCREEN]
    private double screenWidth;
    private double screenHeight;
    
    // Scaling factor
    private double scale;

    // Screen origin (TL)   [WORLD]
    private double screenOriginX;
    private double screenOriginY;

    public CoordinateSystem(double regionX1, double regionY1, double regionX2, double regionY2) {
        setRegion(regionX1, regionY1, regionX2, regionY2);
    }

    public void setRegion(double regionX1, double regionY1, double regionX2, double regionY2) {
        this.regionX1 = regionX1;
        this.regionY1 = regionY1;
        this.regionX2 = regionX2;
        this.regionY2 = regionY2;
    }

    public void update(double screenWidth, double screenHeight) {
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;

        double regionCenterX = getRegionCenterX();
        double regionCenterY = getRegionCenterY();

        scale = Math.min(screenWidth / (regionX2 - regionX1), screenHeight / (regionY2 - regionY1));
        double screenCenterX = screenWidth / 2;
        double screenCenterY = screenHeight / 2;

        screenOriginX = regionCenterX - screenCenterX / scale;
        screenOriginY = regionCenterY - screenCenterY / scale;
    }

    public double getRegionCenterX() {
        return (regionX1 + regionX2) / 2;
    }
    
    public double getRegionCenterY() {
        return (regionY1 + regionY2) / 2;
    }

    public double getScreenOriginX() {
        return screenOriginX;
    }

    public double getScreenOriginY() {
        return screenOriginY;
    }

    public double getPlayableScreenWidth() {
        return (regionX2 - regionX1) * scale;
    }
    
    public double getPlayableScreenHeight() {
        return (regionY2 - regionY1) * scale;
    }

    public double toWorldX(double screenX) {
        return screenOriginX + screenX / scale;
    }

    public double toWorldY(double screenY) {
        return screenOriginY + screenY / scale;
    }

    public double toScreenX(double worldX) {
        return (worldX - screenOriginX) * scale;
    }

    public double toScreenY(double worldY) {
        return (worldY - screenOriginY) * scale;
    }

    public double getTolerance() {
        return tolerance;
    }

    public double getScale() {
        return scale;
    }
}
