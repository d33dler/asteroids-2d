package nl.rug.aoop.asteroids.network.data.deltas_changes;
import nl.rug.aoop.asteroids.network.data.types.DeltasData;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

public class ConfigData implements Serializable, DeltasData {
    //public int data_size; //TODO
    public List<Tuple.T2<String, Integer >> setup;

    public ConfigData(List<Tuple.T2<String, Integer >> setup) {
        this.setup = setup;
    }

}


