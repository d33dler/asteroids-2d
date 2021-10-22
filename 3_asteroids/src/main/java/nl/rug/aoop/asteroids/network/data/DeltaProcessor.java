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

    private void collectPlayers(List<Tuple.T2<String, HashSet<Integer>>> playerKeySets) {
        for (int i = playerKeySets.size() - 1; i >= 0; i--) {
            factory.createNewObject(playerKeySets.get(i).a, playerKeySets.get(i).b);
        }
    }

    private void collectPlayer(Tuple.T2<String, HashSet<Integer>> playerKeySet) {
        factory.createNewObject(playerKeySet.a, playerKeySet.b);
    }

    private void updateObjects(List<GameObject> objectVectors) { //TODO needs 1 obj per tuple?
        for (GameObject object : objectVectors) {
            game.getAsteroidsCache().add((Asteroid) object);
        }
    }

    public Tuple.T2<String, HashSet<Integer>> getPlayerKeyEvents() {
        return new Tuple.T2<>(user.USER_ID, game.getSpaceShip().getKeyEventSet());
    }

    public List<Tuple.T2<String, HashSet<Integer>>> getAllPlayersKeyEvents() {
        List<Tuple.T2<String, HashSet<Integer>>> keyList = new ArrayList<>();
        game.getPlayers().forEach((s, spaceship) -> keyList.add(new Tuple.T2<>(s, spaceship.getKeyEventSet())));
        return keyList;
    }

    public byte[] getHostDeltas() { //TODO move to deltaprocessing
        List<GameObject> objectList = new ArrayList<>(game.getAsteroids());
        List<Tuple.T2<String, HashSet<Integer>>> keyEventList = getAllPlayersKeyEvents();
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
