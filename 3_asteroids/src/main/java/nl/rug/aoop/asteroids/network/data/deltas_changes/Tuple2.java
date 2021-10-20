package nl.rug.aoop.asteroids.network.data.deltas_changes;

import java.io.Serializable;

public class Tuple2<K,V> implements Serializable {
    public final K k;
    public final V v;

    public Tuple2(K k, V v) {
        this.k = k;
        this.v = v;
    }
}
