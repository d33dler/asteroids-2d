package nl.rug.aoop.asteroids.model.gameobjects.bullet;

import nl.rug.aoop.asteroids.model.game.Game;
import nl.rug.aoop.asteroids.model.obj_factory.FactoryCommand;
import nl.rug.aoop.asteroids.model.obj_factory.ObjectCommand;
import nl.rug.aoop.asteroids.network.data.deltas_changes.Tuple;

import java.util.List;

@ObjectCommand(id = "bullet")
public class BulletMaker implements FactoryCommand {

    @Override
    public void updatePassiveObjects(Game game, Tuple.T2<String, List<double[]>> parameters){
     //bullets are not created, any new object can use this class to create obj from delta skeletons
    }
}
