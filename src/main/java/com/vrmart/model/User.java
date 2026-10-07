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

    /** Email verification status. */
    private boolean emailVerified;

    /** Email verification timestamp. */
    private LocalDateTime emailVerifiedAt;

    /** Account creation timestamp. */
    private LocalDateTime createdAt;

    /** Account update timestamp. */
    private LocalDateTime updatedAt;

    /**
     * Creates an empty user.
     */
    public User() {
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
        username = userName;
        email = userEmail;
        phone = userPhone;
        passwordHash = userPasswordHash;
        role = userRole;
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
        id = userId;
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
        username = userName;
    }

    /**
     * Gets the email address.
     *
     * @return email address
     */
    public String getEmail() {
        return email;
    }

    /**
     * Sets the email address.
     *
     * @param userEmail email address
     */
    public void setEmail(final String userEmail) {
        email = userEmail;
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
        phone = userPhone;
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
     * @param userPasswordHash password hash
     */
    public void setPasswordHash(final String userPasswordHash) {
        passwordHash = userPasswordHash;
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
        role = userRole;
    }

    /**
     * Checks whether the email is verified.
     *
     * @return true when email is verified
     */
    public boolean isEmailVerified() {
        return emailVerified;
    }

    /**
     * Sets the email verification status.
     *
     * @param verified verification status
     */
    public void setEmailVerified(final boolean verified) {
        emailVerified = verified;
    }

    /**
     * Gets the email verification timestamp.
     *
     * @return verification timestamp
     */
    public LocalDateTime getEmailVerifiedAt() {
        return emailVerifiedAt;
    }

    /**
     * Sets the email verification timestamp.
     *
     * @param verificationTime verification timestamp
     */
    public void setEmailVerifiedAt(
            final LocalDateTime verificationTime) {
        emailVerifiedAt = verificationTime;
    }

    /**
     * Gets the account creation timestamp.
     *
     * @return creation timestamp
     */
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    /**
     * Sets the account creation timestamp.
     *
     * @param creationTime creation timestamp
     */
    public void setCreatedAt(
            final LocalDateTime creationTime) {
        createdAt = creationTime;
    }

    /**
     * Gets the account update timestamp.
     *
     * @return update timestamp
     */
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    /**
     * Sets the account update timestamp.
     *
     * @param updateTime update timestamp
     */
    public void setUpdatedAt(
            final LocalDateTime updateTime) {
        updatedAt = updateTime;
    }
}
