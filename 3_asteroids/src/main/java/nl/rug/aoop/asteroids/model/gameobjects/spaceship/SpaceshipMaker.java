package nl.rug.aoop.asteroids.model.gameobjects.spaceship;

import nl.rug.aoop.asteroids.model.game.Game;
import nl.rug.aoop.asteroids.model.obj_factory.FactoryCommand;
import nl.rug.aoop.asteroids.model.obj_factory.ObjectCommand;
import nl.rug.aoop.asteroids.network.data.deltas_changes.Tuple;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Spaceship - mini-factory command of GeneralObjectsFactory
 * Creates new spaceship objects from online received delta data
 * and adds them to the local cache;
 */
@ObjectCommand(id = "spaceship")
public class SpaceshipMaker implements FactoryCommand {

    /**
     * Method used by the host to update its caches based on player pool deltas
     * @param game - current game
     * @param data - set of client connection deltas
     */
    @Override
    public void updateActiveObject(Game game, Tuple.T3<Tuple.T2<String,String>, HashSet<Integer>, double[]> data) {
        if (!data.a.a.equals(game.getUSER_ID())) {
            if (!game.isEngineBusy()) {
                game.getResources().getSpaceshipCache().put(data.a.a, new Tuple.T3<>(data.a.b, data.b, data.c));
            }
        }
    }

    /**
     * Method used by the client to updated its caches based on the host's deltas data set
     * @param game - current game
     * @param playersData - set of all players
     */
    public void updateAllObjects(Game game, List<Tuple.T3<Tuple.T2<String,String>, HashSet<Integer>, double[]>> playersData) {
        ConcurrentHashMap<String, Tuple.T3<String,HashSet<Integer>, double[]>> cacheBuff = new ConcurrentHashMap<>();
        for (Tuple.T3<Tuple.T2<String,String>, HashSet<Integer>, double[]> data : playersData) {
            cacheBuff.put(data.a.a, new Tuple.T3<>(data.a.b, data.b, data.c));
        }
        cacheBuff.remove(game.getUSER_ID()); //trim user's clone
        while (true) {
            if (!game.isEngineBusy()) { //avoiding chance of modification exception
                game.getResources().setSpaceshipCache(cacheBuff);
                break;
            }
        }
    }
}

