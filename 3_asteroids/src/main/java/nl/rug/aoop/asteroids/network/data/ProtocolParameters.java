package nl.rug.aoop.asteroids.network.data;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.net.InetAddress;
import java.net.InetSocketAddress;


@NoArgsConstructor
public class ProtocolParameters {
    private InetSocketAddress receptorAddress = null;
    @Getter
    private int receptorPort, dataLength;
    public final static int PKG_SIZE_LIM = 4096;

    public ProtocolParameters(InetSocketAddress address, int dataLength) {
        this.receptorAddress = address;
        this.receptorPort = address.getPort();
        this.dataLength = dataLength;
    }

    public InetAddress getInet(){
        return receptorAddress.getAddress();
    }
    public void updateDataLength(int size) {
        dataLength = Math.min(size, PKG_SIZE_LIM);
    }
}
