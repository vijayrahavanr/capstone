package com.vrmart.listener;

import com.vrmart.util.DatabaseConnection;
import com.vrmart.util.DatabaseInitializer;
import com.zaxxer.hikari.HikariDataSource;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

/**
 * Owns the lifecycle of the VR Mart database connection pool.
 */
@WebListener
public final class DatabaseListener
        implements ServletContextListener {

    /** Servlet context attribute containing the data source. */
    public static final String DATA_SOURCE_ATTRIBUTE =
            "vrMartDataSource";

    /** Railway DATABASE_URL environment variable. */
    private static final String DATABASE_URL_ENV =
            "DATABASE_URL";

    /** Railway PostgreSQL host. */
    private static final String PGHOST_ENV =
            "PGHOST";

    /** Railway PostgreSQL port. */
    private static final String PGPORT_ENV =
            "PGPORT";

    /** Railway PostgreSQL database name. */
    private static final String PGDATABASE_ENV =
            "PGDATABASE";

    /** Railway PostgreSQL username. */
    private static final String PGUSER_ENV =
            "PGUSER";

    /** Railway PostgreSQL password. */
    private static final String PGPASSWORD_ENV =
            "PGPASSWORD";

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

        if (dataSource != null
                && !dataSource.isClosed()) {

            dataSource.close();

            event.getServletContext().log(
                    "VR Mart database connection pool closed.");
        }
    }

    /**
     * Loads database configuration.
     *
     * Railway PostgreSQL variables are preferred.
     * DATABASE_URL is used as a fallback.
     *
     * @return database properties
     */
    private Properties loadDatabaseProperties() {

        final String pgHost =
                environment(PGHOST_ENV);

        final String pgPort =
                environment(PGPORT_ENV);

        final String pgDatabase =
                environment(PGDATABASE_ENV);

        final String pgUser =
                environment(PGUSER_ENV);

        final String pgPassword =
                environment(PGPASSWORD_ENV);

        /*
         * Railway PostgreSQL normally exposes these variables.
         * Use them when all required values are available.
         */
        if (hasValue(pgHost)
                && hasValue(pgPort)
                && hasValue(pgDatabase)
                && hasValue(pgUser)
                && hasValue(pgPassword)) {

            return createProperties(
                    pgHost.trim(),
                    pgPort.trim(),
                    pgDatabase.trim(),
                    pgUser,
                    pgPassword);
        }

        /*
         * Fallback to DATABASE_URL.
         */
        final String databaseUrl =
                environment(DATABASE_URL_ENV);

        if (!hasValue(databaseUrl)) {

            throw new IllegalStateException(
                    "Railway PostgreSQL configuration is missing. "
                            + "DATABASE_URL or PGHOST/PGPORT/"
                            + "PGDATABASE/PGUSER/PGPASSWORD "
                            + "must be configured.");
        }

        return createPropertiesFromDatabaseUrl(
                databaseUrl.trim());
    }

    /**
     * Creates database properties from Railway PG variables.
     *
     * @param host database host
     * @param port database port
     * @param databaseName database name
     * @param username database username
     * @param password database password
     * @return database properties
     */
    private Properties createProperties(
            final String host,
            final String port,
            final String databaseName,
            final String username,
            final String password) {

        if (!hasValue(host)) {
            throw new IllegalStateException(
                    "PostgreSQL host is empty.");
        }

        if (!hasValue(port)) {
            throw new IllegalStateException(
                    "PostgreSQL port is empty.");
        }

        if (!hasValue(databaseName)) {
            throw new IllegalStateException(
                    "PostgreSQL database name is empty.");
        }

        if (!hasValue(username)) {
            throw new IllegalStateException(
                    "PostgreSQL username is empty.");
        }

        final String jdbcUrl =
                "jdbc:postgresql://"
                        + host
                        + ":"
                        + port
                        + "/"
                        + databaseName;

        final Properties properties =
                new Properties();

        properties.setProperty(
                "db.url",
                jdbcUrl);

        properties.setProperty(
                "db.username",
                username);

        properties.setProperty(
                "db.password",
                password == null ? "" : password);

        addPoolProperties(properties);

        return properties;
    }

    /**
     * Creates database properties from DATABASE_URL.
     *
     * This parser deliberately avoids URI.getHost(), because
     * Railway-generated credentials can contain characters that
     * make java.net.URI return a null host.
     *
     * @param databaseUrl Railway DATABASE_URL
     * @return database properties
     */
    private Properties createPropertiesFromDatabaseUrl(
            final String databaseUrl) {

        String normalizedUrl =
                databaseUrl.trim();

        if (normalizedUrl.startsWith(
                "jdbc:postgresql://")) {

            normalizedUrl =
                    normalizedUrl.substring(
                            "jdbc:".length());
        }

        if (normalizedUrl.startsWith(
                "postgres://")) {

            normalizedUrl =
                    "postgresql://"
                            + normalizedUrl.substring(
                            "postgres://".length());
        }

        final int schemeSeparator =
                normalizedUrl.indexOf("://");

        if (schemeSeparator <= 0) {

            throw new IllegalStateException(
                    "Invalid DATABASE_URL configuration.");
        }

        final String authorityAndPath =
                normalizedUrl.substring(
                        schemeSeparator + 3);

        final int slashIndex =
                authorityAndPath.indexOf('/');

        if (slashIndex <= 0) {

            throw new IllegalStateException(
                    "DATABASE_URL does not contain "
                            + "a database path.");
        }

        final String authority =
                authorityAndPath.substring(
                        0,
                        slashIndex);

        String databasePath =
                authorityAndPath.substring(
                        slashIndex + 1);

        if (databasePath.isBlank()) {

            throw new IllegalStateException(
                    "DATABASE_URL does not contain "
                            + "a database name.");
        }

        /*
         * Remove query parameters from database name.
         */
        final int queryIndex =
                databasePath.indexOf('?');

        if (queryIndex >= 0) {
            databasePath =
                    databasePath.substring(
                            0,
                            queryIndex);
        }

        /*
         * Use the LAST '@' so an '@' inside a password
         * does not break host detection.
         */
        final int atIndex =
                authority.lastIndexOf('@');

        if (atIndex <= 0
                || atIndex >= authority.length() - 1) {

            throw new IllegalStateException(
                    "DATABASE_URL does not contain "
                            + "valid database credentials.");
        }

        final String userInfo =
                authority.substring(
                        0,
                        atIndex);

        final String hostPort =
                authority.substring(
                        atIndex + 1);

        final int colonIndex =
                hostPort.lastIndexOf(':');

        if (colonIndex <= 0
                || colonIndex >= hostPort.length() - 1) {

            throw new IllegalStateException(
                    "DATABASE_URL does not contain "
                            + "a valid database host and port.");
        }

        final String host =
                hostPort.substring(
                        0,
                        colonIndex);

        final String port =
                hostPort.substring(
                        colonIndex + 1);

        final int credentialSeparator =
                userInfo.indexOf(':');

        if (credentialSeparator <= 0) {

            throw new IllegalStateException(
                    "DATABASE_URL contains invalid "
                            + "database credentials.");
        }

        final String username =
                decode(
                        userInfo.substring(
                                0,
                                credentialSeparator));

        final String password =
                decode(
                        userInfo.substring(
                                credentialSeparator + 1));

        final String databaseName =
                decode(databasePath);

        return createProperties(
                host,
                port,
                databaseName,
                username,
                password);
    }

    /**
     * Reads an environment variable.
     *
     * @param name environment variable name
     * @return environment value
     */
    private String environment(
            final String name) {

        return System.getenv(name);
    }

    /**
     * Checks whether a value is present.
     *
     * @param value value
     * @return true when non-empty
     */
    private boolean hasValue(
            final String value) {

        return value != null
                && !value.isBlank();
    }

    /**
     * Adds HikariCP pool configuration.
     *
     * @param properties database properties
     */
    private void addPoolProperties(
            final Properties properties) {

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
    }

    /**
     * URL-decodes a database value.
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
