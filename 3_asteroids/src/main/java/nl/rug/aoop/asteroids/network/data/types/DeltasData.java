package nl.rug.aoop.asteroids.network.data.types;

public interface DeltasData {
    default long getTimeStamp() {return 0;};
    void injectChanges(DeltaManager manager);
}
