package nl.rug.aoop.asteroids.control.menu_commands.pause;

import nl.rug.aoop.asteroids.control.ViewController;
import nl.rug.aoop.asteroids.control.menu_commands.MenuCommands;

import javax.swing.*;
import java.awt.event.ActionEvent;

@MenuCommands(id = "return")
public class ReturnCommand extends AbstractAction {

    private final ViewController manager;

    public ReturnCommand(ViewController manager){
        super("MAIN MENU");
        this.manager = manager;
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        manager.displayMainMenu();
    }
}
