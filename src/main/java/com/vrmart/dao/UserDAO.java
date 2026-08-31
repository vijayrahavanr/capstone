package com.vrmart.dao;

import com.vrmart.model.User;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Data access object for users.
 */
public final class UserDAO {

    /** First SQL parameter position. */
    private static final int PARAM_ONE = 1;

    /** Second SQL parameter position. */
    private static final int PARAM_TWO = 2;

    /** Third SQL parameter position. */
    private static final int PARAM_THREE = 3;

    /** Fourth SQL parameter position. */
    private static final int PARAM_FOUR = 4;

    /** Fifth SQL parameter position. */
    private static final int PARAM_FIVE = 5;

    /** Sixth SQL parameter position. */
private static final int PARAM_SIX = 6;

    /** Database connection pool. */
    private final HikariDataSource dataSource;

    /**
     * Creates a user DAO.
     *
     * @param source HikariCP data source
     */
    public UserDAO(final HikariDataSource source) {
        dataSource = Objects.requireNonNull(
                source,
                "Data source cannot be null");
    }

    /**
     * Creates a new user.
     *
     * @param user user to create
     * @return generated user ID
     * @throws SQLException if database operation fails
     */
    public long create(final User user) throws SQLException {
        Objects.requireNonNull(user, "User cannot be null");

        final String sql = """
                INSERT INTO users
                    (username, email, phone, password_hash, role)
                VALUES (?, ?, ?, ?, ?)
                """;

        try (Connection connection = dataSource.getConnection();
                PreparedStatement statement = connection.prepareStatement(
                        sql,
                        Statement.RETURN_GENERATED_KEYS)) {

            statement.setString(PARAM_ONE, user.getUsername());
            statement.setString(PARAM_TWO, user.getEmail());
            statement.setString(PARAM_THREE, user.getPhone());
            statement.setString(PARAM_FOUR, user.getPasswordHash());
            statement.setString(PARAM_FIVE, user.getRole());

            statement.executeUpdate();

            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (!keys.next()) {
                    throw new SQLException(
                            "Failed to retrieve generated user ID.");
                }

                final long generatedId = keys.getLong(PARAM_ONE);
                user.setId(generatedId);
                return generatedId;
            }
        }
    }

    /**
     * Finds a user by ID.
     *
     * @param userId user ID
     * @return user or null when not found
     * @throws SQLException if database operation fails
     */
    public User findById(final long userId) throws SQLException {
        final String sql = """
                SELECT id, username, email, phone, password_hash, role,
                       created_at, updated_at
                FROM users
                WHERE id = ?
                """;

        try (Connection connection = dataSource.getConnection();
                PreparedStatement statement = connection.prepareStatement(
                        sql)) {

            statement.setLong(PARAM_ONE, userId);

            try (ResultSet result = statement.executeQuery()) {
                if (result.next()) {
                    return mapUser(result);
                }
            }
        }

        return null;
    }

    /**
     * Finds a user by username.
     *
     * @param userName username
     * @return user or null when not found
     * @throws SQLException if database operation fails
     */
    public User findByUsername(final String userName) throws SQLException {
        final String sql = """
                SELECT id, username, email, phone, password_hash, role,
                       created_at, updated_at
                FROM users
                WHERE username = ?
                """;

        try (Connection connection = dataSource.getConnection();
                PreparedStatement statement = connection.prepareStatement(
                        sql)) {

            statement.setString(PARAM_ONE, userName);

            try (ResultSet result = statement.executeQuery()) {
                if (result.next()) {
                    return mapUser(result);
                }
            }
        }

        return null;
    }

    /**
     * Finds a user by email.
     *
     * @param userEmail email address
     * @return user or null when not found
     * @throws SQLException if database operation fails
     */
    public User findByEmail(final String userEmail) throws SQLException {
        final String sql = """
                SELECT id, username, email, phone, password_hash, role,
                       created_at, updated_at
                FROM users
                WHERE email = ?
                """;

        try (Connection connection = dataSource.getConnection();
                PreparedStatement statement = connection.prepareStatement(
                        sql)) {

            statement.setString(PARAM_ONE, userEmail);

            try (ResultSet result = statement.executeQuery()) {
                if (result.next()) {
                    return mapUser(result);
                }
            }
        }

        return null;
    }

    /**
     * Returns all users.
     *
     * @return list of users
     * @throws SQLException if database operation fails
     */
    public List<User> findAll() throws SQLException {
        final String sql = """
                SELECT id, username, email, phone, password_hash, role,
                       created_at, updated_at
                FROM users
                ORDER BY id
                """;

        final List<User> users = new ArrayList<>();

        try (Connection connection = dataSource.getConnection();
                PreparedStatement statement = connection.prepareStatement(
                        sql);
                ResultSet result = statement.executeQuery()) {

            while (result.next()) {
                users.add(mapUser(result));
            }
        }

        return users;
    }

    /**
     * Updates an existing user.
     *
     * @param user user to update
     * @return true when a user was updated
     * @throws SQLException if database operation fails
     */
    public boolean update(final User user) throws SQLException {
        Objects.requireNonNull(user, "User cannot be null");

        final String sql = """
                UPDATE users
                SET username = ?,
                    email = ?,
                    phone = ?,
                    password_hash = ?,
                    role = ?,
                    updated_at = CURRENT_TIMESTAMP
                WHERE id = ?
                """;

        try (Connection connection = dataSource.getConnection();
                PreparedStatement statement = connection.prepareStatement(
                        sql)) {

            statement.setString(PARAM_ONE, user.getUsername());
            statement.setString(PARAM_TWO, user.getEmail());
            statement.setString(PARAM_THREE, user.getPhone());
            statement.setString(PARAM_FOUR, user.getPasswordHash());
            statement.setString(PARAM_FIVE, user.getRole());
            statement.setLong(PARAM_SIX, user.getId());

            return statement.executeUpdate() == PARAM_ONE;
        }
    }

    /**
     * Deletes a user by ID.
     *
     * @param userId user ID
     * @return true when a user was deleted
     * @throws SQLException if database operation fails
     */
    public boolean delete(final long userId) throws SQLException {
        final String sql = "DELETE FROM users WHERE id = ?";

        try (Connection connection = dataSource.getConnection();
                PreparedStatement statement = connection.prepareStatement(
                        sql)) {

            statement.setLong(PARAM_ONE, userId);

            return statement.executeUpdate() == PARAM_ONE;
        }
    }

    /**
     * Converts a database row into a User object.
     *
     * @param result database result
     * @return mapped user
     * @throws SQLException if a column cannot be read
     */
    private User mapUser(final ResultSet result) throws SQLException {
        final User user = new User();

        user.setId(result.getLong("id"));
        user.setUsername(result.getString("username"));
        user.setEmail(result.getString("email"));
        user.setPasswordHash(result.getString("password_hash"));
        user.setRole(result.getString("role"));

        final Timestamp createdTimestamp =
                result.getTimestamp("created_at");
        if (createdTimestamp != null) {
            user.setCreatedAt(createdTimestamp.toLocalDateTime());
        }

        final Timestamp updatedTimestamp =
                result.getTimestamp("updated_at");
        if (updatedTimestamp != null) {
            user.setUpdatedAt(updatedTimestamp.toLocalDateTime());
        }

        return user;
    }
}
