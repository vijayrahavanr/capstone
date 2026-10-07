package com.vrmart.controller;

import com.vrmart.dao.UserDAO;
import com.vrmart.listener.DatabaseListener;
import com.vrmart.model.User;
import com.zaxxer.hikari.HikariDataSource;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Locale;

/**
 * Handles buyer and seller profile management.
 */
@WebServlet("/profile")
public final class ProfileServlet extends HttpServlet {
    /** Serialization version. */
    private static final long serialVersionUID = 1L;
    /** Minimum username length. */
    private static final int MIN_USERNAME_LENGTH = 3;
    /** Maximum username length. */
    private static final int MAX_USERNAME_LENGTH = 50;
    /** Phone number length. */
    private static final int PHONE_LENGTH = 10;
    /** First SQL-style parameter position. */
    private static final int PARAM_ONE = 1;

    /**
     * Displays the profile page.
     *
     * @param request HTTP request
     * @param response HTTP response
     * @throws ServletException when processing fails
     * @throws IOException when forwarding fails
     */
    @Override
    protected void doGet(
            final HttpServletRequest request,
            final HttpServletResponse response)
            throws ServletException, IOException {
        final HttpSession session =
                request.getSession(false);
        final User user =
                getAuthenticatedUser(session);

        if (user == null) {
            response.sendRedirect(
                    request.getContextPath() + "/login");
            return;
        }

        try {
            final UserDAO userDAO =
                    new UserDAO(getDataSource(request));
            final User latestUser =
                    userDAO.findById(user.getId());

            if (latestUser == null) {
                session.invalidate();
                response.sendRedirect(
                        request.getContextPath() + "/login");
                return;
            }

            session.setAttribute("user", latestUser);
            forwardProfilePage(
                    request,
                    response,
                    latestUser);
        } catch (SQLException exception) {
            throw new ServletException(
                    "Unable to load profile.",
                    exception);
        }
    }

    /**
     * Updates the authenticated user's profile.
     *
     * @param request HTTP request
     * @param response HTTP response
     * @throws ServletException when processing fails
     * @throws IOException when redirect fails
     */
    @Override
    protected void doPost(
            final HttpServletRequest request,
            final HttpServletResponse response)
            throws ServletException, IOException {
        final HttpSession session =
                request.getSession(false);
        final User currentUser =
                getAuthenticatedUser(session);

        if (currentUser == null) {
            response.sendRedirect(
                    request.getContextPath() + "/login");
            return;
        }

        final String username =
                clean(request.getParameter("username"));
        final String email =
                clean(request.getParameter("email"))
                        .toLowerCase(Locale.ROOT);
        final String phone =
                clean(request.getParameter("phone"));

        final String validationMessage =
                validate(username, email, phone);

        if (validationMessage != null) {
            showProfileWithMessage(
                    request,
                    response,
                    currentUser,
                    validationMessage);
            return;
        }

        try {
            final UserDAO userDAO =
                    new UserDAO(getDataSource(request));

            if (userDAO.existsByUsernameExcept(
                    username,
                    currentUser.getId())) {
                showProfileWithMessage(
                        request,
                        response,
                        currentUser,
                        "Username is already in use.");
                return;
            }

            if (userDAO.existsByEmailExcept(
                    email,
                    currentUser.getId())) {
                showProfileWithMessage(
                        request,
                        response,
                        currentUser,
                        "Email is already in use.");
                return;
            }

            final boolean updated =
                    userDAO.updateProfile(
                            currentUser.getId(),
                            username,
                            email,
                            phone);

            if (!updated) {
                showProfileWithMessage(
                        request,
                        response,
                        currentUser,
                        "Unable to update profile.");
                return;
            }

            final User updatedUser =
                    userDAO.findById(
                            currentUser.getId());

            if (updatedUser == null) {
                throw new SQLException(
                        "Updated user could not be loaded.");
            }

            session.setAttribute(
                    "user",
                    updatedUser);

            response.sendRedirect(
                    request.getContextPath()
                            + "/profile?success="
                            + "Profile+updated+successfully.");
        } catch (SQLException exception) {
            throw new ServletException(
                    "Unable to update profile.",
                    exception);
        }
    }

