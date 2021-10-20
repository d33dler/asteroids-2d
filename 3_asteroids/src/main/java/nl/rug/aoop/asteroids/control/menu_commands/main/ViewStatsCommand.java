package nl.rug.aoop.asteroids.control.menu_commands.main;

import nl.rug.aoop.asteroids.control.menu_commands.MenuCommands;
import nl.rug.aoop.asteroids.view.ViewManager;

import javax.swing.*;
import java.awt.event.ActionEvent;

@MenuCommands(id = "statistic")
public class ViewStatsCommand extends AbstractAction {
    private final ViewManager manager;

    public ViewStatsCommand(ViewManager manager) {
        super("SCOREBOARD");
        this.manager = manager;
    }

    @Override
    public void actionPerformed(ActionEvent e) {

    }
}
