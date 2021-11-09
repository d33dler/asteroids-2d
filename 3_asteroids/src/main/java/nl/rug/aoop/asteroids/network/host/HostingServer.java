package nl.rug.aoop.asteroids.network.host;

import lombok.extern.java.Log;
import nl.rug.aoop.asteroids.gameobserver.GameUpdateListener;
import nl.rug.aoop.asteroids.model.game.Game;
import nl.rug.aoop.asteroids.model.MultiplayerManager;
import nl.rug.aoop.asteroids.network.clients.ClientConnection;
import nl.rug.aoop.asteroids.network.data.ConnectionParameters;
import nl.rug.aoop.asteroids.network.data.deltas_changes.ConfigData;
import nl.rug.aoop.asteroids.network.data.deltas_changes.GameplayDeltas;
import nl.rug.aoop.asteroids.network.data.types.DeltasData;
import nl.rug.aoop.asteroids.network.host.listeners.HostListener;
import nl.rug.aoop.asteroids.network.statistics.ConnectionStatistic;
import nl.rug.aoop.asteroids.util.IOUtils;
import nl.rug.aoop.asteroids.util.Randomizer;
import org.apache.commons.lang3.SerializationUtils;

import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * HostingServer - provides all networking functionality for the hosting user. It implements HostingDevice
 * which enforces basic hosting requirements.
 */
@Log
public class HostingServer implements HostingDevice, GameUpdateListener {

    private final MultiplayerManager multiplayerGame;
    private final List<HostListener> hostListeners = new ArrayList<>();
    private ConnectionParameters parameters;
    private DatagramSocket server_socket;
    private int server_port;
    private final String host_name;
    private ExecutorService executorService = Executors.newFixedThreadPool(10);
    private Thread hostingUserUpdater;


    private final Randomizer randomizer = new Randomizer(6);
    private final String hostId = randomizer.generateId();


    private byte[] hostDeltas;

    private final HashMap<String, GameplayDeltas> deltasMap = new HashMap<>();

    public HostingServer(MultiplayerManager multiplayer, InetAddress address) {
        this.multiplayerGame = multiplayer;
        this.host_name = address.getHostName();
        multiplayer.getGame().addListener(this);
        logHost();
        init();
    }

    /**
     * Starts the hosting updater thread
     */
    private void logHost() {
        hostingUserUpdater = new Thread(new HostingUserUpdater());
        hostingUserUpdater.start();
    }

    /**
     * Initializes a new socket for incoming new connections (a broadcast socket)
     */
    private void init() {
        executorService = Executors.newFixedThreadPool(multiplayerGame.getMAX_CLIENTS());
        try {
            server_socket = new DatagramSocket();
            server_port = server_socket.getLocalPort();
            IOUtils.reportHostPort(null, server_port);
        } catch (SocketException e) {
            log.warning("Could not generate server socket");
        }
        parameters = ConnectionParameters.rawDataParameters();
    }

    @Override
    public void run() {
        acceptConnections();
    }

    @Override
    public void shutdown() {
        try {
            hostingUserUpdater.join(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        hostingUserUpdater = null;
        hostListeners.forEach(HostListener::disconnect);
        executorService.shutdown();
    }

    /**
     * U
      */
    private void acceptConnections() {
        while (multiplayerGame.getGame().isRunning() && hostListeners.size() < multiplayerGame.getMAX_CLIENTS()) {
            byte[] data = new byte[ConnectionParameters.PKG_SIZE_LIM];
            DatagramPacket handshakePacket = new DatagramPacket(data, data.length);
            try {
                server_socket.receive(handshakePacket);
                addNewClient(handshakePacket);
            } catch (IOException e) {
                log.info("Error adding new client");
            }
        }
    }

    /**
     *
     * Reads the clients address data and send back and ACK packet
     * with a generated clientID and the private port on which the client communicates with the host
     * A private socket is created for each connection to separate IO flux
     * @param handshake packet received from the client
     */
    private void addNewClient(DatagramPacket handshake) {
        String clientID = randomizer.generateId();
        DatagramSocket privateSocket = null;
        try {
            privateSocket = new DatagramSocket();
        } catch (SocketException e) {
           log.warning("Failed to create client's private socket");
        }
        if (privateSocket != null) {
            InetSocketAddress inetAddress = new InetSocketAddress(handshake.getAddress(), handshake.getPort());
            String add = privateSocket.getLocalAddress().getCanonicalHostName();
            log.info("NEW CONNECTION ASSIGNED!");
            byte[] data = SerializationUtils.serialize(new ConfigData(clientID, add, privateSocket.getLocalPort()));
            DatagramPacket ACK = new DatagramPacket(data, data.length, inetAddress.getAddress(), inetAddress.getPort());
            try {
                server_socket.send(ACK);
            } catch (IOException e) {
                log.info("Error sending ACK packet to client.");
            }
            ClientConnection newListener = new ClientConnection(this, privateSocket, clientID, inetAddress, handshake.getData());
            executorService.execute(newListener);
            hostListeners.add(newListener);
        }
    }

    /**
     * HostingUserUpdater - updates the host's state with all the collected deltas from the client pool
     * This state is reflected in the hostDeltas byte array which is sent back to all connected clients
     *
     */
    private class HostingUserUpdater implements Runnable {

        @Override
        public synchronized void run() {
            Game hostGame = multiplayerGame.getGame();
            while (hostGame.isRunning()) {
                if (!hostGame.isEngineBusy()) {
                    multiplayerGame.getDeltaManager().collectPlayerDeltas(deltasMap);
                    hostDeltas = multiplayerGame.getDeltaManager().getHostDeltas();
                    try {
                        wait(2);
                    } catch (InterruptedException e) {
                        log.info("Host cache updater thread was interrupted.");
                    }
                }
            }
        }
    }


    @Override
    public void playerEliminated(String id) {
        notifyEliminated(id);
    }

    @Override
    public DatagramSocket getServerSocket() {
        return server_socket;
    }

    /**
     *
     * @return true if the hostDeltas have finished loading
     */
    public boolean updateReady() {
        return !multiplayerGame.isUpdating();
    }

    @Override
    public byte[] getLastDeltas() {
        return hostDeltas;
    }


    /**
     *
     * @param clientID - id of the connection
     * @param data - new delta data to be remapped in the hashmap
     */
    @Override
    public void addNewDelta(String clientID, DeltasData data) {
        deltasMap.put(clientID, (GameplayDeltas) data);
    }

    /**
     *
     * @param id - id of the disconnected user to be removed from the pool
     */
    @Override
    public void notifyDisconnected(String id) {
        multiplayerGame.getGame().requestPlayerRemoval(id);
    }

    /**
     *
     * @param id - if of the eliminated user during gameplay
     */
    @Override
    public void notifyEliminated(String id) {
        deltasMap.remove(id);
    }

    @Override
    public int getHostDefaultLatency() {
        return parameters.LAT_SERVER_millis;
    }

    @Override
    public int getHostMaxLatency() {
        return parameters.LAT_MAX_millis;
    }

    @Override
    public ConnectionParameters getRawConnectionParameters() {
        return parameters;
    }


}
