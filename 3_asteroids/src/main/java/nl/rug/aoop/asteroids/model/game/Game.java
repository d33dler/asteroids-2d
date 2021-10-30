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
import java.awt.image.BufferedImage;
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

    /**
     * Number of milliseconds to wait for the game updater to exit its game loop.
     */
    private static final int EXIT_TIMEOUT_MILLIS = 100;

    public RendererDeepCloner rendererDeepCloner = new RendererDeepCloner();
    public ObjectDeltaMapper objectDeltaMapper = new ObjectDeltaMapper();
    @Setter
    private ViewController viewController;

    @Getter
    private String USER_ID = "Host";
    @Setter
    private String nickname;

    public Asteroid closestAsteroid;
    public boolean proxy = false;

    @Getter
    private final GameResources resources;

    /**
     * Constructs a new game, with a new spaceship and all other model data in its default starting state.
     */

    public Game() {
        this.resources = new GameResources(this);
        dbManager = DatabaseManager.getInstance();
    }


    /**
     * Initializes all the model objects used by the game. Can also be used to reset the game's state back to a
     * default starting state before beginning a new game.
     */
    public void initializeGameThreads() {
        new Thread(rendererDeepCloner).start();
        new Thread(objectDeltaMapper).start();
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

    public void start() {                    //TODO clean ugly mess
        start(false, false);
    }

    public void startOnline(InetSocketAddress address) {
        start(true, false);
        initializeGameThreads();
        initMultiplayerAsClient(address);
    }

    public void startSpectating(InetSocketAddress address) {
        start(true, false);
        initializeGameThreads();
        initMultiplayerAsSpectator(address);
    }

    public void startHosting(InetAddress address) {
        start(true, true);
        initializeGameThreads();
        initMultiplayerAsHost(address);
    }

    public void initMultiplayerAsHost(InetAddress address) {
        getObjFactory();
        resources.setUser(User.newHostUser(this, address));
    }

    private void getObjFactory() {
        resources.setDefaultFactory();
    }

    public void initMultiplayerAsClient(InetSocketAddress address) {
        getObjFactory();
        resources.setUser(User.newClientUser(this, address));
    }

    public void initMultiplayerAsSpectator(InetSocketAddress address) {
        getObjFactory();
        resources.getSpaceShip().updateAsSpectator();
        viewController.getFrame().changeKeyListener(new
                UserKeyListener(resources.getSpaceShip(),this,viewController));
        resources.setUser(User.newSpectatorUser(this, address));
    }

    /**
     * This method performs the operations needed to end the game (update view and database)
     */

    private boolean notifyEnd = false;

    public void checkEndGame() {
        if ((isGameOver() || !resources.isRunProcesses()) && !notifyEnd) {
            notifyEnd = true;
            dbManager.addScore(new Score(nickname, resources.getSpaceShip().getScore()));
            notifyGameOver();
        }
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
        }
    }


    @NoArgsConstructor
    public class RendererDeepCloner implements Runnable {

        @Getter
        public Collection<GameObject> clonedObjects = new ArrayList<>();
        public volatile boolean cycleDone = true;

        @Override
        public synchronized void run() {
            recloneAll();
        }

        private synchronized void recloneAll() {

            while (resources.isRunProcesses()) {
                cycleDone = false;
                clonedObjects.clear();
                reclone(resources.bullets);
                reclone(resources.asteroids);
                reclone(resources.players.values());
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

            for (Map.Entry<String, Tuple.T2<HashSet<Integer>, double[]>> entry : resources.spaceshipCache.entrySet()) {
                String s = entry.getKey();
                Tuple.T2<HashSet<Integer>, double[]> keySet = entry.getValue();
                if (resources.players.containsKey(s)) {
                    Spaceship ship = resources.players.get(s);
                    ship.setKeyEventSet(keySet.a);
                    ship.updateParameters(keySet.b);
                } else {
                    resources.players.put(s, new Spaceship(s, resources));
                    System.out.println("Added new player");
                }
            }
            resources.asteroids.addAll(resources.asteroidsCache);
            resources.asteroidsCache.clear();
            resources.spaceshipCache.clear();
        }

    }

    public class ObjectDeltaMapper implements Runnable {
        public List<Tuple.T2<String, List<double[]>>> mappedObjects = new ArrayList<>();
        public volatile boolean cycleDone = true;

        @Override
        public void run() {
            remapAll();
        }

        private synchronized void remapAll() {
            while (resources.runProcesses) {
                cycleDone = false;
                mappedObjects.clear();
                remap(Asteroid.OBJECT_ID, resources.asteroids);
                cycleDone = true;
                try {
                    wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        private void remap(String objId, Collection<? extends GameObject> collection) {
            List<double[]> mappedCollection = new ArrayList<>();
            for (GameObject object : collection) {
                mappedCollection.add(object.getObjParameters());
            }
            mappedObjects.add(new Tuple.T2<>(objId, mappedCollection));
        }

        public synchronized void wakeup() {
            this.notify();
        }
    }

    public void reportHostPort(int port) {
        JOptionPane.showMessageDialog(viewController.getFrame(), "Assigned port was copied to clipboard!", //TODO refactor garbage
                "ASTEROIDS HOSTING", JOptionPane.INFORMATION_MESSAGE);
        StringSelection stringSelection = new StringSelection(Integer.toString(port));
        Clipboard clpbrd = Toolkit.getDefaultToolkit().getSystemClipboard();
        clpbrd.setContents(stringSelection, null);
    }

    public void updateGameOverDisconnection() {
        JOptionPane.showMessageDialog(viewController.getFrame(), "Host has terminated the connection");
        resources.runProcesses = false;
    }

    public void requestPlayerRemoval(String id) {
        resources.players.remove(id);
    }

    public boolean isEngineBusy() {
        return resources.isEngineBusy();
    }

    public Spaceship getUserSpaceship() {
        return resources.getSpaceShip();
    }

    public void setUSER_ID(String USER_ID) {
        this.USER_ID = USER_ID;
        resources.updateUsersId(USER_ID);
    }
}
