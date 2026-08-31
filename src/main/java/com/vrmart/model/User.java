package com.vrmart.model;

import java.time.LocalDateTime;

/**
 * Represents a VR Mart application user.
 */
public final class User {

    /** Buyer role. */
    public static final String ROLE_BUYER = "BUYER";

    /** Seller role. */
    public static final String ROLE_SELLER = "SELLER";

    /** Administrator role. */
    public static final String ROLE_ADMIN = "ADMIN";

    /** User identifier. */
    private Long id;

    /** Username. */
    private String username;

    /** Email address. */
    private String email;

    /** Phone number. */
    private String phone;

    /** Hashed password. */
    private String passwordHash;

    /** User role. */
    private String role;

    /** Account creation timestamp. */
    private LocalDateTime createdAt;

    /** Account update timestamp. */
    private LocalDateTime updatedAt;

    /**
     * Creates an empty user.
     */
    public User() {
        // Default constructor.
    }

    /**
     * Creates a user with core details.
     *
     * @param userName username
     * @param userEmail email address
     * @param userPhone phone number
     * @param userPasswordHash hashed password
     * @param userRole user role
     */
    public User(
            final String userName,
            final String userEmail,
            final String userPhone,
            final String userPasswordHash,
            final String userRole) {
        this.username = userName;
        this.email = userEmail;
        this.phone = userPhone;
        this.passwordHash = userPasswordHash;
        this.role = userRole;
    }

    /**
     * Gets the user ID.
     *
     * @return user ID
     */
    public Long getId() {
        return id;
    }

    /**
     * Sets the user ID.
     *
     * @param userId user ID
     */
    public void setId(final Long userId) {
        this.id = userId;
    }

    /**
     * Gets the username.
     *
     * @return username
     */
    public String getUsername() {
        return username;
    }

    /**
     * Sets the username.
     *
     * @param userName username
     */
    public void setUsername(final String userName) {
        this.username = userName;
    }

    /**
     * Gets the email.
     *
     * @return email
     */
    public String getEmail() {
        return email;
    }

    /**
     * Sets the email.
     *
     * @param userEmail email address
     */
    public void setEmail(final String userEmail) {
        this.email = userEmail;
    }

    /**
     * Gets the phone number.
     *
     * @return phone number
     */
    public String getPhone() {
        return phone;
    }

    /**
     * Sets the phone number.
     *
     * @param userPhone phone number
     */
    public void setPhone(final String userPhone) {
        this.phone = userPhone;
    }

    /**
     * Gets the password hash.
     *
     * @return password hash
     */
    public String getPasswordHash() {
        return passwordHash;
    }

    /**
     * Sets the password hash.
     *
     * @param userPasswordHash hashed password
     */
    public void setPasswordHash(final String userPasswordHash) {
        this.passwordHash = userPasswordHash;
    }

    /**
     * Gets the user role.
     *
     * @return user role
     */
    public String getRole() {
        return role;
    }

    /**
     * Sets the user role.
     *
     * @param userRole user role
     */
    public void setRole(final String userRole) {
        this.role = userRole;
    }

    /**
     * Gets the creation timestamp.
     *
     * @return creation timestamp
     */
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    /**
     * Sets the creation timestamp.
     *
     * @param creationTime creation timestamp
     */
    public void setCreatedAt(final LocalDateTime creationTime) {
        this.createdAt = creationTime;
    }

    /**
     * Gets the update timestamp.
     *
     * @return update timestamp
     */
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    /**
     * Sets the update timestamp.
     *
     * @param updateTime update timestamp
     */
    public void setUpdatedAt(final LocalDateTime updateTime) {
        this.updatedAt = updateTime;
    }
}
