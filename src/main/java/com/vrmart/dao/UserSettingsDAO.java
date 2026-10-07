package com.vrmart.dao;

import com.vrmart.model.UserSettings;
import com.zaxxer.hikari.HikariDataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Provides database operations for user settings.
 */
public class UserSettingsDAO {

    /** Result-set index for the user identifier. */
    private static final int USER_ID_COLUMN = 1;

    /** Result-set index for order updates. */
    private static final int ORDER_UPDATES_COLUMN = 2;

    /** Result-set index for email notifications. */
    private static final int EMAIL_NOTIFICATIONS_COLUMN = 3;

    /** Result-set index for product recommendations. */
    private static final int PRODUCT_RECOMMENDATIONS_COLUMN = 4;

    /** Result-set index for dark mode. */
    private static final int DARK_MODE_COLUMN = 5;

    /** Result-set index for creation timestamp. */
    private static final int CREATED_AT_COLUMN = 6;

    /** Result-set index for update timestamp. */
    private static final int UPDATED_AT_COLUMN = 7;

    /** Database connection pool. */
    private final HikariDataSource dataSource;

    /** SQL used to find settings for one user. */
    private static final String FIND_SQL =
            "SELECT user_id, order_updates, email_notifications, "
            + "product_recommendations, dark_mode, created_at, updated_at "
            + "FROM user_settings WHERE user_id = ?";

    /** SQL used to insert default settings. */
    private static final String INSERT_SQL =
            "INSERT INTO user_settings "
            + "(user_id, order_updates, email_notifications, "
            + "product_recommendations, dark_mode) "
            + "VALUES (?, TRUE, TRUE, TRUE, TRUE) "
            + "ON CONFLICT (user_id) DO NOTHING";

    /** Prepared-statement parameter for order updates. */
    private static final int ORDER_UPDATES_PARAM = 1;

    /** Prepared-statement parameter for email notifications. */
    private static final int EMAIL_NOTIFICATIONS_PARAM = 2;

    /** Prepared-statement parameter for product recommendations. */
    private static final int PRODUCT_RECOMMENDATIONS_PARAM = 3;

    /** Prepared-statement parameter for dark mode. */
    private static final int DARK_MODE_PARAM = 4;

    /** Prepared-statement parameter for user identifier. */
    private static final int USER_ID_PARAM = 5;

    /** SQL used to update settings. */
    private static final String UPDATE_SQL =
            "UPDATE user_settings SET order_updates = ?, "
            + "email_notifications = ?, product_recommendations = ?, "
            + "dark_mode = ?, updated_at = CURRENT_TIMESTAMP "
            + "WHERE user_id = ?";

    /**
     * Creates the DAO.
     *
     * @param source application database connection pool
     */
    public UserSettingsDAO(final HikariDataSource source) {
        dataSource = source;
    }

    /**
     * Finds settings for a user.
     *
     * @param userId database user identifier
     * @return settings or null when unavailable
     * @throws SQLException when a database operation fails
     */
    public UserSettings findByUser(final long userId) throws SQLException {
        try (Connection connection = dataSource.getConnection();
                PreparedStatement statement =
                        connection.prepareStatement(FIND_SQL)) {
            statement.setLong(1, userId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return mapSettings(resultSet);
                }
            }
        }
        return null;
    }

    /**
     * Updates a user's settings.
     *
     * @param settings settings to save
     * @throws SQLException when a database operation fails
     */
    public void update(final UserSettings settings) throws SQLException {
        try (Connection connection = dataSource.getConnection();
                PreparedStatement statement =
                        connection.prepareStatement(UPDATE_SQL)) {
            statement.setBoolean(
                    ORDER_UPDATES_PARAM,
                    settings.isOrderUpdates());
            statement.setBoolean(
                    EMAIL_NOTIFICATIONS_PARAM,
                    settings.isEmailNotifications());
            statement.setBoolean(
                    PRODUCT_RECOMMENDATIONS_PARAM,
                    settings.isProductRecommendations());
            statement.setBoolean(DARK_MODE_PARAM, settings.isDarkMode());
            statement.setLong(USER_ID_PARAM, settings.getUserId());
            statement.executeUpdate();
        }
    }

    /**
     * Ensures that a user has a settings row.
     *
     * @param userId database user identifier
     * @return existing or newly created settings
     * @throws SQLException when a database operation fails
     */
    public UserSettings ensureSettings(final long userId)
            throws SQLException {
        try (Connection connection = dataSource.getConnection();
                PreparedStatement statement =
                        connection.prepareStatement(INSERT_SQL)) {
            statement.setLong(1, userId);
            statement.executeUpdate();
        }

        UserSettings settings = findByUser(userId);
        if (settings == null) {
            settings = createDefaultSettings(userId);
        }
        return settings;
    }

    /**
     * Creates default settings without a database operation.
     *
     * @param userId database user identifier
     * @return default settings
     */
    public UserSettings createDefaultSettings(final long userId) {
        UserSettings settings = new UserSettings(userId);
        settings.setOrderUpdates(true);
        settings.setEmailNotifications(true);
        settings.setProductRecommendations(true);
        settings.setDarkMode(true);
        return settings;
    }

    /**
     * Maps one database row to a settings model.
     *
     * @param resultSet database result set
     * @return mapped settings
     * @throws SQLException when a column cannot be read
     */
    private UserSettings mapSettings(final ResultSet resultSet)
            throws SQLException {
        UserSettings settings = new UserSettings();
        settings.setUserId(resultSet.getLong(USER_ID_COLUMN));
        settings.setOrderUpdates(resultSet.getBoolean(ORDER_UPDATES_COLUMN));
        settings.setEmailNotifications(
                resultSet.getBoolean(EMAIL_NOTIFICATIONS_COLUMN));
        settings.setProductRecommendations(
                resultSet.getBoolean(PRODUCT_RECOMMENDATIONS_COLUMN));
        settings.setDarkMode(resultSet.getBoolean(DARK_MODE_COLUMN));
        settings.setCreatedAt(resultSet.getTimestamp(CREATED_AT_COLUMN));
        settings.setUpdatedAt(resultSet.getTimestamp(UPDATED_AT_COLUMN));
        return settings;
    }
}
