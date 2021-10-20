package nl.rug.aoop.asteroids.model;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.HashMap;

public interface MultiplayerRenderer {
    HashMap<String, Double[]> getPlayerVectors();
    void updatePlayerVectors(String id, Point.Double pos, Point.Double velocity);
    void updateObjectsVectors(String id, Point.Double pos, Point2D.Double velocity);
    int getMAX_CLIENTS();
    boolean isUpdating();
}
