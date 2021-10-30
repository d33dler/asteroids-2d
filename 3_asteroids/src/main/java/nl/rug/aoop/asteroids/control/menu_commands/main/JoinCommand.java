package nl.rug.aoop.asteroids.control.menu_commands.main;

import nl.rug.aoop.asteroids.control.controls.GameControl;
import nl.rug.aoop.asteroids.control.menu_commands.MenuCommands;
import nl.rug.aoop.asteroids.control.ViewController;
import nl.rug.aoop.asteroids.network.data.deltas_changes.Tuple;
import nl.rug.aoop.asteroids.util.IOUtils;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.net.InetSocketAddress;

@MenuCommands(id = "join")
public class JoinCommand extends AbstractAction {

    private final ViewController manager;

    public JoinCommand(ViewController manager) {
        super("JOIN");
        this.manager = manager;
    }


    @Override
    public void actionPerformed(ActionEvent e) {
        String in = IOUtils.getUserNick(manager.getFrame());
        manager.getGame().updateUSER_NICK(in);
        Tuple.T2<String, Integer> input = getUserInput();
        if (input!=null) {
            InetSocketAddress address = new InetSocketAddress(input.a, input.b);
            manager.getGame().startOnline(address);
            manager.displayPane(new GameControl(manager));
        }
    }

    private Tuple.T2<String, Integer> getUserInput() {
        return IOUtils.getStringIntegerT2(manager);
    }
}
