package nl.rug.aoop.asteroids.util.database;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.io.Serial;
import java.io.Serializable;
import java.util.UUID;

/**
 * This class represents the score of a player
 */
@Getter
@Setter
@Entity
public class Score implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    private final String id = UUID.randomUUID().toString();

    private String playerName;
    private long score;

    /**
     * Empty Constructor
     */
    public Score(){}

    /**
     * Constructor which assigns a player name and the relative score obtained during one game
     *
     * @param playerName The name of the player who obtained this score
     * @param score The score
     */
    public Score(String playerName, long score){
        this.playerName = playerName;
        this.score = score;
    }

    @Override
    public String toString() {
        return "Score{" +
                "id=" + id +
                ", playerName='" + playerName + '\'' +
                ", score=" + score +
                '}';
    }
}
