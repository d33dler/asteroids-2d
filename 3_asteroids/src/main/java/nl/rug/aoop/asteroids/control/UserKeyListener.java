package nl.rug.aoop.asteroids.control;

import nl.rug.aoop.asteroids.model.game.Game;
import nl.rug.aoop.asteroids.model.gameobjects.spaceship.Spaceship;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;


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
