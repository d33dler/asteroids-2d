package nl.rug.aoop.asteroids.model.obj_factory;

import nl.rug.aoop.asteroids.model.Game;
import nl.rug.aoop.asteroids.model.gameobjects.GameObject;

import java.util.HashSet;

public interface FactoryCommand {
   default void updateActiveObject(Game game, String id, HashSet<Integer> keySet){};
   default void updatePassiveObject(Game game, String id, GameObject object){};
}
