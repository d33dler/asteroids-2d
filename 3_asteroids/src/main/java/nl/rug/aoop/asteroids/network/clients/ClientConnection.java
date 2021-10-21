package nl.rug.aoop.asteroids.network.clients;

import nl.rug.aoop.asteroids.network.data.ConnectionParameters;
import nl.rug.aoop.asteroids.network.data.types.DeltasData;
import nl.rug.aoop.asteroids.network.host.listeners.HostListener;
import nl.rug.aoop.asteroids.network.host.HostingDevice;
import nl.rug.aoop.asteroids.network.protocol.IO;

import java.net.InetSocketAddress;

public class ClientConnection implements HostListener, Runnable {

    private final HostingDevice hostingDevice;
    private ConnectionParameters parameters;
    private IO io;
    private final static int INTERVAL_ms = 10;
    private final String clientID;

    public ClientConnection(HostingDevice host, String clientID, InetSocketAddress clientAddress) {
        this.clientID = clientID;
        this.hostingDevice = host;
        initParameters(clientAddress);
        initIO();
    }
    private void initParameters(InetSocketAddress clientAddress){
        ConnectionParameters serverParameters = hostingDevice.getRawConnectionParameters();
        this.parameters = new ConnectionParameters(hostingDevice.getServerSocket(), clientAddress, serverParameters.getDataLength());
    }
    private void initIO(){
        this.io = new IO(parameters);
    }

    @Override
    public void run() {
        new Thread(new Consumer(parameters.LAT_SERVER_millis)).start();
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
            while(isConnected()){
                System.out.println("READING FROM USER");
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
        while (isConnected()) {
            if(hostingDevice.updateReady()){
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
        System.out.println("SENDING TO USER");
        io.updateHolder(data);
        io.send();
    }

    public DeltasData getClientDeltas() {
        return io.getLastDataPackage().getData();
    }

    public boolean checkPingAbuse() {
        return io.getLastDataPackage().getLatency() > parameters.LAT_MAX_millis;
    }

    public boolean isConnected() {
        return true; //TODO statistic packet loss % + time threshold
    }
}
