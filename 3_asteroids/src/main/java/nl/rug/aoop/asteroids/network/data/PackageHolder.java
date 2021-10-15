package nl.rug.aoop.asteroids.network.data;

import lombok.Getter;
import lombok.Setter;
import nl.rug.aoop.asteroids.network.data.types.ConfigData;
import org.apache.commons.lang3.SerializationUtils;

import java.net.DatagramPacket;
import java.net.InetAddress;

/**
 * PackageHolder class : (uses Decorator pattern)
 */
public class PackageHolder {

    @Getter
    @Setter
    private DataPackage dataPackage = null;
    @Getter
    private final ConnectionParameters parameters;
    @Setter
    private byte[] data;

    private PackageHolder(DataPackage dataPackage, ConnectionParameters parameters) {
        this(parameters);
        this.dataPackage = dataPackage;
    }

    private PackageHolder(ConnectionParameters parameters) {
        this.parameters = parameters;
        data = new byte[parameters.getDataLength()];
    }

    public static PackageHolder newEmptyHolder(ConnectionParameters param) {
        return new PackageHolder(param);

    }

    public byte[] getDataInBytes() {
        byte[] data = SerializationUtils.serialize(dataPackage);
        if (data.length <= parameters.getDataLength()) {
            return data;
        }
        return null;
    }

    public void updateDataPackage() {
        DataPackage newPackage = SerializationUtils.deserialize(data);
        if (newPackage.isAcceptedLatency(parameters.LAT_MAX_millis)) { //TODO verify
            dataPackage = newPackage;
        }
    }

    public int getPort() {
        return parameters.getReceptorPort();
    }

    public void loadHandshakeConfigs(DatagramPacket packet) {
        try {
            dataPackage = SerializationUtils.deserialize(packet.getData());
            ConfigData config = dataPackage.getBody();
            parameters.updateDataLength(config.data_size);
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
