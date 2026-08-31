package com.vrmart.util;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.util.Objects;
import java.util.Properties;

/**
 * Creates the HikariCP data source used by VR Mart.
 */
public final class DatabaseConnection {

    /** Default maximum number of connections in the pool. */
    private static final int DEFAULT_MAXIMUM_POOL_SIZE = 10;

    /** Default minimum number of idle connections. */
    private static final int DEFAULT_MINIMUM_IDLE = 2;

    /** Default connection timeout in milliseconds. */
    private static final long DEFAULT_CONNECTION_TIMEOUT = 30000L;

    /** Default idle timeout in milliseconds. */
    private static final long DEFAULT_IDLE_TIMEOUT = 600000L;

    /** Default maximum lifetime in milliseconds. */
    private static final long DEFAULT_MAX_LIFETIME = 1800000L;

    /** PostgreSQL JDBC driver class name. */
    private static final String POSTGRESQL_DRIVER =
            "org.postgresql.Driver";

    private DatabaseConnection() {
        // Utility class.
    }

    /**
     * Creates a configured HikariCP data source.
     *
     * @param properties database configuration properties
     * @return configured HikariCP data source
     */
    public static HikariDataSource createDataSource(
            final Properties properties) {

        Objects.requireNonNull(
                properties,
                "Database properties cannot be null");

        final String jdbcUrl = getRequiredProperty(
                properties,
                "db.url");

        final String username = properties.getProperty(
                "db.username",
                "postgres");

        final String password = properties.getProperty(
                "db.password",
                "");

        final HikariConfig config = new HikariConfig();

        config.setJdbcUrl(jdbcUrl);
        config.setDriverClassName(POSTGRESQL_DRIVER);
        config.setUsername(username);
        config.setPassword(password);

        config.setPoolName("VR-Mart-HikariPool");

        config.setMaximumPoolSize(
                getIntegerProperty(
                        properties,
                        "db.pool.maximum-size",
                        DEFAULT_MAXIMUM_POOL_SIZE));

        config.setMinimumIdle(
                getIntegerProperty(
                        properties,
                        "db.pool.minimum-idle",
                        DEFAULT_MINIMUM_IDLE));

        config.setConnectionTimeout(
                getLongProperty(
                        properties,
                        "db.pool.connection-timeout",
                        DEFAULT_CONNECTION_TIMEOUT));

        config.setIdleTimeout(
                getLongProperty(
                        properties,
                        "db.pool.idle-timeout",
                        DEFAULT_IDLE_TIMEOUT));

        config.setMaxLifetime(
                getLongProperty(
                        properties,
                        "db.pool.max-lifetime",
                        DEFAULT_MAX_LIFETIME));

        config.setAutoCommit(true);

        return new HikariDataSource(config);
    }

    /**
     * Gets a required database property.
     *
     * @param properties database properties
     * @param key property key
     * @return property value
     */
    private static String getRequiredProperty(
            final Properties properties,
            final String key) {

        final String value = properties.getProperty(key);

        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(
                    "Missing required database property: " + key);
        }

        return value.trim();
    }

    /**
     * Gets an integer database property.
     *
     * @param properties database properties
     * @param key property key
     * @param defaultValue default value
     * @return configured integer
     */
    private static int getIntegerProperty(
            final Properties properties,
            final String key,
            final int defaultValue) {

        final String value = properties.getProperty(key);

        if (value == null || value.isBlank()) {
            return defaultValue;
        }

        return Integer.parseInt(value.trim());
    }

    /**
     * Gets a long database property.
     *
     * @param properties database properties
     * @param key property key
     * @param defaultValue default value
     * @return configured long
     */
    private static long getLongProperty(
            final Properties properties,
            final String key,
            final long defaultValue) {

        final String value = properties.getProperty(key);

        if (value == null || value.isBlank()) {
            return defaultValue;
        }

        return Long.parseLong(value.trim());
    }
}
