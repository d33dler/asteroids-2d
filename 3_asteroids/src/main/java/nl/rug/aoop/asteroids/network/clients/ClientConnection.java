package nl.rug.aoop.asteroids.network.clients;

import nl.rug.aoop.asteroids.network.data.ConnectionParameters;
import nl.rug.aoop.asteroids.network.data.types.DeltasData;
import nl.rug.aoop.asteroids.network.host.HostingDevice;
import nl.rug.aoop.asteroids.network.host.listeners.HostListener;
import nl.rug.aoop.asteroids.network.protocol.IO;
import nl.rug.aoop.asteroids.network.protocol.IOProtocol;

import java.net.DatagramSocket;
import java.net.InetSocketAddress;

public class ClientConnection implements HostListener, Runnable {

    private final HostingDevice hostingDevice;
    private ConnectionParameters parameters;
    private IOProtocol io;
    private final static int INTERVAL_ms = 10;
    private final String clientID;
    private Thread consumerThread;

    private final DatagramSocket privateSocket;

    private boolean connected;

    public ClientConnection(HostingDevice host, DatagramSocket socket, String clientID, InetSocketAddress clientAddress) {
        this.privateSocket = socket;
        this.hostingDevice = host;
        this.clientID = clientID;
        this.connected = true;
        initParameters(clientAddress);
        initIO();
    }

    private void initParameters(InetSocketAddress clientAddress) {
        ConnectionParameters serverParameters = hostingDevice.getRawConnectionParameters();
        this.parameters = new ConnectionParameters(privateSocket, clientAddress, 5000);
    }

    private void initIO() {
        this.io = new IO(parameters);
    }

    @Override
    public void run() {
        consumerThread = new Thread(new Consumer(1));
        consumerThread.start();
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
                io.receive();
                hostingDevice.addNewDelta(clientID, io.getLastDataPackage().getData());
                try {
                    wait(LATENCY_ms);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public synchronized void initFlux() {
        while (connected) {
            if (hostingDevice.updateReady()) {
                fireUpdate(hostingDevice.getLastDeltas());
                try {
                    wait(INTERVAL_ms);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void fireUpdate(byte[] data) {
        io.send(data);
    }

    public synchronized void disconnect() {
        try {
            consumerThread.join();
            connected = false;
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
