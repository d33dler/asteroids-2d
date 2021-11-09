package nl.rug.aoop.asteroids.network.protocol;

import lombok.Getter;
import nl.rug.aoop.asteroids.network.data.ConnectionParameters;
import nl.rug.aoop.asteroids.network.data.DataPackage;
import nl.rug.aoop.asteroids.network.data.PackageHandler;
import nl.rug.aoop.asteroids.network.data.types.DeltasData;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

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
        try {
            socket.setSoTimeout(ConnectionParameters.CONNECTION_TIMEOUT);
        } catch (SocketException e) {
            e.printStackTrace();
        }
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
        DatagramPacket packet = new DatagramPacket(data, data.length, packageHandler.getInet(), packageHandler.getPort());
        try {
            socket.send(packet);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean receive() {
        byte[] data = new byte[length];
        DatagramPacket packet = new DatagramPacket(data, data.length);
        try {
            socket.receive(packet);
            packageHandler.updateInDataPackage(packet.getData());  //TODO verify
        } catch (IOException e) {
            return false;
        }
        return true;
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
