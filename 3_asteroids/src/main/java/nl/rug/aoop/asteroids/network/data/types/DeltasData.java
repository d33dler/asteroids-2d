package nl.rug.aoop.asteroids.network.data.types;

import java.io.Serializable;

public interface DeltasData extends Serializable {
    default long getTimeStamp() {return 0;};
    default void injectChanges(DeltaManager manager){};
}
