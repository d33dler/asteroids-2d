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
    private Thread thisUserThread;

    private User(Game game) {
        this.game = game;
        this.resources = game.getResources();
        game.addListener(this);
    }

    public static User newHostUser(Game game, InetAddress address) {
        User user = new User(game);
        user.initHostMultiplayer(address);
        return user;
    }

    public static User newClientUser(Game game, InetSocketAddress address) {
        User user = new User(game);
        user.initSocket();
        user.initClientMultiplayer();
        user.attemptConnect(address, "default");
        user.initConsumerThread();
        user.initProduserThread();
        return user;
    }

    public static User newSpectatorUser(Game game, InetSocketAddress address) {
        User user = new User(game);
        user.initSocket();
        user.initClientMultiplayer();
        user.attemptConnect(address, "spectator");
        user.initConsumerThread();
        return user;
    }

    private void initSocket() {
        try {
            userSocket = new DatagramSocket();
            System.out.println("USER GOT NEW PORT:  " + userSocket.getLocalPort());
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

    private void attemptConnect(InetSocketAddress address, String message) {
        if (address != null) {
            connectionParameters = new ConnectionParameters(userSocket, address, 0, message);
            io = new DefaultHandshake(userSocket).handshake(connectionParameters);
        }
        if (io == null || io.getPackageHandler() == null) {
            log.warning("Failed connection");
        } else {
            USER_ID = io.getOwnerId();
            game.setUSER_ID(USER_ID);
            ioHandler = io.getPackageHandler();
        }
    }

    private void initConsumerThread() {
        clientConsumerThread = new Thread(new Consumer());
        clientConsumerThread.start();
    }

    private void initProduserThread() {
        thisUserThread = new Thread(this);
        thisUserThread.start();
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
                        multiplayerManager.getDeltaManager().getPlayerDeltas()));
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
        if (clientConsumerThread != null) {
            clientConsumerThread.join(100);
        }
        if (thisUserThread != null)
            thisUserThread.join(100);
    }

}
