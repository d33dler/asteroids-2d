package nl.rug.aoop.asteroids.model.obj_factory;

import nl.rug.aoop.asteroids.network.data.deltas_changes.Tuple;

import java.util.HashSet;

public interface GameObjectFactory {
    void loadObject(String PACKAGE);
    void createNewObject(Tuple.T3<String, HashSet<Integer>,double[]> playerKeySet, String objId);
}
