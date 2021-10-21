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
    public static final String DB = "prod";


    private final Path dbPath;
    private static DatabaseManager instance;

    /**
     * Implementation of singleton design pattern for thread safety
     *
     * @return The instance of the DataBase manager
     */
    public static DatabaseManager getInstance(){
        if (instance == null) instance = new DatabaseManager();
        return instance;
    }

    /**
     * Private constructor for singleton pattern
     */
    private DatabaseManager() {
        this.dbPath = Path.of(FOLDER, DatabaseManager.DB, DatabaseManager.DB + EXTENSION);
    }

    /**
     * This method adds a score to the database
     *
     * @param score The score to add to the database
     */
    public synchronized void addScore(Score score){
        EntityManagerFactory emf = Persistence.createEntityManagerFactory(dbPath.toString());
        EntityManager manager = emf.createEntityManager();
        manager.getTransaction().begin();
        manager.persist(score);
        manager.getTransaction().commit();
        manager.close();
        emf.close();
    }

    /**
     * This method retrieves all the scores from the database
     *
     * @return A collection of all the scores in the database
     */
    public synchronized List<Score> getAllScores(){
        EntityManagerFactory emf = Persistence.createEntityManagerFactory(dbPath.toString());
        EntityManager manager = emf.createEntityManager();
        var query = manager.createQuery("SELECT s FROM Score s", Score.class);
        var ret = query.getResultList();
        manager.close();
        emf.close();
        return ret;
    }
}
