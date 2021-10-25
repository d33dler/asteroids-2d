package nl.rug.aoop.asteroids.view;

import lombok.Getter;
import lombok.Setter;
import nl.rug.aoop.asteroids.control.ViewController;
import nl.rug.aoop.asteroids.control.actions.NewGameAction;
import nl.rug.aoop.asteroids.control.PlayerKeyListener;
import nl.rug.aoop.asteroids.control.actions.QuitAction;
import nl.rug.aoop.asteroids.gameobserver.GameUpdateListener;
import nl.rug.aoop.asteroids.model.Game;
import nl.rug.aoop.asteroids.view.panels.SpecialComponent;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.Set;

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
    public static final Dimension WINDOW_SIZE = new Dimension(1050, 1000);

    /**
     * The game model.
     */
    @Setter
    private Game game;

    private final ViewController viewController;

    private PlayerKeyListener playerKeyListener;


    /**
     * Constructs the game's main window.
     *
     * @param game The game model that this window will show.
     */
    public AsteroidsFrame(Game game) {
        this.game = game;
        this.viewController = new ViewController(game, this);
        game.addListener(viewController);
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
        playerKeyListener = new PlayerKeyListener(game, viewController, game.getSpaceShip());

        addKeyListener(playerKeyListener);

        // Add a menu bar with some simple actions.
        JMenuBar menuBar = new JMenuBar();
        JMenu menu = new JMenu("Game");
        menuBar.add(menu);
        menu.add(new QuitAction());
        menu.add(new NewGameAction(game));
        setJMenuBar(menuBar);
        viewController.displayMainMenu();
        // Add the custom panel that the game will be drawn to.
        setResizable(false);
        //add(new AsteroidsPanel(game));
        setVisible(true);
    }


    public void resetGame(Game game) {
        Set<GameUpdateListener> listenerSet = this.game.getListeners();
        this.game = game;
        this.game.setListeners(listenerSet);
        resetPlayerKeyListener();
    }

    private void resetPlayerKeyListener() {
        removeKeyListener(playerKeyListener);
        playerKeyListener = new PlayerKeyListener(game, viewController, game.getSpaceShip());
        addKeyListener(playerKeyListener);
    }

}

