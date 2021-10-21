package nl.rug.aoop.asteroids.network.data.types;

import nl.rug.aoop.asteroids.network.data.deltas_changes.ConfigData;
import nl.rug.aoop.asteroids.network.data.deltas_changes.GameplayDeltas;

import java.util.HashMap;

public interface DeltaManager {
    void setupGame(GameplayDeltas gameplayDeltas);
    void collectPlayerDeltas(HashMap<String, GameplayDeltas> deltas);
}
