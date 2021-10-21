package nl.rug.aoop.asteroids.control.menu_commands.main;

import nl.rug.aoop.asteroids.control.menu_commands.MenuCommands;
import nl.rug.aoop.asteroids.control.ViewController;

import javax.swing.*;
import java.awt.event.ActionEvent;

@MenuCommands(id = "statistic")
public class ViewStatsCommand extends AbstractAction {
    private final ViewController manager;

    public ViewStatsCommand(ViewController manager) {
        super("SCOREBOARD");
        this.manager = manager;
    }

    @Override
    public void actionPerformed(ActionEvent e) {

    }
}
