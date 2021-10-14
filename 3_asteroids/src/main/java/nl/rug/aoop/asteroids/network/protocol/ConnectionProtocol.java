package nl.rug.aoop.asteroids.network.protocol;

import lombok.extern.java.Log;
import nl.rug.aoop.asteroids.network.data.PackageHolder;
import nl.rug.aoop.asteroids.network.data.ProtocolParameters;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;

@Log

public class ConnectionProtocol {
    private final DatagramSocket socket;

    public static final int HANDSHAKE_LEN = 1024;

    public ConnectionProtocol(DatagramSocket socket) {
        this.socket = socket;
    }

    public PackageHolder handshake(InetSocketAddress address) {
        PackageHolder holder = PackageHolder.newEmptyHolder(new ProtocolParameters(address, HANDSHAKE_LEN));
        send(holder);
        byte[] data = holder.getEmptyData();
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
        return holder;
    }

    public void send(PackageHolder holder) {
        byte[] data = holder.getDataBytes();
        DatagramPacket packet = new DatagramPacket(data, data.length, holder.getInet(), holder.getPort());
        try {
            socket.send(packet);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void receive(PackageHolder holder) {
        byte[] data = holder.getEmptyData();
        DatagramPacket packet = new DatagramPacket(data, data.length);
        try {
            socket.receive(packet); //TODO verify
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
