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


/**
 * DefaultHandshake class - is a utility class for performing a mediocre SYN-ACK alternative
 * used to obtain connection parameters. Here during handshake, the user receives his id
 * for the gameplay session which is acknowledged across the network, by other users as well.
 * The id is used for various gameplay processes.
 * This is mainly showcasing an approach (since we're not fully profiting by sending other
 * essential parameters)
 */
@Log
public class DefaultHandshake {
    private final DatagramSocket socket;
    /**
     * Size in bytes of the handshake packet, and gameplay packets as well. But this size
     * can be altered during the process by reporting config files during the gameplay
     */
    public static final int HANDSHAKE_LEN = 4000;

    public DefaultHandshake(DatagramSocket socket) {
        this.socket = socket;
    }

    /**
     *
     * @param parameters local parameters
     * @return new IO handler class
     */
    public IOProtocol handshake(ConnectionParameters parameters) {
        IOProtocol io = new IO(parameters);
        io.send(SerializationUtils.serialize(parameters.getConnectionTypeRequest()));
        DatagramPacket handshake = new DatagramPacket(new byte[HANDSHAKE_LEN], HANDSHAKE_LEN);
        try {
            socket.receive(handshake);
        } catch (IOException e) {
            log.warning("Handshake protocol failed!");
            return null;
        }
       return passNewIOConfigs(io, handshake);
    }

    /**
     *
     * @param io - IO handler
     * @param handshake received packet
     * @return - new IO handler with updated settings
     */
    private IOProtocol passNewIOConfigs(IOProtocol io, DatagramPacket handshake) {
        ConfigData configData = SerializationUtils.deserialize(handshake.getData());
        log.info("Admin: your private port : " + configData.port);
        IO privateIO = new IO(
                new ConnectionParameters(io.getSocket(),
                        new InetSocketAddress(configData.hostAddress,configData.port),HANDSHAKE_LEN));
        privateIO.getPackageHandler().initHandler(configData);
        return privateIO;
    }
}
