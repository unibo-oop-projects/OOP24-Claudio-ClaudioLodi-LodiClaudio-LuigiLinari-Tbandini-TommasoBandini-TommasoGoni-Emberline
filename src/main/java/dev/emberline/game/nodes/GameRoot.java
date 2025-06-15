package dev.emberline.game.nodes;

import dev.emberline.core.components.Inputable;
import dev.emberline.core.components.Renderable;
import dev.emberline.core.components.Updatable;
import dev.emberline.gui.event.GuiEvent;
import dev.emberline.gui.event.GuiEventListener;
import dev.emberline.game.world.World;
import javafx.scene.input.InputEvent;

import java.io.*;

public class GameRoot implements Inputable, Updatable, Renderable, GuiEventListener {
    // Navigation States
    private final World world = new World();
    private boolean isSerialized = false;

    private long acc = 0;

    public GameRoot() {
    }

    private void serialize() throws Exception {
        try (
            final OutputStream file = new FileOutputStream("pippo");
            final OutputStream bstream = new BufferedOutputStream(file);
            final ObjectOutputStream ostream = new ObjectOutputStream(bstream);
        ) {
            ostream.writeObject(world);
            isSerialized = true;
        }
    }

    private void deSerialize() throws Exception {
        try (
            final InputStream file = new FileInputStream("pippo");
            final InputStream bstream = new BufferedInputStream(file);
            final ObjectInputStream ostream = new ObjectInputStream(bstream);
        ) {
            ostream.readObject();
            isSerialized = false;
        }
    }

    @Override
    public void processInput(InputEvent inputEvent) {}

    @Override
    public void update(long elapsed) {
        if (acc > 10_000_000_000L && isSerialized) {
            try {
                //deSerialize();
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }else if (acc > 5_000_000_000L && !isSerialized) {
            try {
                //serialize();
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
        acc += elapsed;
        if (!isSerialized) {
            world.update(elapsed);
        }
    }

    @Override
    public void render() {
        if (!isSerialized) {
            world.render();
        }
    }

    @Override
    public void onGuiEvent(GuiEvent event) {}
}
