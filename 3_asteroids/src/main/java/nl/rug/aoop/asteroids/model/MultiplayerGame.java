package nl.rug.aoop.asteroids.model;

import lombok.Getter;
import nl.rug.aoop.asteroids.network.data.ConnectionParameters;

public class MultiplayerGame {
    @Getter
    private ConnectionParameters parameters;
    @Getter
    private final int MAX_CLIENTS = 10;
}
