package nl.rug.aoop.asteroids.network.protocol;

import lombok.Getter;
import nl.rug.aoop.asteroids.network.data.ConnectionParameters;
import nl.rug.aoop.asteroids.network.data.DataPackage;
import nl.rug.aoop.asteroids.network.data.PackageHandler;
import nl.rug.aoop.asteroids.network.data.deltas_changes.GameplayDeltas;
import nl.rug.aoop.asteroids.network.data.types.DeltasData;
import org.apache.commons.lang3.SerializationUtils;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class IO implements IOProtocol {
    @Getter
    private final PackageHandler packageHandler;
    @Getter
    private final DatagramSocket socket;

    public final int length;

    public IO(ConnectionParameters parameters) {
        this.socket = parameters.getCallerSocket();
        packageHandler = PackageHandler.newEmptyHolder(parameters);
        this.length = packageHandler.getParameters().getDataLength();
    }

    public void send() {
        byte[] data = packageHandler.getOutDataInBytes();
        DatagramPacket packet = new DatagramPacket(data, data.length, packageHandler.getInet(), packageHandler.getPort());
        try {
            socket.send(packet);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void send(byte[] data) {
        System.out.println("Sending length :" + data.length);
        System.out.println("Sending to : " + packageHandler.getInet() + "  , " + packageHandler.getPort());
        DatagramPacket packet = new DatagramPacket(data, data.length, packageHandler.getInet(), packageHandler.getPort());
        try {
            socket.send(packet);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void receive() {
        byte[] data = new byte[length];
        DatagramPacket packet = new DatagramPacket(data, data.length);
            System.out.println("WAITING for data?");
        try {
            socket.receive(packet);
            packageHandler.updateInDataPackage(packet.getData());  //TODO verify
             System.out.println("RECEIVED NEW DATA +" + SerializationUtils.serialize(packageHandler.getInPackage().getData()).length );
            GameplayDeltas deltas = (GameplayDeltas) packageHandler.getInPackage().getData();
            if(deltas.objectList!=null){
                System.out.println("OBJ list size ==" + deltas.objectList.size()) ;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void updateOutPackage(DeltasData data) {
        packageHandler.updateOutDataPackage(data);
    }

    @Override
    public void updateOutPackage(byte[] data) {
        packageHandler.updateOutDataPackage(data);
    }

    public String getOwnerId() {
        return packageHandler.getOwnerId();
    }

    public DataPackage getLastDataPackage() {
        return packageHandler.getInPackage();
    }
}
