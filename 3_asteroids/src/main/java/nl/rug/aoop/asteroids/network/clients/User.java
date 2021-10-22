package nl.rug.aoop.asteroids.network.clients;


import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.java.Log;
import nl.rug.aoop.asteroids.gameobserver.GameUpdateListener;
import nl.rug.aoop.asteroids.model.Game;
import nl.rug.aoop.asteroids.model.MultiplayerGame;
import nl.rug.aoop.asteroids.model.MultiplayerManager;
import nl.rug.aoop.asteroids.model.gameobjects.GameObject;
import nl.rug.aoop.asteroids.network.data.ConnectionParameters;
import nl.rug.aoop.asteroids.network.data.PackageHandler;
import nl.rug.aoop.asteroids.network.data.deltas_changes.GameplayDeltas;
import nl.rug.aoop.asteroids.network.data.deltas_changes.Tuple;
import nl.rug.aoop.asteroids.network.protocol.DefaultHandshake;
import nl.rug.aoop.asteroids.network.protocol.IOProtocol;
import org.apache.commons.lang3.SerializationUtils;

import java.awt.*;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Log
public class User implements Runnable, GameUpdateListener {
    private DatagramSocket userSocket;
    private IOProtocol io;
    @Getter
    private PackageHandler ioHandler;
    private ConnectionParameters connectionParameters;
    private MultiplayerManager multiplayerManager;
    private Game game;
    private String USER_ID = "neo";


    private Thread clientConsumerThread;

    private User(Game game, InetSocketAddress address) {
        this.game = game;
        game.addListener(this);
        initSocket();
        attemptConnect(address);
        initClientMultiplayer();
    }

    private User(Game game, InetAddress inetAddress) {
        this.game = game;
        initHostMultiplayer(inetAddress);
    }

    public static User newHostUser(Game game, InetAddress address) {
        return new User(game, address);
    }

    public static User newClientUser(Game game, InetSocketAddress address) {
        return new User(game, address);
    }

    private void initSocket() {
        try {
            userSocket = new DatagramSocket();
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

    private void attemptConnect(InetSocketAddress address) {
        if (address != null) {
            connectionParameters = new ConnectionParameters(userSocket, address, 0); //TODO !!!!!
            io = new DefaultHandshake(userSocket).handshake(connectionParameters);
        }
        if (io == null || io.getPackageHandler() == null) {
            log.warning("Failed connection");               //TODO interface
        } else {
            USER_ID = io.getOwnerId();
            game.setUSER_ID(USER_ID);
            ioHandler = io.getPackageHandler();
            clientConsumerThread = new Thread(new Consumer());
            new Thread(this).start();
        }
    }

    private void initClientMultiplayer() {
        multiplayerManager = MultiplayerGame.multiplayerClient(game, this);
    }

    private void initHostMultiplayer(InetAddress address) {
        multiplayerManager = MultiplayerGame.multiplayerServer(game, this, address);
        userSocket = multiplayerManager.getHostingDevice().getServerSocket();
    }


    public byte[] getHostDeltas(String userId) { //TODO move to deltaprocessing
        List<Tuple.T2<String, double[]>> playerVecMap = new ArrayList<>();
        HashMap<String, List<double[]>> objectVecMap = new HashMap<>(game.getTickObjMap());
        playerVecMap.add(getPlayerData(userId));
        playerVecMap.addAll(game.getAllPlayers());
        byte[] hostDeltas = SerializationUtils.serialize(
                new GameplayDeltas(playerVecMap, objectVecMap, System.currentTimeMillis()));
        game.resetObjMap();
        return hostDeltas;
    }

    public Tuple.T2<String, double[]> getPlayerData(String userId) {//TODO move to deltaprocessing //don't forget localuser io id
        Point.Double userPos = game.getSpaceShip().getLocation();
        Point.Double userVelocity = game.getSpaceShip().getVelocity();
        return new Tuple.T2<>(userId, new double[]{userPos.x, userPos.y, userVelocity.x, userVelocity.y});
    }


    public HashMap<String, List<double[]>> getPlayerAssets() {
        HashMap<String, List<double[]>> objectVecMap = new HashMap<>(game.getTickObjMap());
        for (GameObject gob : game.getUserAssets()) {
            objectVecMap.get(gob.getObjectId()).add(gob.getObjParameters());
        }

        return objectVecMap;
    }
    private void resetDeltas() {
        game.resetObjMap();
    }

    public synchronized void send(GameplayDeltas data) {
        game.setUserSerializing(true);
        io.updateOutPackage(data);
        game.setUserSerializing(false);
        io.send();
        resetDeltas();
    }

    public void receive() {
        io.receive();
    }

    public boolean isConnected() {
        return (!userSocket.isClosed());
    }

    private synchronized void updateGame(){
        io.getLastDataPackage().getData().injectChanges(multiplayerManager.getDeltaManager());
    }

    private class Consumer implements Runnable {

        @Override
        public synchronized void run() {
            while (isConnected()) {
                if(!game.isRendererBusy()){
                    receive();
                    updateGame();
                }
            }
        }
    }

    @Override
    public synchronized void run() {
        while (isConnected()) {
            if (!game.isRendererBusy()) {
                send(new GameplayDeltas(List.of(getPlayerData(USER_ID)), getPlayerAssets(), System.currentTimeMillis()));
                try {
                    wait(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    @SneakyThrows
    @Override
    public void onGameExit() {
        clientConsumerThread.join();
    }
}
