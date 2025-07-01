package dev.emberline.game.serialization;

import dev.emberline.game.world.World;
import net.harawata.appdirs.AppDirs;
import net.harawata.appdirs.AppDirsFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * Utility class that should be used to serialize the world - save the game.
 * It works by calling 2 different methods, one to save, the other to load the game.
 */
public class Serializer {
    private static final AppDirs APP_DIRS = AppDirsFactory.getInstance();
    private static final String SAVE_PATH = APP_DIRS
            .getUserDataDir("Emberline", "1.0", null) + "/";

    /**
     * Getter for the save path.
     *
     * @return the save path.
     */
    public String getSavePath() {
        return SAVE_PATH;
    }

    /**
     * Serializes the World.
     *
     * @param world is the reference to the instance of the World class to serialize.
     * @param filename is the name of the file to save the serialized world to.
     */
    public void serialize(final World world, final String filename) {
        try {
            final File saveDir = new File(SAVE_PATH);
            if (!saveDir.exists()) {
                saveDir.mkdirs();
            }

            final FileOutputStream file = new FileOutputStream(SAVE_PATH + filename);
            final ObjectOutputStream out = new ObjectOutputStream(file);

            out.writeObject(world);

            out.close();
            file.close();
        } catch (IOException ex) {
            throw new GameSavesSavingException(ex);
        }
    }

    /**
     * Deserializes the specified World saving and returns a reference to it.
     *
     * @param save is the name of the file to load the serialized world from.
     * @return the reference to the new unserialized world.
     */
    public World getDeserializedWorld(final String save) {
        final World world;
        try {
            final FileInputStream file = new FileInputStream(SAVE_PATH + save);
            final ObjectInputStream in = new ObjectInputStream(file);

            world = (World) in.readObject();

            in.close();
            file.close();
        } catch (IOException | ClassNotFoundException ex) {
            throw new GameSavesLoadingException(ex);
        }
        return world;
    }
}
