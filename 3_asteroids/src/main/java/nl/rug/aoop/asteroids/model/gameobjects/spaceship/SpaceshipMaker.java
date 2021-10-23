package nl.rug.aoop.asteroids.model.gameobjects.spaceship;

import nl.rug.aoop.asteroids.model.Game;
import nl.rug.aoop.asteroids.model.obj_factory.FactoryCommand;
import nl.rug.aoop.asteroids.model.obj_factory.ObjectCommand;
import nl.rug.aoop.asteroids.network.data.deltas_changes.Tuple;

import java.util.HashSet;

@ObjectCommand(id = "spaceship")
public class SpaceshipMaker implements FactoryCommand {

    @Override
    public void updateActiveObject(Game game, Tuple.T3<String, HashSet<Integer>,double[]> parameters) {
        if (!parameters.a.equals(game.getUSER_ID())) {
            game.getSpaceshipCache().put(parameters.a,Tuple.T3.lstT2(parameters));
        }
    }
}

