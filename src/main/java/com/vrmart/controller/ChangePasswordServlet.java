package com.vrmart.controller;

import com.vrmart.dao.PasswordDAO;
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

/**
 * Handles password change requests for authenticated users.
 */
@WebServlet("/change-password")
public final class ChangePasswordServlet extends HttpServlet {

    /** Serialization version. */
    private static final long serialVersionUID = 1L;

    /** Minimum password length. */
    private static final int MIN_PASSWORD_LENGTH = 8;

    /**
     * Displays the change password page.
     *
     * @param request HTTP request
     * @param response HTTP response
     * @throws ServletException when forwarding fails
     * @throws IOException when redirect fails
     */
    @Override
    protected void doGet(
            final HttpServletRequest request,
            final HttpServletResponse response)
            throws ServletException, IOException {

        final HttpSession session =
                request.getSession(false);

        if (!isAuthenticated(session)) {
            response.sendRedirect(
                    request.getContextPath() + "/login");
            return;
        }

        request.getRequestDispatcher(
                "/change-password.jsp")
                .forward(request, response);
    }

    /**
     * Processes password change.
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

        if (!isAuthenticated(session)) {
            response.sendRedirect(
                    request.getContextPath() + "/login");
            return;
        }

        final User user =
                (User) session.getAttribute("user");

        final String currentPassword =
                request.getParameter("currentPassword");

        final String newPassword =
                request.getParameter("newPassword");

        final String confirmPassword =
                request.getParameter("confirmPassword");

        if (isBlank(currentPassword)
                || isBlank(newPassword)
                || isBlank(confirmPassword)) {
            request.setAttribute(
                    "error",
                    "All password fields are required.");
            request.getRequestDispatcher(
                    "/change-password.jsp")
                    .forward(request, response);
            return;
        }

        if (newPassword.length() < MIN_PASSWORD_LENGTH) {
            request.setAttribute(
                    "error",
                    "New password must contain at least 8 characters.");
            request.getRequestDispatcher(
                    "/change-password.jsp")
                    .forward(request, response);
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            request.setAttribute(
                    "error",
                    "New password and confirmation do not match.");
            request.getRequestDispatcher(
                    "/change-password.jsp")
                    .forward(request, response);
            return;
        }

        if (currentPassword.equals(newPassword)) {
            request.setAttribute(
                    "error",
                    "New password must be different from current password.");
            request.getRequestDispatcher(
                    "/change-password.jsp")
                    .forward(request, response);
            return;
        }

        try {
            final PasswordDAO passwordDAO =
                    new PasswordDAO(getDataSource(request));

            final boolean changed =
                    passwordDAO.changePassword(
                            user.getId(),
                            currentPassword,
                            newPassword);

            if (!changed) {
                request.setAttribute(
                        "error",
                        "Current password is incorrect.");
                request.getRequestDispatcher(
                        "/change-password.jsp")
                        .forward(request, response);
                return;
            }

            request.setAttribute(
                    "success",
                    "Password changed successfully.");

            request.getRequestDispatcher(
                    "/change-password.jsp")
                    .forward(request, response);

        } catch (Exception exception) {
            throw new ServletException(
                    "Unable to change password.",
                    exception);
        }
    }

    /**
     * Checks whether the user is authenticated.
     *
     * @param session current session
     * @return true when authenticated
     */
    private boolean isAuthenticated(
            final HttpSession session) {

        if (session == null) {
            return false;
        }

        final Object userObject =
                session.getAttribute("user");

        return userObject instanceof User user
                && user.getId() != null;
    }

    /**
     * Checks whether a string is blank.
     *
     * @param value string value
     * @return true when blank
     */
    private boolean isBlank(final String value) {
        return value == null
                || value.trim().isEmpty();
    }

    /**
     * Gets the application data source.
     *
     * @param request HTTP request
     * @return HikariCP data source
     * @throws ServletException when unavailable
     */
    private HikariDataSource getDataSource(
            final HttpServletRequest request)
            throws ServletException {

        final Object source =
                request.getServletContext().getAttribute(
                        DatabaseListener.DATA_SOURCE_ATTRIBUTE);

        if (!(source instanceof HikariDataSource)) {
            throw new ServletException(
                    "VR Mart database connection is unavailable.");
        }

        return (HikariDataSource) source;
    }
}
