package nl.rug.aoop.asteroids.control.updaters;

import nl.rug.aoop.asteroids.control.ViewController;
import nl.rug.aoop.asteroids.model.AsteroidSize;
import nl.rug.aoop.asteroids.model.game.Game;
import nl.rug.aoop.asteroids.model.game.GameResources;
import nl.rug.aoop.asteroids.model.gameobjects.GameObject;
import nl.rug.aoop.asteroids.model.gameobjects.asteroid.Asteroid;
import nl.rug.aoop.asteroids.model.gameobjects.bullet.Bullet;
import nl.rug.aoop.asteroids.model.gameobjects.spaceship.Spaceship;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;


/**
 * A runnable object which, when started in a thread, runs the main game loop and periodically updates the game's model
 * as time goes on. This class can be thought of as the 'Game Engine', because it is solely responsible for all changes
 * to the game model as a result of user input, and this class also defines the very important game loop itself.
 */
public class GameUpdater implements Runnable {
    /**
     * The refresh rate of the display, in frames per second. Increasing this number makes the game look smoother, up to
     * a certain point where it's no longer noticeable.
     */
    private static final int DISPLAY_FPS = 120;

    /**
     * The rate at which the game ticks (how often physics updates are applied), in frames per second. Increasing this
     * number speeds up everything in the game. Ships react faster to input, bullets fly faster, etc.
     */
    private static final int PHYSICS_FPS = 30;

    /**
     * The number of milliseconds in a game tick.
     */
    public static final double MILLISECONDS_PER_TICK = 1000.0 / PHYSICS_FPS;

    /**
     * The default maximum number of asteroids that may be present in the game when starting.
     */
    private static final int ASTEROIDS_LIMIT_DEFAULT = 7;

    /**
     * Set this to true to allow asteroids to collide with each other, potentially causing chain reactions of asteroid
     * collisions.
     */
    private static final boolean KESSLER_SYNDROME = false;

    /**
     * The number of ticks between asteroid spawns
     */
    private static final int ASTEROID_SPAWN_RATE = 200; // -> 200

    /**
     * The game that this updater works for.
     */
    private final Game game;

    /**
     * Counts the number of times the game has updated.
     */
    private int updateCounter;

    /**
     * The limit to the number of asteroids that may be present. If the current number of asteroids exceeds this amount,
     * no new asteroids will spawn.
     */
    private int asteroidsLimit;
    private final ViewController viewController;
    private boolean isOnlineHost;
    private boolean online;

    public static double diff = 300;
    public double diffNow = diff;

    private final GameResources resources;
    /**
     * Constructs a new game updater with the given game.
     *
     * @param game The game that this updater will update when it's running.
     */
    public GameUpdater(Game game, ViewController viewController, boolean online, boolean onlineHost) {
        this.isOnlineHost = onlineHost;
        this.online = online;
        this.game = game;
        this.resources = game.getResources();
        this.viewController = viewController;
        updateCounter = 0;
        asteroidsLimit = ASTEROIDS_LIMIT_DEFAULT;
    }


    /**
     * The main game loop.
     * <p>
     * Starts the game updater thread. This will run until the quit() method is called on this updater's game object.
     */
    @Override
    public void run() {
        long previousTime = System.currentTimeMillis();
        long timeSinceLastTick = 0L;
        long timeSinceLastDisplayFrame = 0L;

        final double millisecondsPerDisplayFrame = 1000.0 / DISPLAY_FPS;

        while (game.isRunning()) {
            long currentTime = System.currentTimeMillis();
            long elapsedTime = currentTime - previousTime;
            timeSinceLastTick += elapsedTime;
            timeSinceLastDisplayFrame += elapsedTime;

            if (timeSinceLastTick >= MILLISECONDS_PER_TICK) {// Check if enough time has passed to update the physics.
                while (true) {
                    if (resources.isDrawingDone()) {
                        resources.setEngineBusy(true);
                        updatePhysics();
                        resources.setEngineBusy(false);
                        break;
                    }
                }
                timeSinceLastTick = 0L;
                game.checkEndGame();
            }

            if (timeSinceLastDisplayFrame >= millisecondsPerDisplayFrame) { // Check if enough time has passed to refresh the display.
                game.notifyListeners(timeSinceLastTick); // Tell the asteroids panel that it should refresh.
                timeSinceLastDisplayFrame = 0L;
            }
            previousTime = currentTime;
        }
    }

