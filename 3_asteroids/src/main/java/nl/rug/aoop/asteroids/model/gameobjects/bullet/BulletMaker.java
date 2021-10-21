package nl.rug.aoop.asteroids.model.gameobjects.bullet;

import nl.rug.aoop.asteroids.model.Game;
import nl.rug.aoop.asteroids.model.obj_factory.FactoryCommand;
import nl.rug.aoop.asteroids.model.obj_factory.ObjectCommand;

@ObjectCommand(id = "bullet")
public class BulletMaker implements FactoryCommand {

    @Override
    public void updateObject(Game game,String id, double[] parameters) {
        game.getOnlineBullets().add(new Bullet(parameters[0],parameters[1],parameters[2],parameters[3]));
    }
}
