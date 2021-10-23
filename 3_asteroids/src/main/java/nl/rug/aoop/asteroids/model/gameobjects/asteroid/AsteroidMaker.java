package nl.rug.aoop.asteroids.model.gameobjects.asteroid;

import nl.rug.aoop.asteroids.model.AsteroidSize;
import nl.rug.aoop.asteroids.model.Game;
import nl.rug.aoop.asteroids.model.obj_factory.FactoryCommand;
import nl.rug.aoop.asteroids.model.obj_factory.ObjectCommand;
import nl.rug.aoop.asteroids.network.data.deltas_changes.Tuple;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

@ObjectCommand(id = "asteroid")
public class AsteroidMaker implements FactoryCommand {

    @Override
    public void updatePassiveObjects(Game game, Tuple.T2<String, List<double[]>> parameters) {
        if (parameters.b.size() != game.getAsteroids().size()) {
            List<Asteroid> cacheBuff = new ArrayList<>();
            parameters.b.forEach(param -> {
                cacheBuff.add(new Asteroid(new Point.Double(param[0], param[1]),
                        new Point.Double(param[2], param[3]), AsteroidSize.X.getSize(param[4])));
            });
            while (true) {
                if (game.rendererDeepCloner.cycleDone) {
                    game.setAsteroids(cacheBuff);
                    break;
                }

            }
        }

    }

}
