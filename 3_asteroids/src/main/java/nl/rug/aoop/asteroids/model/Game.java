package nl.rug.aoop.asteroids.model;

import com.google.common.collect.Lists;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import nl.rug.aoop.asteroids.control.ViewController;
import nl.rug.aoop.asteroids.control.updaters.GameUpdater;
import nl.rug.aoop.asteroids.gameobserver.GameUpdateListener;
import nl.rug.aoop.asteroids.gameobserver.ObservableGame;
import nl.rug.aoop.asteroids.model.gameobjects.GameObject;
import nl.rug.aoop.asteroids.model.gameobjects.asteroid.Asteroid;
import nl.rug.aoop.asteroids.model.gameobjects.bullet.Bullet;
import nl.rug.aoop.asteroids.model.gameobjects.spaceship.Spaceship;
import nl.rug.aoop.asteroids.model.obj_factory.GameObjectFactory;
import nl.rug.aoop.asteroids.model.obj_factory.GeneralObjectsFactory;
import nl.rug.aoop.asteroids.network.clients.User;
import nl.rug.aoop.asteroids.network.data.deltas_changes.Tuple;
import nl.rug.aoop.asteroids.util.database.DatabaseManager;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.*;

/**
 * This class is the main model for the Asteroids game. It contains all game objects, and has methods to start and stop
 * the game.
 * <p>
 * This is strictly a model class, containing only the state of the game. Updates to the game are done in
 * {@link GameUpdater}, which runs in its own thread, and manages the main game loop and physics updates.
 */
public class Game extends ObservableGame {

    /**
     * The spaceship object that the player is in control of.
     */
    @Getter
    private Spaceship spaceShip;
    @Getter
    private HashMap<String, Spaceship> players;
    /**
     * The list of all bullets currently active in the game.
     */
    @Getter
    private Collection<Bullet> bullets;

    /**
     * The list of all asteroids in the game.
     */
    @Getter
    @Setter
    private List<Asteroid> asteroids;

    @Getter
    @Setter
    private List<Asteroid> asteroidsCache;
    @Getter
    private Collection<Bullet> bulletCache;
    @Getter
    private HashMap<String, Spaceship> spaceshipCache;

    /**
     * Indicates whether or not the game is running. Setting this to false causes the game to exit its loop and quit.
     */
    private volatile boolean running = false;

    /**
     * Indicates if the game updater is computing the physics tick.
     */
    @Setter
    @Getter
    private volatile boolean isEngineBusy = true;

    @Setter
    @Getter
    private volatile boolean isUserSerializing = false;

    /**
     * The game updater thread, which is responsible for updating the game's state as time goes on.
     */
    private Thread gameUpdaterThread;

    private Thread user;

    @Getter
    private GameObjectFactory objectFactory;

    /**
     * Used to manipulate score DataBase
     */
    private DatabaseManager dbManager;

    /**
     * Number of milliseconds to wait for the game updater to exit its game loop.
     */
    private static final int EXIT_TIMEOUT_MILLIS = 100;

    public RendererDeepCloner rendererDeepCloner = new RendererDeepCloner();
    @Setter
    private ViewController viewController;
    @Setter
    @Getter
    private String USER_ID = "neo";


    /**
     * Constructs a new game, with a new spaceship and all other model data in its default starting state.
     */
    public Game() {
        spaceShip = new Spaceship();
        initializeGameData();
        //dbManager = new DatabaseManager("prod");
    }

    /**
     * Initializes all the model objects used by the game. Can also be used to reset the game's state back to a
     * default starting state before beginning a new game.
     */
    public void initializeGameData() {
        bullets = new ArrayList<>();
        bulletCache = new ArrayList<>();
        spaceshipCache = new HashMap<>();
        asteroidsCache = new ArrayList<>();
        asteroids = new ArrayList<>();
        players = new HashMap<>();
        new Thread(rendererDeepCloner).start();
        spaceShip.reset();
    }