    /**
     * Called every game tick, to update all of the game's model objects.
     * <p>
     * First, each object's movement is updated by calling nextStep() on it.
     * Then, if the player is pressing the key to fire the ship's weapon, a new bullet should spawn.
     * Then, once all objects' positions are updated, we check for any collisions between them.
     * And finally, any objects which are destroyed by collisions are removed from the game.
     * <p>
     * Also, every 200 game ticks, if possible, a new random asteroid is added to the game.
     */
    private void updatePhysics() {
        game.rendererDeepCloner.loadCache();
        Collection<Bullet> bullets = resources.getBullets();
        Collection<Asteroid> asteroids = resources.getAsteroids();
        Collection<Spaceship> players = resources.getPlayers().values();
        players.forEach(Spaceship::nextStep);
        asteroids.forEach(GameObject::nextStep);
        bullets.forEach(GameObject::nextStep);

        generateBullet(players, bullets);
        checkCollisions();
        removeDestroyedObjects();

        // Every 200 game ticks, try and spawn a new asteroid.
        if ((isOnlineHost || !online) && updateCounter % ASTEROID_SPAWN_RATE == 0 && asteroids.size() < asteroidsLimit) {
            addRandomAsteroid();

        }
        updateCounter++;
        game.objectDeltaMapper.wakeup();
        game.rendererDeepCloner.wakeup();
    }

    private void generateBullet(Collection<Spaceship> ships, Collection<Bullet> bullets) {
        ships.forEach(ship -> {
            if (ship.canFireWeapon()) {
                double direction = ship.getDirection();
                Bullet b = new Bullet(ship,
                        ship.getLocation().getX(),
                        ship.getLocation().getY(),
                        ship.getVelocity().x + Math.sin(direction) * 15,
                        ship.getVelocity().y - Math.cos(direction) * 15
                );
                bullets.add(b);
                ship.setFired();
            }
        });
    }

    /**
     * Adds a random asteroid at least 50 pixels away from the player's spaceship.
     */
    private void addRandomAsteroid() {
        ThreadLocalRandom rng = ThreadLocalRandom.current();
        Point.Double newAsteroidLocation;
        Point.Double shipLocation = resources.getSpaceShip().getLocation();
        double distanceX, distanceY;
        do { // Iterate until a point is found that is far enough away from the player.
            newAsteroidLocation = new Point.Double(rng.nextDouble(0.0, 800.0), rng.nextDouble(0.0, 800.0));
            distanceX = newAsteroidLocation.x - shipLocation.x;
            distanceY = newAsteroidLocation.y - shipLocation.y;
        } while (distanceX * distanceX + distanceY * distanceY < 50 * 50); // Pythagorean theorem for distance between two points.

        double randomChance = rng.nextDouble();
        Point.Double randomVelocity = new Point.Double(rng.nextDouble() * 6 - 3, rng.nextDouble() * 6 - 3);
        AsteroidSize randomSize;
        if (randomChance < 0.333) { // 33% chance of spawning a large asteroid.
            randomSize = AsteroidSize.LARGE;
        } else if (randomChance < 0.666) { // 33% chance of spawning a medium asteroid.
            randomSize = AsteroidSize.MEDIUM;
        } else { // And finally a 33% chance of spawning a small asteroid.
            randomSize = AsteroidSize.SMALL;
        }
        Asteroid a = new Asteroid(newAsteroidLocation, randomVelocity, randomSize);
        resources.getAsteroidsCache().add(a);
    }

