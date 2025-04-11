package dev.emberline.core.nodes;

import dev.emberline.core.game.components.Renderable;
import dev.emberline.core.render.*;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Paint;

import java.util.Objects;

public class OptionsRender implements Renderable {
    private static final int _IMAGE_N = 39;
    private final Image[] _IMAGES = new Image[_IMAGE_N];

    public OptionsRender() {
        for (int i = 1; i <= _IMAGE_N; ++i) {
            _IMAGES[i - 1] = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/debug/" + i + ".png")));
        }
    }

    @Override
    public void render() {
        if (Renderer.getGraphicsContext().isEmpty())
            return;

        GraphicsContext gc = Renderer.getGraphicsContext().get();
        CoordinateSystem worldCs = Renderer.getWorldCs();

        var screen_height = worldCs.getScreenHeight();
        var screen_width = worldCs.getScreenWidth();
        var tolerance = worldCs.getTolerance();
        var k = worldCs.getScalingFactor();
        var region_x1 = worldCs.getRegion_x1();
        var region_x2 = worldCs.getRegion_x2();
        var region_y1 = worldCs.getRegion_y1();
        var region_y2 = worldCs.getRegion_y2();

        Renderer.addRenderTask(new RenderTask(RenderPriority.GUI, () -> {
            gc.setImageSmoothing(false);
            gc.clearRect(0, 0, screen_width, screen_height);

            for (int curr_wx = (int)Math.floor(worldCs.toWorldX(0)); curr_wx < (int)Math.ceil(worldCs.toWorldX(screen_width)); curr_wx++) {
                for (int curr_wy = (int)Math.floor(worldCs.toWorldY(0)); curr_wy < (int)Math.ceil(worldCs.toWorldY(screen_height)); curr_wy++) {
                    double to_x = worldCs.toScreenX(curr_wx);
                    double to_y = worldCs.toScreenY(curr_wy);
                    boolean is_inside = curr_wx < region_x2 && curr_wx >= region_x1 && curr_wy < region_y2 && curr_wy >= region_y1;
                    gc.drawImage(_IMAGES[is_inside ? 22 : 5], to_x, to_y, k, k);
                }
            }

            // DRAW REGION
            double p1_sx = worldCs.toScreenX(region_x1);
            double p1_sy = worldCs.toScreenY(region_y1);
            double p2_sx = worldCs.toScreenX(region_x2);
            double p2_sy = worldCs.toScreenY(region_y1);
            double p3_sx = worldCs.toScreenX(region_x2);
            double p3_sy = worldCs.toScreenY(region_y2);
            double p4_sx = worldCs.toScreenX(region_x1);
            double p4_sy = worldCs.toScreenY(region_y2);

            gc.setStroke(Paint.valueOf("#ff0000"));
            gc.strokeLine(p1_sx,p1_sy, p2_sx,p2_sy);
            gc.strokeLine(p2_sx,p2_sy, p3_sx,p3_sy);
            gc.strokeLine(p3_sx,p3_sy, p4_sx,p4_sy);
            gc.strokeLine(p4_sx,p4_sy, p1_sx,p1_sy);

            gc.setFill(Paint.valueOf("#ff00ff"));
            gc.fillOval(p1_sx - 5, p1_sy - 5, 10, 10);

            // DRAWING BLACK BARS
            gc.setFill(Paint.valueOf("#000"));
            gc.fillRect(p1_sx - screen_width, 0, screen_width - tolerance * k, screen_height);
            gc.fillRect(p2_sx + tolerance * k, 0, screen_width, screen_height);
            gc.fillRect(0, p1_sy - screen_height, screen_width, screen_height - tolerance * k);
            gc.fillRect(0, p4_sy + tolerance * k, screen_width, screen_height);
        }));
    }
}