package com.vrmart.model;

import java.sql.Timestamp;

/**
 * Stores notification and appearance preferences for a user.
 */
public class UserSettings {

    /** Database user identifier. */
    private long userId;

    /** Whether order updates are enabled. */
    private boolean orderUpdates;

    /** Whether email notifications are enabled. */
    private boolean emailNotifications;

    /** Whether product recommendations are enabled. */
    private boolean productRecommendations;

    /** Whether dark mode is enabled. */
    private boolean darkMode;

    /** Settings creation timestamp. */
    private Timestamp createdAt;

    /** Settings last-update timestamp. */
    private Timestamp updatedAt;

    /**
     * Creates an empty settings object.
     */
    public UserSettings() {
        // Default constructor.
    }

    /**
     * Creates a settings object with all preference values.
     *
     * @param settingsUserId database user identifier
     */
    public UserSettings(final long settingsUserId) {
        this.userId = settingsUserId;
    }

    /**
     * Returns the database user identifier.
     *
     * @return user identifier
     */
    public long getUserId() {
        return userId;
    }

    /**
     * Sets the database user identifier.
     *
     * @param settingsUserId user identifier
     */
    public void setUserId(final long settingsUserId) {
        userId = settingsUserId;
    }

    /**
     * Returns whether order updates are enabled.
     *
     * @return true when enabled
     */
    public boolean isOrderUpdates() {
        return orderUpdates;
    }

    /**
     * Sets whether order updates are enabled.
     *
     * @param enabledOrderUpdates preference value
     */
    public void setOrderUpdates(final boolean enabledOrderUpdates) {
        orderUpdates = enabledOrderUpdates;
    }

    /**
     * Returns whether email notifications are enabled.
     *
     * @return true when enabled
     */
    public boolean isEmailNotifications() {
        return emailNotifications;
    }

    /**
     * Sets whether email notifications are enabled.
     *
     * @param enabledEmailNotifications preference value
     */
    public void setEmailNotifications(
            final boolean enabledEmailNotifications) {
        emailNotifications = enabledEmailNotifications;
    }

    /**
     * Returns whether product recommendations are enabled.
     *
     * @return true when enabled
     */
    public boolean isProductRecommendations() {
        return productRecommendations;
    }

    /**
     * Sets whether product recommendations are enabled.
     *
     * @param enabledProductRecommendations preference value
     */
    public void setProductRecommendations(
            final boolean enabledProductRecommendations) {
        productRecommendations = enabledProductRecommendations;
    }

    /**
     * Returns whether dark mode is enabled.
     *
     * @return true when enabled
     */
    public boolean isDarkMode() {
        return darkMode;
    }

    /**
     * Sets whether dark mode is enabled.
     *
     * @param enabledDarkMode preference value
     */
    public void setDarkMode(final boolean enabledDarkMode) {
        darkMode = enabledDarkMode;
    }

    /**
     * Returns the creation timestamp.
     *
     * @return creation timestamp
     */
    public Timestamp getCreatedAt() {
        return createdAt;
    }

    /**
     * Sets the creation timestamp.
     *
     * @param settingsCreatedAt creation timestamp
     */
    public void setCreatedAt(final Timestamp settingsCreatedAt) {
        createdAt = settingsCreatedAt;
    }

    /**
     * Returns the update timestamp.
     *
     * @return update timestamp
     */
    public Timestamp getUpdatedAt() {
        return updatedAt;
    }

    /**
     * Sets the update timestamp.
     *
     * @param settingsUpdatedAt update timestamp
     */
    public void setUpdatedAt(final Timestamp settingsUpdatedAt) {
        updatedAt = settingsUpdatedAt;
    }
}
