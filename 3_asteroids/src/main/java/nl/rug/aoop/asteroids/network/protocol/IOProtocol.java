package nl.rug.aoop.asteroids.network.protocol;

import nl.rug.aoop.asteroids.network.data.DataPackage;
import nl.rug.aoop.asteroids.network.data.PackageHandler;
import nl.rug.aoop.asteroids.network.data.types.DeltasData;

public interface IOProtocol {
    void send();
    void send(byte[] data);
    void receive();
    DataPackage getLastDataPackage();
    String getOwnerId();
    void updateOutPackage(DeltasData data);
    void updateOutPackage(byte[] data);
    PackageHandler getPackageHandler();
}