    /**
     * @return Whether the game is running.
     */
    public synchronized boolean isRunning() {
        return running;
    }

    /**
     * @return True if the player's ship has been destroyed, or false otherwise.
     */
    public boolean isGameOver() {
        return spaceShip.isDestroyed();
    }

    /**
     * Using this game's current model, spools up a new game updater thread to begin a game loop and start processing
     * user input and physics updates. Only if the game isn't currently running, that is.
     */
    public void start(boolean online, boolean onlineHost) {
        if (!running) {
            running = true;
            gameUpdaterThread = new Thread(new GameUpdater(this, viewController, online, onlineHost));
            gameUpdaterThread.start();
        }
    }

    public void start() {                    //TODO clean ugly mess
        start(false, false);
    }

    public void startOnline(InetSocketAddress address) {
        start(true, false);
        initMultiplayerAsClient(address);
    }

    public void startSpectating(InetSocketAddress address) {
        start(true, false);
        initMultiplayerAsSpectator(address);
    }

    public void startHosting(InetAddress address) {
        start(true, true);
        initMultiplayerAsHost(address);
    }

    public final static String default_OBJ_PKG = "nl.rug.aoop.asteroids.model.gameobjects";

    public void initMultiplayerAsHost(InetAddress address) {
        initDefaultFactory(); //TODO command pattern
        user = new Thread(User.newHostUser(this, address));
        //TODO move all obj creation to factory?
    }

    private void initDefaultFactory() {
        objectFactory = new GeneralObjectsFactory(this, default_OBJ_PKG);
    }

    public void initMultiplayerAsClient(InetSocketAddress address) {
        initDefaultFactory();
        user = new Thread(User.newClientUser(this, address));
    }

    public void initMultiplayerAsSpectator(InetSocketAddress address) {
        initDefaultFactory();
        user = new Thread(User.newClientUser(this, address));
    }

    /**
     * This method performs the operations needed to end the game (update view and database)
     */
    public void endGame() {
        notifyGameOver();
        //dbManager.addScore(new Score("player", spaceShip.getScore()));
    }

    /**
     * Notifies view to render the endgame panel
     */
    private void notifyGameOver() {
        listeners.forEach(GameUpdateListener::onGameOver);
    }

    /**
     * Tries to quit the game, if it is running.
     */
    public void quit() {
        if (running) {

            try {
                // Attempt to wait for the game updater to exit its game loop.
                gameUpdaterThread.join(EXIT_TIMEOUT_MILLIS);
                if (user != null) {
                    user.join(EXIT_TIMEOUT_MILLIS);
                }
            } catch (InterruptedException exception) {
                System.err.println("Interrupted while waiting for the game updater thread to finish execution.");
            } finally {
                listeners.forEach(GameUpdateListener::onGameExit);
                running = false;
                // Throw away the game updater thread and let the GC remove it.
                gameUpdaterThread = null;
                user = null;

            }
        }
    }

    public void resetObjMap() {
        asteroidsCache.clear();
    }


    @NoArgsConstructor
    public class RendererDeepCloner implements Runnable {

        @Getter
        public Collection<GameObject> clonedObjects = new ArrayList<>();
        public boolean cycleDone = false;
        @Override
        public synchronized void run() {
            recloneAll();
        }
        private synchronized void recloneAll() {

            while (true) {
                cycleDone = false;
                clonedObjects = new ArrayList<>();
                reclone(bullets);
                reclone(asteroids);
                reclone(players.values());
                cycleDone = true;
                try {
                    wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        private synchronized void reclone(Collection<? extends GameObject> c) {
            for (GameObject origin : c) {
                clonedObjects.add(origin.clone());
            }
        }
        public synchronized void wakeup() {
            this.notify();
        }

        public synchronized void loadCache() {
            bullets.addAll(bulletCache);
            //asteroids.addAll(asteroidsCache);
            players.putAll(spaceshipCache);
            bulletCache.clear();
            asteroidsCache.clear();
        }
    }

}
