package nl.rug.aoop.asteroids.network.host.listeners;

import nl.rug.aoop.asteroids.network.data.types.DeltasData;

public interface HostListener {
    void fireUpdate(byte[] data);
    DeltasData getClientDeltas();
    public boolean checkPingAbuse();
    void initFlux();
    boolean isConnected();
}
