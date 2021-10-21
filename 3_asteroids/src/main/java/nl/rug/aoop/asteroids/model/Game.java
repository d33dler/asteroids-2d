package nl.rug.aoop.asteroids.model;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import lombok.Getter;
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
import nl.rug.aoop.asteroids.util.database.Score;

import java.net.*;
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
    private HashMap<String, Spaceship> players = new HashMap<>();
    /**
     * The list of all bullets currently active in the game.
     */
    @Getter
    private Collection<Bullet> playerBullets;

    @Getter
    private Collection<Bullet> onlineBullets;

    /**
     * The list of all asteroids in the game.
     */
    @Getter
    private Collection<Asteroid> asteroids;


    /**
     * Indicates whether or not the game is running. Setting this to false causes the game to exit its loop and quit.
     */
    private volatile boolean running = false;

    /**
     * Indicates if the game updater is computing the physics tick.
     */
    @Setter
    @Getter
    private volatile boolean isRendererBusy = true;
    /**
     * The game updater thread, which is responsible for updating the game's state as time goes on.
     */
    private Thread gameUpdaterThread;

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

    @Setter
    private ViewController viewController;
    /**
     * Constructs a new game, with a new spaceship and all other model data in its default starting state.
     */
    public Game() {
        spaceShip = new Spaceship();
        initializeGameData();
        try {
            InetAddress a = InetAddress.getByName("asteroidsonline.mooo.com");
            System.out.println(a);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        try {
            DatagramSocket socket = new DatagramSocket();
            System.out.println(socket.getInetAddress());
        } catch (SocketException e) {
            e.printStackTrace();
        }
        InetAddress address = new InetSocketAddress(0).getAddress();
        System.out.println(address);
        //dbManager = new DatabaseManager("prod");
    }

    /**
     * Initializes all the model objects used by the game. Can also be used to reset the game's state back to a
     * default starting state before beginning a new game.
     */
    public void initializeGameData() {
        playerBullets = new ArrayList<>();
        onlineBullets = new ArrayList<>();
        asteroids = new ArrayList<>();
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
        initializeObjMap();
        if (!running) {
            running = true;
            gameUpdaterThread = new Thread(new GameUpdater(this,viewController, online, onlineHost));
            gameUpdaterThread.start();
        }
    }
    public void start(){                    //TODO clean ugly mess
        start(false,false);
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

    public void initMultiplayerAsHost(InetAddress address) { //TODO command pattern
        user = User.newHostUser(this, address);
        objectFactory = new GeneralObjectsFactory(this, default_OBJ_PKG); //TODO move all obj creation to factory?
    }

    public void initMultiplayerAsClient(InetSocketAddress address) {
        user = User.newClientUser(this, address);
    }

    public void initMultiplayerAsSpectator(InetSocketAddress address) {
        user = User.newClientUser(this, address);
    }

    /**
     * This method performs the operations needed to end the game (update view and database)
     */
    public void endGame(){
        notifyEnd();
        //dbManager.addScore(new Score("player", spaceShip.getScore()));
    }

    /**
     * Notifies view to render the endgame panel
     */
    private void notifyEnd(){
        listeners.forEach(GameUpdateListener::onGameEnd);
    }

    /**
     * Tries to quit the game, if it is running.
     */
    public void quit() {
        if (running) {
            try {
                // Attempt to wait for the game updater to exit its game loop.
                gameUpdaterThread.join(EXIT_TIMEOUT_MILLIS);
            } catch (InterruptedException exception) {
                System.err.println("Interrupted while waiting for the game updater thread to finish execution.");
            } finally {
                running = false;
                // Throw away the game updater thread and let the GC remove it.
                gameUpdaterThread = null;
            }
        }
    }
    @Getter
    private final HashMap<String, List<double[]>> objMap = new HashMap<>();

    private void initializeObjMap() {
        objMap.put(Asteroid.OBJECT_ID, new ArrayList<>());
        objMap.put(Bullet.OBJECT_ID, new ArrayList<>());
    }
    public List<GameObject> getAllGameObj(){
        Iterable<GameObject> allObj = Iterables.unmodifiableIterable(Iterables.concat(asteroids, playerBullets, onlineBullets));
        return Lists.newArrayList(allObj);
    }
    public List<Tuple.T2<String, double[]>> getAllPlayers(){
        List<Tuple.T2<String, double[]>> playersList = new ArrayList<>();
        for (Map.Entry<String, Spaceship> player : players.entrySet()) {
            playersList.add(new Tuple.T2<>(player.getKey(),player.getValue().getObjParameters()));
        }
        return playersList;
    }
}
