package com.vrmart.controller;

import com.vrmart.dao.UserDAO;
import com.vrmart.listener.DatabaseListener;
import com.vrmart.model.User;
import com.vrmart.service.AuthService;
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
 * Handles the dedicated VR Mart administrator login.
 */
@WebServlet("/admin/login")
public final class AdminLoginServlet extends HttpServlet {

    /** Serialization version. */
    private static final long serialVersionUID = 1L;

    /** Administrator login page. */
    private static final String LOGIN_PAGE = "/admin/login.jsp";

    /** Administrator dashboard. */
    private static final String DASHBOARD = "/admin/dashboard";

    /**
     * Shows the administrator login page.
     *
     * @param request HTTP request
     * @param response HTTP response
     * @throws ServletException when forwarding fails
     * @throws IOException when forwarding fails
     */
    @Override
    protected void doGet(
            final HttpServletRequest request,
            final HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher(LOGIN_PAGE)
                .forward(request, response);
    }

    /**
     * Authenticates an administrator.
     *
     * @param request HTTP request
     * @param response HTTP response
     * @throws ServletException when processing fails
     * @throws IOException when forwarding fails
     */
    @Override
    protected void doPost(
            final HttpServletRequest request,
            final HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        final String username = request.getParameter("username");
        final String password = request.getParameter("password");

        try {
            final HikariDataSource dataSource = getDataSource(request);
            final AuthService authService =
                    new AuthService(new UserDAO(dataSource));
            final User user = authService.authenticate(username, password);

            if (!User.ROLE_ADMIN.equals(user.getRole())) {
                request.setAttribute(
                        "error",
                        "This login is only for VR Mart administrators.");
                request.getRequestDispatcher(LOGIN_PAGE)
                        .forward(request, response);
                return;
            }

            final HttpSession session = request.getSession(true);
            request.changeSessionId();
            session.setAttribute("user", user);
            session.setAttribute("role", User.ROLE_ADMIN);

            response.sendRedirect(
                    request.getContextPath() + DASHBOARD);
        } catch (IllegalArgumentException exception) {
            request.setAttribute("error", exception.getMessage());
            request.getRequestDispatcher(LOGIN_PAGE)
                    .forward(request, response);
        } catch (SQLException exception) {
            request.setAttribute(
                    "error",
                    "Unable to login right now. Please try again.");
            request.getRequestDispatcher(LOGIN_PAGE)
                    .forward(request, response);
        }
    }

    /**
     * Returns the application data source.
     *
     * @param request HTTP request
     * @return HikariCP data source
     * @throws ServletException when unavailable
     */
    private HikariDataSource getDataSource(
            final HttpServletRequest request) throws ServletException {
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
