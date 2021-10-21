package nl.rug.aoop.asteroids.network.host;

import nl.rug.aoop.asteroids.model.Game;
import nl.rug.aoop.asteroids.model.MultiplayerManager;
import nl.rug.aoop.asteroids.network.clients.ClientConnection;
import nl.rug.aoop.asteroids.network.clients.User;
import nl.rug.aoop.asteroids.network.data.ConnectionParameters;
import nl.rug.aoop.asteroids.network.data.deltas_changes.ConfigData;
import nl.rug.aoop.asteroids.network.data.deltas_changes.GameplayDeltas;
import nl.rug.aoop.asteroids.network.data.deltas_changes.Tuple;
import nl.rug.aoop.asteroids.network.data.types.DeltasData;
import nl.rug.aoop.asteroids.network.host.listeners.HostListener;
import nl.rug.aoop.asteroids.network.statistics.ConnectionStatistic;
import nl.rug.aoop.asteroids.network.statistics.StatisticCalculator;
import nl.rug.aoop.asteroids.util.Randomizer;
import nl.rug.aoop.asteroids.util.ReflectionUtils;
import org.apache.commons.lang3.SerializationUtils;

import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HostingServer implements HostingDevice, Runnable {

    private final MultiplayerManager multiplayerGame;
    private final List<HostListener> hostListeners = new ArrayList<>();
    private ConnectionParameters parameters;
    private DatagramSocket server_socket;
    private int server_port;
    private final String host_name;
    private ExecutorService executorService;
    private ConnectionStatistic connectionStatistic = new ConnectionStatistic();

    private final InetAddress socketAddress;
    private InetSocketAddress inetSocketAddress;

    private final Randomizer randomizer = new Randomizer(6);
    private final HashMap<String, GameplayDeltas> deltasMap = new HashMap<>();
    private final String hostId = randomizer.generateId();
    private GameplayDeltas hostDeltas;


    public HostingServer(MultiplayerManager multiplayer, InetAddress address) {
        this.multiplayerGame = multiplayer;
        this.socketAddress = address;
        this.host_name = address.getHostName();
        logHost();
        init();
    }

    private void logHost() {
        hostDeltas = multiplayerGame.getHost().getUserDeltas();
        new HostingUserUpdater(multiplayerGame.getHost()).run();
    }

    private void init() {
        executorService = Executors.newFixedThreadPool(multiplayerGame.getMAX_CLIENTS());
        try {
            server_socket = new DatagramSocket();
            server_port = server_socket.getLocalPort(); //TODO this is for local networks
            System.out.println(server_port);
            //  inetSocketAddress = new InetSocketAddress(socketAddress, server_port);
        } catch (SocketException e) {
            e.printStackTrace();
        }
        parameters = ConnectionParameters.rawDataParameters();
    }

    @Override
    public void run() {
        acceptConnections();
    }

    private void acceptConnections() {
        while (hostListeners.size() < multiplayerGame.getMAX_CLIENTS()) {
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
        InetSocketAddress inetAddress = new InetSocketAddress(handshake.getAddress(), handshake.getPort());
        byte[] data = SerializationUtils.serialize(new ConfigData(ReflectionUtils.getNetworkParams(parameters))); //TODO refactor;
        DatagramPacket ACK = new DatagramPacket(data, data.length, inetAddress.getAddress(), inetAddress.getPort());
        try {
            server_socket.send(ACK);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String id = randomizer.generateId();
        HostListener newListener = new ClientConnection(this, id, inetAddress);
        deltasMap.put(id, null);
        newListener.initFlux();
        hostListeners.add(newListener);
    }


    private class HostingUserUpdater implements Runnable {
        private final User hostingUser;

        public HostingUserUpdater(User user) {
            hostingUser = user;
        }

        @Override
        public synchronized void run() {
            Game hostGame = multiplayerGame.getGame();
            while (hostGame.isRunning()) {
                multiplayerGame.getDeltaManager().collectPlayerDeltas(deltasMap);
                hostDeltas = hostingUser.getHostDeltas(hostId);
                try {
                    wait(5);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public DatagramSocket getServerSocket() {
        return server_socket;
    }

    @Override
    public InetSocketAddress getInetSocketAddress() {
        return inetSocketAddress;
    }

    public boolean updateReady() {
        return !multiplayerGame.isUpdating();
    }

    @Override
    public byte[] getLastDeltas() {
        return SerializationUtils.serialize(hostDeltas);
    }

    @Override
    public void addNewDelta(String clientIp, DeltasData data) {
        deltasMap.put(clientIp, (GameplayDeltas) data);
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
