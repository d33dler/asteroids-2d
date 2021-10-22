package nl.rug.aoop.asteroids.network.data.deltas_changes;
import nl.rug.aoop.asteroids.network.data.types.DeltasData;

import java.io.Serializable;
import java.util.List;

public class ConfigData implements Serializable, DeltasData {
    //public int data_size; //TODO
    public List<Tuple.T2<String, Integer >> setup;

    public String id, hostAddress;
    public int port,data_size;

    public ConfigData(List<Tuple.T2<String, Integer >> setup) {
        this.setup = setup;
    }
    public ConfigData(String id, String address, int privatePort) {
        this.port = privatePort;
        this.hostAddress = address;
        this.id = id;
    }
}


