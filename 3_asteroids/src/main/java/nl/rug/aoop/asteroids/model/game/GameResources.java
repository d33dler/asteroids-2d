package nl.rug.aoop.asteroids.model.game;

import lombok.Getter;
import lombok.Setter;
import nl.rug.aoop.asteroids.control.ViewController;
import nl.rug.aoop.asteroids.control.updaters.GameUpdater;
import nl.rug.aoop.asteroids.gameobserver.GameUpdateListener;
import nl.rug.aoop.asteroids.model.gameobjects.asteroid.Asteroid;
import nl.rug.aoop.asteroids.model.gameobjects.bullet.Bullet;
import nl.rug.aoop.asteroids.model.gameobjects.spaceship.Spaceship;
import nl.rug.aoop.asteroids.model.obj_factory.GameObjectFactory;
import nl.rug.aoop.asteroids.model.obj_factory.GeneralObjectsFactory;
import nl.rug.aoop.asteroids.network.data.deltas_changes.Tuple;
import nl.rug.aoop.asteroids.util.database.DatabaseManager;
import org.jetbrains.annotations.NotNull;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class GameResources {
    List<Tuple.T2<BufferedImage, BufferedImage>> spriteImages;

    public final static String default_OBJ_PKG = "nl.rug.aoop.asteroids.model.gameobjects";
    /**
     * The spaceship object that the player is in control of.
     */
    @Getter
    private Spaceship spaceShip;

    @Getter
    protected final ConcurrentHashMap<String, Spaceship> players = new ConcurrentHashMap<>() {
        @Override
        public synchronized Spaceship remove(@NotNull Object key) {
            game.getListeners().forEach(listener -> listener.playerEliminated((String) key));
            spaceshipCache.remove(key);
            return super.remove(key);
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
    protected HashMap<String, Tuple.T2<HashSet<Integer>, double[]>> spaceshipCache;

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
    private volatile boolean isEngineBusy = true;

    @Setter
    @Getter
    private volatile boolean isUserSerializing = false;

    @Getter
    @Setter
    private volatile boolean isDrawingDone = false;
    @Getter
    @Setter
    protected volatile boolean runProcesses = true;
    /**
     * The game updater thread, which is responsible for updating the game's state as time goes on.
     */
    private Thread gameUpdaterThread;

    @Setter
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

    @Setter
    private ViewController viewController;

    @Getter
    private String USER_ID = "Host";
    @Getter
    protected Asteroid closestAsteroid;
    protected boolean proxy = false;
    public static List<BufferedImage> spriteImgList = new ArrayList<>();

    private final Game game;

    public GameResources(Game game) {
        this.game = game;
        init();
        loadSprites();
    }

    private void init() {
        this.spaceShip = new Spaceship(USER_ID);
        initializeGameData();
    }

    private void loadSprites() {
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
            System.out.println(spriteImgList.size());
        }
    }

    public boolean isGameOver() {
        return spaceShip.isDestroyed();
    }

    public void initGameEngine(boolean online, boolean onlineHost) {
        gameUpdaterThread = new Thread(new GameUpdater(game, viewController, online, onlineHost));
        gameUpdaterThread.start();
    }

    public void addUser(String id) {
        players.put(id, spaceShip);
    }

    public void setDefaultFactory() {
        objectFactory = new GeneralObjectsFactory(game, default_OBJ_PKG);
    }

    public void initializeGameData() {
        bullets = new ArrayList<>();
        bulletCache = new ArrayList<>();
        spaceshipCache = new HashMap<>();
        asteroidsCache = new ArrayList<>();
        asteroids = new ArrayList<>();
        spaceShip.reset();
    }

    public void releaseResources() {
        try {
            // Attempt to wait for the game updater to exit its game loop.
            gameUpdaterThread.join(EXIT_TIMEOUT_MILLIS);
            if (user != null) {
                user.join(EXIT_TIMEOUT_MILLIS);
            }
        } catch (InterruptedException exception) {
            System.err.println("Interrupted while waiting for the game updater thread to finish execution.");
        }
        running = false;
        runProcesses = false;
        gameUpdaterThread = null;
        user = null;
    }

    public void updateUsersId(String USER_ID) {
        players.remove(this.USER_ID, spaceShip);
        this.USER_ID = USER_ID;
        players.put(USER_ID, spaceShip);
        spaceShip.setNickId(USER_ID);
    }
}
