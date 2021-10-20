package nl.rug.aoop.asteroids.network.clients;


import lombok.Getter;
import lombok.extern.java.Log;
import nl.rug.aoop.asteroids.model.MultiplayerRenderer;
import nl.rug.aoop.asteroids.model.MultiplayerGame;
import nl.rug.aoop.asteroids.network.data.DataPackage;
import nl.rug.aoop.asteroids.network.data.DeltaProcessor;
import nl.rug.aoop.asteroids.network.data.PackageHandler;
import nl.rug.aoop.asteroids.network.data.types.DeltaManager;
import nl.rug.aoop.asteroids.network.protocol.DefaultHandshake;
import nl.rug.aoop.asteroids.network.protocol.IOProtocol;

import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;

@Log
public class User {
    private DatagramSocket client;
    private IOProtocol io;
    @Getter
    private PackageHandler ioHolder;
    @Getter
    private MultiplayerRenderer multiplayerRenderer;
    @Getter
    private DeltaManager deltaManager;


    public User(String hostAddress, int port) {
        initSocket();
        initMultiplayerTools();
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
            io = new DefaultHandshake(client).handshake(deltaManager, new InetSocketAddress(hostAddress, port));
        }
        if (io == null || io.getHolder() == null) {
            log.warning("Failed connection"); //TODO interface
        } else ioHolder = io.getHolder();
    }

    private void initMultiplayerTools() {
        multiplayerRenderer = new MultiplayerGame(this);
        deltaManager = new DeltaProcessor(this);
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
