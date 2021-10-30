package nl.rug.aoop.asteroids.network.protocol;

import lombok.extern.java.Log;
import nl.rug.aoop.asteroids.network.data.ConnectionParameters;
import nl.rug.aoop.asteroids.network.data.PackageHandler;
import nl.rug.aoop.asteroids.network.data.deltas_changes.ConfigData;
import org.apache.commons.lang3.SerializationUtils;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;

@Log

public class DefaultHandshake {
    private final DatagramSocket socket;
    public static final int HANDSHAKE_LEN = 4000;

    public DefaultHandshake(DatagramSocket socket) {
        this.socket = socket;
    }

    public IOProtocol handshake(ConnectionParameters parameters) {
        IO io = new IO(parameters);
        io.send(SerializationUtils.serialize(parameters.getConnectionTypeRequest()));
        DatagramPacket handshake = new DatagramPacket(new byte[HANDSHAKE_LEN], HANDSHAKE_LEN);
        try {
            socket.receive(handshake);
            System.out.println("got here");
        } catch (IOException e) {
            e.printStackTrace();
            log.warning("Handshake protocol failed");
            return null;
        }
       return passNewIOConfigs(io, handshake);
    }

    private IO passNewIOConfigs(IO io, DatagramPacket handshake) {
        ConfigData configData = SerializationUtils.deserialize(handshake.getData());
        System.out.println("YOUR private PORT: " +configData.port);
        IO privateIO = new IO(
                new ConnectionParameters(io.getSocket(),
                        new InetSocketAddress(configData.hostAddress,configData.port),HANDSHAKE_LEN));
        privateIO.getPackageHandler().initHandler(configData);
        return privateIO;
    }
}
