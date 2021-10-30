package nl.rug.aoop.asteroids.network.data.deltas_changes;

import nl.rug.aoop.asteroids.network.data.types.DeltaManager;
import nl.rug.aoop.asteroids.network.data.types.DeltasData;

import java.util.HashSet;
import java.util.List;

/**
 * GameplayDeltas - is the wrapper class for the delta data sent over the network during the game process
 *
 */
public class GameplayDeltas implements DeltasData {

    public final long timestamp;
    public Tuple.T3<Tuple.T2<String,String>, HashSet<Integer>,double[]> ownerClientDeltas;
    public final List<Tuple.T3<Tuple.T2<String,String>, HashSet<Integer>, double[]>> clientsPoolDeltas;
    public final List<Tuple.T2<String, List<double[]>>> objectMapping;

    /**
     * Constructor used by the host
     * @param kList list of all active objects in 3-Tuple with ((id,nickname),Input set, Vectors)
     * @param objectMapping - list of all passive objects
     */
    public GameplayDeltas(List<Tuple.T3<Tuple.T2<String,String>, HashSet<Integer>, double[]>> kList,
                          List<Tuple.T2<String, List<double[]>>> objectMapping) {
        this.timestamp = System.currentTimeMillis();
        this.clientsPoolDeltas = kList;
        this.objectMapping = objectMapping;
    }
    /**
     * Constructor used by the user
     * @param ownerClientDeltas list of all active objects in 3-Tuple with ((id,nickname),Input set, Vectors)
     */
    public GameplayDeltas(Tuple.T3<Tuple.T2<String,String>, HashSet<Integer>, double[]> ownerClientDeltas) {
        this(null, null);
        this.ownerClientDeltas = ownerClientDeltas;
    }
    @Override
    public long getTimeStamp() {
        return timestamp;
    }

    /**
     *
     * @param manager - DeltaManager implementing class dispatching changes
     */
    @Override
    public void injectChanges(DeltaManager manager) {
        manager.injectDeltas(this);
    }
}
