package nl.rug.aoop.asteroids.util.database;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.nio.file.Path;
import java.util.List;

/**
 * This class manages the DataBase
 */
public class DatabaseManager {

    public static final String EXTENSION = ".odb";
    public static final String FOLDER = "db";

    private EntityManagerFactory managerFactory;
    private EntityManager manager;

    /**
     * The constructor initialise the database
     */
    public DatabaseManager(String dbName) {
        initDatabase(dbName);
    }

    /**
     * This method initializes the database
     *
     * @param filename the name of the database file
     */
    private void initDatabase(String filename){
        Path dbPath = Path.of(FOLDER, filename, filename + EXTENSION);
        managerFactory = Persistence.createEntityManagerFactory(dbPath.toString());
        manager = managerFactory.createEntityManager();
    }

    /**
     * This method performs closing operations and can be called when application stops
     */
    public void closeDatabase(){
        manager.close();
        managerFactory.close();
    }

    /**
     * This method adds a score to the database
     *
     * @param score The score to add to the database
     */
    public void addScore(Score score){
        manager.getTransaction().begin();
        manager.persist(score);
        manager.getTransaction().commit();
    }

    /**
     * This method retrieves all the scores from the database
     *
     * @return A collection of all the scores in the database
     */
    public List<Score> getAllScores(){
        var query = manager.createQuery("SELECT s FROM Score s", Score.class);
        return query.getResultList();
    }
}
