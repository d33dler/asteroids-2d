package nl.rug.aoop.asteroids.model.gameobjects.bullet;

import nl.rug.aoop.asteroids.model.AsteroidSize;
import nl.rug.aoop.asteroids.model.Game;
import nl.rug.aoop.asteroids.model.gameobjects.asteroid.Asteroid;
import nl.rug.aoop.asteroids.model.obj_factory.FactoryCommand;
import nl.rug.aoop.asteroids.model.obj_factory.ObjectCommand;

import java.awt.*;

@ObjectCommand(id = "bullet")
public class BulletMaker implements FactoryCommand {

    @Override
    public void updateObject(Game game,String id, double[] parameters) {
        game.getBullets().add(new Bullet(parameters[0],parameters[1],parameters[2],parameters[3]));
    }
}
