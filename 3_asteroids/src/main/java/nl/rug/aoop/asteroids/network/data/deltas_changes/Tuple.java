package nl.rug.aoop.asteroids.network.data.deltas_changes;

import java.io.Serializable;

public class Tuple<K> implements Serializable {
    public final K x;
    public Tuple(K k) {
        this.x = k;
    }
    public static class T2<K,V> extends Tuple<K> {
        public final V y;

        public T2(K x, V y) {
            super(x);
            this.y = y;
        }
    }

    public static class T3<K,V,Z> extends Tuple.T2<K,V> {
        public final Z z;

        public T3(K x, V y, Z z) {
            super(x,y);
            this.z = z;
        }
    }

}
