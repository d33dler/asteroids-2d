package nl.rug.aoop.asteroids.model.game;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import nl.rug.aoop.asteroids.control.UserKeyListener;
import nl.rug.aoop.asteroids.control.ViewController;
import nl.rug.aoop.asteroids.control.updaters.GameUpdater;
import nl.rug.aoop.asteroids.gameobserver.GameUpdateListener;
import nl.rug.aoop.asteroids.gameobserver.ObservableGame;
import nl.rug.aoop.asteroids.model.gameobjects.GameObject;
import nl.rug.aoop.asteroids.model.gameobjects.asteroid.Asteroid;
import nl.rug.aoop.asteroids.model.gameobjects.spaceship.Spaceship;
import nl.rug.aoop.asteroids.network.clients.User;
import nl.rug.aoop.asteroids.network.data.deltas_changes.Tuple;
import nl.rug.aoop.asteroids.util.database.DatabaseManager;
import nl.rug.aoop.asteroids.util.database.Score;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.List;
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
     * Used to manipulate score DataBase
     */
    private DatabaseManager dbManager;

    public RendererDeepCloner rendererDeepCloner;
    public ObjectDeltaMapper objectDeltaMapper;
    private Thread rendererDeepClonerThread, objectDeltaMapperThread;

    @Setter
    @Getter
    private ViewController viewController;

    @Getter
    private String USER_ID = "Host";
    @Setter
    private String USER_NICK = "unknown";
    @Getter
    private final GameResources resources;

    /**
     * Constructs a new game, with a new spaceship and all other model data in its default starting state.
     */

    public Game() {
        this.resources = new GameResources(this);
        rendererDeepCloner = new RendererDeepCloner(resources);
        objectDeltaMapper = new ObjectDeltaMapper(resources);
        dbManager = DatabaseManager.getInstance();
    }


    /**
     * Initializes all the model objects used by the game. Can also be used to reset the game's state back to a
     * default starting state before beginning a new game.
     */
    public void initializeGameThreads() {
        rendererDeepClonerThread = new Thread(rendererDeepCloner);
        objectDeltaMapperThread = new Thread(objectDeltaMapper);
        rendererDeepClonerThread.start();
        objectDeltaMapperThread.start();
    }

    /**
     * @return Whether the game is running.
     */
    public synchronized boolean isRunning() {
        return resources.isRunning();
    }

    /**
     * @return True if the player's ship has been destroyed, or false otherwise.
     */
    public boolean isGameOver() {
        return resources.isGameOver();
    }

    /**
     * Using this game's current model, spools up a new game updater thread to begin a game loop and start processing
     * user input and physics updates. Only if the game isn't currently running, that is.
     */
    public void start(boolean online, boolean onlineHost) {
        resources.addUser(USER_ID);
        if (!resources.isRunning()) {
            resources.setRunning(true);
            resources.initGameEngine(online, onlineHost);
        }
    }

    public void start() {
        start(false, false);
    }

    /**
     * Initiates the object factory
     * Initiates a new user that acts as host
     *
     * @param address specified hosting address
     */
    public void initMultiplayerAsHost(InetAddress address) {
        getObjFactory();
        resources.setUser(User.newHostUser(this, address));
    }

    /**
     * Initiates the object factory
     * Initiates a new user that acts as client
     *
     * @param address specified host's address
     */
    public void initMultiplayerAsClient(InetSocketAddress address) {
        getObjFactory();
        resources.setUser(User.newClientUser(this, address));
    }

    /**
     * Factory class used for creation of objects
     * generated in multiplayer environment
     */
    private void getObjFactory() {
        resources.setDefaultFactory();
    }

    /**
     * Initiates the object factory
     * Sets the user's spaceship to spectator settings
     * Updates view controller key listener to a new key listener class mapping
     * Initiates a new user that acts as spectator
     *
     * @param address pecified host's address
     */
    public void initMultiplayerAsSpectator(InetSocketAddress address) {
        getObjFactory();
        resources.getSpaceShip().updateAsSpectator();
        viewController.getFrame().changeKeyListener(new
                UserKeyListener(resources.getSpaceShip(), this, viewController));
        resources.setUser(User.newSpectatorUser(this, address));
    }

    /**
     * This method performs the operations needed to end the game (update view and database)
     */

    private boolean notifyEnd = false;

    /**
     * Verifies if the spaceship is destroyed, or client is disconnected from server;
     */
    public void checkEndGame() {
        if ((isGameOver() || !resources.isRunProcesses()) && !notifyEnd) {
            notifyEnd = true;
            updateDatabaseScore();
            notifyGameOver();
        }
    }

    /**
     * Updates database score set, if user wasn't a spectator.
     */
    private void updateDatabaseScore() {
        if (!resources.getSpaceShip().isSpectatorShip())
            dbManager.addScore(new Score(USER_NICK, resources.getSpaceShip().getScore()));

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
        if (resources.isRunning()) {
            resources.releaseResources();
            listeners.forEach(GameUpdateListener::onGameExit);
            if (objectDeltaMapperThread != null) {
                try {
                    objectDeltaMapperThread.join(100);
                    rendererDeepClonerThread.join(100);
                } catch (InterruptedException ignored) {}
            }
        }
    }

    /**
     * Kills all processes upon disconnection from server notification
     */
    public void updateGameOverDisconnection() {
        resources.runProcesses = false;
    }

    /**
     * @param id users id to be removed from the game's local pool of players
     */
    public void requestPlayerRemoval(String id) {
        resources.players.remove(id);
    }

    /**
     * Used to avoid concurrent modification exceptions
     *
     * @return true if engine is busy computing physics
     */
    public boolean isEngineBusy() {
        return resources.isEngineBusy();
    }

    /**
     * @return local user's spaceship
     */
    public Spaceship getUserSpaceship() {
        return resources.getSpaceShip();
    }

    /**
     * Below are user's id and nickname updates that include side effects:
     * Calling GameResources to include additional side effects upon property changes.
     */
    public void setUSER_ID(String USER_ID) {
        this.USER_ID = USER_ID;
        resources.updateUsersId(USER_ID);
    }

    /**
     * Updates User NickName
     *
     * @param USER_NICKNAME New User nickname
     */
    public void updateUSER_NICK(String USER_NICKNAME) {
        this.USER_NICK = USER_NICKNAME;
        resources.updateUserNick(USER_NICKNAME);
    }
}
