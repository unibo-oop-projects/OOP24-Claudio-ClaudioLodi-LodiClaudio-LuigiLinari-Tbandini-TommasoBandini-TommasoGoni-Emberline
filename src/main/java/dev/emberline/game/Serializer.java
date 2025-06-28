package dev.emberline.game;

import dev.emberline.core.components.Inputable;
import dev.emberline.core.components.Renderable;
import dev.emberline.core.components.Updatable;
import dev.emberline.game.world.World;
import javafx.scene.input.InputEvent;

import java.io.*;

public class Serializer implements Updatable, Renderable, Inputable {

    private final String fileName = "./save";
    private long acc = 0;
    private boolean isSerialized;
    World world;

    public Serializer(World world) {
        this.world = world;
    }

    public void serialize() {
        try {
            FileOutputStream file = new FileOutputStream(fileName);
            ObjectOutputStream out = new ObjectOutputStream(file);

            out.writeObject(world);
            out.close();
            file.close();
        } catch (IOException ex) {
            System.out.println("serialization issue from: " + ex.getMessage());
            for (var x : ex.getStackTrace()) {
                System.out.println(x);
            }
        }
    }

    public void deserialize() {
        try {
            FileInputStream file = new FileInputStream(fileName);
            ObjectInputStream in = new ObjectInputStream(file);

            world = (World) in.readObject();
            in.close();
            file.close();
        } catch (IOException | ClassNotFoundException ex) {
            System.out.println("deserialization issue from: " + ex.getMessage());
            for (var x : ex.getStackTrace()) {
                System.out.println(x);
            }
        }
    }

    @Override
    public void update(long elapsed) {
        long nsToSeconds = 1_000_000_000;
        acc += elapsed;
        long tmp = acc / nsToSeconds;
        if ((tmp >= 20 && tmp <= 30 )) {
            if (tmp >= 25 && isSerialized) {
                deserialize();
                isSerialized = false;
            } else if (tmp < 21 && !isSerialized) {
                isSerialized = true;
                serialize();
            }
        } else {
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
    public void processInput(InputEvent inputEvent) {
        if (!isSerialized) {
            world.processInput(inputEvent);
        }
    }
}
