package nl.rug.aoop.asteroids.model;

import nl.rug.aoop.asteroids.model.gameobjects.StandardObjParams;
import nl.rug.aoop.asteroids.network.data.ConnectionParameters;
import nl.rug.aoop.asteroids.network.data.types.DeltaManager;
import nl.rug.aoop.asteroids.network.host.HostingDevice;

import java.util.HashMap;

public interface MultiplayerManager {
    HashMap<String, Double[]> getPlayerVectors();
    int getMAX_CLIENTS();
    DeltaManager getDeltaManager();
    Game getGame();
    HostingDevice getHostingDevice();
    ConnectionParameters getParameters();
    boolean isUpdating();
}