    /**
     * Validates profile information.
     *
     * @param username username
     * @param email email address
     * @param phone phone number
     * @return validation error or null
     */
    private String validate(
            final String username,
            final String email,
            final String phone) {
        if (username.isEmpty()) {
            return "Username is required.";
        }

        if (username.length() < MIN_USERNAME_LENGTH) {
            return "Username must contain at least "
                    + MIN_USERNAME_LENGTH
                    + " characters.";
        }

        if (username.length() > MAX_USERNAME_LENGTH) {
            return "Username must not exceed "
                    + MAX_USERNAME_LENGTH
                    + " characters.";
        }

        if (!username.matches("[A-Za-z0-9_]+")) {
            return "Username can contain only "
                    + "letters, numbers and underscore.";
        }

        if (email.isEmpty()
                || !email.matches(
                        "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
            return "Enter a valid email address.";
        }

        if (!phone.matches(
                "[6-9][0-9]{"
                        + (PHONE_LENGTH - PARAM_ONE)
                        + "}")) {
            return "Enter a valid 10-digit phone number.";
        }

        return null;
    }

    /**
     * Displays profile page with an error message.
     *
     * @param request HTTP request
     * @param response HTTP response
     * @param user current user
     * @param message message to display
     * @throws ServletException when forwarding fails
     * @throws IOException when forwarding fails
     */
    private void showProfileWithMessage(
            final HttpServletRequest request,
            final HttpServletResponse response,
            final User user,
            final String message)
            throws ServletException, IOException {
        request.setAttribute(
                "profileMessage",
                message);
        request.setAttribute(
                "profileUser",
                user);
        forwardProfilePage(
                request,
                response,
                user);
    }

    /**
     * Forwards to the role-specific profile page.
     *
     * @param request HTTP request
     * @param response HTTP response
     * @param user authenticated user
     * @throws ServletException when forwarding fails
     * @throws IOException when forwarding fails
     */
    private void forwardProfilePage(
            final HttpServletRequest request,
            final HttpServletResponse response,
            final User user)
            throws ServletException, IOException {
        final String page;

        if (User.ROLE_SELLER.equals(user.getRole())) {
            page = "/seller/profile.jsp";
        } else {
            page = "/buyer/profile.jsp";
        }

        request.getRequestDispatcher(page)
                .forward(request, response);
    }

    /**
     * Gets the authenticated buyer or seller.
     *
     * @param session current HTTP session
     * @return authenticated user or null
     */
    private User getAuthenticatedUser(
            final HttpSession session) {
        if (session == null) {
            return null;
        }

        final Object userObject =
                session.getAttribute("user");

        if (!(userObject instanceof User)) {
            return null;
        }

        final User user =
                (User) userObject;

        if (User.ROLE_BUYER.equals(user.getRole())
                || User.ROLE_SELLER.equals(user.getRole())) {
            return user;
        }

        return null;
    }

    /**
     * Gets the configured database source.
     *
     * @param request HTTP request
     * @return database source
     * @throws ServletException when source is unavailable
     */
    private HikariDataSource getDataSource(
            final HttpServletRequest request)
            throws ServletException {
        final Object source =
                request.getServletContext()
                        .getAttribute(
                                DatabaseListener
                                        .DATA_SOURCE_ATTRIBUTE);

        if (!(source instanceof HikariDataSource)) {
            throw new ServletException(
                    "VR Mart database connection is unavailable.");
        }

        return (HikariDataSource) source;
    }

    /**
     * Removes surrounding whitespace.
     *
     * @param value input value
     * @return cleaned value
     */
    private String clean(final String value) {
        return value == null ? "" : value.trim();
    }
}
