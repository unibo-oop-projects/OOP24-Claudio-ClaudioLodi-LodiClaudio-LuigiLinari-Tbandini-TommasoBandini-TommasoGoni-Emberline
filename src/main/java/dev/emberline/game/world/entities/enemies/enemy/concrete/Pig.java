package dev.emberline.game.world.entities.enemies.enemy.concrete;

import dev.emberline.game.world.World;
import dev.emberline.game.world.entities.enemies.enemy.AbstractEnemy;
import dev.emberline.utility.Vector2D;

public class Pig extends AbstractEnemy {
    public Pig(Vector2D spawnPoint, World world) {
        super(spawnPoint, world);
    }

    @Override
    protected double getWidth() {
        return 1;
    }

    @Override
    protected double getHeight() {
        return 1;
    }
}
