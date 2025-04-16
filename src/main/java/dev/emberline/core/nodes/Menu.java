package dev.emberline.core.nodes;

import dev.emberline.core.render.RenderContext;
import dev.emberline.core.render.RenderPriority;
import dev.emberline.core.render.RenderTask;
import dev.emberline.core.render.Renderer;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Paint;

import java.io.InputStream;
import java.util.Objects;

import dev.emberline.core.GameLoop;

public class Menu implements NavigationState {

    public Menu() {
        
    }

    public void render() {
        Renderer renderer = GameLoop.getInstance().getRenderer();
        GraphicsContext gc = renderer.getGraphicsContext();

        // GUI CONTEXT //
        RenderContext guiContext = renderer.getGuiContext();
        var guiCS = guiContext.getCS();

        Image logoImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/menu_assets/logo.png")));
        Image playImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/menu_assets/play.png")));
        Image optionsImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/menu_assets/options.png")));

        double scaleFactor = 1.0 / 150.0;

        double logoWidth = logoImage.getWidth() * scaleFactor;
        double logoHeight = logoImage.getHeight() * scaleFactor;

        double centerX = guiCS.getRegionCenterX();
        double centerY = guiCS.getRegionCenterY();

        double imagesX = guiCS.toScreenX(centerX - logoWidth / 2);
        double logoY = guiCS.toScreenY(centerY / 2 - logoHeight / 2);
        double playY = guiCS.toScreenY(centerY - logoHeight / 2);
        double optionsY = guiCS.toScreenY(centerY * 1.4 - logoHeight / 2);

        renderer.addRenderTask(new RenderTask(RenderPriority.GUI, () -> {
            gc.drawImage(logoImage, imagesX, logoY, logoWidth * guiCS.getScale(), logoHeight * guiCS.getScale());
            gc.drawImage(playImage, imagesX, playY, logoWidth * guiCS.getScale(), logoHeight * guiCS.getScale());
            gc.drawImage(optionsImage, imagesX, optionsY, logoWidth * guiCS.getScale(), logoHeight * guiCS.getScale());
        }));

        Image backgroundImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/menu_assets/background.png")));
        
        double backgroundWidth = backgroundImage.getWidth() / 50;
        double backgroundHeight = backgroundImage.getHeight() / 50;

        renderer.addRenderTask(new RenderTask(RenderPriority.BACKGROUND, () -> {
            gc.drawImage(backgroundImage, guiCS.toScreenX(0), guiCS.toScreenY(0), backgroundWidth * guiCS.getScale(), backgroundHeight * guiCS.getScale());
        }));

        // WORLD CONTEXT //
        RenderContext worldContext = renderer.getWorldContext();
        
        double screenHeight = worldContext.getCanvas().getHeight();
        double screenWidth = worldContext.getCanvas().getWidth(); 

        renderer.addRenderTask(new RenderTask(RenderPriority.LETTERBOXING, () -> {
            gc.setFill(Paint.valueOf("#aaa"));
            gc.fillRect(0, 0, screenWidth, screenHeight);
        }));
    }
}

