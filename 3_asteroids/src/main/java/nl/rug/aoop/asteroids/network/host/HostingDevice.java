package nl.rug.aoop.asteroids.network.host;

import nl.rug.aoop.asteroids.network.data.ConnectionParameters;
import nl.rug.aoop.asteroids.network.data.types.DeltasData;
import nl.rug.aoop.asteroids.network.statistics.StatisticCalculator;

import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;

/**
 * HostingDevice interface - implementing by any hosting device which shares data with a pool of
 * clients . A hosting device will extend Runnable to be able to launch on a separate thread.
 */
public interface HostingDevice extends Runnable {
    DatagramSocket getServerSocket();
    int getHostDefaultLatency();
    int getHostMaxLatency();
    boolean updateReady();
    byte[] getLastDeltas();
    void addNewDelta(String clientIp, DeltasData data);
    void notifyDisconnected(String id);
    void notifyEliminated(String id);
    void run();
    void shutdown();
    ConnectionParameters getRawConnectionParameters();
}
