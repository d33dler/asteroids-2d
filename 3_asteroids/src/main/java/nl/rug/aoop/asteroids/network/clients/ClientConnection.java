package nl.rug.aoop.asteroids.network.clients;

import nl.rug.aoop.asteroids.network.data.ConnectionParameters;
import nl.rug.aoop.asteroids.network.data.types.DeltasData;
import nl.rug.aoop.asteroids.network.host.HostingDevice;
import nl.rug.aoop.asteroids.network.host.listeners.HostListener;
import nl.rug.aoop.asteroids.network.protocol.IO;
import nl.rug.aoop.asteroids.network.protocol.IOProtocol;
import org.apache.commons.lang3.SerializationException;
import org.apache.commons.lang3.SerializationUtils;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.util.Objects;

/**
 * ClientConnection class - acts as server side client communication handler
 * A new instance is created for each new client
 */
public class ClientConnection implements HostListener, Runnable {

    private final HostingDevice hostingDevice;
    private ConnectionParameters parameters;
    private IOProtocol io;
    private final static int INTERVAL_ms = 10;
    private final String clientID;
    private Thread consumerThread;

    private final DatagramSocket privateSocket;

    private boolean connected;
    private boolean spectator = false;

    public ClientConnection(HostingDevice host, DatagramSocket socket, String clientID, InetSocketAddress clientAddress, byte[] msg) {
        this.privateSocket = socket;
        this.hostingDevice = host;
        this.clientID = clientID;
        this.connected = true;
        setUp(clientAddress, msg);
    }

    private void setUp(InetSocketAddress address, byte[] msg) {
        initParameters(address);
        initIO();
        readConnectionRequestType(msg);
    }

    private void readConnectionRequestType(byte[] msg) {
        try {
            String message = SerializationUtils.deserialize(msg);
            spectator = (Objects.equals(message, "spectator"));
        } catch (SerializationException ignored) {}
    }

    private void initParameters(InetSocketAddress clientAddress) {
        this.parameters = new ConnectionParameters(privateSocket, clientAddress, 5000);
    }

    private void initIO() {
        this.io = new IO(parameters);
    }

    @Override
    public void run() {
        if (!spectator) {
            consumerThread = new Thread(new Consumer(1));
            consumerThread.start();
        }
        initFlux();
    }

    /**
     * Consumer inner class that works on a separate thread to read client incoming packets
     */
    private class Consumer implements Runnable {
        private final int LATENCY_ms;

        public Consumer(int maxLat) {
            this.LATENCY_ms = maxLat;
        }

        @Override
        public void run() {
            listen();
        }
        /**
         * Receives packets with a set timeout (which results in termination of comms.
         * Adds the clients received deltas to the hosting device caches
         */
        private synchronized void listen() {
            while (connected) {
                if (io.receive()) {
                    hostingDevice.addNewDelta(clientID, io.getLastDataPackage().getData());
                } else {
                    disconnect();
                }
            }
        }
    }

    /**
     * While the socket is responsive send last state from the host
     * Notice that we set a 10ms interval to allow any close-incoming changes
     * to record into the state and not be ignored
     */
    public synchronized void initFlux() {
        while (connected) {
            if (hostingDevice.updateReady()) {
                fireUpdate(hostingDevice.getLastDeltas());
                try {
                    wait(INTERVAL_ms);
                } catch (InterruptedException ignored) {}
            }
        }
    }

    /**
     *
     * @param id - of client that is not responsive/ is disconnected
     */
    @Override
    public void notifyDisconnected(String id) {
        hostingDevice.notifyDisconnected(id);
    }

    /**
     *
     * @param data current server state of the game in bytes sent to the client
     */
    public void fireUpdate(byte[] data) {
        io.send(data);
    }

    /**
     * Disconnect if the server is closing or the client is not responsive (SO timeout)
     * Kills consumer thread
     */
    public synchronized void disconnect() {
        try {
            connected = false;
            if (consumerThread != null && consumerThread.isAlive()) {
                consumerThread.join(100);
            }
            notifyDisconnected(clientID);
        } catch (InterruptedException ignored) {}
    }

    public DeltasData getClientDeltas() {
        return io.getLastDataPackage().getData();
    }

    public boolean checkPingAbuse() {
        return io.getLastDataPackage().getLatency() > parameters.LAT_MAX_millis;
    }

    public boolean isConnected() {
        return connected; //TODO statistic packet loss % + time threshold
    }
}
