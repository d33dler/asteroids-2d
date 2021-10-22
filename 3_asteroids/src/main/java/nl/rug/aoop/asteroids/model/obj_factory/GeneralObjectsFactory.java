package nl.rug.aoop.asteroids.model.obj_factory;

import nl.rug.aoop.asteroids.gameobserver.GameUpdateListener;
import nl.rug.aoop.asteroids.model.Game;
import nl.rug.aoop.asteroids.util.ReflectionUtils;

import java.util.HashMap;
import java.util.HashSet;

public class GeneralObjectsFactory implements GameObjectFactory, GameUpdateListener {

    private HashMap<String, FactoryCommand> objFactoryMap;
    private Game game;

    public GeneralObjectsFactory(Game game, String objPACKAGE) {
        this.game = game;
        game.addListener(this);
        loadObject(objPACKAGE);
    }

    @Override
    public void loadObject(String PACKAGE) {
        objFactoryMap = ReflectionUtils.getFactoryCommands(PACKAGE);
    }

    @Override
    public void createNewObject(String id, HashSet<Integer> params) {
        FactoryCommand command = objFactoryMap.get(id);
        if (command != null) {
            while (true){
                if(!game.isEngineBusy()){
                    command.updateActiveObject(game, id, params);
                }
            }
        }
    }

    @Override
    public void onGameExit() {
        objFactoryMap = null;
    }
}
