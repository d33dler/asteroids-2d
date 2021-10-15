package nl.rug.aoop.asteroids.network.protocol;

import lombok.extern.java.Log;
import nl.rug.aoop.asteroids.network.data.PackageHolder;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;

@Log

public class DefaultHandshake  {
    private final DatagramSocket socket;
    public static final int HANDSHAKE_LEN = 1024;

    public DefaultHandshake(DatagramSocket socket) {
        this.socket = socket;
    }
    public IO handshake(InetSocketAddress receptor) {
        IO io = new IO(socket, receptor);
        PackageHolder holder = io.getHolder();
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
        holder.loadHandshakeConfigs(handshake);
        socket.connect(holder.getInet(), holder.getPort());
        return io;
    }
}
