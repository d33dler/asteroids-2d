package nl.rug.aoop.asteroids.network.data;

import nl.rug.aoop.asteroids.model.MultiplayerManager;
import nl.rug.aoop.asteroids.model.obj_factory.GameObjectFactory;
import nl.rug.aoop.asteroids.network.clients.User;
import nl.rug.aoop.asteroids.network.data.deltas_changes.ConfigData;
import nl.rug.aoop.asteroids.network.data.deltas_changes.GameplayDeltas;
import nl.rug.aoop.asteroids.network.data.deltas_changes.Tuple2;
import nl.rug.aoop.asteroids.network.data.types.DeltaManager;

import java.awt.*;
import java.io.Serializable;

public class DeltaProcessor implements DeltaManager, Serializable, Runnable {

    private final User user;
    private final MultiplayerManager multiplayerBase;
    private final GameObjectFactory factory;

    public DeltaProcessor(MultiplayerManager manager, User user) {
        this.user = user;
        this.multiplayerBase = manager;
        this.factory = multiplayerBase.getGame().getObjectFactory();
    }

    private void updatePlayers(Tuple2<String, double[]>[] playerVectors) {
       for(int i = playerVectors.length - 1; i >= 0; i--) {
            factory.createNewObject(playerVectors[i].k, playerVectors[i].v);
       }
    }

    private void updateObjects(Tuple2<String, double[][]>[] objectVectors) { //TODO needs 1 obj per tuple?
        for (int i = objectVectors.length - 1; i >= 0; i--) {
            for (int x = 0; x < objectVectors[i].v.length; i++){
                factory.createNewObject(objectVectors[i].k, objectVectors[i].v[x]);
            }
        }
    }

    @Override
    public void setupGame(GameplayDeltas gameplayDeltas) {
        updatePlayers(gameplayDeltas.playerVecMap);
        updateObjects(gameplayDeltas.objectVecMap);
    }

    private void compareChanges() {

    }


    @Override
    public void run() {

    }
}
