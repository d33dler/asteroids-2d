package nl.rug.aoop.asteroids.model.gameobjects.gameui;

import nl.rug.aoop.asteroids.model.Game;
import nl.rug.aoop.asteroids.model.gameobjects.asteroid.Asteroid;

import java.awt.*;
import java.awt.geom.Point2D;

public class InteractionHud {

    public Game game;
    public final static Color proxLine = new Color(51, 231, 114, 223),
            proxMedium = new Color(239, 118, 28, 223),
            proxHigh = new Color(211, 14, 29, 255);
    ;

    public InteractionHud(Game game) {
        this.game = game;
    }

    public void drawHud(Graphics g) {
        drawProximityLine(g);
    }

    private void drawProximityLine(Graphics g) {
        Asteroid a = game.closestAsteroid;
        if (a != null && game.proxy) {
            Point2D.Double p = game.getSpaceShip().getLocation();
            Point2D.Double ap = a.getLocation();
            double diff = Math.abs(ap.x - p.x) + Math.abs(ap.y - p.y);
            g.setColor(proxLine);
            g.drawOval((int) p.x, (int) p.y, 7, 7);

            g.setColor(new Color((int) Math.max(Math.min(255, 350 - diff), 0), 0, 0, 255));
            g.drawLine((int) p.x, (int) p.y, (int) ap.x, (int) ap.y);
            g.drawOval((int) ap.x, (int) ap.y, 7, 7);


            g.drawString(Double.toString(Math.round(diff - 50)), (int) ap.x, (int) ap.y);
        }
    }
}
