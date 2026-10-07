package com.vrmart.dao;

import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Objects;

/** Handles one-time password persistence for VR Mart. */
public final class OtpDAO {
    /** First SQL parameter position. */
    private static final int PARAM_ONE = 1;
    /** Second SQL parameter position. */
    private static final int PARAM_TWO = 2;
    /** Third SQL parameter position. */
    private static final int PARAM_THREE = 3;
    /** Fourth SQL parameter position. */
    private static final int PARAM_FOUR = 4;
    /** Database connection pool. */
    private final HikariDataSource dataSource;

    /**
     * Creates the OTP DAO.
     *
     * @param source HikariCP data source
     */
    public OtpDAO(final HikariDataSource source) {
        dataSource = Objects.requireNonNull(
                source, "Data source cannot be null");
    }

    /**
     * Stores a new OTP challenge and invalidates older unused challenges.
     *
     * @param userId user identifier
     * @param purpose challenge purpose
     * @param otpHash BCrypt hash of the OTP
     * @param expiresAt challenge expiry time
     * @throws SQLException when database access fails
     */
    public void createChallenge(final long userId, final String purpose,
                                final String otpHash,
                                final LocalDateTime expiresAt)
            throws SQLException {
        final String invalidateSql = """
                UPDATE otp_challenges
                SET used = TRUE
                WHERE user_id = ? AND purpose = ? AND used = FALSE
                """;
        final String insertSql = """
                INSERT INTO otp_challenges
                    (user_id, purpose, otp_hash, expires_at)
                VALUES (?, ?, ?, ?)
                """;
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement statement =
                         connection.prepareStatement(invalidateSql)) {
                statement.setLong(PARAM_ONE, userId);
                statement.setString(PARAM_TWO, purpose);
                statement.executeUpdate();
            }
            try (PreparedStatement statement =
                         connection.prepareStatement(insertSql)) {
                statement.setLong(PARAM_ONE, userId);
                statement.setString(PARAM_TWO, purpose);
                statement.setString(PARAM_THREE, otpHash);
                statement.setTimestamp(
                        PARAM_FOUR, Timestamp.valueOf(expiresAt));
                statement.executeUpdate();
            }
        }
    }

    /**
     * Finds the latest active challenge for a user and purpose.
     *
     * @param userId user identifier
     * @param purpose challenge purpose
     * @return challenge or null when unavailable
     * @throws SQLException when database access fails
     */
    public OtpChallenge findActiveChallenge(final long userId,
                                             final String purpose)
            throws SQLException {
        final String sql = """
                SELECT id, otp_hash, expires_at, attempts
                FROM otp_challenges
                WHERE user_id = ? AND purpose = ?
                  AND used = FALSE
                ORDER BY created_at DESC
                LIMIT 1
                """;
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {
            statement.setLong(PARAM_ONE, userId);
            statement.setString(PARAM_TWO, purpose);
            try (ResultSet result = statement.executeQuery()) {
                if (result.next()) {
                    return new OtpChallenge(
                            result.getLong("id"),
                            result.getString("otp_hash"),
                            result.getTimestamp("expires_at")
                                    .toLocalDateTime(),
                            result.getInt("attempts"));
                }
            }
        }
        return null;
    }

    /**
     * Marks a challenge as used.
     *
     * @param challengeId challenge identifier
     * @throws SQLException when database access fails
     */
    public void markUsed(final long challengeId) throws SQLException {
        final String sql =
                "UPDATE otp_challenges SET used = TRUE WHERE id = ?";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {
            statement.setLong(PARAM_ONE, challengeId);
            statement.executeUpdate();
        }
    }

    /**
     * Increments failed verification attempts.
     *
     * @param challengeId challenge identifier
     * @return updated attempt count
     * @throws SQLException when database access fails
     */
    public int incrementAttempts(final long challengeId)
            throws SQLException {
        final String sql = """
                UPDATE otp_challenges
                SET attempts = attempts + 1
                WHERE id = ?
                RETURNING attempts
                """;
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {
            statement.setLong(PARAM_ONE, challengeId);
            try (ResultSet result = statement.executeQuery()) {
                if (result.next()) {
                    return result.getInt(PARAM_ONE);
                }
            }
        }
        return 0;
    }

    /**
     * Represents an OTP challenge stored for verification.
     *
     * @param id challenge identifier
     * @param otpHash BCrypt hash of the OTP
     * @param expiresAt challenge expiry time
     * @param attempts failed verification attempts
     */
    public record OtpChallenge(long id, String otpHash,
                               LocalDateTime expiresAt, int attempts) {
    }
}
