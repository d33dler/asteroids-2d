package nl.rug.aoop.asteroids.util;

import nl.rug.aoop.asteroids.control.ViewController;
import nl.rug.aoop.asteroids.network.data.deltas_changes.Tuple;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;

public class IOUtils {

    @Nullable
    public static Tuple.T2<String, Integer> getStringIntegerT2(ViewController manager) {
        String hostAddress = JOptionPane.showInputDialog(manager.getFrame(), "Enter host DNS/IP address");
        String port = JOptionPane.showInputDialog(manager.getFrame(), "Enter host port");
        if(hostAddress != null && port!=null){
            return new Tuple.T2<>(hostAddress, Integer.valueOf(port));
        }
        return null;
    }


    public static String getUserNick(Component parent) {
        return JOptionPane.showInputDialog(parent, "Input your nickname");
    }

    public static String getUserInputDNS(Component parent) {
        return JOptionPane.showInputDialog(parent, "Input your DNS/IP address");
    }

    public static void reportMessage(Component parent, String msg){
        JOptionPane.showMessageDialog(parent,msg, "ASTEROIDS",JOptionPane.INFORMATION_MESSAGE);
    }

    public static void reportHostPort(Component parent, int port) {
        JOptionPane.showMessageDialog(parent, "Assigned port was copied to clipboard!",
                "ASTEROIDS HOSTING", JOptionPane.INFORMATION_MESSAGE);
        StringSelection stringSelection = new StringSelection(Integer.toString(port));
        Clipboard clpbrd = Toolkit.getDefaultToolkit().getSystemClipboard();
        clpbrd.setContents(stringSelection, null);
    }


}
