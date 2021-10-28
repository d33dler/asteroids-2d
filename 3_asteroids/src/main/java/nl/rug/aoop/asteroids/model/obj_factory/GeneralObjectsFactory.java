package nl.rug.aoop.asteroids.model.obj_factory;

import nl.rug.aoop.asteroids.gameobserver.GameUpdateListener;
import nl.rug.aoop.asteroids.model.game.Game;
import nl.rug.aoop.asteroids.model.game.GameResources;
import nl.rug.aoop.asteroids.network.data.deltas_changes.Tuple;
import nl.rug.aoop.asteroids.util.ReflectionUtils;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

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

    @Override
    public void loadObject(String PACKAGE) {
        objFactoryMap = ReflectionUtils.getFactoryCommands(PACKAGE);
    }

    @Override
    public void updateActiveObject(String objId, Tuple.T3<String, HashSet<Integer>, double[]> playerKeySet) { //objId here
        FactoryCommand command = objFactoryMap.get(objId);
        if (command != null) {
            command.updateActiveObject(game, playerKeySet);
        }
    }

    @Override
    public void updateAllActiveObjects(String objId, List<Tuple.T3<String, HashSet<Integer>, double[]>> playerKeySet) {
        FactoryCommand command = objFactoryMap.get(objId);
        if (command != null) {
            command.updateAllObjects(game, playerKeySet);
        }
    }

    @Override
    public void updatePassiveObjects(List<Tuple.T2<String, List<double[]>>> playerKeySet) {
        for (Tuple.T2<String, List<double[]>> tuple : playerKeySet) {
            FactoryCommand command = objFactoryMap.get(tuple.a);
            if (command != null) {
                command.updatePassiveObjects(game, tuple);
            }
        }

    }

}
