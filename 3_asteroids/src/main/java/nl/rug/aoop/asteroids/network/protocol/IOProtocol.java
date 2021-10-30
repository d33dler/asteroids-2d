package nl.rug.aoop.asteroids.network.protocol;

import nl.rug.aoop.asteroids.network.data.DataPackage;
import nl.rug.aoop.asteroids.network.data.PackageHandler;
import nl.rug.aoop.asteroids.network.data.types.DeltasData;

import java.net.DatagramSocket;

/**
 * Interface used by classes providing IO handling functionality
 */
public interface IOProtocol {
    void send();
    void send(byte[] data);
    boolean receive();
    DataPackage getLastDataPackage();
    String getOwnerId();
    DatagramSocket getSocket();
    void updateOutPackage(DeltasData data);
    void updateOutPackage(byte[] data);
    PackageHandler getPackageHandler();
}
