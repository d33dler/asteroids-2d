package nl.rug.aoop.asteroids.control;

import nl.rug.aoop.asteroids.model.game.Game;
import nl.rug.aoop.asteroids.model.gameobjects.spaceship.Spaceship;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/**
 * UserKeyListener - all classes recording input keys must extend this class to
 * optionally inherit general input reads (interface manipulation , etc.)
 * Currently, used to limit spectators in key inputs availability.
 */
public class UserKeyListener implements KeyListener {

    protected final static int pauseMenu = KeyEvent.VK_ESCAPE;
    /**
     * The spaceship that will respond to key events caught by this listener.
     */
    protected final Spaceship ship;

    protected final Game game;
    protected ViewController controller;

    public UserKeyListener(Spaceship ship, Game game, ViewController controller) {
        this.ship = ship;
        this.game = game;
        this.controller = controller;
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }
    /**
     * This method is invoked when a key is released and activates
     * view controller methods for interface manipulation
     */
    @Override
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case pauseMenu -> {
                controller.requestPauseMenu();
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {

    }
}
