package nl.rug.aoop.asteroids.network.data.deltas_changes;

import nl.rug.aoop.asteroids.model.gameobjects.GameObject;
import nl.rug.aoop.asteroids.network.data.types.DeltaManager;
import nl.rug.aoop.asteroids.network.data.types.DeltasData;

import java.util.HashSet;
import java.util.List;

public class GameplayDeltas implements DeltasData {

    public final long timestamp;

    public Tuple.T3<String, HashSet<Integer>,double[]> clientKeyEvents;
    public final List<Tuple.T3<String, HashSet<Integer>, double[]>> keyEventList;
    public final List<GameObject> objectList;

    public GameplayDeltas(long timestamp, List<Tuple.T3<String, HashSet<Integer>, double[]>> kList, List<GameObject> objectList) {
        this.timestamp = timestamp;
        this.keyEventList = kList;
        this.objectList = objectList;
    }

    public GameplayDeltas(long timestamp, Tuple.T3<String, HashSet<Integer>, double[]> clientKeyEvents) {
        this(timestamp, null, null);
        this.clientKeyEvents = clientKeyEvents;
    }
    @Override
    public long getTimeStamp() {
        return timestamp;
    }

    @Override
    public void injectChanges(DeltaManager manager) {
        manager.injectDeltas(this);
    }
}
