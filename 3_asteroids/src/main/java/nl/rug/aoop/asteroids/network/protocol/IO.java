package nl.rug.aoop.asteroids.network.protocol;

import lombok.Getter;
import nl.rug.aoop.asteroids.network.data.ConnectionParameters;
import nl.rug.aoop.asteroids.network.data.DataPackage;
import nl.rug.aoop.asteroids.network.data.PackageHandler;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;

import static nl.rug.aoop.asteroids.network.protocol.DefaultHandshake.HANDSHAKE_LEN;

public class IO implements IOProtocol {
    @Getter
    private final PackageHandler holder;
    private final DatagramSocket socket;

    public IO(ConnectionParameters parameters) {
        this.socket = parameters.getCallerSocket();
        holder = PackageHandler.newEmptyHolder(parameters);
    }

    public void send() {
        byte[] data = holder.getDataInBytes();
        DatagramPacket packet = new DatagramPacket(data, data.length, holder.getInet(), holder.getPort());
        System.out.println(holder.getInet());
        try {
            socket.send(packet);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void receive() {
        byte[] data = holder.getData();
        DatagramPacket packet = new DatagramPacket(data, data.length);
        try {
            socket.receive(packet);
            holder.updateDataPackage();  //TODO verify
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void updateHolder(byte[] data) {
        holder.setData(data);
    }

    public DataPackage getLastDataPackage() {
        return holder.getDataPackage();
    }
}
