package nl.rug.aoop.asteroids.control;

import lombok.Getter;
import lombok.Setter;
import nl.rug.aoop.asteroids.gameobserver.GameUpdateListener;
import nl.rug.aoop.asteroids.model.game.Game;
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

public class ViewController implements GameUpdateListener {
    @Getter
    @Setter
    private Game game;
    @Getter
    @Setter
    private AsteroidsFrame frame;

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
        frame.setFocusable(true);
        MainMenu menu = new MainMenu(this, mainMenuActions, MAIN_M_BG);
        validatePanel(menu);
    }

    public void displayGame() {
        removePanels();
        frame.activateKeyListener();
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
        EndgameMenu egMenu = new EndgameMenu(this, game.getUserSpaceship().getScore(), GAME_OVER_BG);
        validatePanel(egMenu);
    }

    public void displayPauseMenu() {
        asteroidsPanel.setPaused(true);
        asteroidsPanel.add(pMenu);
        asteroidsPanel.validate();
    }
    public void returnToGame(){
        asteroidsPanel.setPaused(false);
        asteroidsPanel.remove(pMenu);
        asteroidsPanel.validate();
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
        activePanels.clear();
    }

    @Override
    public void onGameOver() {
        displayEndGame();
    }


    public void requestPauseMenu() {
        if(asteroidsPanel!=null){
            if (asteroidsPanel.isPaused()) {
                returnToGame();
            } else {
                displayPauseMenu();
            }
        }
    }

}
