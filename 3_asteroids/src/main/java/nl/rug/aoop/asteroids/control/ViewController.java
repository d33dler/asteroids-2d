package nl.rug.aoop.asteroids.control;

import lombok.Getter;
import lombok.Setter;
import nl.rug.aoop.asteroids.model.Game;
import nl.rug.aoop.asteroids.util.ReflectionUtils;
import nl.rug.aoop.asteroids.view.AsteroidsFrame;
import nl.rug.aoop.asteroids.view.menus.EndgameMenu;
import nl.rug.aoop.asteroids.view.menus.main_menu.MainMenu;
import nl.rug.aoop.asteroids.view.menus.pause_menu.PauseMenu;
import nl.rug.aoop.asteroids.view.panels.AsteroidsPanel;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public class ViewController {
    @Getter
    @Setter
    private Game game;
    @Getter
    private final AsteroidsFrame frame;

    private List<AbstractAction> mainMenuActions;
    private List<AbstractAction> pauseActions;

    private final static String MAIN_M_PKG = "nl.rug.aoop.asteroids.control.menu_commands.main",
            PAUSE_M_PKG = "nl.rug.aoop.asteroids.control.menu_commands.pause";

    private final static String MAIN_M_BG = "images/menu_bg.png";
    private AsteroidsPanel asteroidsPanel;

    private final List<JPanel> activePanels = new ArrayList<>();

    public ViewController(Game game, AsteroidsFrame frame) {
        this.game = game;
        this.frame = frame;
        game.setViewController(this);
        initAllCommands();
    }

    private void initAllCommands() {
        mainMenuActions = ReflectionUtils.getMenuCommands(this, MAIN_M_PKG);
        pauseActions = ReflectionUtils.getMenuCommands(this, PAUSE_M_PKG);
    }

    public void displayMainMenu() {
        removePanels();
        MainMenu menu = new MainMenu(this, mainMenuActions, MAIN_M_BG);
        activePanels.add(menu);
        frame.add(menu);
        frame.revalidate();
    }

    public void displayGame() {
        removePanels();
        asteroidsPanel = new AsteroidsPanel(game, this);
        activePanels.add(asteroidsPanel);
        frame.add(asteroidsPanel);
        frame.revalidate();
    }


    public void displayEndGame() {
        game.endGame();
        removePanels();
        EndgameMenu egMenu = new EndgameMenu(this, game.getSpaceShip().getScore());
        activePanels.add(egMenu);
        frame.add(egMenu);
        frame.revalidate();
    }

    public void displayPauseMenu() {
        frame.add(new PauseMenu(this));
    }


    public void displayScoreboards() {

    }

    private void requestGameReset(){
        this.game = new Game();
        frame.setGame(game);
    }

    private void removePanels() {
        for (JPanel activePanel : activePanels) {
            frame.remove(activePanel);
        }
    }

}
