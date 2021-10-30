package nl.rug.aoop.asteroids.network.data.deltas_changes;

import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * Tuple - generic class implementing the tuple data structure
 * used for convenience of coupling bound data / objects.
 * @param <K> - any object held by the tuple data structure
 */
@NoArgsConstructor
public class Tuple<K> implements Serializable {
    public K a;
    public Tuple(K k) {
        this.a = k;
    }
    @NoArgsConstructor
    public static class T2<K,V> extends Tuple<K> {
        public V b;

        public T2(K x, V b) {
            super(x);
            this.b = b;
        }
    }
    @NoArgsConstructor
    public static class T3<K,V,Z> extends Tuple.T2<K,V> {
        public Z c;

        public T3(K x, V y, Z c) {
            super(x,y);
            this.c = c;
        }

        public static <K,V,Z> T2<V, Z> lstT2(T3<K,V,Z> t3) {
            return new Tuple.T2<>(t3.b,t3.c);
        }
    }


}
