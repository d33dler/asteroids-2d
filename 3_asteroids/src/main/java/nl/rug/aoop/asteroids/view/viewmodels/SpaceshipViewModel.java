package nl.rug.aoop.asteroids.view.viewmodels;

import nl.rug.aoop.asteroids.model.game.Game;
import nl.rug.aoop.asteroids.model.game.GameResources;
import nl.rug.aoop.asteroids.model.gameobjects.spaceship.Spaceship;
import nl.rug.aoop.asteroids.util.PolarCoordinate;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Path;

/**
 * View model for displaying a spaceship object.
 */
public class SpaceshipViewModel extends GameObjectViewModel<Spaceship> {


    public static BufferedImage exhaust;

    static {
        try {
            exhaust = ImageIO.read(Path.of("images/ship_sprites/exhaust/exhaust.png").toFile());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Constructs a new view model with the given game object.
     *
     * @param gameObject The object that will be displayed when this view model is drawn.
     */
    public SpaceshipViewModel(Spaceship gameObject) {
        super(gameObject);
    }

    /**
     * Draws the game object that was given to this view model.
     *
     * @param graphics2D The graphics object which provides the necessary drawing methods.
     * @param location   The location at which to draw the spaceship.
     */
    @Override
    public void draw(Graphics2D graphics2D, Point.Double location) {
        Spaceship spaceship = getGameObject();
        if (spaceship.isAccelerating()) {
            drawExhaust(spaceship, graphics2D, new Point2D.Double(location.x, location.y));
        }
        drawMainBody(spaceship, graphics2D, location);

    }

    /**
     * Draws the main body of the spaceship as a white triangle.
     *
     * @param spaceship  The spaceship object to draw. While we could retrieve this from getGameObject(), it is a
     *                   little easier to read this way.
     * @param graphics2D The graphics object to use when drawing.
     * @param location   The location at which to draw the spaceship.
     */
    private void drawMainBody(Spaceship spaceship, Graphics2D graphics2D, Point.Double location) {
        int size = Spaceship.SHIP_SIZE;
        double dir = spaceship.getDirection();

        BufferedImage rotated = spaceship.getSprite_img();

        AffineTransform transform = new AffineTransform();
        transform.translate(-15.5, -15.5);
        transform.rotate(dir, location.x + (size / 2), location.y + (size / 2));
        graphics2D.setTransform(transform);
        graphics2D.drawImage(rotated, (int) location.x, (int) location.y, size, size, null);
        graphics2D.setTransform(new AffineTransform());

        Spaceship o = getGameObject();
        graphics2D.setColor(Color.WHITE);
        graphics2D.drawString(o.getNickId(), (int) o.getLocation().x + 20, (int) o.getLocation().y - 10);
    }

    /**
     * Draws the exhaust of the spaceship as a small yellow triangle.
     *
     * @param spaceship  The spaceship whose exhaust to draw.
     * @param graphics2D The graphics object to use when drawing.
     * @param location   The location at which to draw the spaceship.
     */
    private void drawExhaust(Spaceship spaceship, Graphics2D graphics2D, Point.Double location) {
        AffineTransform backup = graphics2D.getTransform();
        AffineTransform trans = new AffineTransform();
        trans.translate(-15.5, -15.5);
        trans.rotate(spaceship.getDirection(), spaceship.getLocation().x + Spaceship.SHIP_SIZE / 2,
                spaceship.getLocation().y + Spaceship.SHIP_SIZE / 2);
        graphics2D.transform(trans);
        graphics2D.drawImage(exhaust, (int) (location.x + 2), (int) (location.y +
                +Spaceship.SHIP_SIZE - 1), 14, 14, null);
        graphics2D.drawImage(exhaust, (int) (location.x + 20), (int) (location.y +
                Spaceship.SHIP_SIZE - 1), 14, 14, null);
        graphics2D.setTransform(backup);

    }

    /**
     * Builds a triangle shape using a starting location, direction, and three polar coordinates that define the corners
     * of the triangle.
     *
     * @param location        The location at which to center the triangle. This can be treated as the origin for the polar
     *                        coordinates.
     * @param facingDirection The direction that the triangle is facing, in radians. This essentially works as an offset
     *                        for the angle of every point on the triangle.
     * @param a               The first coordinate.
     * @param b               The second coordinate.
     * @param c               The third coordinate.
     * @return A path representing the points identified by the three polar coordinates given.
     */
    private Path2D.Double buildTriangle(
            Point.Double location,
            double facingDirection,
            PolarCoordinate a,
            PolarCoordinate b,
            PolarCoordinate c
    ) {
        Path2D.Double path = new Path2D.Double();
        path.moveTo(
                location.getX() + Math.sin(facingDirection + a.getAngle()) * a.getRadius(),
                location.getY() - Math.cos(facingDirection + a.getAngle()) * a.getRadius()
        );
        path.lineTo(
                location.getX() + Math.sin(facingDirection + b.getAngle()) * b.getRadius(),
                location.getY() - Math.cos(facingDirection + b.getAngle()) * b.getRadius()
        );
        path.lineTo(
                location.getX() + Math.sin(facingDirection + c.getAngle()) * c.getRadius(),
                location.getY() - Math.cos(facingDirection + c.getAngle()) * c.getRadius()
        );
        path.closePath();
        return path;
    }
}
