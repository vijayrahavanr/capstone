package com.vrmart.controller;

import com.vrmart.dao.OtpDAO;
import com.vrmart.dao.UserDAO;
import com.vrmart.listener.DatabaseListener;
import com.vrmart.model.User;
import com.vrmart.service.OtpService;
import com.zaxxer.hikari.HikariDataSource;
import org.mindrot.jbcrypt.BCrypt;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/reset-password")
public final class ResetPasswordServlet extends HttpServlet {

    /** Password reset OTP purpose. */
    private static final String PURPOSE = "PASSWORD_RESET";

    /** Session attribute for reset user ID. */
    private static final String RESET_USER_ID = "resetUserId";

    /** Session attribute for reset email. */
    private static final String RESET_EMAIL = "resetEmail";

    /** BCrypt work factor used by VR Mart authentication. */
    private static final int BCRYPT_ROUNDS = 12;

    /** Minimum password length. */
    private static final int MIN_PASSWORD_LENGTH = 8;

    /**
     * Displays the reset-password page.
     *
     * @param request HTTP request
     * @param response HTTP response
     * @throws ServletException when forwarding fails
     * @throws IOException when redirect or forwarding fails
     */
    @Override
    protected void doGet(
            final HttpServletRequest request,
            final HttpServletResponse response)
            throws ServletException, IOException {

        final HttpSession session = request.getSession(false);

        if (session == null
                || session.getAttribute(RESET_USER_ID) == null) {
            response.sendRedirect(
                    request.getContextPath()
                            + "/forgot-password");
            return;
        }

        request.getRequestDispatcher("/reset-password.jsp")
                .forward(request, response);
    }

    /**
     * Verifies the OTP and updates the password.
     *
     * @param request HTTP request
     * @param response HTTP response
     * @throws ServletException when servlet processing fails
     * @throws IOException when redirect or forwarding fails
     */
    @Override
    protected void doPost(
            final HttpServletRequest request,
            final HttpServletResponse response)
            throws ServletException, IOException {

        final HttpSession session = request.getSession(false);

        if (session == null
                || session.getAttribute(RESET_USER_ID) == null) {
            response.sendRedirect(
                    request.getContextPath()
                            + "/forgot-password");
            return;
        }

        final Long userId = getUserId(session);

        if (userId == null) {
            session.invalidate();
            response.sendRedirect(
                    request.getContextPath()
                            + "/forgot-password");
            return;
        }

        final String otp = trim(request.getParameter("otp"));
        final String newPassword =
                request.getParameter("newPassword");
        final String confirmPassword =
                request.getParameter("confirmPassword");

        final String validationError = validatePassword(
                otp,
                newPassword,
                confirmPassword);

        if (validationError != null) {
            showError(request, response, validationError);
            return;
        }

        final HikariDataSource dataSource =
                getDataSource(request);

        try {
            final OtpService otpService =
                    new OtpService(new OtpDAO(dataSource));

            if (!otpService.verifyOtp(
                    userId,
                    PURPOSE,
                    otp)) {
                showError(
                        request,
                        response,
                        "Invalid or expired verification code.");
                return;
            }

            final UserDAO userDAO =
                    new UserDAO(dataSource);
            final User user = userDAO.findById(userId);

            if (user == null) {
                session.invalidate();
                response.sendRedirect(
                        request.getContextPath()
                                + "/forgot-password");
                return;
            }

            final String passwordHash = BCrypt.hashpw(
                    newPassword,
                    BCrypt.gensalt(BCRYPT_ROUNDS));

            user.setPasswordHash(passwordHash);

            if (!userDAO.update(user)) {
                showError(
                        request,
                        response,
                        "Unable to update your password. "
                                + "Please try again.");
                return;
            }

            session.removeAttribute(RESET_USER_ID);
            session.removeAttribute(RESET_EMAIL);

            request.setAttribute(
                    "success",
                    "Password updated successfully. "
                            + "Please log in with your new password.");

            request.getRequestDispatcher("/login.jsp")
                    .forward(request, response);
        } catch (SQLException exception) {
            showError(
                    request,
                    response,
                    "Unable to reset your password. "
                            + "Please try again.");
        }
    }

    /**
     * Validates the reset-password form.
     *
     * @param otp submitted OTP
     * @param password new password
     * @param confirmation confirmation password
     * @return validation error or null
     */
    private String validatePassword(
            final String otp,
            final String password,
            final String confirmation) {

        if (otp == null || !otp.matches("\\d{6}")) {
            return "Please enter the 6-digit verification code.";
        }

        if (password == null
                || password.length() < MIN_PASSWORD_LENGTH) {
            return "Password must be at least 8 characters.";
        }

        if (!hasUppercase(password)) {
            return "Password must contain an uppercase letter.";
        }

        if (!hasLowercase(password)) {
            return "Password must contain a lowercase letter.";
        }

        if (!hasDigit(password)) {
            return "Password must contain a number.";
        }

        if (!hasSpecialCharacter(password)) {
            return "Password must contain a special character.";
        }

        if (!password.equals(confirmation)) {
            return "Passwords do not match.";
        }

        return null;
    }

    /**
     * Checks for an uppercase character.
     *
     * @param value password
     * @return true when present
     */
    private boolean hasUppercase(final String value) {
        return value.chars().anyMatch(Character::isUpperCase);
    }

    /**
     * Checks for a lowercase character.
     *
     * @param value password
     * @return true when present
     */
    private boolean hasLowercase(final String value) {
        return value.chars().anyMatch(Character::isLowerCase);
    }

    /**
     * Checks for a numeric character.
     *
     * @param value password
     * @return true when present
     */
    private boolean hasDigit(final String value) {
        return value.chars().anyMatch(Character::isDigit);
    }

    /**
     * Checks for a special character.
     *
     * @param value password
     * @return true when present
     */
    private boolean hasSpecialCharacter(final String value) {
        return value.chars()
                .anyMatch(character ->
                        !Character.isLetterOrDigit(character)
                                && !Character.isWhitespace(character));
    }

    /**
     * Gets the reset user ID from the session.
     *
     * @param session current session
     * @return user ID or null
     */
    private Long getUserId(final HttpSession session) {
        final Object value =
                session.getAttribute(RESET_USER_ID);

        if (value instanceof Long) {
            return (Long) value;
        }

        if (value instanceof Number) {
            return ((Number) value).longValue();
        }

        return null;
    }

    /**
     * Gets the application data source.
     *
     * @param request HTTP request
     * @return Hikari data source
     * @throws ServletException when unavailable
     */
    private HikariDataSource getDataSource(
            final HttpServletRequest request)
            throws ServletException {

        final Object attribute =
                request.getServletContext()
                        .getAttribute(
                                DatabaseListener.DATA_SOURCE_ATTRIBUTE);

        if (!(attribute instanceof HikariDataSource)) {
            throw new ServletException(
                    "VR Mart data source is unavailable.");
        }

        return (HikariDataSource) attribute;
    }

    /**
     * Displays a reset-password error.
     *
     * @param request HTTP request
     * @param response HTTP response
     * @param message error message
     * @throws ServletException when forwarding fails
     * @throws IOException when forwarding fails
     */
    private void showError(
            final HttpServletRequest request,
            final HttpServletResponse response,
            final String message)
            throws ServletException, IOException {

        request.setAttribute("error", message);
        request.getRequestDispatcher("/reset-password.jsp")
                .forward(request, response);
    }

    /**
     * Trims a request parameter.
     *
     * @param value request value
     * @return trimmed value
     */
    private String trim(final String value) {
        return value == null ? "" : value.trim();
    }
}
