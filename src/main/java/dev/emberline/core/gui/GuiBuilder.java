package dev.emberline.core.gui;

import dev.emberline.core.input.InputResult;
import javafx.geometry.Point2D;

import java.util.List;

class GuiBuilder {
    static GuiLayer buildMenu() {
        GuiButton play = new GuiButton("Play", new Point2D(0,0), new Point2D(6,6), InputResult.GOTO_WORLD);
        GuiButton settings = new GuiButton("Settings", new Point2D(6,6), new Point2D(26,8), InputResult.GOTO_SETTINGS);
        GuiButton exit = new GuiButton("Exit", new Point2D(6,10), new Point2D(26,12), InputResult.EXIT_GAME);

        return new GuiLayer(List.of(play, settings, exit), null);
    }

    static GuiLayer buildSettings() {
        GuiButton back = new GuiButton("Back", new Point2D(2,2), new Point2D(6,3), InputResult.PREVIOUS_GUI);

        return new GuiLayer(List.of(back), null);
    }

    // TODO public static GuiLayer buildTowerDialog(position) ...

    // TODO public static GuiLayer buildGameSaves(gamesaves) ...
}
