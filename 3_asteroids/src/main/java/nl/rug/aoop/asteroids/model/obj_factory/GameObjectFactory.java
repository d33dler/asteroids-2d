package nl.rug.aoop.asteroids.model.obj_factory;

import nl.rug.aoop.asteroids.model.gameobjects.StandardObjParams;

import java.util.HashSet;

public interface GameObjectFactory {
    void loadObject(String PACKAGE);
    void createNewObject(String id, HashSet<Integer> keySet);
}
