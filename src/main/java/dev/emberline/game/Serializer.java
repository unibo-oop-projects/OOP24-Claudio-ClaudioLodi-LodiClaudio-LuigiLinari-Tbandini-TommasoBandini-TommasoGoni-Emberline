package dev.emberline.game;

import dev.emberline.game.world.World;

import java.io.*;

/**
 * Utility class that should be used to serialize the world - save the game.
 * It works by calling 2 different methods, one to save, the other to load the game.
 */
public class Serializer {

    private final String fileName = "./save";

    /**
     * Serializes the World.
     *
     * @param world is the reference to the instance of the World class to serialize.
     */
    public void serialize(World world) {
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

    /**
     * Deserializes the specified World saving and returns a reference to it.
     *
     * @return the reference to the new unserialized world.
     */
    public World getDeserializedWorld() {
        World world = null;
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
        return world;
    }
}
