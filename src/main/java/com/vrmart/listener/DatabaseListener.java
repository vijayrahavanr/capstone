package com.vrmart.listener;

import com.vrmart.util.DatabaseConnection;
import com.vrmart.util.DatabaseInitializer;
import com.zaxxer.hikari.HikariDataSource;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

/**
 * Owns the lifecycle of the VR Mart database connection pool.
 */
@WebListener
public final class DatabaseListener implements ServletContextListener {

    /** Servlet context attribute containing the data source. */
    public static final String DATA_SOURCE_ATTRIBUTE =
            "vrMartDataSource";

    /** Railway PostgreSQL connection URL. */
    private static final String DATABASE_URL_ENV =
            "DATABASE_URL";

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
                            + "initialized successfully.");

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
     * Loads database configuration from Railway DATABASE_URL.
     *
     * @return database properties
     */
    private Properties loadDatabaseProperties() {

        final String databaseUrl =
                System.getenv(DATABASE_URL_ENV);

        if (databaseUrl == null
                || databaseUrl.isBlank()) {

            throw new IllegalStateException(
                    "DATABASE_URL environment variable is not configured.");
        }

        final URI databaseUri =
                parseDatabaseUri(databaseUrl.trim());

        final String host =
                databaseUri.getHost();

        final int port =
                databaseUri.getPort();

        final String databaseName =
                extractDatabaseName(databaseUri);

        final String jdbcUrl =
                buildJdbcUrl(
                        databaseUri,
                        host,
                        port,
                        databaseName);

        final String[] credentials =
                extractCredentials(databaseUri);

        final Properties properties =
                new Properties();

        properties.setProperty(
                "db.url",
                jdbcUrl);

        properties.setProperty(
                "db.username",
                credentials[0]);

        properties.setProperty(
                "db.password",
                credentials[1]);

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

    /**
     * Parses the Railway PostgreSQL URL.
     *
     * @param databaseUrl Railway DATABASE_URL
     * @return parsed URI
     */
    private URI parseDatabaseUri(
            final String databaseUrl) {

        String normalizedUrl = databaseUrl;

        if (normalizedUrl.startsWith("postgres://")) {
            normalizedUrl =
                    "postgresql://"
                            + normalizedUrl.substring(
                            "postgres://".length());
        }

        if (normalizedUrl.startsWith("jdbc:postgresql://")) {
            normalizedUrl =
                    normalizedUrl.substring(
                            "jdbc:".length());
        }

        try {
            return URI.create(normalizedUrl);
        } catch (IllegalArgumentException exception) {
            throw new IllegalStateException(
                    "Invalid DATABASE_URL configuration.",
                    exception);
        }
    }

    /**
     * Extracts the database name from the URI path.
     *
     * @param databaseUri database URI
     * @return database name
     */
    private String extractDatabaseName(
            final URI databaseUri) {

        final String path =
                databaseUri.getPath();

        if (path == null
                || path.length() <= 1) {

            throw new IllegalStateException(
                    "DATABASE_URL does not contain a database name.");
        }

        return path.substring(1);
    }

    /**
     * Builds a PostgreSQL JDBC URL.
     *
     * @param databaseUri database URI
     * @param host database host
     * @param port database port
     * @param databaseName database name
     * @return JDBC URL
     */
    private String buildJdbcUrl(
            final URI databaseUri,
            final String host,
            final int port,
            final String databaseName) {

        if (host == null || host.isBlank()) {
            throw new IllegalStateException(
                    "DATABASE_URL does not contain a database host.");
        }

        if (port <= 0) {
            throw new IllegalStateException(
                    "DATABASE_URL does not contain a valid database port.");
        }

        final StringBuilder jdbcUrl =
                new StringBuilder();

        jdbcUrl.append("jdbc:postgresql://")
                .append(host)
                .append(":")
                .append(port)
                .append("/")
                .append(databaseName);

        if (databaseUri.getRawQuery() != null
                && !databaseUri.getRawQuery().isBlank()) {

            jdbcUrl.append("?")
                    .append(databaseUri.getRawQuery());
        }

        return jdbcUrl.toString();
    }

    /**
     * Extracts username and password from the URI.
     *
     * @param databaseUri database URI
     * @return username and password
     */
    private String[] extractCredentials(
            final URI databaseUri) {

        final String userInfo =
                databaseUri.getRawUserInfo();

        if (userInfo == null
                || userInfo.isBlank()) {

            throw new IllegalStateException(
                    "DATABASE_URL does not contain database credentials.");
        }

        final int separator =
                userInfo.indexOf(':');

        if (separator <= 0) {
            throw new IllegalStateException(
                    "DATABASE_URL contains invalid database credentials.");
        }

        final String username =
                decode(userInfo.substring(0, separator));

        final String password =
                decode(userInfo.substring(separator + 1));

        if (username.isBlank()) {
            throw new IllegalStateException(
                    "Database username is empty.");
        }

        return new String[] {
                username,
                password
        };
    }

    /**
     * URL-decodes a database credential.
     *
     * @param value encoded value
     * @return decoded value
     */
    private String decode(
            final String value) {

        return URLDecoder.decode(
                value,
                StandardCharsets.UTF_8);
    }
}
