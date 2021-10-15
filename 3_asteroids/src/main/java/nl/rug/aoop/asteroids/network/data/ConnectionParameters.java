package nl.rug.aoop.asteroids.network.data;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;


@NoArgsConstructor
public class ConnectionParameters {
    private SocketAddress socketAddress;
    @Getter
    private InetSocketAddress callerAddress,receptorAddress;
    @Getter
    private int callerPort, receptorPort, dataLength;
    public final static int PKG_SIZE_LIM = 8192, PKG_SIZE_MIN = 1024;
    @Setter
    public int LAT_MAX_millis = 100;
    public int LAT_SERVER_millis = 10;

    public ConnectionParameters(InetSocketAddress caller, InetSocketAddress receptor, int dataLength) {
        this.receptorAddress = receptor;
        this.callerAddress = caller;
        this.receptorPort = receptor.getPort();
        this.callerPort = caller.getPort();
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
