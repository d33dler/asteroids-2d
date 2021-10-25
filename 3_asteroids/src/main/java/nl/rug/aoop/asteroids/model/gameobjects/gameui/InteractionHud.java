package nl.rug.aoop.asteroids.model.gameobjects.gameui;

import nl.rug.aoop.asteroids.model.Game;
import nl.rug.aoop.asteroids.model.gameobjects.GameObject;
import nl.rug.aoop.asteroids.model.gameobjects.asteroid.Asteroid;
import nl.rug.aoop.asteroids.view.viewmodels.GameObjectViewModel;

import java.awt.*;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;

public class InteractionHud  {

    public Game game;
    public final static Color proxLine = new Color(224, 33, 81, 223);

    public InteractionHud(Game game) {
        this.game = game;
    }

    public void drawHud(Graphics g) {
        drawProximityLine(g);
    }

    private void drawProximityLine(Graphics g) {
        Asteroid a = game.closestAsteroid;
        if (a != null && game.proxy ) {
            Point2D.Double p = game.getSpaceShip().getLocation();
            Point2D.Double ap = a.getLocation();
            g.setColor(proxLine);
            g.drawLine((int) p.x, (int) p.y, (int) ap.x, (int) ap.y);
        }
    }
}
