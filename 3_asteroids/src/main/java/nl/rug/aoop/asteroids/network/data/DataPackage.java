package nl.rug.aoop.asteroids.network.data;

import lombok.Getter;
import nl.rug.aoop.asteroids.network.data.types.DeltasData;

import java.io.Serializable;

public class DataPackage implements Serializable {
    @Getter
    private final DeltasData data;

    /**
     * Creates a new data handler with DeltasData payload.
     */
    public DataPackage(DeltasData data) {
        this.data = data;
    }

    public long getLatency() {
        return System.currentTimeMillis() - data.getTimeStamp();
    }
    public boolean isAcceptedLatency(long bound) {
        return getLatency() <= bound;
    }
}
