package nl.rug.aoop.asteroids.network.data;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.SerializationException;
import org.apache.commons.lang3.SerializationUtils;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.util.HashMap;

import static nl.rug.aoop.asteroids.network.ConfigCodes.PACKET_SIZE;

/**
 * PackageHolder class : (uses Decorator pattern)
 */
public class PackageHandler {

    @Getter
    @Setter
    private DataPackage dataPackage = null;
    @Getter
    private final ConnectionParameters parameters;
    @Setter
    private byte[] data;

    private PackageHandler(DataPackage dataPackage, ConnectionParameters parameters) {
        this(parameters);
        this.dataPackage = dataPackage;
    }

    private PackageHandler(ConnectionParameters parameters) {
        this.parameters = parameters;
        data = new byte[parameters.getDataLength()];
    }

    public static PackageHandler newEmptyHolder(ConnectionParameters param) {
        return new PackageHandler(param);

    }

    public byte[] getDataInBytes() {
        byte[] data = SerializationUtils.serialize(dataPackage);
        if (data.length <= parameters.getDataLength()) {
            return data;
        }
        return null;
    }

    public void updateDataPackage() {
        try {
            DataPackage newPackage = new DataPackage(SerializationUtils.deserialize(data));
            if (newPackage.isAcceptedLatency(parameters.LAT_MAX_millis)) { //TODO verify
                dataPackage = newPackage;
            }
        } catch (SerializationException e) {
            e.printStackTrace();
        }

    }

    public int getPort() {
        return parameters.getReceptorPort();
    }

    public void loadHandshakeConfigs(HashMap<String, Integer> config) {
        try {
            parameters.updateDataLength(config.get(PACKET_SIZE));
        } catch (Exception e) {
            e.printStackTrace();
        }
        initData();
    }

    public void initData() {
        data = new byte[parameters.getDataLength()];
    }

    public void loadData(DatagramPacket packet) {
        data = packet.getData();
    }

    public InetAddress getInet() {
        return parameters.getInet();
    }

    public byte[] getData() {
        return data;
    }
}
