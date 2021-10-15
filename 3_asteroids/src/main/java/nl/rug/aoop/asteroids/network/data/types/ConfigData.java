package nl.rug.aoop.asteroids.network.data.types;

import java.io.Serializable;

public class ConfigData implements Serializable {
    public int data_size;

    public ConfigData(int data_size) {
        this.data_size = data_size;
    }
}
