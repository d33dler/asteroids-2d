package nl.rug.aoop.asteroids.network.data;

import nl.rug.aoop.asteroids.model.Game;
import nl.rug.aoop.asteroids.model.MultiplayerManager;
import nl.rug.aoop.asteroids.model.gameobjects.GameObject;
import nl.rug.aoop.asteroids.model.gameobjects.asteroid.Asteroid;
import nl.rug.aoop.asteroids.model.obj_factory.GameObjectFactory;
import nl.rug.aoop.asteroids.network.clients.User;
import nl.rug.aoop.asteroids.network.data.deltas_changes.GameplayDeltas;
import nl.rug.aoop.asteroids.network.data.deltas_changes.Tuple;
import nl.rug.aoop.asteroids.network.data.types.DeltaManager;
import org.apache.commons.lang3.SerializationUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class DeltaProcessor implements DeltaManager {

    private final Game game;
    private final User user;
    private final MultiplayerManager multiplayerBase;
    private final GameObjectFactory factory;

    public DeltaProcessor(MultiplayerManager manager, User user) {
        this.game = manager.getGame();
        this.user = user;
        this.multiplayerBase = manager;
        this.factory = multiplayerBase.getGame().getObjectFactory();
    }

    private void collectPlayers(List<Tuple.T3<String, HashSet<Integer>, double[]>> playerKeySets) {
        factory.updateAllActiveObjects("spaceship", playerKeySets);
    }

    private void collectPlayer(Tuple.T3<String, HashSet<Integer>, double[]> playerKeySet) {
        factory.updateActiveObject("spaceship", playerKeySet);
    }

    private void updateObjects(List<Tuple.T2<String, List<double[]>>> objectVectors) {
        factory.updatePassiveObjects(objectVectors);
    }

    public Tuple.T3<String, HashSet<Integer>, double[]> getPlayerKeyEvents() {
        return new Tuple.T3<>(user.USER_ID, game.getSpaceShip().getKeyEventSet(), game.getSpaceShip().getObjParameters());
    }

    public List<Tuple.T3<String, HashSet<Integer>, double[]>> getAllPlayersKeyEvents() {
        List<Tuple.T3<String, HashSet<Integer>, double[]>> keyList = new ArrayList<>();
        game.getPlayers().forEach((s, spaceship) -> keyList.add(new Tuple.T3<>(s, spaceship.getKeyEventSet(), spaceship.getObjParameters())));
        keyList.add(getPlayerKeyEvents());
        return keyList;
    }

    public byte[] getHostDeltas() {
        List<Tuple.T2<String, List<double[]>>> objectList = new ArrayList<>(game.objectDeltaMapper.mappedObjects);
        List<Tuple.T3<String, HashSet<Integer>, double[]>> keyEventList = getAllPlayersKeyEvents();
        return SerializationUtils.serialize(
                new GameplayDeltas(System.currentTimeMillis(), keyEventList, objectList));
    }

    @Override
    public void injectDeltas(GameplayDeltas gameplayDeltas) {
        collectPlayers(gameplayDeltas.keyEventList);
        updateObjects(gameplayDeltas.objectList);
    }

    public synchronized void collectPlayerDeltas(HashMap<String, GameplayDeltas> deltas) {
        for (GameplayDeltas delta : deltas.values()) {
            collectPlayer(delta.clientKeyEvents);
        }
    }

}
