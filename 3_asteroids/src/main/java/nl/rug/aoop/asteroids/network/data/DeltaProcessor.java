package nl.rug.aoop.asteroids.network.data;

import nl.rug.aoop.asteroids.model.game.Game;
import nl.rug.aoop.asteroids.model.MultiplayerManager;
import nl.rug.aoop.asteroids.model.game.GameResources;
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

/**
 * DeltaProcessor class - dispatches all gameplay changes occurring in the online game
 * environment by calling method classes that update the local game state
 *
 */
public class DeltaProcessor implements DeltaManager {

    private final Game game;
    private final User user;
    private final MultiplayerManager multiplayerBase;
    private final GameObjectFactory factory;
    private final GameResources resources;

    public DeltaProcessor(MultiplayerManager manager, User user) {
        this.game = manager.getGame();
        this.resources = game.getResources();
        this.user = user;
        this.multiplayerBase = manager;
        this.factory = resources.getObjectFactory();
    }

    /**
     *
     * @param playerDeltaSet list of all deltas for all players in the pool
     */
    private void collectPlayers(List<Tuple.T3<Tuple.T2<String,String>, HashSet<Integer>, double[]>> playerDeltaSet) {
        factory.updateAllActiveObjects("spaceship", playerDeltaSet);
    }

    /**
     *
     * @param playerDeltaSet set of single user delta data
     */
    private void collectPlayer(Tuple.T3<Tuple.T2<String,String>, HashSet<Integer>, double[]> playerDeltaSet) {
        factory.updateActiveObject("spaceship", playerDeltaSet);
    }

    /**
     *
     * @param objectVectors set of passive objects (uncontrolled) deltas
     */
    private void updateObjects(List<Tuple.T2<String, List<double[]>>> objectVectors) {
        factory.updatePassiveObjects(objectVectors);
    }

    /**
     *
     * @return the local user's deltas (used by the client)
     */
    public Tuple.T3<Tuple.T2<String,String>, HashSet<Integer>, double[]> getPlayerDeltas() {
        return new Tuple.T3<>(new Tuple.T2<>(user.USER_ID, user.USER_NICK),
                resources.getSpaceShip().getKeyEventSet(), resources.getSpaceShip().getObjParameters());
    }

    /**
     *
     * @return all player pool deltas (used by the host)
     */
    public List<Tuple.T3<Tuple.T2<String,String>, HashSet<Integer>, double[]>> getAllPlayerDeltas() {
        List<Tuple.T3<Tuple.T2<String,String>, HashSet<Integer>, double[]>> keyList = new ArrayList<>();
        resources.getPlayers().forEach((s, spaceship) ->
                keyList.add(new Tuple.T3<>(new Tuple.T2<>(s,spaceship.getNickId()),
                        spaceship.getKeyEventSet(), spaceship.getObjParameters())));
        return keyList;
    }

    /**
     *
     * @return returns all members deltas
     */
    public byte[] getHostDeltas() {
        List<Tuple.T2<String, List<double[]>>> objectList = new ArrayList<>(game.objectDeltaMapper.mappedObjects);
        List<Tuple.T3<Tuple.T2<String,String>, HashSet<Integer>, double[]>> keyEventList = getAllPlayerDeltas();
        return SerializationUtils.serialize(
                new GameplayDeltas(keyEventList, objectList));
    }

    /**
     *
     * @param gameplayDeltas updates the local state based on the deltas received (used by user)
     */
    @Override
    public void injectDeltas(GameplayDeltas gameplayDeltas) {
        collectPlayers(gameplayDeltas.clientsPoolDeltas);
        updateObjects(gameplayDeltas.objectMapping);
    }

    /**
     *
     * @param deltas all player deltas( used by host)
     */
    public synchronized void collectPlayerDeltas(HashMap<String, GameplayDeltas> deltas) {
        for (GameplayDeltas delta : deltas.values()) {
            collectPlayer(delta.ownerClientDeltas);
        }
    }

}
