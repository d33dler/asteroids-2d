package nl.rug.aoop.asteroids.network.clients;


import lombok.extern.java.Log;
import nl.rug.aoop.asteroids.network.data.DataPackage;
import nl.rug.aoop.asteroids.network.data.PackageHolder;
import nl.rug.aoop.asteroids.network.protocol.DefaultHandshake;
import nl.rug.aoop.asteroids.network.protocol.IOProtocol;

import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;

@Log
public class User {
    private DatagramSocket client;
    private IOProtocol io;
    private PackageHolder ioHolder;

    public User(String hostAddress, int port) {
        initSocket();
        attemptConnect(hostAddress, port);
    }

    private void initSocket() {
        try {
            client = new DatagramSocket();
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

    private void attemptConnect(String hostAddress, int port) {
        if (hostAddress != null) {
            io = new DefaultHandshake(client).handshake(new InetSocketAddress(hostAddress, port));
        }
        if (io == null || io.getHolder() == null) {
            log.warning("Failed connection"); //TODO interface
        } else ioHolder = io.getHolder();
    }

    public void send(DataPackage data) {
        ioHolder.setDataPackage(data);
        io.send();
    }

    public void receive() {
        io.receive();
    }

    public boolean isConnected() {
        return (!client.isClosed() && ioHolder != null);
    }
}
