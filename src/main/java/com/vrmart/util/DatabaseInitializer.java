package com.vrmart.util;

import org.mindrot.jbcrypt.BCrypt;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.PreparedStatement;
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
    /** Username parameter position. */
    private static final int USERNAME_PARAM = 1;
    /** Email parameter position. */
    private static final int EMAIL_PARAM = 2;
    /** Phone parameter position. */
    private static final int PHONE_PARAM = 3;
    /** Password parameter position. */
    private static final int PASSWORD_PARAM = 4;

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

            ensureUserSecurityColumns(connection);
            ensureOtpTable(connection);
            ensureAssuranceTable(connection);
            ensureAdminAccount(connection);
        }

        ProductSeeder.seed(dataSource);
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
     * Adds security-related user columns when required.
     *
     * @param connection database connection
     * @throws SQLException when column creation fails
     */
    private static void ensureUserSecurityColumns(
            final Connection connection)
            throws SQLException {
        final String phoneSql =
                "ALTER TABLE users ADD COLUMN IF NOT EXISTS "
                        + "phone VARCHAR(20)";
        final String verifiedSql =
                "ALTER TABLE users ADD COLUMN IF NOT EXISTS "
                        + "email_verified BOOLEAN NOT NULL DEFAULT FALSE";
        final String verifiedAtSql =
                "ALTER TABLE users ADD COLUMN IF NOT EXISTS "
                        + "email_verified_at TIMESTAMP";

        try (Statement statement =
                connection.createStatement()) {
            statement.execute(phoneSql);
            statement.execute(verifiedSql);
            statement.execute(verifiedAtSql);
        }
    }

    /**
     * Creates the OTP challenge table when required.
     *
     * @param connection database connection
     * @throws SQLException when table creation fails
     */
    private static void ensureOtpTable(
            final Connection connection)
            throws SQLException {
        final String tableSql =
                "CREATE TABLE IF NOT EXISTS otp_challenges ("
                        + "id BIGSERIAL PRIMARY KEY, "
                        + "user_id BIGINT NOT NULL, "
                        + "purpose VARCHAR(32) NOT NULL, "
                        + "otp_hash VARCHAR(255) NOT NULL, "
                        + "expires_at TIMESTAMP NOT NULL, "
                        + "attempts INTEGER NOT NULL DEFAULT 0, "
                        + "used BOOLEAN NOT NULL DEFAULT FALSE, "
                        + "created_at TIMESTAMP NOT NULL "
                        + "DEFAULT CURRENT_TIMESTAMP, "
                        + "CONSTRAINT fk_otp_user "
                        + "FOREIGN KEY (user_id) REFERENCES users(id) "
                        + "ON DELETE CASCADE)";

        final String indexSql =
                "CREATE INDEX IF NOT EXISTS "
                        + "idx_otp_user_purpose "
                        + "ON otp_challenges(user_id, purpose)";

        final String expiryIndexSql =
                "CREATE INDEX IF NOT EXISTS "
                        + "idx_otp_expiry "
                        + "ON otp_challenges(expires_at)";

        try (Statement statement =
                connection.createStatement()) {
            statement.execute(tableSql);
            statement.execute(indexSql);
            statement.execute(expiryIndexSql);
        }
    }

    /**
     * Creates the VR Mart product assurance table when required.
     *
     * @param connection database connection
     * @throws SQLException when table creation fails
     */
    private static void ensureAssuranceTable(
            final Connection connection)
            throws SQLException {
        final String sql =
                "CREATE TABLE IF NOT EXISTS vr_mart_assurance ("
                        + "product_id BIGINT PRIMARY KEY, "
                        + "assured BOOLEAN NOT NULL DEFAULT TRUE, "
                        + "assured_by BIGINT, "
                        + "assured_at TIMESTAMP NOT NULL "
                        + "DEFAULT CURRENT_TIMESTAMP, "
                        + "CONSTRAINT fk_assurance_product "
                        + "FOREIGN KEY (product_id) "
                        + "REFERENCES products(id) "
                        + "ON DELETE CASCADE, "
                        + "CONSTRAINT fk_assurance_admin "
                        + "FOREIGN KEY (assured_by) "
                        + "REFERENCES users(id) "
                        + "ON DELETE SET NULL)";

        try (Statement statement =
                connection.createStatement()) {
            statement.execute(sql);
        }
    }

    /**
     * Ensures that the dedicated VR Mart administrator exists.
     *
     * @param connection database connection
     * @throws SQLException when administrator creation fails
     */
    private static void ensureAdminAccount(
            final Connection connection)
            throws SQLException {
        final String username = "vr_admin";
        final String email = "admin@vrmart.local";
        final String phone = "9999999999";
        final String password = "Admin@12345";
        final String checkSql =
                "SELECT COUNT(*) FROM users WHERE username = ?";

        try (PreparedStatement check =
                connection.prepareStatement(checkSql)) {
            check.setString(USERNAME_PARAM, username);

            try (ResultSet result = check.executeQuery()) {
                if (result.next()
                        && result.getInt(1) > 0) {
                    return;
                }
            }
        }

        final String insertSql =
                "INSERT INTO users "
                        + "(username, email, phone, password_hash, role, "
                        + "email_verified) "
                        + "VALUES (?, ?, ?, ?, 'ADMIN', TRUE)";

        try (PreparedStatement insert =
                connection.prepareStatement(insertSql)) {
            insert.setString(
                    USERNAME_PARAM,
                    username);
            insert.setString(
                    EMAIL_PARAM,
                    email);
            insert.setString(
                    PHONE_PARAM,
                    phone);
            insert.setString(
                    PASSWORD_PARAM,
                    BCrypt.hashpw(
                            password,
                            BCrypt.gensalt()));
            insert.executeUpdate();
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
