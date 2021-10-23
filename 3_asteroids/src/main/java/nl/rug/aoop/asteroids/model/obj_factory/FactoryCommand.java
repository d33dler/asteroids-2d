package nl.rug.aoop.asteroids.model.obj_factory;

import nl.rug.aoop.asteroids.model.Game;
import nl.rug.aoop.asteroids.model.gameobjects.GameObject;
import nl.rug.aoop.asteroids.network.data.deltas_changes.Tuple;

import java.util.HashSet;

public interface FactoryCommand {
   default void updateActiveObject(Game game, Tuple.T3<String, HashSet<Integer>,double[]> playerKeySet){};
   default void updatePassiveObject(Game game, String id, GameObject object){};
}
