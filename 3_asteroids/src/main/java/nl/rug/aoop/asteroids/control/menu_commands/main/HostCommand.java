package nl.rug.aoop.asteroids.control.menu_commands.main;

import nl.rug.aoop.asteroids.control.menu_commands.MenuCommand;
import nl.rug.aoop.asteroids.control.menu_commands.MenuCommands;
import nl.rug.aoop.asteroids.view.ViewManager;

import javax.swing.*;
import java.awt.event.ActionEvent;

@MenuCommands(id = "host")
public class HostCommand extends AbstractAction {

   private final ViewManager manager;

    public HostCommand(ViewManager manager) {
        super("HOST");
        this.manager = manager;
    }

    @Override
    public void actionPerformed(ActionEvent e) {

    }
}
