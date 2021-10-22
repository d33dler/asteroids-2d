package nl.rug.aoop.asteroids.model.gameobjects.asteroid;

import nl.rug.aoop.asteroids.model.Game;
import nl.rug.aoop.asteroids.model.gameobjects.GameObject;
import nl.rug.aoop.asteroids.model.obj_factory.FactoryCommand;
import nl.rug.aoop.asteroids.model.obj_factory.ObjectCommand;

@ObjectCommand(id = "asteroid")
public class AsteroidMaker implements FactoryCommand {

    @Override
    public void updatePassiveObject(Game game, String id, GameObject object) {
        game.getAsteroidsCache().add((Asteroid) object);
    }

}
