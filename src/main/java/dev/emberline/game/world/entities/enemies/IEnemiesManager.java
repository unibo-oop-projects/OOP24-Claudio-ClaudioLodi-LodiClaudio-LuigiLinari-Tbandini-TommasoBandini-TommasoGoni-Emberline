package dev.emberline.game.world.entities.enemies;

import dev.emberline.core.components.Renderable;
import dev.emberline.core.components.Updatable;
import dev.emberline.game.world.entities.enemies.enemy.EnemyType;
import dev.emberline.game.world.entities.enemies.enemy.IEnemy;
import dev.emberline.utility.Vector2D;

import java.util.List;

public interface IEnemiesManager extends Updatable, Renderable {

    void addEnemy(Vector2D spawnPoint, EnemyType type);

    List<IEnemy> getNear(Vector2D location, double radius);

    boolean areAllDead();

}
