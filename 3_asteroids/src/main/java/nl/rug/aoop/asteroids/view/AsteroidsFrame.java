package nl.rug.aoop.asteroids.view;

import nl.rug.aoop.asteroids.control.actions.NewGameAction;
import nl.rug.aoop.asteroids.control.PlayerKeyListener;
import nl.rug.aoop.asteroids.control.actions.QuitAction;
import nl.rug.aoop.asteroids.model.Game;

import javax.swing.*;
import java.awt.*;

/**
 * The main window that's used for displaying the game.
 */
public class AsteroidsFrame extends JFrame {
    /**
     * The title which appears in the upper border of the window.
     */
    private static final String WINDOW_TITLE = "Asteroids";

    /**
     * The size that the window should be.
     */
    public static final Dimension WINDOW_SIZE = new Dimension(800, 800);

    /**
     * The game model.
     */
    private final Game game;

    /**
     * Constructs the game's main window.
     *
     * @param game The game model that this window will show.
     */
    public AsteroidsFrame(Game game) {
        this.game = game;
        initSwingUI();
    }

    /**
     * A helper method to do the tedious task of initializing the Swing UI components.
     */
    private void initSwingUI() {
        // Basic frame properties.
        setTitle(WINDOW_TITLE);
        setSize(WINDOW_SIZE);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Add a key listener that can control the game's spaceship.
        addKeyListener(new PlayerKeyListener(game.getSpaceShip()));

        // Add a menu bar with some simple actions.
        JMenuBar menuBar = new JMenuBar();
        JMenu menu = new JMenu("Game");
        menuBar.add(menu);
        menu.add(new QuitAction());
        menu.add(new NewGameAction(game));
        setJMenuBar(menuBar);

        // Add the custom panel that the game will be drawn to.
        add(new AsteroidsPanel(game));
        setVisible(true);
    }
}
