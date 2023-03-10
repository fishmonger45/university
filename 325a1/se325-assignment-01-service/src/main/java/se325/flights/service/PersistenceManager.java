package se325.flights.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

/**
 * Singleton class that manages an EntityManagerFactory. When a
 * PersistenceManager is instantiated, it creates an EntityManagerFactory. An
 * EntityManagerFactory is required to create an EntityManager, which represents
 * a persistence context (session with a database).
 * <p>
 * When a Web service application component (e.g. a resource object) requires a
 * persistence context, it should call the PersistentManager's
 * createEntityManager() method to acquire one.
 */
public class PersistenceManager {
    private static PersistenceManager instance = null;

    private EntityManagerFactory entityManagerFactory;
    private static final Logger LOGGER = LoggerFactory.getLogger(PersistenceManager.class);

    public static PersistenceManager instance() {
        if (instance == null) {
            instance = new PersistenceManager();
        }
        return instance;
    }

    protected PersistenceManager() {
        init();
    }

    public EntityManager createEntityManager() {
        return entityManagerFactory.createEntityManager();
    }

    private void init() {
        try {
            entityManagerFactory = Persistence.createEntityManagerFactory("se325.flights");
        } catch (Exception ex) {
            LOGGER.error("Failed to create persistence manager", ex);
            throw ex;
        }
    }

    public void close() {
        entityManagerFactory.close();
        entityManagerFactory = null;
    }

    /**
     * Wipes the database.
     */
    public void reset() {
        close();
        init();
    }

}
