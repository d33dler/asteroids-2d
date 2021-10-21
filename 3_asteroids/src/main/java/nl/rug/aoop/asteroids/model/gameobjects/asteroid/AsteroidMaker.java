package nl.rug.aoop.asteroids.model.gameobjects.asteroid;

import nl.rug.aoop.asteroids.model.AsteroidSize;
import nl.rug.aoop.asteroids.model.Game;
import nl.rug.aoop.asteroids.model.obj_factory.FactoryCommand;
import nl.rug.aoop.asteroids.model.obj_factory.ObjectCommand;

import java.awt.*;

@ObjectCommand(id = "asteroid")
public class AsteroidMaker implements FactoryCommand {

    @Override
    public void updateObject(Game game,String id, double[] parameters) {
        Point.Double pos = new Point.Double(parameters[0],parameters[1]);
        Point.Double velocity = new Point.Double(parameters[2],parameters[3]);
        game.getAsteroids().add(new Asteroid(pos,velocity, AsteroidSize.X.getSize(parameters[4])));
    }
}
