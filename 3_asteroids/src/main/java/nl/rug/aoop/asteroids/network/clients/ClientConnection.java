package nl.rug.aoop.asteroids.network.clients;

import nl.rug.aoop.asteroids.network.data.ConnectionParameters;
import nl.rug.aoop.asteroids.network.data.types.GameData;
import nl.rug.aoop.asteroids.network.host.HostListener;
import nl.rug.aoop.asteroids.network.host.HostingDevice;
import nl.rug.aoop.asteroids.network.protocol.IO;

public class ClientConnection implements HostListener, Runnable {
    private final HostingDevice hostingDevice;
    private final ConnectionParameters parameters;
    private IO io;

    public ClientConnection(HostingDevice host) {
        this.hostingDevice = host;
        this.parameters = host.getConnectionParameters();
        initIO();
    }
    private void initIO(){
        this.io = new IO(hostingDevice.getServerSocket(), parameters.getCallerAddress());
    }

    @Override
    public void run() {
        new Consumer(parameters.LAT_SERVER_millis).run();
    }

    private class Consumer implements Runnable {
        private final int LATENCY;

        public Consumer(int maxLat) {
            this.LATENCY = maxLat;
        }

        @Override
        public void run() {
            listen();
        }

        private void listen() {
            io.receive();
            try {
                wait(LATENCY);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void fireUpdate(byte[] data) {
        io.updateHolder(data);
        io.send();
    }

    public GameData getClientInput() {
        return io.getLastDataPackage().getBody();
    }

    public boolean checkPingAbuse() {
        return io.getLastDataPackage().getLatency() > parameters.LAT_MAX_millis;
    }
}
