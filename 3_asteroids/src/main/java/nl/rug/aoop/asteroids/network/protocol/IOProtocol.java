package nl.rug.aoop.asteroids.network.protocol;

import nl.rug.aoop.asteroids.network.data.PackageHolder;

public interface IOProtocol {
    void send();
    void receive();
    PackageHolder getHolder();
}
