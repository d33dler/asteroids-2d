package nl.rug.aoop.asteroids.network.data;

import lombok.Getter;
import lombok.Setter;
import nl.rug.aoop.asteroids.network.data.types.DeltasData;
import org.apache.commons.lang3.SerializationUtils;

import javax.swing.text.Utilities;
import java.io.Serializable;

/**
 * DataPackage is another decorator to manage DeltasData, currently used as a wrapper
 * since latency is not an issue on local networks.
 */
public class DataPackage implements Serializable {

    @Getter
    private DeltasData data = null;
    @Getter
    private byte[] byteData = null;

    /**
     * Creates a new data handler with DeltasData payload.
     */
    public DataPackage(DeltasData data, long bound) {
        setData(data, bound);
    }

    public DataPackage() {
    }

    public void setData(DeltasData data, long bound) {
        this.data = data;

    }

    public void setData(byte[] data) {
        this.byteData = data;
    }

    public long getLatency(DeltasData data) {
        return System.currentTimeMillis() - data.getTimeStamp();
    }

    public long getLatency() {
        return System.currentTimeMillis() - data.getTimeStamp();
    }

    public boolean isAcceptedLatency(DeltasData data, long bound) {
        return getLatency(data) <= bound;
    }

    public byte[] getByteData() {
        return byteData;
    }
    public byte[] serializeDeltas() {
        return SerializationUtils.serialize(data);
    }
}
