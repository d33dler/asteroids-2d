package nl.rug.aoop.asteroids.control;

import lombok.Getter;
import lombok.Setter;
import nl.rug.aoop.asteroids.control.controls.Control;
import nl.rug.aoop.asteroids.control.controls.EndGameControl;
import nl.rug.aoop.asteroids.control.controls.GameControl;
import nl.rug.aoop.asteroids.control.controls.PauseControl;
import nl.rug.aoop.asteroids.gameobserver.GameUpdateListener;
import nl.rug.aoop.asteroids.model.game.Game;
import nl.rug.aoop.asteroids.view.AsteroidsFrame;

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

    private final List<JPanel> activePanels = new ArrayList<>();

    public ViewController(Game game, AsteroidsFrame frame) {
        this.game = game;
        this.frame = frame;
        game.setViewController(this);
        game.addListener(this);
    }

    /**
     * Executes the given control
     *
     * @param control An object implementing the control interface
     */
    public void displayPane(Control control){
        control.display();
    }

    /**
     * This method adds a panel to the frame and validates it
     *
     * @param menu A Jpanel to add to the frame
     */
    public void validatePanel(JPanel menu) {
        activePanels.add(menu);
        frame.add(menu);
        frame.validate();
    }

    // TODO Useless??
    private void requestGameReset() {
        this.game = new Game();
        frame.setGame(game);
    }

    /**
     * This method clears the frame from the tracked panels
     */
    public void removePanels() {
        for (JPanel activePanel : activePanels) {
            frame.remove(activePanel);
        }
        activePanels.clear();
    }

    @Override
    public void onGameOver() {
        displayPane(new EndGameControl(this));
    }

    @Setter
    private volatile boolean paused = false;

    /**
     * This method handles the pause menu, making it so the game can only
     * be paused while the game is actually running
     */
    public void requestPauseMenu() {
        if (!game.isGameOver() && game.isRunning()) {
            if (paused) {
                displayPane(new GameControl(this));
            } else {
                displayPane(new PauseControl(this));
            }
            setPaused(!paused);
        }
    }
}
