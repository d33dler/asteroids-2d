package nl.rug.aoop.asteroids.network.host;

import nl.rug.aoop.asteroids.model.MultiplayerGame;
import nl.rug.aoop.asteroids.network.data.ConnectionParameters;
import nl.rug.aoop.asteroids.network.data.PackageHolder;

import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.List;

public class HostingServer implements HostingDevice, Runnable {
    private MultiplayerGame multiplayerGame;
    private List<SocketAddress> clientConnections = new ArrayList<>();
    private List<HostListener> hostListeners = new ArrayList<>();
    private ConnectionParameters parameters;
    private DatagramSocket server_socket;
    private int server_port;
    private String host_name;

    public HostingServer(MultiplayerGame multiplayer, String host_name) {
        this.multiplayerGame = multiplayer;
        this.parameters = multiplayer.getParameters();
        init();
    }

    private void init() {
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
            PackageHolder holder = PackageHolder.newEmptyHolder(parameters);
            byte[] data = holder.getData();
            DatagramPacket handshakePacket = new DatagramPacket(data,data.length);
            try {
                server_socket.receive(handshakePacket);
                clientConnections.add(handshakePacket.getSocketAddress());

            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    @Override
    public DatagramSocket getServerSocket() {
        return server_socket;
    }

    @Override
    public InetSocketAddress getInetSocketAddress() {
        return parameters.getReceptorAddress(); //TODO server must be receptor
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
