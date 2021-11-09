package nl.rug.aoop.asteroids.control.menu_commands.main;

import nl.rug.aoop.asteroids.control.ViewController;
import nl.rug.aoop.asteroids.control.controls.GameControl;
import nl.rug.aoop.asteroids.control.menu_commands.MenuCommands;
import nl.rug.aoop.asteroids.model.game.Game;
import nl.rug.aoop.asteroids.network.data.deltas_changes.Tuple;
import nl.rug.aoop.asteroids.util.IOUtils;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;

/**
 * SpectateCommand - abstract action class attached to menu button; Activated upon click
 */
@MenuCommands(id = "spectate")
public class SpectateCommand extends AbstractAction {
    private final ViewController manager;

    /**
     *  Constructor is called through reflection; All commands must have
     *  the view controller as single parameter;
     * @param manager view controller class
     */

    public SpectateCommand(ViewController manager) {
        super("SPECTATE");
        this.manager = manager;
    }

    /**
     * Reads user input;
     * Verifies host address validity;
     * Initiates the game in spectator mode.
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        Tuple.T2<String, Integer> input = getUserInput();
        if (input!=null) {
            InetSocketAddress address = new InetSocketAddress(input.a, input.b);
            try {
                InetAddress.getByName(input.a);
                spectatingRoutine(manager.getGame(),address);
                manager.displayPane(new GameControl(manager));
            } catch (UnknownHostException ex) {
                IOUtils.reportMessage(manager.getFrame(),"Unknown host address");
            }
        }
    }

    private void spectatingRoutine(Game game, InetSocketAddress address){
        game.start(true, false);
        game.initializeGameThreads();
        game.initMultiplayerAsSpectator(address);
    }

    private Tuple.T2<String, Integer> getUserInput() {
        return IOUtils.getStringIntegerT2(manager);
    }

}
