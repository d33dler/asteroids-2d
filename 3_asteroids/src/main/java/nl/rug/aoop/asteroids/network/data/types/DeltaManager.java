package nl.rug.aoop.asteroids.network.data.types;

import nl.rug.aoop.asteroids.network.data.deltas_changes.ConfigData;
import nl.rug.aoop.asteroids.network.data.deltas_changes.GameplayDeltas;
import nl.rug.aoop.asteroids.network.data.deltas_changes.Tuple;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public interface DeltaManager {
    void injectDeltas(GameplayDeltas gameplayDeltas);
    void collectPlayerDeltas(HashMap<String, GameplayDeltas> deltas);
    List<Tuple.T2<String, HashSet<Integer>>> getAllPlayersKeyEvents();
    byte[] getHostDeltas();
    Tuple.T2<String, HashSet<Integer>> getPlayerKeyEvents();
}