    /**
     * Checks all objects for collisions and marks them as destroyed upon collision. All objects can collide with
     * objects of a different type, but not with objects of the same type. I.e. bullets cannot collide with bullets etc.
     */
    private void checkCollisions() {
        // First check collisions between bullets and other objects.
        diffNow = diff;
        Collection<Spaceship> players = resources.getPlayers().values();


        resources.getBullets().forEach(bullet -> {
            resources.getAsteroids().forEach(asteroid -> { // Check collision with any of the asteroids.
                if (asteroid.collides(bullet)) {
                    asteroid.destroy();
                    bullet.destroy();
                    bullet.getOwner().increaseScore();
                    updateAsteroidLimit();
                }
            });
            players.forEach(ship -> {
                if (ship.collides(bullet)) {
                    bullet.destroy();
                    ship.destroy();
                }
            }); // Check collision with ship.

        });
        // Next check for collisions between asteroids and the spaceship.
        double x = resources.getSpaceShip().getLocation().x;
        double y = resources.getSpaceShip().getLocation().y;

        resources.getAsteroids().forEach(asteroid -> {
            // Added new interface feature
            recheckProxyAsteroids(asteroid, x, y);
            players.forEach(ship -> {
                if (ship.collides(asteroid)) {
                    asteroid.destroy();
                    ship.destroy();
                }
            });
            if (KESSLER_SYNDROME) { // Only check for asteroid - asteroid collisions if we allow kessler syndrome.
                resources.getAsteroids().forEach(secondAsteroid -> {
                    if (!asteroid.equals(secondAsteroid) && asteroid.collides(secondAsteroid)) {
                        asteroid.destroy();
                        secondAsteroid.destroy();
                    }
                });
            }
        });
        resources.proxy = (recheckProxyAsteroids(resources.closestAsteroid, x, y) < diff);
    }

    /**
     *
     * @param a active asteroid
     * @param x - ship coordinate
     * @param y - ship coordinate
     * @return distance to the closest asteroid from the user's spaceship
     */
    public double recheckProxyAsteroids(Asteroid a, double x, double y) {
        if (a != null) {
            Point.Double p = a.getLocation();
            double diff = Math.abs(p.x - x) + Math.abs(p.y - y);
            if (diff < diffNow) {
                resources.closestAsteroid = a;
                diffNow = diff;
            }
            return diff;
        }
        return diff;
    }


    /**
     * For every five score points, the asteroids limit is incremented.
     */
    private void updateAsteroidLimit() {
        if (resources.getSpaceShip().getScore() % 5 == 0) {
            asteroidsLimit++;
        }
    }

    /**
     * Removes all destroyed objects (those which have collided with another object).
     * <p>
     * When an asteroid is destroyed, it may spawn some smaller successor asteroids, and these are added to the game's
     * list of asteroids.
     */
    private void removeDestroyedObjects() {
        // Avoid reallocation and assume every asteroid spawns successors.
        Collection<Asteroid> newAsteroids = new ArrayList<>(resources.getAsteroids().size() * 2);
        resources.getAsteroids().forEach(asteroid -> {
            if (asteroid.isDestroyed()) {
                newAsteroids.addAll(asteroid.getSuccessors());
            }
        });
        resources.getAsteroids().addAll(newAsteroids);
        // Remove all asteroids that are destroyed.
        resources.getAsteroids().removeIf(GameObject::isDestroyed);
        // Remove any bullets that are destroyed.
        resources.getBullets().removeIf(GameObject::isDestroyed);

        ConcurrentHashMap<String, Spaceship> playersMap = resources.getPlayers();
        playersMap.forEach(( s, spaceship) -> {
            if (spaceship.isDestroyed() || resources.getDestroyedShipsCache().contains(s)) {
                playersMap.remove(s);
            }
        });
    }
}
