package nl.rug.aoop.asteroids.model.obj_factory;

import nl.rug.aoop.asteroids.model.Game;
import nl.rug.aoop.asteroids.util.ReflectionUtils;

import java.util.HashMap;

public class GeneralObjectsFactory implements GameObjectFactory {

    private HashMap<String, FactoryCommand> objFactoryMap;
    private final Game game;

    public GeneralObjectsFactory(Game game, String objPACKAGE) {
        this.game = game;
        loadObject(objPACKAGE);
    }

    @Override
    public void loadObject(String PACKAGE) {
        objFactoryMap = ReflectionUtils.getFactoryCommands(PACKAGE);
    }

    @Override
    public void createNewObject(String id, double[] params) {
        FactoryCommand command = objFactoryMap.get(id);
        if (command != null) {
            command.updateObject(game, id, params);
        }
    }
}
