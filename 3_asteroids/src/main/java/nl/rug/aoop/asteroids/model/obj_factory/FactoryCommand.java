package nl.rug.aoop.asteroids.model.obj_factory;

import nl.rug.aoop.asteroids.model.Game;
import nl.rug.aoop.asteroids.network.data.deltas_changes.Tuple;

import java.util.HashSet;
import java.util.List;

public interface FactoryCommand {
   default void updateActiveObject(Game game, Tuple.T3<String, HashSet<Integer>,double[]> playerData){};
   default void updateAllObjects(Game game, List<Tuple.T3<String, HashSet<Integer>,double[]>> playersData){};
   default void updatePassiveObjects(Game game, Tuple.T2<String, List<double[]>> parameters){};
}
