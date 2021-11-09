package nl.rug.aoop.asteroids.view.viewmodels;

import nl.rug.aoop.asteroids.model.gameobjects.bullet.Bullet;

import java.awt.*;
import java.awt.geom.Ellipse2D;

/**
 * View model for displaying bullet objects.
 */
public class BulletViewModel extends GameObjectViewModel<Bullet> {
    /**
     * Constructs the view model.
     *
     * @param gameObject The bullet to be displayed.
     */
    public BulletViewModel(Bullet gameObject) {
        super(gameObject);
    }

    /**
     * Draws the bullet that was given to this view model.
     *
     * @param graphics2D The graphics object which provides the necessary drawing methods.
     * @param location   The location at which to draw the object.
     */
    @Override
    public void draw(Graphics2D graphics2D, Point.Double location) {
        Ellipse2D.Double bulletEllipse = new Ellipse2D.Double(
                location.getX() - Bullet.BULLET_RADIUS / 2.0,
                location.getY() - Bullet.BULLET_RADIUS / 2.0,
                Bullet.BULLET_RADIUS,
                Bullet.BULLET_RADIUS
        );
        graphics2D.setColor(Color.ORANGE);
        graphics2D.fill(bulletEllipse);
        graphics2D.setColor(Color.BLUE);
        graphics2D.draw(bulletEllipse);
    }
}
