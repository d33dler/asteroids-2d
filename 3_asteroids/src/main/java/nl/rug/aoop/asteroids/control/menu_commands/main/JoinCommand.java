package nl.rug.aoop.asteroids.control.menu_commands.main;

import nl.rug.aoop.asteroids.control.menu_commands.MenuCommands;
import nl.rug.aoop.asteroids.control.ViewController;
import nl.rug.aoop.asteroids.network.data.deltas_changes.Tuple;

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
        Tuple.T2<String, Integer> input = getUserInput();
        if (input!=null) {
            InetSocketAddress address = new InetSocketAddress(input.a, input.b);
            manager.getGame().startOnline(address);
            manager.displayGame();
        }
    }

    private Tuple.T2<String, Integer> getUserInput() {
        String hostAddress = JOptionPane.showInputDialog(manager.getFrame(), "Enter host DNS/IP address");
        String port = JOptionPane.showInputDialog(manager.getFrame(), "Enter host port");
        if(hostAddress != null && !port.isEmpty()){
            return new Tuple.T2<>(hostAddress, Integer.valueOf(port));
        }
        return null;
    }
}
