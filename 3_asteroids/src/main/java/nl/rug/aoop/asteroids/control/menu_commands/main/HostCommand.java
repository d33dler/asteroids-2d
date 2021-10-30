package nl.rug.aoop.asteroids.control.menu_commands.main;

import lombok.extern.java.Log;
import nl.rug.aoop.asteroids.control.controls.GameControl;
import nl.rug.aoop.asteroids.control.menu_commands.MenuCommands;
import nl.rug.aoop.asteroids.control.ViewController;
import nl.rug.aoop.asteroids.model.game.Game;
import nl.rug.aoop.asteroids.util.IOUtils;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.net.InetAddress;
import java.net.UnknownHostException;

@MenuCommands(id = "host")
@Log
/**
 * HostCommand - abstract action class attached to menu button; Activated upon click
 */
public class HostCommand extends AbstractAction {

    private final ViewController manager;

    /**
     *  Constructor is called through reflection; All commands must have
     *  the view controller as single parameter;
     * @param manager view controller class
     */
    public HostCommand(ViewController manager) {
        super("HOST");
        this.manager = manager;
    }

    /**
     * Reads user's nickname, updates nick through controller,
     * reads DNS address input and initiates the game method for online hosting.
     * Finally, displays the game panel;
     * Alternatively catches UnknownHostException is the input does not address
     * a reachable DNS;
     */

    @Override
    public void actionPerformed(ActionEvent e) {
        String input = IOUtils.getUserNick(manager.getFrame());
        manager.getGame().updateUSER_NICK(input);
        input = IOUtils.getUserInputDNS(manager.getFrame());
        InetAddress address;
        try {
            address = InetAddress.getByName(input);
            hostingRoutine(manager.getGame(),address);
            manager.displayPane(new GameControl(manager));
        } catch (UnknownHostException ex) {
           IOUtils.reportMessage(manager.getFrame(),"Unknown host address");
        }
    }

    private void hostingRoutine(Game game, InetAddress address){
        game.start(true, true);
        game.initializeGameThreads();
        game.initMultiplayerAsHost(address);
    }

}
