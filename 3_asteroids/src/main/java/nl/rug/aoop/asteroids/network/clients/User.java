package nl.rug.aoop.asteroids.network.clients;


import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.java.Log;
import nl.rug.aoop.asteroids.gameobserver.GameUpdateListener;
import nl.rug.aoop.asteroids.model.game.Game;
import nl.rug.aoop.asteroids.model.MultiplayerGame;
import nl.rug.aoop.asteroids.model.MultiplayerManager;
import nl.rug.aoop.asteroids.model.game.GameResources;
import nl.rug.aoop.asteroids.network.data.ConnectionParameters;
import nl.rug.aoop.asteroids.network.data.PackageHandler;
import nl.rug.aoop.asteroids.network.data.deltas_changes.GameplayDeltas;
import nl.rug.aoop.asteroids.network.protocol.DefaultHandshake;
import nl.rug.aoop.asteroids.network.protocol.IOProtocol;
import nl.rug.aoop.asteroids.util.Randomizer;

import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketException;

@Log
public class User implements Runnable, GameUpdateListener {
    private DatagramSocket userSocket;
    private IOProtocol io;
    @Getter
    private PackageHandler ioHandler;
    private ConnectionParameters connectionParameters;
    private MultiplayerManager multiplayerManager;
    private Game game;
    private GameResources resources;
    private final Randomizer randomizer = new Randomizer(6);
    public String USER_ID = "Host";


    private Thread clientConsumerThread;

    private User(Game game, InetSocketAddress address) {
        this.game = game;
        this.resources = game.getResources();
        game.addListener(this);
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
            System.out.println("USER GOT NEW PORT:  " + userSocket.getLocalPort());
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

    private void attemptConnect(InetSocketAddress address) {
        if (address != null) {
            connectionParameters = new ConnectionParameters(userSocket, address, 0); //TODO !!!!!
            io = new DefaultHandshake(userSocket).handshake(connectionParameters);
        }
        if (io == null || io.getPackageHandler() == null) {
            log.warning("Failed connection");               //TODO interface
        } else {
            USER_ID = io.getOwnerId();
            game.setUSER_ID(USER_ID);

            ioHandler = io.getPackageHandler();
            clientConsumerThread = new Thread(new Consumer());
            clientConsumerThread.start();
            new Thread(this).start();
        }
    }

    private void initClientMultiplayer() {
        multiplayerManager = MultiplayerGame.multiplayerClient(game, this);
    }

    private void initHostMultiplayer(InetAddress address) {
        multiplayerManager = MultiplayerGame.multiplayerServer(game, this, address);
        userSocket = multiplayerManager.getHostingDevice().getServerSocket();
    }


    public synchronized void send(GameplayDeltas data) {
        resources.setUserSerializing(true);
        io.updateOutPackage(data);
        resources.setUserSerializing(false);
        io.send();
    }


    public boolean isConnected() {
        return (!userSocket.isClosed());
    }

    private synchronized void updateGame() {
        io.getLastDataPackage().getData().injectChanges(multiplayerManager.getDeltaManager());
    }

    private class Consumer implements Runnable {

        @Override
        public synchronized void run() {
            while (isConnected()) {
                if (!game.isEngineBusy()) {
                    if (io.receive()) {
                        updateGame();
                        try {
                            wait(5);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    } else {
                        userSocket.close();
                        multiplayerManager.notifyDisconnect();
                    }

                }
            }
        }

    }

    @SneakyThrows
    @Override
    public synchronized void run() {
        while (isConnected() && !game.isGameOver()) {
            if (!game.isEngineBusy()) {
                send(new GameplayDeltas(System.currentTimeMillis(),
                        multiplayerManager.getDeltaManager().getPlayerKeyEvents()));
                try {
                    wait(5);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

    }


    @SneakyThrows
    @Override
    public void onGameExit() {
        clientConsumerThread.join(100);
    }

}
