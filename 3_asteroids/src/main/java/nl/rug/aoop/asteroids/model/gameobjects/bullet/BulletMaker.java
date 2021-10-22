package nl.rug.aoop.asteroids.model.gameobjects.bullet;

import nl.rug.aoop.asteroids.model.Game;
import nl.rug.aoop.asteroids.model.gameobjects.GameObject;
import nl.rug.aoop.asteroids.model.obj_factory.FactoryCommand;
import nl.rug.aoop.asteroids.model.obj_factory.ObjectCommand;

import java.util.HashSet;

@ObjectCommand(id = "bullet")
public class BulletMaker implements FactoryCommand {

    @Override
    public void updatePassiveObject(Game game, String id, GameObject object) {
        game.getBulletCache().add((Bullet) object);
    }
}
