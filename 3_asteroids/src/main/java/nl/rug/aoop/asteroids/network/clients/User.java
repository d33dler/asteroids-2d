package nl.rug.aoop.asteroids.network.clients;


import lombok.Getter;
import lombok.extern.java.Log;
import nl.rug.aoop.asteroids.model.Game;
import nl.rug.aoop.asteroids.model.MultiplayerGame;
import nl.rug.aoop.asteroids.model.MultiplayerManager;
import nl.rug.aoop.asteroids.network.data.ConnectionParameters;
import nl.rug.aoop.asteroids.network.data.DataPackage;
import nl.rug.aoop.asteroids.network.data.PackageHandler;
import nl.rug.aoop.asteroids.network.protocol.DefaultHandshake;
import nl.rug.aoop.asteroids.network.protocol.IOProtocol;

import javax.naming.spi.ObjectFactory;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketException;

@Log
public class User {
    private DatagramSocket userSocket;
    private IOProtocol io;
    @Getter
    private PackageHandler ioHolder;
    private ConnectionParameters connectionParameters;
    private ObjectFactory objectFactory;
    private MultiplayerManager multiplayerManager;
    private final Game game;

    private User(Game game, InetSocketAddress address) {
        this.game = game;
        initSocket();
        initClientMultiplayer();
        attemptConnect(address);
    }

    private User(Game game, InetAddress inetAddress) {
        this.game = game;
        initHostMultiplayer(inetAddress);
    }

    public static User newHostUser(Game game, InetAddress address) {
        return new User(game, address);
    }

    public static User newClientUser(Game game, InetSocketAddress address) {
        return new User(game, address);
    }

    private void initSocket() {
        try {
            userSocket = new DatagramSocket();
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

    private void attemptConnect(InetSocketAddress address) {
        if (address != null) {
            connectionParameters = new ConnectionParameters(userSocket, address,0); //TODO !!!!!
            io = new DefaultHandshake(userSocket).handshake(connectionParameters);
        }
        if (io == null || io.getHolder() == null) {
            log.warning("Failed connection"); //TODO interface
        } else ioHolder = io.getHolder();
    }



    private void initClientMultiplayer() {
        multiplayerManager = MultiplayerGame.multiplayerClient(game,this);
    }

    private void initHostMultiplayer(InetAddress address){
        multiplayerManager = MultiplayerGame.multiplayerServer(game,this, address);
        userSocket = multiplayerManager.getHostingDevice().getServerSocket();
    }


    public void send(DataPackage data) {
        ioHolder.setDataPackage(data);
        io.send();
    }

    public void receive() {
        io.receive();
    }

    public boolean isConnected() {
        return (!userSocket.isClosed() && ioHolder != null);
    }
}
