package nl.rug.aoop.asteroids.util;

import nl.rug.aoop.asteroids.control.ViewController;
import nl.rug.aoop.asteroids.network.data.deltas_changes.Tuple;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

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
}
