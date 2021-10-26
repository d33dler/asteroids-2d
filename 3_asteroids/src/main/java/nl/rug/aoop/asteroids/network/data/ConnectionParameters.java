package nl.rug.aoop.asteroids.network.data;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;


@NoArgsConstructor
public class ConnectionParameters {
    @Getter
    private DatagramSocket callerSocket;
    @Getter
    private InetSocketAddress receptorAddress;
    @Getter
    private int receptorPort;
    @Getter
    @NetworkParam(id = "packet_size")
    private int dataLength = PKG_SIZE_LIM;
    public final static int PKG_SIZE_LIM = 5000, PKG_SIZE_MIN = 1024;
    @Setter
    public int LAT_MAX_millis = 100;
    public int LAT_SERVER_millis = 50;
    /**
     * Maximum allowed packet loss in percentages
     */
    @Getter
    public int MAX_PACKET_LOSS = 50;

    public final static int CONNECTION_TIMEOUT = 0x1388;

    public ConnectionParameters(DatagramSocket callerSocket,
                                InetSocketAddress receptor, int dataLength) {
        this.callerSocket = callerSocket;
        this.receptorAddress = receptor;
        this.receptorPort = receptor.getPort();
        updateDataLength(dataLength);
    }

    public static ConnectionParameters rawDataParameters(){
        return new ConnectionParameters();
    }

    public InetAddress getInet() {
        return receptorAddress.getAddress();
    }

    public void updateDataLength(int size) {
        if (size <= PKG_SIZE_LIM && size != 0) {
            dataLength = size;
        } else dataLength = PKG_SIZE_LIM;
    }
}
