package nl.rug.aoop.asteroids.network.clients;

import nl.rug.aoop.asteroids.network.data.ConnectionParameters;
import nl.rug.aoop.asteroids.network.data.deltas_changes.GameplayDeltas;
import nl.rug.aoop.asteroids.network.host.listeners.HostListener;
import nl.rug.aoop.asteroids.network.host.HostingDevice;
import nl.rug.aoop.asteroids.network.protocol.IO;

import java.net.InetSocketAddress;

public class ClientConnection implements HostListener, Runnable {

    private final HostingDevice hostingDevice;
    private ConnectionParameters parameters;
    private IO io;
    private final static int INTERVAL_ms = 10;

    public ClientConnection(HostingDevice host, InetSocketAddress clientAddress) {
        this.hostingDevice = host;
        initParameters(clientAddress);
        initIO();
    }
    private void initParameters(InetSocketAddress clientAddress){
        ConnectionParameters serverParameters = hostingDevice.getConnectionParameters();
        this.parameters = new ConnectionParameters(clientAddress,
                hostingDevice.getInetSocketAddress(), serverParameters.getDataLength());
    }
    private void initIO(){
        this.io = new IO(hostingDevice.getServerSocket(), parameters.getCallerAddress());
    }

    @Override
    public void run() {
        new Consumer(parameters.LAT_SERVER_millis).run();
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
            while(true){
                io.receive();
                try {
                    wait(LATENCY_ms);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void initFlux() {
        run();
        while (true) {
            if(hostingDevice.updateReady()){
                io.receive();
                try {
                    wait(INTERVAL_ms);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void fireUpdate(byte[] data) {
        io.updateHolder(data);
        io.send();
    }

    public GameplayDeltas getClientDeltas() {
        return io.getLastDataPackage().getBody();
    }

    public boolean checkPingAbuse() {
        return io.getLastDataPackage().getLatency() > parameters.LAT_MAX_millis;
    }

    public boolean isConnected() {
        return true; //TODO statistic packet loss % + time threshold
    }
}
