package nl.rug.aoop.asteroids.model.obj_factory;

import java.util.HashSet;

public interface GameObjectFactory {
    void loadObject(String PACKAGE);
    void createNewObject(String id, HashSet<Integer> keySet, String objId);
}
