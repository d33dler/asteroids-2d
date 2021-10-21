package nl.rug.aoop.asteroids.network.protocol;

import lombok.extern.java.Log;
import nl.rug.aoop.asteroids.network.data.ConnectionParameters;
import nl.rug.aoop.asteroids.network.data.PackageHandler;
import nl.rug.aoop.asteroids.network.data.deltas_changes.ConfigData;
import org.apache.commons.lang3.SerializationUtils;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;

@Log

public class DefaultHandshake {
    private final DatagramSocket socket;
    public static final int HANDSHAKE_LEN = 1490;

    public DefaultHandshake(DatagramSocket socket) {
        this.socket = socket;
    }

    public IOProtocol handshake(ConnectionParameters parameters) {
        IO io = new IO(parameters);
        PackageHandler holder = io.getHolder();
        io.send();
        byte[] data = holder.getData();
        DatagramPacket handshake = new DatagramPacket(data, data.length);
        try {
            socket.receive(handshake);
        } catch (IOException e) {
            e.printStackTrace();
            log.warning("Handshake protocol failed");
            return null;
        }
        passHandshakeConfigs(holder, handshake);
        socket.connect(holder.getInet(), holder.getPort());
        return io;
    }

    private void passHandshakeConfigs(PackageHandler handler, DatagramPacket handshake) {
        ConfigData configData = SerializationUtils.deserialize(handshake.getData());
        if (configData != null) {
            handler.initData();
        }
    }
}
