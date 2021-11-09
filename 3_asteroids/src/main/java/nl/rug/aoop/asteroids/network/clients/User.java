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

/**
 * User class - acts as client side device to manage online activity in-game
 *
 */
@Log
public class User implements Runnable, GameUpdateListener {
    private DatagramSocket userSocket;
    private IOProtocol io;
    @Getter
    private PackageHandler ioHandler;
    private ConnectionParameters connectionParameters;
    private MultiplayerManager multiplayerManager;

    public String USER_ID = "Host";
    public String USER_NICK;

    private final static String SPECTATOR_KEY = "spectator",
            DEF_CONNECTION_KEY = "default";

    private Thread clientConsumerThread;
    private Thread thisUserThread;

    private final Game game;
    private final GameResources resources;

    private User(Game game) {
        this.game = game;
        this.resources = game.getResources();
        this.USER_NICK = resources.getSpaceShip().getNickId();
        game.addListener(this);
    }

    /**
     *
     * @param game current game
     * @param address hosting address
     * @return new host user
     */
    public static User newHostUser(Game game, InetAddress address) {
        User user = new User(game);
        user.initHostMultiplayer(address);
        return user;
    }

    /**
     *
     * @param game current game
     * @param address host's address specified by the user
     * @return new user set up as client
     */
    public static User newClientUser(Game game, InetSocketAddress address) {
        User user = new User(game);
        user.initSocket();
        user.initClientMultiplayer();
        user.attemptConnect(address, DEF_CONNECTION_KEY);
        user.initConsumerThread();
        user.initProduserThread();
        return user;
    }

    /**
     *
     * @param game current game
     * @param address host's address specified by the user
     * @return new User set up as spectator
     */
    public static User newSpectatorUser(Game game, InetSocketAddress address) {
        User user = new User(game);
        user.initSocket();
        user.initClientMultiplayer();
        user.attemptConnect(address, SPECTATOR_KEY);
        user.initConsumerThread();
        return user;
    }

    /**
     * initialize a client side socket - used by clients(players) and spectators
     */
    private void initSocket() {
        try {
            userSocket = new DatagramSocket();
            System.out.println("USER GOT NEW PORT:  " + userSocket.getLocalPort());
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

    /**
     *
     * Attempts to communicate with  the server (since the communication is connectionless,
     * we simulate a handshake)
     * @param address host's address
     * @param message connection type request
     */
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

    /**
     * Starts the consumer thread for receiving packets
     */
    private void initConsumerThread() {
        clientConsumerThread = new Thread(new Consumer());
        clientConsumerThread.start();
    }

    /**
     * Starts the producer thread for sending packets
     */
    private void initProduserThread() {
        thisUserThread = new Thread(this);
        thisUserThread.start();
    }

    /**
     * Calls the multiplayer manager for a specific setup (client setup)
     */
    private void initClientMultiplayer() {
        multiplayerManager = MultiplayerGame.multiplayerClient(game, this);
    }
    /**
     * Calls the multiplayer manager for a specific setup (host setup)
     */
    private void initHostMultiplayer(InetAddress address) {
        multiplayerManager = MultiplayerGame.multiplayerServer(game, this, address);
        userSocket = multiplayerManager.getHostingDevice().getServerSocket();
    }

    /**
     *
     * @param data local deltas to be serialized and sent to the host
     */
    public synchronized void send(GameplayDeltas data) {
        resources.setUserSerializing(true);
        io.updateOutPackage(data);
        resources.setUserSerializing(false);
        io.send();
    }

    /**
     *
     * @return if socket is not closed
     * (closes if timeout happens on client side while waiting for the server)
     */
    public boolean isConnected() {
        return (!userSocket.isClosed());
    }

    /**
     * Update local state by calling the DeltaManager and injecting the deltas
     */
    private synchronized void updateGame() {
        io.getLastDataPackage().getData().injectChanges(multiplayerManager.getDeltaManager());
    }

    private class Consumer implements Runnable {
        /**
         * Receives packets with a set timeout (which results in socket closure and
         * termination of game update processes
         * Calls for  game state update if the engine is paused
         *
         */
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

    /**
     *
     * Generates new gameplay deltas instances from the players state
     * Calls for IO to send then .
     */
    @SneakyThrows
    @Override
    public synchronized void run() {
        while (isConnected() && !game.isGameOver()) {
            if (!game.isEngineBusy()) {
                send(new GameplayDeltas(multiplayerManager.getDeltaManager().getPlayerDeltas()));
                try {
                    wait(5);
                } catch (InterruptedException ignored) {}
            }
        }

    }

    /**
     * Thread processes termination upon game exit
     */
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
