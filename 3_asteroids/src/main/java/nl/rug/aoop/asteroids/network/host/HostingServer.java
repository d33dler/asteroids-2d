package nl.rug.aoop.asteroids.network.host;

import nl.rug.aoop.asteroids.model.MultiplayerManager;
import nl.rug.aoop.asteroids.network.clients.ClientConnection;
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
    private HashMap<String, GameplayDeltas> deltasMap = new HashMap<>();
    private final int DELAY = 5;

    public HostingServer(MultiplayerManager multiplayer, InetAddress address) {
        this.multiplayerGame = multiplayer;
        this.socketAddress = address;
        this.host_name = address.getHostName();
        init();
    }

    private void logHost(){
        //multiplayerGame.getHost().
    }

    private void init() {
        executorService = Executors.newFixedThreadPool(multiplayerGame.getMAX_CLIENTS());
        try {
            server_socket = new DatagramSocket(0, socketAddress);
            server_port = server_socket.getPort();
            inetSocketAddress = new InetSocketAddress(socketAddress, server_port);
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


    private byte[] createDelta(HashMap<String, GameplayDeltas> deltas, GameplayDeltas hostDeltas){
        List<Tuple.T2<String, double[]>> playerVec = new ArrayList<>();
        for (GameplayDeltas gpDs : deltas.values()) {
            playerVec.add(gpDs.playerVecMap.get(0));
        }
        return SerializationUtils.serialize(new GameplayDeltas(playerVec,hostDeltas.objectVecMap,System.currentTimeMillis()));
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
    public byte[] getLastDeltas(String id) {
        HashMap<String, GameplayDeltas> newDeltaPacket = new HashMap<>(deltasMap);
        newDeltaPacket.remove(id);
        return createDelta(newDeltaPacket, null);// multiplayerGame.getHost().get)
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
