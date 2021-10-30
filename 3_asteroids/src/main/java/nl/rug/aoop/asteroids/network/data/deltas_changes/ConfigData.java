package nl.rug.aoop.asteroids.network.data.deltas_changes;
import nl.rug.aoop.asteroids.network.data.types.DeltaManager;
import nl.rug.aoop.asteroids.network.data.types.DeltasData;

import java.io.Serializable;
import java.util.List;

/**
 * ConfigData is a wrapper class for data sent during the handshake process
 */
public class ConfigData implements DeltasData {
    //public int data_size; //TODO
    public List<Tuple.T2<String, Integer >> setup;

    public String id, hostAddress, connectionRequest;
    public int port,data_size;

    public ConfigData(String requestConnection) {
        this.connectionRequest = requestConnection;
    }
    public ConfigData(List<Tuple.T2<String, Integer >> setup) {
        this.setup = setup;
    }
    public ConfigData(String id, String address, int privatePort) {
        this.port = privatePort;
        this.hostAddress = address;
        this.id = id;
    }

    @Override
    public void injectChanges(DeltaManager manager) {
        //any changes requested by the server to be reflected on the user through DeltaManager
        //e.g. game mode change / or any requested change outside the current gameplay
    }
}


