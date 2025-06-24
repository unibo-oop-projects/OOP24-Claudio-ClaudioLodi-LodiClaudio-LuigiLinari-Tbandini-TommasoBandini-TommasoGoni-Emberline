package dev.emberline.gui.menu;

import com.fasterxml.jackson.annotation.JsonProperty;

import dev.emberline.core.ConfigLoader;
import dev.emberline.core.GameLoop;
import dev.emberline.core.components.Inputable;
import dev.emberline.core.components.Renderable;
import dev.emberline.core.graphics.SpriteLoader;
import dev.emberline.core.graphics.spritekeys.SingleSpriteKey;
import dev.emberline.core.render.CoordinateSystem;
import dev.emberline.core.render.RenderPriority;
import dev.emberline.core.render.RenderTask;
import dev.emberline.core.render.Renderer;
import dev.emberline.game.GameState;
import dev.emberline.gui.GuiButton;
import dev.emberline.gui.GuiLayer;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

public class MainMenu extends GuiLayer implements GameState {
    // menu bounds
    private record Coordinate(
            @JsonProperty int x,
            @JsonProperty int y
    ) {}
    private record MenuBounds(
            @JsonProperty Coordinate topLeftBound,
            @JsonProperty Coordinate bottomRightBound
    ) {}

    public MainMenu() {
        this(ConfigLoader.loadConfig("/gui/menu/menuBounds.json", MenuBounds.class));
    }

    private MainMenu(MenuBounds menuBounds) {
        super(menuBounds.topLeftBound.x, menuBounds.topLeftBound.y,
            menuBounds.bottomRightBound.x - menuBounds.topLeftBound.x,
            menuBounds.bottomRightBound.y - menuBounds.topLeftBound.y);
        this.menuBounds = menuBounds;
    }

    private final MenuBounds menuBounds;

    // Start button
    void addStartButton() {
        GuiButton startButton = new GuiButton(x, y, width, height, SpriteLoader.loadSprite(SingleSpriteKey.START_BUTTON).image());
        startButton.setOnClick(() -> { });
    }

    @Override
    public void render() {
        Renderer renderer = GameLoop.getInstance().getRenderer();
        GraphicsContext gc = renderer.getGraphicsContext();
        CoordinateSystem cs = renderer.getGuiCoordinateSystem();

        double menuScreenWidth = menuBounds.bottomRightBound.x * cs.getScale();
        double menuScreenHeight = menuBounds.bottomRightBound.y * cs.getScale();
        double menuScreenX = cs.toScreenX(menuBounds.topLeftBound.x);
        double menuScreenY = cs.toScreenY(menuBounds.topLeftBound.y);

        Image menuBackGound = SpriteLoader.loadSprite(SingleSpriteKey.MENU_BACKGROUND).image();

        renderer.addRenderTask(new RenderTask(RenderPriority.BACKGROUND, () -> {
            gc.drawImage(menuBackGound, menuScreenX, menuScreenY, menuScreenWidth, menuScreenHeight);
        }));

    }

    @Override
    public void update(long elapsed) {
    }
}
