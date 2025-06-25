package dev.emberline.game.world.entities.enemies.enemy.concrete;

import com.fasterxml.jackson.databind.JsonNode;
import dev.emberline.core.ConfigLoader;
import dev.emberline.game.world.World;
import dev.emberline.game.world.entities.enemies.enemy.AbstractEnemy;
import dev.emberline.game.world.entities.enemies.enemy.EnemyType;
import dev.emberline.utility.Vector2D;

/**
 * Represents an Ogre, a specific type of enemy in the game.
 * This class extends the {@link AbstractEnemy} class to provide
 * the implementations unique to an Ogre enemy.
 * The Ogre class defines its metadata properties by using
 * a JSON configuration file specified by {@link #ASSET_PATH}.
 * <p>
 * An Ogre should represent a tank, that means that it should have more health
 * than a regular enemy and be much slower.
 */
public class Ogre extends AbstractEnemy {
    private static final Metadata METADATA;
    private static final String ASSET_PATH = "/sprites/enemyAssets/ogre.json";

    static {
        final JsonNode metadataNode = ConfigLoader.loadNode(ASSET_PATH).get("metadata");
        METADATA = ConfigLoader.loadConfig(metadataNode, Metadata.class);
    }

    /**
     * Constructs an Ogre enemy at the specified spawn point within the given world.
     *
     * @param spawnPoint the location where the ogre should be instantiated
     * @param world      the game world in which the ogre exists
     * @see AbstractEnemy#AbstractEnemy(Vector2D, World)
     */
    public Ogre(final Vector2D spawnPoint, final World world) {
        super(spawnPoint, world);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Metadata getMetadata() {
        return METADATA;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected EnemyType getEnemyType() {
        return EnemyType.OGRE;
    }
}
