package nl.rug.aoop.asteroids.network.clients;


import lombok.Getter;
import lombok.extern.java.Log;
import nl.rug.aoop.asteroids.model.Game;
import nl.rug.aoop.asteroids.model.MultiplayerGame;
import nl.rug.aoop.asteroids.model.MultiplayerManager;
import nl.rug.aoop.asteroids.model.gameobjects.GameObject;
import nl.rug.aoop.asteroids.network.data.ConnectionParameters;
import nl.rug.aoop.asteroids.network.data.DataPackage;
import nl.rug.aoop.asteroids.network.data.PackageHandler;
import nl.rug.aoop.asteroids.network.data.deltas_changes.GameplayDeltas;
import nl.rug.aoop.asteroids.network.data.deltas_changes.Tuple;
import nl.rug.aoop.asteroids.network.host.HostingDevice;
import nl.rug.aoop.asteroids.network.protocol.DefaultHandshake;
import nl.rug.aoop.asteroids.network.protocol.IOProtocol;

import javax.naming.spi.ObjectFactory;
import java.awt.*;
import java.awt.geom.Point2D;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Log
public class User implements Runnable{
    private DatagramSocket userSocket;
    private IOProtocol io;
    @Getter
    private PackageHandler ioHolder;
    private ConnectionParameters connectionParameters;
    private ObjectFactory objectFactory;
    private MultiplayerManager multiplayerManager;
    private final Game game;

    @Getter
    private GameplayDeltas userDeltas = new GameplayDeltas(null,null,0);

    private User(Game game, InetSocketAddress address) {
        this.game = game;
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
            connectionParameters = new ConnectionParameters(userSocket, address,0); //TODO !!!!!
            io = new DefaultHandshake(userSocket).handshake(connectionParameters);
            System.out.println("Connected");
        }
        if (io == null || io.getHolder() == null) {
            log.warning("Failed connection");               //TODO interface
        } else {
            ioHolder = io.getHolder();
        }
    }

    private void initClientMultiplayer() {
        multiplayerManager = MultiplayerGame.multiplayerClient(game,this);
    }

    private void initHostMultiplayer(InetAddress address){
        multiplayerManager = MultiplayerGame.multiplayerServer(game,this, address);
        userSocket = multiplayerManager.getHostingDevice().getServerSocket();
    }


    public void send(DataPackage data) {
        ioHolder.setDataPackage(data);
        io.send();
    }

    public GameplayDeltas getHostDeltas(String userId){
        List<Tuple.T2<String, double[]>> playerVecMap = new ArrayList<>();
        HashMap<String, List<double[]>> objectVecMap = game.getObjMap();
        playerVecMap.add(getPlayerData(userId));
        playerVecMap.addAll(game.getAllPlayers());
        for (GameObject gob : game.getAllGameObj()) {
            objectVecMap.get(gob.getObjectId()).add(gob.getObjParameters());
        }
        return new GameplayDeltas(playerVecMap,objectVecMap,System.currentTimeMillis());
    }

    public Tuple.T2<String, double[]> getPlayerData(String userId) {
        Point.Double userPos = game.getSpaceShip().getLocation();
        Point.Double userVelocity = game.getSpaceShip().getVelocity();
        return new Tuple.T2<>(userId, new double[]{userPos.x,userPos.y,userVelocity.x,userVelocity.y});
    }
    public void receive() {
        io.receive();
    }

    public boolean isConnected() {
        return (!userSocket.isClosed() && ioHolder != null);
    }

    @Override
    public void run() {

    }

}
