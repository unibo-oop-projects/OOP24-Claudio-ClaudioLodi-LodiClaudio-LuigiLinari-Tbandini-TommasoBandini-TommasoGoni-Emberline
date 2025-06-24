package dev.emberline.game.world.entities.enemies.enemy.concrete;

import com.fasterxml.jackson.databind.JsonNode;
import dev.emberline.core.ConfigLoader;
import dev.emberline.game.world.World;
import dev.emberline.game.world.entities.enemies.enemy.AbstractEnemy;
import dev.emberline.game.world.entities.enemies.enemy.EnemyType;
import dev.emberline.utility.Vector2D;

public class Pig extends AbstractEnemy {
    private static Metadata metadata;
    private static final String ASSET_PATH = "/sprites/enemyAssets/pig.json";

    static {
        JsonNode metadataNode = ConfigLoader.loadNode(ASSET_PATH).get("metadata");
        metadata = ConfigLoader.loadConfig(metadataNode, Metadata.class);
    }

    public Pig(Vector2D spawnPoint, World world) {
        super(spawnPoint, world);
    }

    @Override
    protected Metadata getMetadata() {
        return metadata;
    }

    @Override
    protected EnemyType getEnemyType() {
        return EnemyType.PIG;
    }
}
