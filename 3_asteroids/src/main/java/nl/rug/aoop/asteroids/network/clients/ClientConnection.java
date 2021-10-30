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
            System.out.println("USERTYPE: " + message);
        } catch (SerializationException ignored) {
        }
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

    private class Consumer implements Runnable {
        private final int LATENCY_ms;

        public Consumer(int maxLat) {
            this.LATENCY_ms = maxLat;
        }

        @Override
        public void run() {
            listen();
        }

        private synchronized void listen() {
            while (connected) {
                boolean success = io.receive();
                if (success) {
                    hostingDevice.addNewDelta(clientID, io.getLastDataPackage().getData());
                } else {
                    disconnect();
                }
            }
        }
    }

    public synchronized void initFlux() {
        while (connected) {
            if (hostingDevice.updateReady()) {
                fireUpdate(hostingDevice.getLastDeltas());
                try {
                    wait(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void notifyDisconnected(String id) {
        hostingDevice.notifyDisconnected(id);
    }

    public void fireUpdate(byte[] data) {
        io.send(data);
    }

    public synchronized void disconnect() {
        try {
            connected = false;
            if (consumerThread != null && consumerThread.isAlive()) {
                consumerThread.join(100);
            }
            notifyDisconnected(clientID);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
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
