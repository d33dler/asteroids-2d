package nl.rug.aoop.asteroids.model;

import lombok.Getter;
import nl.rug.aoop.asteroids.network.clients.User;
import nl.rug.aoop.asteroids.network.data.ConnectionParameters;
import nl.rug.aoop.asteroids.network.data.PackageHandler;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.HashMap;

public class MultiplayerGame implements MultiplayerRenderer {
    @Getter
    private ConnectionParameters parameters;
    private PackageHandler packageHandler;
    @Getter
    private final int MAX_CLIENTS = 10;
    private final User user;

    public MultiplayerGame(User user) {
        this.user = user;
    }

    @Override
    public HashMap<String, Double[]> getPlayerVectors() {
        return null;
    }

    @Override
    public void updateObjectsVectors(String id, Point.Double pos, Point2D.Double velocity) {

    }

    @Override
    public void updatePlayerVectors(String id, Point.Double pos, Point.Double velocity) {

    }

    @Override
    public boolean isUpdating() {
        return false;
    }
}
