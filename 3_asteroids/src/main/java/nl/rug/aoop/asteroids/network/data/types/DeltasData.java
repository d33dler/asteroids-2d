package nl.rug.aoop.asteroids.network.data.types;

import java.io.Serializable;

/**
 * Any data packet sent over the network must implement DeltasData in order to communicate
 * through the DeltaManager with the applet on the local machine
 */
public interface DeltasData extends Serializable {
    default long getTimeStamp() {return 0;};
    void injectChanges(DeltaManager manager);
}
