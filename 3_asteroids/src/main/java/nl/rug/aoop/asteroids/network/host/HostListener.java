package nl.rug.aoop.asteroids.network.host;

public interface HostListener {
    void fireUpdate(byte[] data);

}
