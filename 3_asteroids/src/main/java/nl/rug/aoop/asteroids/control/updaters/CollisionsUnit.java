package nl.rug.aoop.asteroids.control.updaters;

import nl.rug.aoop.asteroids.model.Game;

public class CollisionsUnit extends GameUpdater implements EngineUnit{
    /**
     * Constructs a new game updater with the given game.
     *
     * @param game The game that this updater will update when it's running.
     */
    public CollisionsUnit(Game game, boolean online) {
        super(game, online);
    }

    @Override
    public void compute() {

    }
}
