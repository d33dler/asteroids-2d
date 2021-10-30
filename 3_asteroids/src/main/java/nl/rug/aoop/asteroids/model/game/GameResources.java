package nl.rug.aoop.asteroids.model.game;

import lombok.Getter;
import lombok.Setter;
import nl.rug.aoop.asteroids.control.ViewController;
import nl.rug.aoop.asteroids.control.updaters.GameUpdater;
import nl.rug.aoop.asteroids.model.gameobjects.asteroid.Asteroid;
import nl.rug.aoop.asteroids.model.gameobjects.bullet.Bullet;
import nl.rug.aoop.asteroids.model.gameobjects.spaceship.Spaceship;
import nl.rug.aoop.asteroids.model.obj_factory.GameObjectFactory;
import nl.rug.aoop.asteroids.model.obj_factory.GeneralObjectsFactory;
import nl.rug.aoop.asteroids.network.clients.User;
import nl.rug.aoop.asteroids.network.data.deltas_changes.Tuple;
import nl.rug.aoop.asteroids.util.database.DatabaseManager;
import org.jetbrains.annotations.NotNull;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * GameResource - contains all transient memory data regarding game,
 * graphical output & engine states
 * It is used to store, update and terminate acting threads;
 */
public class GameResources {

    @Setter
    @Getter
    private ViewController viewController;

    /**
     * Package patch to game objects factory methods classes
     */
    public final static String default_OBJ_PKG = "nl.rug.aoop.asteroids.model.gameobjects";
    /**
     * The spaceship object that the player is in control of.
     */
    @Getter
    private Spaceship spaceShip;


    /**
     * HashMap of all players active during the multiplayer game
     * We override the remove(Object key) method to add side effects:
     * Notify listeners about the removal
     * Record the ship as destroyed to avoid cache remnants
     * Remove from the current cache
     */
    @Getter
    protected final ConcurrentHashMap<String, Spaceship> players = new ConcurrentHashMap<>() {
        @Override
        public synchronized Spaceship remove(@NotNull Object key) {
            game.getListeners().forEach(listener -> listener.playerEliminated((String) key));
            destroyedShipsCache.add((String) key);
            spaceshipCache.remove(key);
            return super.remove(key);
        }

        /**
         * We override the clear method so that upon resetting the player map during the cache loading
         * @see RendererDeepCloner  loadCache()
         *
         */
        @Override
        public void clear() {
            super.clear();
            this.put(USER_ID,spaceShip);
        }
    };


    /**
     * The list of all bullets currently active in the game.
     */
    @Getter
    protected Collection<Bullet> bullets;

    /**
     * The list of all asteroids in the game.
     */
    @Getter
    @Setter
    protected List<Asteroid> asteroids;

    @Getter
    protected List<Asteroid> asteroidsCache;
    @Getter
    protected Collection<Bullet> bulletCache;
    @Getter
    @Setter
    protected ConcurrentHashMap<String, Tuple.T3<String,HashSet<Integer>, double[]>> spaceshipCache;

    @Getter
    protected HashSet<String> destroyedShipsCache = new HashSet<>();
    /**
     * Indicates whether the game is running. Setting this to false causes the game to exit its loop and quit.
     */
    @Getter
    @Setter
    private volatile boolean running = false;

    /**
     * Indicates if the game updater is computing the physics tick.
     */
    @Setter
    @Getter
    private volatile boolean isEngineBusy = false;


    /**
     * True if user is currently serializing local data before sending it.
     */
    @Setter
    @Getter
    private volatile boolean isUserSerializing = false;

    /**
     * True if asteroidsPanel is currently drawing objects
     */
    @Getter
    @Setter
    private volatile boolean isDrawingDone = false;

    /**
     * True if game processes are still intact
     */
    @Getter
    @Setter
    protected volatile boolean runProcesses = true;
    /**
     * The game updater thread, which is responsible for updating the game's state as time goes on.
     */
    private Thread gameUpdaterThread;

    @Setter
    private User user;

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


    @Getter
    private String USER_ID = "Host";

    public Asteroid closestAsteroid;
    public boolean proxy = false;

    /**
     * List of all buffered sprite images
     */
    @Getter
    private List<BufferedImage> spriteImgList;
    @Getter
    private BufferedImage spectatorImg;
    @Getter
    private final Game game;

    /**
     * This constructor assigns a model to this and initializes game data
     *
     * @param game The model to be assigned
     */
    public GameResources(Game game) {
        this.game = game;
        init();
    }

    /**
     * This method intializes game data and loads the sprites
     */
    private void init() {
        loadSprites();
        this.spaceShip = new Spaceship(USER_ID, this);
        initializeGameData();
    }

    /**
     * Loads sprite textures for the game
     */
    private void loadSprites() {
        if (spriteImgList == null) {
            spriteImgList = new ArrayList<>();
            File folder = new File("images/ship_sprites/");
            File[] spriteFiles = folder.listFiles();
            assert spriteFiles != null;
            for (File file : spriteFiles) {
                if (file.isFile()) {
                    try {
                        BufferedImage img = ImageIO.read(file);
                        spriteImgList.add(img);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            try {
                spectatorImg = ImageIO.read(new File("images/ship_sprites/spectator/spectator.png"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    /**
     * Checks conditions to determine whether a game is over or not
     *
     * @return True if conditions for game over are met, false otherwise
     */
    public boolean isGameOver() {
        return spaceShip.isDestroyed() && !spaceShip.isSpectatorShip();
    }

    /**
     * Initializes and starts the game engine in a new thread
     *
     * @param online Informs the engine whether the game is online
     * @param onlineHost Informs the engine whether this instance is the host
     */
    public void initGameEngine(boolean online, boolean onlineHost) {
        gameUpdaterThread = new Thread(new GameUpdater(game, viewController, online, onlineHost));
        gameUpdaterThread.start();
    }

    /**
     * Adds a new user to the game
     *
     * @param id the ID of the new user
     */
    public void addUser(String id) {
        players.put(id, spaceShip);
    }

    /**
     * This method sets what object factory to be used
     */
    public void setDefaultFactory() {
        objectFactory = new GeneralObjectsFactory(game, default_OBJ_PKG);
    }

    /**
     * This method initializes all the lists needed to keep track of game objects and
     * data
     */
    public void initializeGameData() {
        bullets = new ArrayList<>();
        bulletCache = new ArrayList<>();
        spaceshipCache = new ConcurrentHashMap<>();
        asteroidsCache = new ArrayList<>();
        asteroids = new ArrayList<>();
        spaceShip.reset();
    }

    /**
     * Upon exiting, terminate all processes and joint threads
     */
    public void releaseResources() {
        try {
            // Attempt to wait for the game updater to exit its game loop.
            gameUpdaterThread.join(EXIT_TIMEOUT_MILLIS);
        } catch (InterruptedException exception) {
            System.err.println("Interrupted while waiting for the game updater thread to finish execution.");
        }
        running = false;
        runProcesses = false;
        gameUpdaterThread = null;
        user = null;
    }

    /**
     * Bellow are setters with side effects, changing players id will change it's mapping
     * in the hash map of player pool mappings;
     * @param USER_ID new user id
     */
    public void updateUsersId(String USER_ID) {
        players.remove(this.USER_ID, spaceShip);
        this.USER_ID = USER_ID;
        players.put(USER_ID, spaceShip);
    }
    public void updateUserNick(String USER_NICK){
        spaceShip.setNickId(USER_NICK);
    }
}
