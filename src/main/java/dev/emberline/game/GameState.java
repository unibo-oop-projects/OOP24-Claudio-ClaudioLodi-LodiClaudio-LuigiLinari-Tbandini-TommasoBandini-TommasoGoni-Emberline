package dev.emberline.game;

import dev.emberline.core.components.InputComponent;
import dev.emberline.core.components.RenderComponent;
import dev.emberline.core.components.UpdateComponent;

/**
 * Represents the general interface for game states in the game.
 * A GameState defines the core structure and behavior expected from various
 * states of the game by incorporating the functionalities of {@link InputComponent},
 * {@link UpdateComponent}, and {@link RenderComponent} interfaces.
 *
 * @see InputComponent
 * @see UpdateComponent
 * @see RenderComponent
 */
public interface GameState extends InputComponent, UpdateComponent, RenderComponent {
}
