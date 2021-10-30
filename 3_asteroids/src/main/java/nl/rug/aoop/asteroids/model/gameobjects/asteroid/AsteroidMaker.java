package nl.rug.aoop.asteroids.model.gameobjects.asteroid;

import nl.rug.aoop.asteroids.model.AsteroidSize;
import nl.rug.aoop.asteroids.model.game.Game;
import nl.rug.aoop.asteroids.model.obj_factory.FactoryCommand;
import nl.rug.aoop.asteroids.model.obj_factory.ObjectCommand;
import nl.rug.aoop.asteroids.network.data.deltas_changes.Tuple;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * AsteroidMaker - mini-factory command of GeneralObjectsFactory
 * Creates new asteroid objects from online received delta data
 * and adds them to the local cache, only if the set size has changed.
 */
@ObjectCommand(id = "asteroid")
public class AsteroidMaker implements FactoryCommand {


    /**
     *
     * Adds to the asteroid cache, after checking if the deep cloner has finished its cycle.
     * @param game - current game
     * @param parameters object set parameters
     */
    @Override
    public void updatePassiveObjects(Game game, Tuple.T2<String, List<double[]>> parameters) {
        if (parameters.b.size() != game.getResources().getAsteroids().size()) {
            List<Asteroid> cacheBuff = new ArrayList<>();
            parameters.b.forEach(param -> {
                cacheBuff.add(new Asteroid(new Point.Double(param[0], param[1]),
                        new Point.Double(param[2], param[3]), AsteroidSize.X.getSize(param[4])));
            });
            while (true) {
                if (game.rendererDeepCloner.cycleDone) {
                    game.getResources().setAsteroids(cacheBuff);
                    break;
                }
            }
        }

    }

}
