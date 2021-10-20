package nl.rug.aoop.asteroids.network.host;

import nl.rug.aoop.asteroids.model.MultiplayerRenderer;
import nl.rug.aoop.asteroids.model.MultiplayerGame;
import nl.rug.aoop.asteroids.network.clients.ClientConnection;
import nl.rug.aoop.asteroids.network.data.ConnectionParameters;
import nl.rug.aoop.asteroids.network.data.PackageHandler;
import nl.rug.aoop.asteroids.network.host.listeners.HostListener;
import nl.rug.aoop.asteroids.network.statistics.ConnectionStatistic;
import nl.rug.aoop.asteroids.network.statistics.StatisticCalculator;

import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HostingServer implements HostingDevice, Runnable {

    private MultiplayerRenderer multiplayerGame;
    private List<SocketAddress> clientConnections = new ArrayList<>();
    private List<HostListener> hostListeners = new ArrayList<>();
    private ConnectionParameters parameters;
    private DatagramSocket server_socket;
    private int server_port;
    private final String host_name;
    private ExecutorService executorService;
    private ConnectionStatistic connectionStatistic = new ConnectionStatistic();

    public HostingServer(MultiplayerGame multiplayer, String host_name) {
        this.multiplayerGame = multiplayer;
        this.parameters = multiplayer.getParameters();
        this.host_name = host_name;
        init();
    }

    private void init() {
        executorService = Executors.newFixedThreadPool(multiplayerGame.getMAX_CLIENTS());
        try {
            server_socket = new DatagramSocket(0, parameters.getInet());
            server_port = server_socket.getPort();
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        acceptConnections();
    }

    private void acceptConnections() {
        while (clientConnections.size() < multiplayerGame.getMAX_CLIENTS()) {
            PackageHandler holder = PackageHandler.newEmptyHolder(parameters);
            byte[] data = holder.getData();
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
        SocketAddress socketAddress = handshake.getSocketAddress();
        clientConnections.add(socketAddress);
        InetSocketAddress inetAddress = new InetSocketAddress(handshake.getAddress(), handshake.getPort());
        HostListener newListener = new ClientConnection(this, inetAddress);
        newListener.initFlux();
        hostListeners.add(newListener);
    }

    @Override
    public DatagramSocket getServerSocket() {
        return server_socket;
    }

    @Override
    public InetSocketAddress getInetSocketAddress() {
        return parameters.getReceptorAddress(); //TODO server must be receptor
    }

    public boolean updateReady() {
       return !multiplayerGame.isUpdating();
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
    public ConnectionParameters getConnectionParameters() {
        return parameters;
    }
}
