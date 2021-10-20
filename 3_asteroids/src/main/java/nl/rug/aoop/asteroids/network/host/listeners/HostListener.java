package nl.rug.aoop.asteroids.network.host.listeners;

import nl.rug.aoop.asteroids.network.data.deltas_changes.GameplayDeltas;

public interface HostListener {
    void fireUpdate(byte[] data);
    GameplayDeltas getClientDeltas();
    public boolean checkPingAbuse();
    void initFlux();
    boolean isConnected();
}
