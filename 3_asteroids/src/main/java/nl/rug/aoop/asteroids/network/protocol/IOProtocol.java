package nl.rug.aoop.asteroids.network.protocol;

import nl.rug.aoop.asteroids.network.data.PackageHandler;

public interface IOProtocol {
    void send();
    void receive();
    PackageHandler getHolder();
}
