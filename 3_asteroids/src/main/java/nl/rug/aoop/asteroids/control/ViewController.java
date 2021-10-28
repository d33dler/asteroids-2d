package nl.rug.aoop.asteroids.control;

import lombok.Getter;
import lombok.Setter;
import nl.rug.aoop.asteroids.control.updaters.controls.Control;
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

    private List<AbstractAction> pauseActions;
    @Getter
    private final PauseMenu pMenu = new PauseMenu(this);

    private final static String PAUSE_M_PKG = "nl.rug.aoop.asteroids.control.menu_commands.pause";

    private final static String GAME_OVER_BG = "images/game_over.png";

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
        pauseActions = ReflectionUtils.getMenuCommands(this, PAUSE_M_PKG);
    }

    /**
     * Executes the given control
     *
     * @param control An object implementing the control interface
     */
    public void displayPane(Control control){
        control.display();
    }

    public void displayGame() {
        removePanels();
       frame.setFocusable(true);
        asteroidsPanel = new AsteroidsPanel(this, game);
        validatePanel(asteroidsPanel);
    }

    public void displayEndGame() {
        removePanels();
        EndgameMenu egMenu = new EndgameMenu(this, game.getUserSpaceship().getScore(), GAME_OVER_BG);
        validatePanel(egMenu);
    }

    public void displayPauseMenu() {
       removePanels();
        validatePanel(pMenu);
        pMenu.refresh();
    }

    public void validatePanel(JPanel menu) {
        activePanels.add(menu);
        frame.add(menu);
        frame.validate();
    }

    private void requestGameReset() {
        this.game = new Game();
        frame.setGame(game);
    }

    public void removePanels() {
        for (JPanel activePanel : activePanels) {
            frame.remove(activePanel);
        }
        activePanels.clear();
    }

    @Override
    public void onGameOver() {
        displayEndGame();
    }

@Setter
    private volatile boolean paused = false;

    public void requestPauseMenu() {
        if (!game.isGameOver() && game.isRunning()) {
            if (paused) {
               displayGame();
            } else {
                displayPauseMenu();
            }
            setPaused(!paused);
        }
    }
}
