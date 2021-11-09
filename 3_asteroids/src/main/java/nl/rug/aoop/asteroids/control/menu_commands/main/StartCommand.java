package nl.rug.aoop.asteroids.control.menu_commands.main;

import nl.rug.aoop.asteroids.control.actions.NewGameAction;
import nl.rug.aoop.asteroids.control.controls.GameControl;
import nl.rug.aoop.asteroids.control.menu_commands.MenuCommands;
import nl.rug.aoop.asteroids.control.ViewController;
import nl.rug.aoop.asteroids.util.IOUtils;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * StartCommand - abstract action class attached to menu button; Activated upon click
 */
@MenuCommands(id = "start_default")
public class StartCommand extends AbstractAction {

    private final ViewController manager;

    /**
     * Constructor is called through reflection; All commands must have
     * the view controller as single parameter;
     *
     * @param manager view controller class
     */
    public StartCommand(ViewController manager) {
        super("ARCADE");
        this.manager = manager;
    }

    /**
     * Reads user's nickname and initiates a single player game.
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        String input = IOUtils.getUserNick(manager.getFrame());
        if (input != null) {
            manager.getGame().updateUSER_NICK(input);
            manager.displayPane(new GameControl(manager));
            new NewGameAction(manager.getGame()).actionPerformed(null);
        } else IOUtils.reportMessage(manager.getFrame(), "Invalid nickname");
    }

}
