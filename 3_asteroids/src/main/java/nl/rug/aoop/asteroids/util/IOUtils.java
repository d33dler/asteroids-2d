package nl.rug.aoop.asteroids.util;

import nl.rug.aoop.asteroids.control.ViewController;
import nl.rug.aoop.asteroids.network.data.deltas_changes.Tuple;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;

/**
 * This class collects methods to display dialog windows
 */
public class IOUtils {

    @Nullable
    public static Tuple.T2<String, Integer> getStringIntegerT2(ViewController manager) {
        String hostAddress = JOptionPane.showInputDialog(manager.getFrame(), "Enter host DNS/IP address");
        if(hostAddress!= null && !hostAddress.isBlank()){
            String port = JOptionPane.showInputDialog(manager.getFrame(), "Enter host port");
            if(port!=null && !port.isBlank()){
                return new Tuple.T2<>(hostAddress, Integer.valueOf(port));
            } else {
                assert port != null;
                if (port.isBlank()) reportMessage(manager.getFrame(), "Invalid port number");
            }
        }
        return null;
    }


    /**
     * This method asks the user for a nickname
     *
     * @param parent The UI component to which the dialog panel belongs
     * @return The text inserted by the user
     */
    public static String getUserNick(Component parent) {
        return JOptionPane.showInputDialog(parent, "Input your nickname");
    }

    /**
     * This method asks the user to input their DNS/IP address for online games
     *
     * @param parent The UI component to which the dialog panel belongs
     * @return The text inserted by the user
     */
    public static String getUserInputDNS(Component parent) {
        return JOptionPane.showInputDialog(parent, "Input your DNS/IP address");
    }

    /**
     * General method to display some message to the user
     *
     * @param parent The parent component of this dialog panel
     * @param msg The message that should be displayed
     */
    public static void reportMessage(Component parent, String msg){
        JOptionPane.showMessageDialog(parent,msg, "ASTEROIDS",JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Copies assigned port to clipboard and notifies user with a dialog window
     *
     * @param parent The parent component  of this dialog panel
     * @param port The port used by the game
     */
    public static void reportHostPort(Component parent, int port) {
        JOptionPane.showMessageDialog(parent, "Assigned port was copied to clipboard!",
                "ASTEROIDS HOSTING", JOptionPane.INFORMATION_MESSAGE);
        StringSelection stringSelection = new StringSelection(Integer.toString(port));
        Clipboard clpbrd = Toolkit.getDefaultToolkit().getSystemClipboard();
        clpbrd.setContents(stringSelection, null);
    }


}
