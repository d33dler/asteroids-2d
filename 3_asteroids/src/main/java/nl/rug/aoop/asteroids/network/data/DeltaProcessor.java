package nl.rug.aoop.asteroids.network.data;

import nl.rug.aoop.asteroids.model.MultiplayerRenderer;
import nl.rug.aoop.asteroids.network.clients.User;
import nl.rug.aoop.asteroids.network.data.deltas_changes.ConfigData;
import nl.rug.aoop.asteroids.network.data.deltas_changes.GameplayDeltas;
import nl.rug.aoop.asteroids.network.data.deltas_changes.Tuple2;
import nl.rug.aoop.asteroids.network.data.types.DeltaManager;

import java.io.Serializable;

public class DeltaProcessor implements DeltaManager, Serializable {

    private final User user;
    private final MultiplayerRenderer base;

    public DeltaProcessor(User user) {
        this.user = user;
        this.base = user.getMultiplayerRenderer();
    }

    private void updatePlayers(Tuple2<String, double[]>[] playerVectors) {

    }

    private void updateObjects(Tuple2<String, double[][]>[] objectVectors) {

    }

    @Override
    public void setupConfig(ConfigData configChange) {
       user.getIoHolder().loadHandshakeConfigs(configChange.setup);
    }

    @Override
    public void setupGame(GameplayDeltas gameplayDeltas) {
        updatePlayers(gameplayDeltas.playerVecMap);
        updateObjects(gameplayDeltas.objectVecMap);
    }

    private void compareChanges() {

    }


}
