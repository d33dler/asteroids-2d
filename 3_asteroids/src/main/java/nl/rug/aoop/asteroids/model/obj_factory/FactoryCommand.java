package nl.rug.aoop.asteroids.model.obj_factory;

import nl.rug.aoop.asteroids.model.Game;

public interface FactoryCommand {
   void updateObject(Game game, String id, double[] params);
}
