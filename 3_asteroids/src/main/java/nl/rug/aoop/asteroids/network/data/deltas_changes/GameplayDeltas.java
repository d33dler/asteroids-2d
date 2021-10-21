package nl.rug.aoop.asteroids.network.data.deltas_changes;

import nl.rug.aoop.asteroids.network.data.types.DeltaManager;
import nl.rug.aoop.asteroids.network.data.types.DeltasData;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

public class GameplayDeltas implements DeltasData, Serializable {

    public final long timestamp;
    public final List<Tuple.T2<String, double[]>> playerVecMap;
    public final HashMap<String, List<double[]>> objectVecMap;

    public GameplayDeltas(List<Tuple.T2<String, double[]>> vecMap,
                          HashMap<String, List<double[]>> objectVecMap,
                          long timestamp) {
        this.playerVecMap = vecMap;
        this.objectVecMap = objectVecMap;
        this.timestamp = timestamp;
    }

    @Override
    public long getTimeStamp() {
        return timestamp;
    }

    @Override
    public void injectChanges(DeltaManager manager) {
        manager.setupGame(this);
    }
}
