package com.vrmart.controller;

import com.vrmart.dao.UserSettingsDAO;
import com.vrmart.listener.DatabaseListener;
import com.vrmart.model.User;
import com.vrmart.model.UserSettings;
import com.zaxxer.hikari.HikariDataSource;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.SQLException;

/**
 * Handles authenticated user preference settings.
 */
@WebServlet("/settings")
public final class SettingsServlet extends HttpServlet {

    /** Serialization version. */
    private static final long serialVersionUID = 1L;

    /** Login page used when authentication is missing. */
    private static final String LOGIN_PAGE = "/login";

    /** Buyer role. */
    private static final String BUYER_ROLE = User.ROLE_BUYER;

    /** Seller role. */
    private static final String SELLER_ROLE = User.ROLE_SELLER;

    /** Save action. */
    private static final String ACTION_SAVE = "save";

    /** Reset action. */
    private static final String ACTION_RESET = "reset";

    /** Settings success message. */
    private static final String SAVE_SUCCESS =
            "Settings+saved+successfully.";

    /** Settings reset message. */
    private static final String RESET_SUCCESS =
            "Settings+reset+successfully.";

    /**
     * Displays the settings page for the authenticated user.
     *
     * @param request HTTP request
     * @param response HTTP response
     * @throws ServletException when database or forwarding fails
     * @throws IOException when request processing fails
     */
    @Override
    protected void doGet(
            final HttpServletRequest request,
            final HttpServletResponse response)
            throws ServletException, IOException {

        final User user = getAuthenticatedUser(request);
        if (!isAllowedRole(user)) {
            redirectToLogin(request, response);
            return;
        }

        try {
            final UserSettingsDAO settingsDAO =
                    new UserSettingsDAO(getDataSource(request));
            final UserSettings settings =
                    settingsDAO.ensureSettings(user.getId());

            request.getSession().setAttribute(
                    "userSettings",
                    settings);

            forwardToSettingsPage(request, response, user);
        } catch (SQLException exception) {
            throw new ServletException(
                    "Unable to load user settings.",
                    exception);
        }
    }

    /**
     * Saves or resets preferences for the authenticated user.
     *
     * @param request HTTP request
     * @param response HTTP response
     * @throws ServletException when database access fails
     * @throws IOException when request processing fails
     */
    @Override
    protected void doPost(
            final HttpServletRequest request,
            final HttpServletResponse response)
            throws ServletException, IOException {

        final User user = getAuthenticatedUser(request);
        if (!isAllowedRole(user)) {
            redirectToLogin(request, response);
            return;
        }

        try {
            final UserSettingsDAO settingsDAO =
                    new UserSettingsDAO(getDataSource(request));
            final UserSettings settings =
                    settingsDAO.ensureSettings(user.getId());
            final String action = clean(request.getParameter("action"));

            if (ACTION_RESET.equalsIgnoreCase(action)) {
                resetSettings(settings);
                settingsDAO.update(settings);
                updateSession(request, settings);
                redirectWithMessage(
                        request,
                        response,
                        RESET_SUCCESS);
                return;
            }

            if (!action.isEmpty()
                    && !ACTION_SAVE.equalsIgnoreCase(action)) {
                response.sendError(
                        HttpServletResponse.SC_BAD_REQUEST,
                        "Invalid settings action.");
                return;
            }

            settings.setOrderUpdates(
                    request.getParameter("orderUpdates") != null);
            settings.setEmailNotifications(
                    request.getParameter("emailNotifications") != null);
            settings.setProductRecommendations(
                    request.getParameter("recommendations") != null);
            settings.setDarkMode(
                    request.getParameter("darkMode") != null);

            settingsDAO.update(settings);
            updateSession(request, settings);

            redirectWithMessage(
                    request,
                    response,
                    SAVE_SUCCESS);
        } catch (SQLException exception) {
            throw new ServletException(
                    "Unable to save user settings.",
                    exception);
        }
    }

    /**
     * Gets the logged-in user from the session.
     *
     * @param request HTTP request
     * @return authenticated user or null
     */
    private User getAuthenticatedUser(
            final HttpServletRequest request) {

        final HttpSession session =
                request.getSession(false);

        if (session == null) {
            return null;
        }

        final Object sessionUser = session.getAttribute("user");
        if (!(sessionUser instanceof User)) {
            return null;
        }

        return (User) sessionUser;
    }

    /**
     * Checks whether the user has a supported role.
     *
     * @param user authenticated user
     * @return true when buyer or seller
     */
    private boolean isAllowedRole(final User user) {
        if (user == null || user.getId() == null) {
            return false;
        }

        return BUYER_ROLE.equals(user.getRole())
                || SELLER_ROLE.equals(user.getRole());
    }

    /**
     * Resets all preferences to the application's default state.
     *
     * @param settings settings model
     */
    private void resetSettings(final UserSettings settings) {
        settings.setOrderUpdates(true);
        settings.setEmailNotifications(true);
        settings.setProductRecommendations(true);
        settings.setDarkMode(true);
    }

    /**
     * Updates the current session with saved settings.
     *
     * @param request HTTP request
     * @param settings saved settings
     */
    private void updateSession(
            final HttpServletRequest request,
            final UserSettings settings) {

        request.getSession().setAttribute(
                "userSettings",
                settings);
    }

    /**
     * Redirects an unauthenticated user to login.
     *
     * @param request HTTP request
     * @param response HTTP response
     * @throws IOException when redirect fails
     */
    private void redirectToLogin(
            final HttpServletRequest request,
            final HttpServletResponse response)
            throws IOException {

        response.sendRedirect(
                request.getContextPath() + LOGIN_PAGE);
    }

    /**
     * Redirects to settings with a success message.
     *
     * @param request HTTP request
     * @param response HTTP response
     * @param message URL-safe success message
     * @throws IOException when redirect fails
     */
    private void redirectWithMessage(
            final HttpServletRequest request,
            final HttpServletResponse response,
            final String message)
            throws IOException {

        response.sendRedirect(
                request.getContextPath()
                        + "/settings?success="
                        + message);
    }

    /**
     * Forwards to the role-specific settings page.
     *
     * @param request HTTP request
     * @param response HTTP response
     * @param user authenticated user
     * @throws ServletException when forwarding fails
     * @throws IOException when forwarding fails
     */
    private void forwardToSettingsPage(
            final HttpServletRequest request,
            final HttpServletResponse response,
            final User user)
            throws ServletException, IOException {

        if (SELLER_ROLE.equals(user.getRole())) {
            request.getRequestDispatcher(
                    "/seller/settings.jsp")
                    .forward(request, response);
            return;
        }

        request.getRequestDispatcher(
                "/buyer/settings.jsp")
                .forward(request, response);
    }

    /**
     * Gets the application database connection pool.
     *
     * @param request HTTP request
     * @return HikariCP data source
     * @throws ServletException when the pool is unavailable
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
                    "VR Mart database connection "
                            + "is unavailable.");
        }

        return (HikariDataSource) source;
    }

    /**
     * Cleans request input.
     *
     * @param value request value
     * @return trimmed value or empty string
     */
    private String clean(final String value) {
        return value == null ? "" : value.trim();
    }
}
