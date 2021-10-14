package nl.rug.aoop.asteroids.network.clients;


import lombok.Getter;
import lombok.extern.java.Log;
import nl.rug.aoop.asteroids.network.data.DataPackage;
import nl.rug.aoop.asteroids.network.data.PackageHolder;
import nl.rug.aoop.asteroids.network.protocol.ConnectionProtocol;

import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;

@Log
public class ClientConnection {
    private DatagramSocket client;
    private ConnectionProtocol protocol;
    @Getter
    private PackageHolder holder;

    public ClientConnection(String hostAddress, int port) {
        initSocket();
        attemptConnect(hostAddress, port);
    }

    private void initSocket() {
        try {
            client = new DatagramSocket();
            protocol = new ConnectionProtocol(client);
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

    private void attemptConnect(String hostAddress, int port) {
        if (hostAddress != null) {
            holder = protocol.handshake(new InetSocketAddress(hostAddress, port));
        }
        if (holder == null) {
            log.warning("Failed connection"); //TODO interface
        }
    }

    public void send(DataPackage data) {
        holder.setDataPackage(data);
        protocol.send(holder);
    }

    public void receive() {
        protocol.receive(holder);
    }

    public boolean isConnected() {
        return (!client.isClosed() && holder != null);
    }
}
