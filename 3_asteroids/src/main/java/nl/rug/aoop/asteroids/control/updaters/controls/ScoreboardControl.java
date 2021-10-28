package nl.rug.aoop.asteroids.control.updaters.controls;

import nl.rug.aoop.asteroids.control.ViewController;
import nl.rug.aoop.asteroids.view.panels.ScoreboardPanel;

/**
 * Control used by controller to display the scoreboard
 */
public class ScoreboardControl extends Control{

    private final static String SCOREBOARD = "images/scoreboard.png";

    /**
     * Default constructor that only assigns the view controller
     *
     * @param controller The view controller
     */
    public ScoreboardControl(ViewController controller) {
        super(controller);
    }

    @Override
    public void display() {
        ScoreboardPanel scoreboardPanel = new ScoreboardPanel(controller, SCOREBOARD);
        super.display(scoreboardPanel);
    }
}
