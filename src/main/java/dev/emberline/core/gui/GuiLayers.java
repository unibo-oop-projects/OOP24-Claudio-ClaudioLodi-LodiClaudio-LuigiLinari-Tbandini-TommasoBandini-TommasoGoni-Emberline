package dev.emberline.core.gui;

public enum GuiLayers {
    MENU(GuiBuilder.buildMenu()),
    SETTINGS(GuiBuilder.buildSettings());

    private final GuiLayer layer;

    GuiLayers(GuiLayer layer) {
        this.layer = layer;
    }

    public GuiLayer get() {
        return layer;
    }
}
