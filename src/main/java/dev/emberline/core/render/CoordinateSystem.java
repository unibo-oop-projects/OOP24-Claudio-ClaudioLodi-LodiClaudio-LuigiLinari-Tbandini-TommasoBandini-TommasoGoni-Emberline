package dev.emberline.core.render;

import java.util.function.DoubleFunction;

import javafx.scene.canvas.Canvas;

public class CoordinateSystem {
    // Screen coordinates:  [SCREEN]
    // World coordinates:   [WORLD]

    // World region bounded by two points (TL & BR) [WORLD] // TODO: possible dynamics region setter to abilitate zoom/pan
    private final double region_x1 = 0;
    private final double region_y1 = 0;
    private final double region_x2 = 25;
    private final double region_y2 = 15;

    private final double tolerance = 10;

    // Screen Dimensions [SCREEN]
    private double screen_width;
    private double screen_height;
    
    // World region center  [WORLD]
    private final double reg_cx = region_x1 + (region_x2 - region_x1) / 2;
    private final double reg_cy = region_y1 + (region_y2 - region_y1) / 2;
    
    // Screen center        [SCREEN]
    private double scx;
    private double scy;

    // Scaling factor
    private double k;

    // Screen origin (TL)   [WORLD]
    private double sox;
    private double soy;

    // Lambdas to convert between [SCREEN] and [WORLD]
    private DoubleFunction<Double> scr_to_wrd_x;
    private DoubleFunction<Double> scr_to_wrd_y;
    private DoubleFunction<Double> wrd_to_scr_x;
    private DoubleFunction<Double> wrd_to_scr_y;

    private final Canvas canvas;

    public CoordinateSystem(Canvas canvas){
        this.canvas = canvas;
        update();
    }

    public void update() {
        this.screen_width = canvas.getWidth();
        this.screen_height = canvas.getHeight();

        this.scx = screen_width / 2;
        this.scy = screen_height / 2;

        this.k = Math.min(screen_width / (region_x2 - region_x1), screen_height / (region_y2 - region_y1));

        this.sox = reg_cx - scx / k;
        this.soy = reg_cy - scy / k;

        this.scr_to_wrd_x = (double scr_x) -> sox + scr_x / k;
        this.scr_to_wrd_y = (double scr_y) -> soy + scr_y / k;
        this.wrd_to_scr_x = (double wrd_x) -> (wrd_x - sox) * k;
        this.wrd_to_scr_y = (double wrd_y) -> (wrd_y - soy) * k;
    }

    public double toWorldX(double screenX) {
        return scr_to_wrd_x.apply(screenX);
    }
    
    public double toWorldY(double screenY) {
        return scr_to_wrd_y.apply(screenY);
    }
    
    public double toScreenX(double worldX) {
        return wrd_to_scr_x.apply(worldX);
    }
    
    public double toScreenY(double worldY) {
        return wrd_to_scr_y.apply(worldY);
    }

    // Getters
    public double getWorldWidth() {
        return region_x2 - region_x1;
    }

    public double getWorldHeight() {
        return region_y2 - region_y1;
    }

    public double getScreenWidth() {
        return screen_width;
    }

    public double getScreenHeight() {
        return screen_height;
    }   

    public double getTolerance() {
        return tolerance;
    }

    public double getScalingFactor() {
        return k;
    }

    public double getRegion_x1() {
        return region_x1;
    }

    public double getRegion_y1() {
        return region_y1;
    }

    public double getRegion_x2() {
        return region_x2;
    }

    public double getRegion_y2() {
        return region_y2;
    }
}
