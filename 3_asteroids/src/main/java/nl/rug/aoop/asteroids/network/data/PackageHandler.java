package nl.rug.aoop.asteroids.network.data;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.java.Log;
import nl.rug.aoop.asteroids.network.data.deltas_changes.ConfigData;
import nl.rug.aoop.asteroids.network.data.types.DeltasData;
import org.apache.commons.lang3.SerializationUtils;

import java.net.InetAddress;


/**
 * PackageHolder class : (uses Decorator pattern)
 */
@Log
public class PackageHandler {

    @Getter
    @Setter
    private DataPackage outPackage;
    @Getter
    @Setter
    private DataPackage inPackage;

    @Getter
    private final ConnectionParameters parameters;

    @Setter
    @Getter
    private String ownerId = "user";

    private PackageHandler(DataPackage dataPackage, ConnectionParameters parameters) {
        this(parameters);
        this.inPackage = dataPackage;
    }

    private PackageHandler(ConnectionParameters p) {
        parameters = p;
        outPackage = new DataPackage();
        inPackage = new DataPackage();
    }

    public static PackageHandler newEmptyHolder(ConnectionParameters param) {
        return new PackageHandler(param);
    }

    public byte[] getOutDataInBytes() {
        byte[] data = outPackage.serializeDeltas();
        if (data.length <= parameters.getDataLength()) {
            return data;
        }
        return null;
    }

    public void updateInDataPackage(byte[] data) {
        dataPkgUpdate(inPackage, data);
    }

    public void updateOutDataPackage(DeltasData data) {
        outPackage.setData(data, parameters.LAT_MAX_millis);
    }

    public void updateOutDataPackage(byte[] data) {
        outPackage.setData(data);
    }

    private void dataPkgUpdate(DataPackage pkg, byte[] data) {
        DeltasData dd = SerializationUtils.deserialize(data);
        pkg.setData(dd, parameters.LAT_MAX_millis);

    }

    public int getPort() {
        return parameters.getReceptorPort();
    }


    public void initHandler(ConfigData handshake) {
        this.ownerId = handshake.id;
    }

    public InetAddress getInet() {
        return parameters.getInet();
    }

}
