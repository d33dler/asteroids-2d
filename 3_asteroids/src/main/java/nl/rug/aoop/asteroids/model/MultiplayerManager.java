package nl.rug.aoop.asteroids.model;

import nl.rug.aoop.asteroids.model.game.Game;
import nl.rug.aoop.asteroids.network.clients.User;
import nl.rug.aoop.asteroids.network.data.types.DeltaManager;
import nl.rug.aoop.asteroids.network.host.HostingDevice;

import java.util.HashMap;

public interface MultiplayerManager {
    HashMap<String, Double[]> getPlayerVectors();
    User getHost();
    int getMAX_CLIENTS();
    DeltaManager getDeltaManager();
    Game getGame();
    void notifyDisconnect();
    HostingDevice getHostingDevice();
    boolean isUpdating();
}
