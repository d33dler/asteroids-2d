package nl.rug.aoop.asteroids.model.gameobjects.spaceship;

import nl.rug.aoop.asteroids.model.Game;
import nl.rug.aoop.asteroids.model.obj_factory.FactoryCommand;
import nl.rug.aoop.asteroids.model.obj_factory.ObjectCommand;

@ObjectCommand(id = "spaceship")
public class SpaceshipMaker implements FactoryCommand {

    @Override
    public void updateObject(Game game, String id, double[] params) {
        if(game.getPlayers().containsKey(id)) {
            Spaceship player = game.getPlayers().get(id);
            player.updatePosition(params[0],params[1]);
            player.updateVelocity(params[2],params[3]); //TODO verify
        } else {
            game.getPlayers().put(id, new Spaceship(params[0],params[1],params[2],params[3]));
        }

    }
}
