package dev.emberline.gui.event;

public class GuiEvent {
    private final String sourceId; // ad esempio ID del pulsante
    private final Object payload;  // può essere null o qualsiasi cosa

    public GuiEvent(String sourceId, Object payload) {
        this.sourceId = sourceId;
        this.payload = payload;
    }

    public String getSourceId() { return sourceId; }
    public Object getPayload() { return payload; }
}
