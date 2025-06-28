package dev.emberline.game;

import dev.emberline.core.components.Renderable;
import dev.emberline.core.components.Updatable;
import dev.emberline.game.world.World;

import java.io.*;

public class Serializer implements Updatable, Renderable {

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
}
