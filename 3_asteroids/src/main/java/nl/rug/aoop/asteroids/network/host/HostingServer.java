package nl.rug.aoop.asteroids.network.host;

import nl.rug.aoop.asteroids.gameobserver.GameUpdateListener;
import nl.rug.aoop.asteroids.model.Game;
import nl.rug.aoop.asteroids.model.MultiplayerManager;
import nl.rug.aoop.asteroids.network.clients.ClientConnection;
import nl.rug.aoop.asteroids.network.clients.User;
import nl.rug.aoop.asteroids.network.data.ConnectionParameters;
import nl.rug.aoop.asteroids.network.data.deltas_changes.ConfigData;
import nl.rug.aoop.asteroids.network.data.deltas_changes.GameplayDeltas;
import nl.rug.aoop.asteroids.network.data.types.DeltasData;
import nl.rug.aoop.asteroids.network.host.listeners.HostListener;
import nl.rug.aoop.asteroids.network.statistics.ConnectionStatistic;
import nl.rug.aoop.asteroids.network.statistics.StatisticCalculator;
import nl.rug.aoop.asteroids.util.Randomizer;
import org.apache.commons.lang3.SerializationUtils;

import javax.swing.*;
import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HostingServer implements HostingDevice, Runnable, GameUpdateListener {

    private final MultiplayerManager multiplayerGame;
    private final List<HostListener> hostListeners = new ArrayList<>();
    private ConnectionParameters parameters;
    private DatagramSocket server_socket;
    private int server_port;
    private final String host_name;
    private ExecutorService executorService = Executors.newFixedThreadPool(10);
    private Thread hostingUserUpdater;
    private ConnectionStatistic connectionStatistic = new ConnectionStatistic();


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

    private void logHost() {
        hostingUserUpdater = new Thread(new HostingUserUpdater());
        hostingUserUpdater.start();
    }

    private void init() {
        executorService = Executors.newFixedThreadPool(multiplayerGame.getMAX_CLIENTS());
        try {
            server_socket = new DatagramSocket();
            server_port = server_socket.getLocalPort(); //TODO this is for local networks
            multiplayerGame.getGame().reportHostPort(server_port);
        } catch (SocketException e) {
            e.printStackTrace();
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

    private void acceptConnections() {
        while (multiplayerGame.getGame().isRunning() && hostListeners.size() < multiplayerGame.getMAX_CLIENTS()) {
            byte[] data = new byte[ConnectionParameters.PKG_SIZE_LIM];
            DatagramPacket handshakePacket = new DatagramPacket(data, data.length);
            try {
                server_socket.receive(handshakePacket);
                addNewClient(handshakePacket);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void addNewClient(DatagramPacket handshake) {
        String clientID = randomizer.generateId();
        DatagramSocket privateSocket = null;
        try {
            privateSocket = new DatagramSocket();
        } catch (SocketException e) {
            e.printStackTrace();
        }
        if (privateSocket != null) {
            InetSocketAddress inetAddress = new InetSocketAddress(handshake.getAddress(), handshake.getPort());
            String add = privateSocket.getLocalAddress().getCanonicalHostName();
            System.out.println("NEW CONNECTION ASSIGNED");
            byte[] data = SerializationUtils.serialize(new ConfigData(clientID, add, privateSocket.getLocalPort())); //TODO refactor;
            DatagramPacket ACK = new DatagramPacket(data, data.length, inetAddress.getAddress(), inetAddress.getPort());
            try {
                server_socket.send(ACK);
            } catch (IOException e) {
                e.printStackTrace();
            }
            ClientConnection newListener = new ClientConnection(this, privateSocket, clientID, inetAddress);
            executorService.execute(newListener);
            hostListeners.add(newListener);
        }
    }

    @Override
    public void playerEliminated(String id) {
        notifyEliminated(id);
    }


    private class HostingUserUpdater implements Runnable {

        @Override
        public synchronized void run() {
            Game hostGame = multiplayerGame.getGame();
            while (hostGame.isRunning()) {
                if (!hostGame.isEngineBusy()) {
                    multiplayerGame.getDeltaManager().collectPlayerDeltas(deltasMap);
                    hostDeltas = multiplayerGame.getDeltaManager().getHostDeltas(); //TODO modification?
                    try {
                        wait(2);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    @Override
    public DatagramSocket getServerSocket() {
        return server_socket;
    }

    public boolean updateReady() {
        return !multiplayerGame.isUpdating();
    }

    @Override
    public byte[] getLastDeltas() {
        return hostDeltas;
    }

    @Override
    public void addNewDelta(String clientIp, DeltasData data) {
        deltasMap.put(clientIp, (GameplayDeltas) data);
    }

    @Override
    public void notifyDisconnected(String id) {
        deltasMap.remove(id);
    }

    @Override
    public void notifyEliminated(String id) {
        notifyDisconnected(id);
    }

    @Override
    public StatisticCalculator getStatisticCalculator() {
        return connectionStatistic;
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
