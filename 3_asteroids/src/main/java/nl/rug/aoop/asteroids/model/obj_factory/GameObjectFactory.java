package nl.rug.aoop.asteroids.model.obj_factory;

import nl.rug.aoop.asteroids.network.data.deltas_changes.Tuple;

import java.util.HashSet;
import java.util.List;

public interface GameObjectFactory {
    void loadObject(String PACKAGE);
    void updateActiveObject(String objId, Tuple.T3<Tuple.T2<String,String>, HashSet<Integer>, double[]> playerKeySet);
    void updateAllActiveObjects(String objId, List<Tuple.T3<Tuple.T2<String,String>, HashSet<Integer>,
            double[]>> playerKeySet);
    void updatePassiveObjects(List<Tuple.T2<String, List<double[]>>> playerKeySet);
}
