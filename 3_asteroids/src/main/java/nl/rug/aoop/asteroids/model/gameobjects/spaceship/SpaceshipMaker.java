package nl.rug.aoop.asteroids.model.gameobjects.spaceship;

import nl.rug.aoop.asteroids.model.Game;
import nl.rug.aoop.asteroids.model.obj_factory.FactoryCommand;
import nl.rug.aoop.asteroids.model.obj_factory.ObjectCommand;

import java.util.HashSet;

@ObjectCommand(id = "spaceship")
public class SpaceshipMaker implements FactoryCommand {

    @Override
    public void updateActiveObject(Game game, String id, HashSet<Integer> params) {
        if (game.getPlayers().containsKey(id)) {
            game.getPlayers().get(id).setKeyEventSet(params);
        } else {
            if (!id.equals(game.getUSER_ID())) {
                Spaceship s = new Spaceship();
                s.setKeyEventSet(params);
                game.getSpaceshipCache().put(id,s);
            }
        }
    }
}
