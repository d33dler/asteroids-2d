package nl.rug.aoop.asteroids.view.viewmodels.gameui;

import nl.rug.aoop.asteroids.model.game.Game;
import nl.rug.aoop.asteroids.model.game.GameResources;
import nl.rug.aoop.asteroids.model.gameobjects.asteroid.Asteroid;

import java.awt.*;
import java.awt.geom.Point2D;

/**
 * InteractionHud - adds additional asteroid panel graphical effects
 */
public class InteractionHud {

    public Game game;
    private GameResources resources;
    public final static Color proxLine = new Color(255, 255, 0, 223),
            proxMedium = new Color(239, 118, 28, 223),
            proxHigh = new Color(211, 14, 29, 255);
    ;

    public InteractionHud(Game game) {
        this.game = game;
        this.resources = game.getResources();
    }

    public void drawHud(Graphics g) {
        drawProximityLine(g);
    }

    
    private void drawProximityLine(Graphics g) {
        Asteroid a = resources.closestAsteroid;
        if (a != null && !a.isDestroyed() && resources.proxy ) {
            Point2D.Double p = resources.getSpaceShip().getLocation();
            Point2D.Double ap = a.getLocation();
            double diff = Math.abs(ap.x - p.x) + Math.abs(ap.y - p.y);
            g.setColor(proxLine);
            g.drawOval((int) p.x, (int) p.y, 7, 7);

            double diffVal = Math.max(Math.min(255, 370 - diff), 0);

            g.setColor(new Color((int) diffVal, (int) Math.max(Math.min(255, diff), 0), 0, (int) diffVal));
            g.drawLine((int) p.x, (int) p.y, (int) ap.x, (int) ap.y);
            g.drawOval((int) ap.x, (int) ap.y, 7, 7);


            g.drawString(Double.toString(Math.round(diff - 50)), (int) ap.x, (int) ap.y);
        }
    }
}
