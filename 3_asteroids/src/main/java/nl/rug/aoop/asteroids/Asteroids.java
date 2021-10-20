package nl.rug.aoop.asteroids;

import nl.rug.aoop.asteroids.control.GameUpdater;
import nl.rug.aoop.asteroids.control.actions.NewGameAction;
import nl.rug.aoop.asteroids.model.Game;
import nl.rug.aoop.asteroids.view.AsteroidsFrame;
import nl.rug.aoop.asteroids.view.panels.AsteroidsPanel;
import com.formdev.flatlaf.FlatDarculaLaf;

import java.awt.event.ActionEvent;

/**
 * Main class of the Asteroids program.
 *
 * Asteroids is simple game, in which the player is represented by a small spaceship. The goal is to destroy as many
 * asteroids as possible and thus survive for as long as possible.
 *
 * The game rules are as follows:
 *
 * 1. All game objects are updated according to their own rules every game tick.
 * 2. Every so often, a new asteroid will spawn, but not within 50 pixels of a player.
 * 3. There is a limit to the number of asteroids that may be present in the game at once, and this limit grows as the
 * player's score does.
 * 4. Destroying an asteroid spawns two smaller asteroids, unless you destroyed a small asteroid.
 * 5. The player dies upon colliding with an asteroid or a bullet.
 *
 * Some shortcuts to help you get started with this codebase:
 *
 * 1. The model that holds all game state information: {@link Game}
 * 2. The game engine, which does the main game loop and physics updates: {@link GameUpdater}
 * 3. The JPanel that is responsible for drawing all the game's objects on the screen: {@link AsteroidsPanel}
 */
public class Asteroids {
	/**
	 * Main method, where the program starts. All this needs to do is begin the game.
	 *
	 * @param args The array of arguments passed to the program from the command line.
	 */
	public static void main(String[] args) {
		if (System.getProperty("os.name").contains("Mac")) {
			System.setProperty("apple.laf.useScreenMenuBar", "true");
		}
		FlatDarculaLaf.setup();

		// Create the game model and display frame.
		Game game = new Game();
		AsteroidsFrame frame = new AsteroidsFrame(game);

	/*	// Generate a new action event so that we can use the NewGameAction to start a new game.
		new NewGameAction(game).actionPerformed(
				// Just use a dummy action; NewGameAction doesn't care about the action event's properties.
				new ActionEvent(frame, ActionEvent.ACTION_PERFORMED, null)
		);

	 */
	}


}
