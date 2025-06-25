package dev.emberline.game;

import dev.emberline.core.components.Inputable;
import dev.emberline.core.components.Renderable;
import dev.emberline.core.components.Updatable;

/**
 * Represents the general interface for game states in the game.
 * A GameState defines the core structure and behavior expected from various
 * states of the game by incorporating the functionalities of {@link Inputable},
 * {@link Updatable}, and {@link Renderable} interfaces.
 *
 * @see Inputable
 * @see Updatable
 * @see Renderable
 */
public interface GameState extends Inputable, Updatable, Renderable {
}