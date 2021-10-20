package nl.rug.aoop.asteroids.network.data.deltas_changes;
import nl.rug.aoop.asteroids.network.data.types.DeltasData;

import java.io.Serializable;
import java.util.HashMap;

public class ConfigData implements Serializable, DeltasData {
    //public int data_size; //TODO
    public HashMap<String, Integer > setup;

    public ConfigData(HashMap<String, Integer > setup) {
        this.setup = setup;
    }

}


