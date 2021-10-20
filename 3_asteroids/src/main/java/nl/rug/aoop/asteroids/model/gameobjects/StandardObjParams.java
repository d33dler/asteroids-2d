package nl.rug.aoop.asteroids.model.gameobjects;
import lombok.Builder;
import nl.rug.aoop.asteroids.model.AsteroidSize;

import java.awt.*;

@Builder
public class StandardObjParams {

    public Point.Double pos;
    public Point.Double velocity;
    public double radius;

}
