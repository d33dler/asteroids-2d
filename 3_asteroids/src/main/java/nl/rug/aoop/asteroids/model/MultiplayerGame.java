package nl.rug.aoop.asteroids.model;

import lombok.Getter;
import nl.rug.aoop.asteroids.network.clients.User;
import nl.rug.aoop.asteroids.network.data.ConnectionParameters;
import nl.rug.aoop.asteroids.network.data.DeltaProcessor;
import nl.rug.aoop.asteroids.network.data.types.DeltaManager;
import nl.rug.aoop.asteroids.network.host.HostingDevice;
import nl.rug.aoop.asteroids.network.host.HostingServer;

import java.net.InetAddress;
import java.util.HashMap;

public class MultiplayerGame implements MultiplayerManager {
    @Getter
    private ConnectionParameters parameters;
    @Getter
    private final int MAX_CLIENTS = 10;
    private final User user;
    @Getter
    private final Game game;
    @Getter
    private HostingDevice hostingDevice;
    @Getter
    private final DeltaManager deltaManager;

    private MultiplayerGame(Game game, User user) {
        this.game = game;
        this.user = user;
        this.deltaManager = new DeltaProcessor(this, user);
    }


    public static MultiplayerManager multiplayerClient(Game game, User user) {
        MultiplayerGame multiplayerManager = new MultiplayerGame(game, user);
        multiplayerManager.launchAsClient();
        return new MultiplayerGame(game,user);
    }

    public static MultiplayerManager multiplayerServer(Game game, User user, InetAddress address){
        MultiplayerGame multiplayerManager = new MultiplayerGame(game,user);
        multiplayerManager.launchAsHost(address);
        return multiplayerManager;
    }

    private void launchAsClient() {
        initClientComponents();
    }

    private void launchAsSpectator() {
        initClientComponents();
    }

    private void launchAsHost(InetAddress address){
        initHostingDevice(address);
    }

    private void initClientComponents() {
        parameters = user.getIoHolder().getParameters();
    }

    private void initHostingDevice(InetAddress address){
        hostingDevice = new HostingServer(this,address ) ;
        parameters = hostingDevice.getRawConnectionParameters();
    }

    @Override
    public HashMap<String, Double[]> getPlayerVectors() {
        return null;
    }

    @Override
    public User getHost() {
        return user;
    }

    @Override
    public boolean isUpdating() {
        return game.isRendererBusy();
    }

}
