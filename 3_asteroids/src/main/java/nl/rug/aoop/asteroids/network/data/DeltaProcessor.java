package nl.rug.aoop.asteroids.network.data;

import nl.rug.aoop.asteroids.model.MultiplayerManager;
import nl.rug.aoop.asteroids.model.obj_factory.GameObjectFactory;
import nl.rug.aoop.asteroids.network.clients.User;
import nl.rug.aoop.asteroids.network.data.deltas_changes.GameplayDeltas;
import nl.rug.aoop.asteroids.network.data.deltas_changes.Tuple;
import nl.rug.aoop.asteroids.network.data.types.DeltaManager;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DeltaProcessor implements DeltaManager, Serializable, Runnable {

    private final User user;
    private final MultiplayerManager multiplayerBase;
    private final GameObjectFactory factory;

    public DeltaProcessor(MultiplayerManager manager, User user) {
        this.user = user;
        this.multiplayerBase = manager;
        this.factory = multiplayerBase.getGame().getObjectFactory();
    }

    private void updatePlayers(List<Tuple.T2<String, double[]>> playerVectors) {
        for (int i = playerVectors.size() - 1; i >= 0; i--) {
            factory.createNewObject(playerVectors.get(i).a, playerVectors.get(i).b);
        }
    }

    private void updateObjects(HashMap<String, double[][]> objectVectors) { //TODO needs 1 obj per tuple?
        for (Map.Entry<String, double[][]> entry : objectVectors.entrySet()) {
            String objId = entry.getKey();
            double[][] matrix = entry.getValue();
            for (double[] params : matrix) {
                factory.createNewObject(objId, params);
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
