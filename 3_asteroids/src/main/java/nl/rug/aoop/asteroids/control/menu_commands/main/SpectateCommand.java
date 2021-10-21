package nl.rug.aoop.asteroids.control.menu_commands.main;

import nl.rug.aoop.asteroids.control.menu_commands.MenuCommands;
import nl.rug.aoop.asteroids.control.ViewController;

import javax.swing.*;
import java.awt.event.ActionEvent;

@MenuCommands(id = "spectate")
public class SpectateCommand extends AbstractAction {
    private final ViewController manager;

    public SpectateCommand(ViewController manager) {
        super("SPECTATE");
        this.manager = manager;
    }

    @Override
    public void actionPerformed(ActionEvent e) {

    }
}
