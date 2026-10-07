package com.vrmart.dao;

import com.zaxxer.hikari.HikariDataSource;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;

/**
 * Handles password-related database operations for VR Mart users.
 */
public final class PasswordDAO {

    /** First SQL parameter position. */
    private static final int PARAM_ONE = 1;

    /** Second SQL parameter position. */
    private static final int PARAM_TWO = 2;

    /** BCrypt work factor. */
    private static final int BCRYPT_ROUNDS = 12;

    /** Database connection pool. */
    private final HikariDataSource dataSource;

    /**
     * Creates the password DAO.
     *
     * @param source HikariCP data source
     */
    public PasswordDAO(final HikariDataSource source) {
        dataSource = Objects.requireNonNull(
                source,
                "Data source cannot be null");
    }

    /**
     * Verifies the current password of a user.
     *
     * @param userId user identifier
     * @param currentPassword current plain-text password
     * @return true when the password is correct
     * @throws SQLException when database access fails
     */
    public boolean verifyCurrentPassword(
            final long userId,
            final String currentPassword)
            throws SQLException {

        if (currentPassword == null
                || currentPassword.isEmpty()) {
            return false;
        }

        final String sql = """
                SELECT password_hash
                FROM users
                WHERE id = ?
                """;

        try (Connection connection =
                     dataSource.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setLong(
                    PARAM_ONE,
                    userId);

            try (ResultSet result =
                         statement.executeQuery()) {

                if (!result.next()) {
                    return false;
                }

                final String passwordHash =
                        result.getString("password_hash");

                return passwordHash != null
                        && BCrypt.checkpw(
                                currentPassword,
                                passwordHash);
            }
        }
    }

    /**
     * Updates the password for a user.
     *
     * @param userId user identifier
     * @param newPassword new plain-text password
     * @return true when the password was updated
     * @throws SQLException when database access fails
     */
    public boolean updatePassword(
            final long userId,
            final String newPassword)
            throws SQLException {

        if (newPassword == null
                || newPassword.isEmpty()) {
            return false;
        }

        final String passwordHash =
                BCrypt.hashpw(
                        newPassword,
                        BCrypt.gensalt(BCRYPT_ROUNDS));

        final String sql = """
                UPDATE users
                SET password_hash = ?,
                    updated_at = CURRENT_TIMESTAMP
                WHERE id = ?
                """;

        try (Connection connection =
                     dataSource.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setString(
                    PARAM_ONE,
                    passwordHash);

            statement.setLong(
                    PARAM_TWO,
                    userId);

            return statement.executeUpdate() == 1;
        }
    }

    /**
     * Changes a user's password after verifying
     * the current password.
     *
     * @param userId user identifier
     * @param currentPassword current password
     * @param newPassword new password
     * @return true when the password was changed
     * @throws SQLException when database access fails
     */
    public boolean changePassword(
            final long userId,
            final String currentPassword,
            final String newPassword)
            throws SQLException {

        if (!verifyCurrentPassword(
                userId,
                currentPassword)) {
            return false;
        }

        return updatePassword(
                userId,
                newPassword);
    }
}
