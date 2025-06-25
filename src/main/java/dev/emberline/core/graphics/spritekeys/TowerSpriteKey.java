package dev.emberline.core.graphics.spritekeys;

import dev.emberline.game.model.EnchantmentInfo;
import dev.emberline.game.model.ProjectileInfo;

public record TowerSpriteKey(ProjectileInfo.Type size, EnchantmentInfo.Type enchant) implements SpriteKey {
}
