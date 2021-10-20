package nl.rug.aoop.asteroids.control.menu_commands.main;

import nl.rug.aoop.asteroids.control.menu_commands.MenuCommands;
import nl.rug.aoop.asteroids.view.ViewManager;

import javax.swing.*;
import java.awt.event.ActionEvent;

@MenuCommands(id = "start_default")
public class StartCommand extends AbstractAction {

    private final ViewManager manager;

    public StartCommand(ViewManager manager) {
        super("ARCADE");
        this.manager = manager;
    }


    @Override
    public void actionPerformed(ActionEvent e) {

    }
}
