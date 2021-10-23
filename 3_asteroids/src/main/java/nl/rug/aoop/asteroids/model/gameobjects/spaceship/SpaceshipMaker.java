package nl.rug.aoop.asteroids.model.gameobjects.spaceship;

import nl.rug.aoop.asteroids.model.Game;
import nl.rug.aoop.asteroids.model.obj_factory.FactoryCommand;
import nl.rug.aoop.asteroids.model.obj_factory.ObjectCommand;
import nl.rug.aoop.asteroids.network.data.deltas_changes.Tuple;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

@ObjectCommand(id = "spaceship")
public class SpaceshipMaker implements FactoryCommand {

    @Override
    public void updateActiveObject(Game game, Tuple.T3<String, HashSet<Integer>, double[]> playerData) {
        if (!playerData.a.equals(game.getUSER_ID())) {
            game.getSpaceshipCache().put(playerData.a, Tuple.T3.lstT2(playerData));
        }
    }

    public void updateAllObjects(Game game, List<Tuple.T3<String, HashSet<Integer>, double[]>> playersData) {
        HashMap<String, Tuple.T2<HashSet<Integer>, double[]>> cacheBuff = new HashMap<>();
        for (Tuple.T3<String, HashSet<Integer>, double[]> data : playersData) {
            cacheBuff.put(data.a, Tuple.T3.lstT2(data));
        }
        cacheBuff.remove(game.getUSER_ID()); //trim user's clone
        while (true) {
            if (!game.isEngineBusy()) { //avoiding chance of modification exception
                game.setSpaceshipCache(cacheBuff);
                break;
            }
        }
    }
}

