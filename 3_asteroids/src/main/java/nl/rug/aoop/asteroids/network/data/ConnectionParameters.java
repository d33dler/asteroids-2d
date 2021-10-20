package nl.rug.aoop.asteroids.network.data;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.net.InetAddress;
import java.net.InetSocketAddress;


@NoArgsConstructor
public class ConnectionParameters {
    @Getter
    private InetSocketAddress callerAddress, receptorAddress;
    @Getter
    private int callerPort, receptorPort, dataLength;
    public final static int PKG_SIZE_LIM = 8192, PKG_SIZE_MIN = 1024;
    @Setter
    public int LAT_MAX_millis = 100;
    public int LAT_SERVER_millis = 10;
    /**
     * Maximum allowed packet loss in percentages
     */
    @Getter
    public int MAX_PACKET_LOSS = 50;

    public ConnectionParameters(InetSocketAddress caller, InetSocketAddress receptor, int dataLength) {
        this.callerAddress = caller;
        this.callerPort = caller.getPort();
        this.receptorAddress = receptor;
        this.receptorPort = receptor.getPort();
        updateDataLength(dataLength);
    }

    public InetAddress getInet() {
        return receptorAddress.getAddress();
    }

    public void updateDataLength(int size) {
        if (size <= PKG_SIZE_LIM && size != 0) {
            dataLength = size;
        } else dataLength = PKG_SIZE_MIN;
    }
}
