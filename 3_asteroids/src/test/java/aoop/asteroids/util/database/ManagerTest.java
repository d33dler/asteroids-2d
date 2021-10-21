package aoop.asteroids.util.database;

import nl.rug.aoop.asteroids.util.database.DatabaseManager;
import nl.rug.aoop.asteroids.util.database.Score;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ManagerTest {

    @Test
    void ScoreTest(){
        Score scoreTest = new Score("player", 200);
        assertNotNull(scoreTest.getId());
        assertEquals("player", scoreTest.getPlayerName());
        assertEquals(200, scoreTest.getScore());
        System.out.println(scoreTest);
    }

    @Test
    void IdTest(){
        ArrayList<Score> scores = new ArrayList<>();
        for (int idx = 0; idx < 100; idx++) {
            scores.add(new Score("player" + idx, 200+idx));
        }
        scores.forEach(System.out::println);
    }

    @Test
    void DBTest(){
        DatabaseManager manager = DatabaseManager.getInstance();
        assertNotNull(manager);
        Score score1 = new Score("player1", 200);
        Score score2 = new Score("player2", 201);
        manager.addScore(score1);
        manager.addScore(score2);
        List<Score> retrievedScores = manager.getAllScores();
        assertNotNull(retrievedScores);
        retrievedScores.forEach(score -> System.out.println(score.toString()));
    }

    @Test
    void getListTest(){
        DatabaseManager manager = DatabaseManager.getInstance();
        assertNotNull(manager);
        List<Score> retrievedScores = manager.getAllScores();
        assertNotNull(retrievedScores);
        retrievedScores.forEach(score -> System.out.println(score.toString()));
    }

    @Test
    void liveDBCheck(){
        DatabaseManager manager = DatabaseManager.getInstance();

        List<Score> retrievedScores = manager.getAllScores();
        retrievedScores.forEach(score -> System.out.println(score.toString()));
    }
}
