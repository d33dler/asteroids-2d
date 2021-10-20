package nl.rug.aoop.asteroids.network.data.types;

public interface DeltasData {
    default long getTimeStamp() {return 0;};
    default void injectChanges(DeltaManager manager){};
}
