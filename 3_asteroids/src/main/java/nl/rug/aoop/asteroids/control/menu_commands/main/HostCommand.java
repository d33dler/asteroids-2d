package nl.rug.aoop.asteroids.control.menu_commands.main;

import lombok.extern.java.Log;
import nl.rug.aoop.asteroids.control.controls.GameControl;
import nl.rug.aoop.asteroids.control.menu_commands.MenuCommands;
import nl.rug.aoop.asteroids.control.ViewController;
import nl.rug.aoop.asteroids.network.data.deltas_changes.Tuple;

import javax.persistence.criteria.CriteriaBuilder;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;

@MenuCommands(id = "host")
@Log
public class HostCommand extends AbstractAction {

    private final ViewController manager;

    public HostCommand(ViewController manager) {
        super("HOST");
        this.manager = manager;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String input = getUserInput();
        InetAddress address;
        try {
            address = InetAddress.getByName(input);
            manager.getGame().startHosting(address);
            manager.displayPane(new GameControl(manager));
        } catch (UnknownHostException ex) {
            log.warning("Unknown host address");
        }
    }

    private String getUserInput() {
        return JOptionPane.showInputDialog(manager.getFrame(), "Input your DNS/IP address");
    }
}
