package com.vrmart.service;

import com.vrmart.dao.UserDAO;
import com.vrmart.model.User;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.SQLException;
import java.util.Objects;

/**
 * Handles user registration and authentication for VR Mart.
 */
public final class AuthService {

    /** Minimum password length. */
    private static final int MINIMUM_PASSWORD_LENGTH = 8;

    /** Maximum password length supported by BCrypt safely. */
    private static final int MAXIMUM_PASSWORD_LENGTH = 72;

    /** Maximum username length. */
    private static final int MAXIMUM_USERNAME_LENGTH = 50;

    /** Maximum email length. */
    private static final int MAXIMUM_EMAIL_LENGTH = 254;

    /** BCrypt work factor. */
    private static final int BCRYPT_ROUNDS = 12;

    /** Basic RFC-compatible email validation pattern. */
    private static final String EMAIL_PATTERN =
            "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";

    /** User data access object. */
    private final UserDAO userDAO;

    /**
     * Creates the authentication service.
     *
     * @param dao user data access object
     */
    public AuthService(final UserDAO dao) {
        userDAO = Objects.requireNonNull(
                dao,
                "User DAO cannot be null");
    }

    /**
     * Registers a new buyer account.
     *
     * @param username username
     * @param email email address
     * @param phone phone number
     * @param password plain-text password
     * @return created buyer
     * @throws SQLException if database operation fails
     */
    public User register(
            final String username,
            final String email,
            final String phone,
            final String password) throws SQLException {
        return registerUser(
                username,
                email,
                phone,
                password,
                User.ROLE_BUYER);
    }

    /**
     * Registers a new seller account.
     *
     * @param username username
     * @param email email address
     * @param phone phone number
     * @param password plain-text password
     * @return created seller
     * @throws SQLException if database operation fails
     */
    public User registerSeller(
            final String username,
            final String email,
            final String phone,
            final String password) throws SQLException {
        return registerUser(
                username,
                email,
                phone,
                password,
                User.ROLE_SELLER);
    }

    /**
     * Creates a user with the specified role.
     *
     * @param username username
     * @param email email address
     * @param phone phone number
     * @param password plain-text password
     * @param role user role
     * @return created user
     * @throws SQLException if database operation fails
     */
    private User registerUser(
            final String username,
            final String email,
            final String phone,
            final String password,
            final String role) throws SQLException {

        validateRegistrationInput(
                username,
                email,
                phone,
                password);

        final String normalizedUsername = username.trim();
        final String normalizedEmail =
                email.trim().toLowerCase();

        if (userDAO.findByUsername(normalizedUsername) != null) {
            throw new IllegalArgumentException(
                    "Username is already registered.");
        }

        if (userDAO.findByEmail(normalizedEmail) != null) {
            throw new IllegalArgumentException(
                    "Email is already registered.");
        }

        final String passwordHash = BCrypt.hashpw(
                password,
                BCrypt.gensalt(BCRYPT_ROUNDS));

        final User user = new User(
                normalizedUsername,
                normalizedEmail,
                phone.trim(),
                passwordHash,
                role);

        user.setEmailVerified(false);
        user.setEmailVerifiedAt(null);

        userDAO.create(user);

        return user;
    }

    /**
     * Authenticates a user using username and password.
     *
     * @param username username
     * @param password plain-text password
     * @return authenticated user
     * @throws SQLException if database operation fails
     */
    public User authenticate(
            final String username,
            final String password) throws SQLException {

        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException(
                    "Username is required.");
        }

        if (password == null || password.isEmpty()) {
            throw new IllegalArgumentException(
                    "Password is required.");
        }

        final User user =
                userDAO.findByUsername(username.trim());

        if (user == null) {
            throw new IllegalArgumentException(
                    "Invalid username or password.");
        }

        if (!BCrypt.checkpw(
                password,
                user.getPasswordHash())) {
            throw new IllegalArgumentException(
                    "Invalid username or password.");
        }

        if (!User.ROLE_ADMIN.equals(user.getRole())
                && !user.isEmailVerified()) {
            throw new IllegalArgumentException(
                    "Please verify your email before logging in.");
        }

        return user;
    }

    /**
     * Validates registration input before accessing the DAO.
     *
     * @param username username
     * @param email email address
     * @param phone phone number
     * @param password password
     */
    private void validateRegistrationInput(
            final String username,
            final String email,
            final String phone,
            final String password) {

        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException(
                    "Username is required.");
        }

        final String normalizedUsername = username.trim();

        if (normalizedUsername.length()
                > MAXIMUM_USERNAME_LENGTH) {
            throw new IllegalArgumentException(
                    "Username must not exceed "
                            + MAXIMUM_USERNAME_LENGTH
                            + " characters.");
        }

        if (!normalizedUsername.matches(
                "^[A-Za-z0-9._-]+$")) {
            throw new IllegalArgumentException(
                    "Username may contain only letters, numbers, "
                            + "dots, underscores, and hyphens.");
        }

        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException(
                    "Email is required.");
        }

        final String normalizedEmail =
                email.trim().toLowerCase();

        if (normalizedEmail.length()
                > MAXIMUM_EMAIL_LENGTH) {
            throw new IllegalArgumentException(
                    "Email address is too long.");
        }

        if (!normalizedEmail.matches(EMAIL_PATTERN)) {
            throw new IllegalArgumentException(
                    "Enter a valid email address.");
        }

        if (phone == null
                || !phone.trim().matches("\\d{10,15}")) {
            throw new IllegalArgumentException(
                    "Phone number must contain 10 to 15 digits.");
        }

        if (password == null
                || password.length()
                < MINIMUM_PASSWORD_LENGTH) {
            throw new IllegalArgumentException(
                    "Password must contain at least "
                            + MINIMUM_PASSWORD_LENGTH
                            + " characters.");
        }

        if (password.length()
                > MAXIMUM_PASSWORD_LENGTH) {
            throw new IllegalArgumentException(
                    "Password must not exceed "
                            + MAXIMUM_PASSWORD_LENGTH
                            + " characters.");
        }

        if (!password.matches(".*[A-Z].*")) {
            throw new IllegalArgumentException(
                    "Password must contain at least one "
                            + "uppercase letter.");
        }

        if (!password.matches(".*[a-z].*")) {
            throw new IllegalArgumentException(
                    "Password must contain at least one "
                            + "lowercase letter.");
        }

        if (!password.matches(".*\\d.*")) {
            throw new IllegalArgumentException(
                    "Password must contain at least one number.");
        }

        if (!password.matches(
                ".*[^A-Za-z0-9].*")) {
            throw new IllegalArgumentException(
                    "Password must contain at least one "
                            + "special character.");
        }
    }
}
