package nl.rug.aoop.asteroids.network.host;

import nl.rug.aoop.asteroids.network.data.ConnectionParameters;
import nl.rug.aoop.asteroids.network.statistics.StatisticCalculator;

import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;

public interface HostingDevice {
    DatagramSocket getServerSocket();
    InetSocketAddress getInetSocketAddress();
    int getHostDefaultLatency();
    int getHostMaxLatency();
    boolean updateReady();
    byte[] getLastDeltas();
    StatisticCalculator getStatisticCalculator();
    ConnectionParameters getRawConnectionParameters();
}
