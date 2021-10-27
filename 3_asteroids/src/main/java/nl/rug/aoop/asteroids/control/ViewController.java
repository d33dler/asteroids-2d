package nl.rug.aoop.asteroids.control;

import lombok.Getter;
import lombok.Setter;
import nl.rug.aoop.asteroids.gameobserver.GameUpdateListener;
import nl.rug.aoop.asteroids.model.Game;
import nl.rug.aoop.asteroids.util.ReflectionUtils;
import nl.rug.aoop.asteroids.view.AsteroidsFrame;
import nl.rug.aoop.asteroids.view.menus.EndgameMenu;
import nl.rug.aoop.asteroids.view.menus.main_menu.MainMenu;
import nl.rug.aoop.asteroids.view.menus.pause_menu.PauseMenu;
import nl.rug.aoop.asteroids.view.panels.AsteroidsPanel;
import nl.rug.aoop.asteroids.view.panels.ScoreboardPanel;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public class ViewController implements GameUpdateListener{
    @Getter
    @Setter
    private Game game;
    @Getter
    private final AsteroidsFrame frame;

    private List<AbstractAction> mainMenuActions;
    private List<AbstractAction> pauseActions;
    @Getter
    private final PauseMenu pMenu = new PauseMenu(this);

    private final static String MAIN_M_PKG = "nl.rug.aoop.asteroids.control.menu_commands.main",
            PAUSE_M_PKG = "nl.rug.aoop.asteroids.control.menu_commands.pause";

    private final static String MAIN_M_BG = "images/menu_bg.png",
            GAME_OVER_BG = "images/game_over.png";
    private final static String SCOREBOARD = "images/scoreboard.png";

    private AsteroidsPanel asteroidsPanel;

    private final List<JPanel> activePanels = new ArrayList<>();
    private JDialog pauseMenu;
    private JLayeredPane layeredPane;
    public ViewController(Game game, AsteroidsFrame frame) {
        this.game = game;
        this.frame = frame;
        game.setViewController(this);
        game.addListener(this);
        initAllCommands();
    }

    private void initAllCommands() {
        mainMenuActions = ReflectionUtils.getMenuCommands(this, MAIN_M_PKG);
        pauseActions = ReflectionUtils.getMenuCommands(this, PAUSE_M_PKG);
    }

    public void displayMainMenu() {
        removePanels();
        MainMenu menu = new MainMenu(this, mainMenuActions, MAIN_M_BG);
        validatePanel(menu);
    }

    public void displayGame() {
        removePanels();
        asteroidsPanel = new AsteroidsPanel(this, game);
        validatePanel(asteroidsPanel);
    }

    public void displayScoreBoard() {
        removePanels();
        ScoreboardPanel scoreboardPanel = new ScoreboardPanel(this, SCOREBOARD);
        validatePanel(scoreboardPanel);
    }

    public void displayEndGame() {
        removePanels();
        EndgameMenu egMenu = new EndgameMenu(this, game.getSpaceShip().getScore(), GAME_OVER_BG);
        validatePanel(egMenu);
    }

    public void displayPauseMenu() {
        asteroidsPanel.add(pMenu);
    validatePanel(pMenu);
        asteroidsPanel.setPaused(true);
    }

    private void validatePanel(JPanel menu) {
        activePanels.add(menu);
        frame.add(menu);
        frame.validate();
    }

    private void requestGameReset() {
        this.game = new Game();
        frame.setGame(game);
    }

    private void removePanels() {
        for (JPanel activePanel : activePanels) {
            frame.remove(activePanel);
        }
    }

    @Override
    public void onGameOver() {
        displayEndGame();
    }


    private boolean paused = false;

    public void requestPauseMenu() {
        System.out.println("REQUESTING");
        if (paused) {
            displayGame();
        } else {
            displayPauseMenu();
        }
        paused = !paused;
    }

}
