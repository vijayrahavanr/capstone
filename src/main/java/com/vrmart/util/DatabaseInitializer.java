package com.vrmart.util;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Initializes the VR Mart database schema.
 */
public final class DatabaseInitializer {

    /** Database schema resource. */
    private static final String SCHEMA_RESOURCE = "/schema.sql";

    /** Users table name. */
    private static final String USERS_TABLE = "users";

    /** Buffer size for reading the schema file. */
    private static final int BUFFER_SIZE = 4096;

    /**
     * Utility class constructor.
     */
    private DatabaseInitializer() {
        // Utility class.
    }

    /**
     * Initializes the VR Mart database.
     *
     * @param dataSource application data source
     * @throws SQLException when database initialization fails
     * @throws IOException when schema cannot be read
     */
    public static void initialize(
            final DataSource dataSource)
            throws SQLException, IOException {

        if (dataSource == null) {
            throw new IllegalArgumentException(
                    "Data source cannot be null.");
        }

        try (Connection connection = dataSource.getConnection()) {

            if (!usersTableExists(connection)) {
                executeSchema(connection);
            }

            if (!usersTableExists(connection)) {
                throw new SQLException(
                        "Database schema was executed, "
                                + "but USERS table was not created.");
            }
        }
    }

    /**
     * Executes the PostgreSQL database schema.
     *
     * @param connection database connection
     * @throws SQLException when schema execution fails
     * @throws IOException when schema cannot be read
     */
    private static void executeSchema(
            final Connection connection)
            throws SQLException, IOException {

        final InputStream schemaStream =
                DatabaseInitializer.class.getResourceAsStream(
                        SCHEMA_RESOURCE);

        if (schemaStream == null) {
            throw new IOException(
                    "Database schema not found: "
                            + SCHEMA_RESOURCE);
        }

        try (InputStream inputStream = schemaStream;
             InputStreamReader reader =
                     new InputStreamReader(
                             inputStream,
                             StandardCharsets.UTF_8)) {

            final StringBuilder schema =
                    new StringBuilder();

            int charactersRead;
            final char[] buffer = new char[BUFFER_SIZE];

            while ((charactersRead =
                    reader.read(buffer)) != -1) {

                schema.append(
                        buffer, 0, charactersRead);
            }

            final String[] statements =
                    schema.toString().split(";");

            try (Statement statement =
                         connection.createStatement()) {

                for (String sql : statements) {

                    final String trimmedSql =
                            sql.trim();

                    if (!trimmedSql.isEmpty()) {
                        statement.execute(trimmedSql);
                    }
                }
            }
        }
    }

    /**
     * Checks whether the USERS table exists.
     *
     * @param connection database connection
     * @return true when USERS exists
     * @throws SQLException when metadata query fails
     */
    private static boolean usersTableExists(
            final Connection connection)
            throws SQLException {

        final String sql =
                "SELECT COUNT(*) "
                        + "FROM information_schema.tables "
                        + "WHERE table_schema = 'public' "
                        + "AND table_name = '"
                        + USERS_TABLE
                        + "'";

        try (Statement statement =
                     connection.createStatement();
             ResultSet resultSet =
                     statement.executeQuery(sql)) {

            if (resultSet.next()) {
                return resultSet.getInt(1) > 0;
            }

            return false;
        }
    }
}
