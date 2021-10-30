package nl.rug.aoop.asteroids.model;

import lombok.Getter;
import lombok.SneakyThrows;
import nl.rug.aoop.asteroids.gameobserver.GameUpdateListener;
import nl.rug.aoop.asteroids.model.game.Game;
import nl.rug.aoop.asteroids.network.clients.User;
import nl.rug.aoop.asteroids.network.data.DeltaProcessor;
import nl.rug.aoop.asteroids.network.data.types.DeltaManager;
import nl.rug.aoop.asteroids.network.host.HostingDevice;
import nl.rug.aoop.asteroids.network.host.HostingServer;
import nl.rug.aoop.asteroids.util.IOUtils;

import java.net.InetAddress;
import java.util.HashMap;

public class MultiplayerGame implements MultiplayerManager, GameUpdateListener {

    @Getter
    private final int MAX_CLIENTS = 10;
    private User user;
    @Getter
    private Game game;

    @Getter
    private HostingDevice hostingDevice = null;
    @Getter
    private DeltaManager deltaManager;

    private Thread hostingDeviceThread;

    private MultiplayerGame(Game game, User user) {
        this.game = game;
        this.user = user;
        this.deltaManager = new DeltaProcessor(this, user);
        game.addListener(this);
    }


    public static MultiplayerManager multiplayerClient(Game game, User user) {
        return new MultiplayerGame(game, user);
    }

    public static MultiplayerManager multiplayerServer(Game game, User user, InetAddress address) {
        MultiplayerGame multiplayerManager = new MultiplayerGame(game, user);
        multiplayerManager.launchAsHost(address);
        return multiplayerManager;
    }

    private void launchAsHost(InetAddress address) {
        initHostingDevice(address);
    }


    private void initHostingDevice(InetAddress address) {
        hostingDevice = new HostingServer(this, address);
        hostingDeviceThread = new Thread((Runnable) hostingDevice);
        hostingDeviceThread.start();
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
        return game.isEngineBusy();
    }

    public void notifyDisconnect() {
        IOUtils.reportMessage(game.getViewController().getFrame(),"Host has terminated the connection" );
       game.updateGameOverDisconnection();
    }

    @SneakyThrows
    @Override
    public void onGameExit() {
        if (hostingDevice != null) {
            hostingDevice.shutdown();
            hostingDevice = null;
        }
        if (hostingDeviceThread != null) hostingDeviceThread.join(100);
    }
}
