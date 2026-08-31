package com.vrmart.listener;

import com.vrmart.util.DatabaseConnection;
import com.vrmart.util.DatabaseInitializer;
import com.zaxxer.hikari.HikariDataSource;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import java.util.Properties;

/**
 * Owns the lifecycle of the VR Mart database connection pool.
 */
@WebListener
public final class DatabaseListener implements ServletContextListener {

    /** Servlet context attribute containing the data source. */
    public static final String DATA_SOURCE_ATTRIBUTE =
            "vrMartDataSource";

    /** Local VR Mart PostgreSQL database URL. */
    private static final String DATABASE_URL =
            "jdbc:postgresql://localhost:5432/VRMart";

    /** PostgreSQL database username. */
    private static final String DATABASE_USERNAME = "postgres";

    /** PostgreSQL database password. */
    private static final String DATABASE_PASSWORD =
            "vrgt@*";

    /** Maximum HikariCP pool size. */
    private static final String MAXIMUM_POOL_SIZE = "10";

    /** Minimum HikariCP idle connections. */
    private static final String MINIMUM_IDLE = "2";

    /** HikariCP connection timeout. */
    private static final String CONNECTION_TIMEOUT = "30000";

    /** HikariCP idle timeout. */
    private static final String IDLE_TIMEOUT = "600000";

    /** HikariCP maximum lifetime. */
    private static final String MAX_LIFETIME = "1800000";

    /** Database connection pool. */
    private HikariDataSource dataSource;

    /**
     * Initializes the VR Mart database connection pool.
     *
     * @param event servlet context initialization event
     */
    @Override
    public void contextInitialized(
            final ServletContextEvent event) {

        final ServletContext context =
                event.getServletContext();

        try {
            final Properties properties =
                    loadDatabaseProperties();

            dataSource =
                    DatabaseConnection.createDataSource(
                            properties);

            /*
             * Create the database tables before the
             * application starts using the database.
             */
            DatabaseInitializer.initialize(dataSource);

            context.setAttribute(
                    DATA_SOURCE_ATTRIBUTE,
                    dataSource);

            context.log(
                    "VR Mart database connection pool "
                            + "initialized.");

        } catch (Exception exception) {
            context.log(
                    "Failed to initialize VR Mart database "
                            + "connection pool.",
                    exception);

            throw new IllegalStateException(
                    "VR Mart database initialization failed.",
                    exception);
        }
    }

    /**
     * Closes the VR Mart database connection pool.
     *
     * @param event servlet context destruction event
     */
    @Override
    public void contextDestroyed(
            final ServletContextEvent event) {

        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();

            event.getServletContext().log(
                    "VR Mart database connection pool closed.");
        }
    }

    /**
     * Loads local database configuration.
     *
     * @return database properties
     */
    private Properties loadDatabaseProperties() {

        final Properties properties = new Properties();

        properties.setProperty(
                "db.url",
                DATABASE_URL);

        properties.setProperty(
                "db.username",
                DATABASE_USERNAME);

        properties.setProperty(
                "db.password",
                DATABASE_PASSWORD);

        properties.setProperty(
                "db.pool.maximum-size",
                MAXIMUM_POOL_SIZE);

        properties.setProperty(
                "db.pool.minimum-idle",
                MINIMUM_IDLE);

        properties.setProperty(
                "db.pool.connection-timeout",
                CONNECTION_TIMEOUT);

        properties.setProperty(
                "db.pool.idle-timeout",
                IDLE_TIMEOUT);

        properties.setProperty(
                "db.pool.max-lifetime",
                MAX_LIFETIME);

        return properties;
    }
}
