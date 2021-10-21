package nl.rug.aoop.asteroids.control.menu_commands.main;

import nl.rug.aoop.asteroids.control.actions.NewGameAction;
import nl.rug.aoop.asteroids.control.menu_commands.MenuCommands;
import nl.rug.aoop.asteroids.control.ViewController;

import javax.swing.*;
import java.awt.event.ActionEvent;

@MenuCommands(id = "start_default")
public class StartCommand extends AbstractAction {

    private final ViewController manager;

    public StartCommand(ViewController manager) {
        super("ARCADE");
        this.manager = manager;
    }


    @Override
    public void actionPerformed(ActionEvent e) {
        manager.displayGame();
        new NewGameAction(manager.getGame()).actionPerformed(null);
    }
}
