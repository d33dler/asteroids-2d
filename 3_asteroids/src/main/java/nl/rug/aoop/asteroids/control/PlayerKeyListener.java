package nl.rug.aoop.asteroids.control;

import nl.rug.aoop.asteroids.model.game.Game;
import nl.rug.aoop.asteroids.model.gameobjects.spaceship.Spaceship;
import nl.rug.aoop.asteroids.network.clients.User;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/**
 * This class is responsible for handling keyboard input for a single player that is bound to a ship.
 */
public class PlayerKeyListener extends UserKeyListener {
    /**
     * The key that, when pressed, causes the ship to accelerate.
     */
    protected static final int ACCELERATION_KEY = KeyEvent.VK_W;

    /**
     * The key that turns the ship left, or counter-clockwise.
     */
    protected static final int LEFT_KEY = KeyEvent.VK_A;

    /**
     * The key that turns the ship right, or clockwise.
     */
    protected static final int RIGHT_KEY = KeyEvent.VK_D;

    /**
     * The key that causes the ship to fire its weapon.
     */
    protected static final int FIRE_WEAPON_KEY = KeyEvent.VK_SPACE;




    /**
     * Constructs a new player key listener to control the given ship.
     *
     * @param ship The ship that this key listener will control.
     */
    public PlayerKeyListener(Game game, ViewController viewController, Spaceship ship) {
        super(ship, game, viewController);
    }

    /**
     * This method is invoked when a key is pressed and sets the corresponding fields in the spaceship to true.
     *
     * @param event Key event that triggered the method.
     */
    @Override
    public void keyPressed(KeyEvent event) {
        switch (event.getKeyCode()) {
            case ACCELERATION_KEY -> {
                ship.setAccelerateKeyPressed(true);
                ship.getKeyEventSet().add(ACCELERATION_KEY);
            }
            case LEFT_KEY -> {
                ship.setTurnLeftKeyPressed(true);
                ship.getKeyEventSet().add(LEFT_KEY);
            }
            case RIGHT_KEY -> {
                ship.setTurnRightKeyPressed(true);
                ship.getKeyEventSet().add(RIGHT_KEY);
            }
            case FIRE_WEAPON_KEY -> {
                ship.setFiring(true);
                ship.getKeyEventSet().add(FIRE_WEAPON_KEY);
            }
            case pauseMenu -> {
                controller.requestPauseMenu();
            }
        }
    }

    /**
     * This method is invoked when a key is released and sets the corresponding fields in the spaceship to false.
     *
     * @param event Key event that triggered the method.
     */
    @Override
    public void keyReleased(KeyEvent event) {           //TODO this is sloppy
        switch (event.getKeyCode()) {
            case ACCELERATION_KEY -> {
                ship.setAccelerateKeyPressed(false);
                ship.getKeyEventSet().remove(ACCELERATION_KEY);
            }
            case LEFT_KEY -> {
                ship.setTurnLeftKeyPressed(false);
                ship.getKeyEventSet().remove(LEFT_KEY);
            }
            case RIGHT_KEY -> {
                ship.setTurnRightKeyPressed(false);
                ship.getKeyEventSet().remove(RIGHT_KEY);
            }
            case FIRE_WEAPON_KEY -> {
                ship.setFiring(false);
                ship.getKeyEventSet().remove(FIRE_WEAPON_KEY);
            }
        }
    }

    /**
     * This method doesn't do anything, but we must provide an empty implementation to satisfy the contract of the
     * KeyListener interface.
     *
     * @param event Key event that triggered the method.
     */
    @Override
    public void keyTyped(KeyEvent event) {

    }
}
