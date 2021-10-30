package nl.rug.aoop.asteroids.control.menu_commands.main;

import nl.rug.aoop.asteroids.control.menu_commands.MenuCommands;
import nl.rug.aoop.asteroids.control.ViewController;
import nl.rug.aoop.asteroids.control.controls.ScoreboardControl;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * ViewStats - abstract action class attached to menu button; Activated upon click
 */
@MenuCommands(id = "statistic")
public class ViewStatsCommand extends AbstractAction {
    private final ViewController manager;
    /**
     *  Constructor is called through reflection; All commands must have
     *  the view controller as single parameter;
     * @param manager view controller class
     */
    public ViewStatsCommand(ViewController manager) {
        super("SCOREBOARD");
        this.manager = manager;
    }

    /**
     * Displays the scoreboard panel
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        manager.displayPane(new ScoreboardControl(manager));
    }
}
