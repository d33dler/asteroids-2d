package nl.rug.aoop.asteroids.model.obj_factory;

import nl.rug.aoop.asteroids.gameobserver.GameUpdateListener;
import nl.rug.aoop.asteroids.model.game.Game;
import nl.rug.aoop.asteroids.model.game.GameResources;
import nl.rug.aoop.asteroids.network.data.deltas_changes.Tuple;
import nl.rug.aoop.asteroids.util.ReflectionUtils;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

/**
 * GeneralObjectsFactory class - updates caches and states of objects generated
 * in online game environment
 */
public class GeneralObjectsFactory implements GameObjectFactory, GameUpdateListener {

    private HashMap<String, FactoryCommand> objFactoryMap;
    private Game game;
    private GameResources resources;

    public GeneralObjectsFactory(Game game, String objPACKAGE) {
        this.game = game;
        this.resources = game.getResources();
        game.addListener(this);
        loadObject(objPACKAGE);
    }

    /**
     *
     * @param PACKAGE loads object mini-factories commands .
     * @implNote GeneralObjectsFactory acts as a dispatcher.
     */
    @Override
    public void loadObject(String PACKAGE) {
        objFactoryMap = ReflectionUtils.getFactoryCommands(PACKAGE);
    }

    /**
     *
     * @param objId player id,( or any object id that is controlled in real time)
     * @param deltaSet set of data comprised of input data and vectors
     */
    @Override
    public void updateActiveObject(String objId, Tuple.T3<Tuple.T2<String, String>, HashSet<Integer>, double[]> deltaSet) { //objId here
        FactoryCommand command = objFactoryMap.get(objId);
        if (command != null) {
            command.updateActiveObject(game, deltaSet);
        }
    }

    /**
     *
     * @param objId player id,( or any object id that is controlled in real time)
     * @param deltaSet set of data comprised of input data and vectors
     */
    @Override
    public void updateAllActiveObjects(String objId, List<Tuple.T3<Tuple.T2<String, String>, HashSet<Integer>,
            double[]>> deltaSet) {
        FactoryCommand command = objFactoryMap.get(objId);
        if (command != null) {
            command.updateAllObjects(game, deltaSet);
        }
    }
    /**
     *
     * @param objDeltaSet list of data comprised of tuples coupling object ids with vector data
     */
    @Override
    public void updatePassiveObjects(List<Tuple.T2<String, List<double[]>>> objDeltaSet) {
        for (Tuple.T2<String, List<double[]>> tuple : objDeltaSet) {
            try {
                FactoryCommand command = objFactoryMap.get(tuple.a);
                if (command != null) {
                    command.updatePassiveObjects(game, tuple);
                }
            } catch (NullPointerException ignored) {
            }
        }
    }
}
