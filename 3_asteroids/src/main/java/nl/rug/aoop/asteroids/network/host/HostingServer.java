package nl.rug.aoop.asteroids.network.host;

import nl.rug.aoop.asteroids.model.MultiplayerManager;
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

    private final MultiplayerManager multiplayerGame;
    private final List<SocketAddress> clientConnections = new ArrayList<>();
    private final List<HostListener> hostListeners = new ArrayList<>();
    private ConnectionParameters parameters;
    private DatagramSocket server_socket;
    private int server_port;
    private final String host_name;
    private ExecutorService executorService;
    private ConnectionStatistic connectionStatistic = new ConnectionStatistic();

    private final InetAddress socketAddress;
    private InetSocketAddress inetSocketAddress;

    public HostingServer(MultiplayerManager multiplayer, InetAddress address) {
        this.multiplayerGame = multiplayer;
        this.socketAddress = address;
        this.host_name = address.getHostName();
        init();
    }

    private void init() {
        executorService = Executors.newFixedThreadPool(multiplayerGame.getMAX_CLIENTS());
        try {
            server_socket = new DatagramSocket(0, socketAddress);
            server_port = server_socket.getPort();
            inetSocketAddress = new InetSocketAddress(socketAddress,server_port);
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
        while (clientConnections.size() < multiplayerGame.getMAX_CLIENTS()) {
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
        return inetSocketAddress;
    }

    public boolean updateReady() {
       return !multiplayerGame.isUpdating();
    }

    @Override
    public byte[] getLastDeltas() {
        return new byte[0];
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
