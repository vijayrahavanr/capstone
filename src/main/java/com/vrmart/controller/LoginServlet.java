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
 * Handles VR Mart user login requests.
 */
@WebServlet("/login")
public final class LoginServlet extends HttpServlet {

    /** Serialization version. */
    private static final long serialVersionUID = 1L;

    /** Login page path. */
    private static final String LOGIN_PAGE = "/login.jsp";

    /** Buyer dashboard path. */
    private static final String BUYER_DASHBOARD =
            "/buyer/dashboard.jsp";

    /** Seller dashboard path. */
    private static final String SELLER_DASHBOARD =
            "/seller/dashboard.jsp";

    /** Admin dashboard path. */
    private static final String ADMIN_DASHBOARD =
            "/admin/dashboard.jsp";

    /**
     * Displays the login page.
     *
     * @param request HTTP request
     * @param response HTTP response
     * @throws ServletException when forwarding fails
     * @throws IOException when request processing fails
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
     * Processes the login form.
     *
     * @param request HTTP request
     * @param response HTTP response
     * @throws ServletException when servlet processing fails
     * @throws IOException when request processing fails
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
            final HikariDataSource dataSource =
                    getDataSource(request);

            final UserDAO userDAO = new UserDAO(dataSource);
            final AuthService authService =
                    new AuthService(userDAO);

            final User user = authService.authenticate(
                    username,
                    password);

            final HttpSession session =
                    request.getSession(true);

            session.setAttribute("user", user);
            session.setAttribute("role", user.getRole());

            redirectByRole(
                    request,
                    response,
                    user.getRole());

        } catch (IllegalArgumentException exception) {
            request.setAttribute(
                    "error",
                    exception.getMessage());

            request.getRequestDispatcher(LOGIN_PAGE)
                    .forward(request, response);

        } catch (SQLException exception) {
            request.setAttribute(
                    "error",
                    "Unable to login right now. "
                            + "Please try again.");

            request.getRequestDispatcher(LOGIN_PAGE)
                    .forward(request, response);
        }
    }

    /**
     * Redirects the authenticated user based on role.
     *
     * @param request HTTP request
     * @param response HTTP response
     * @param role authenticated user role
     * @throws IOException when redirect fails
     */
    private void redirectByRole(
            final HttpServletRequest request,
            final HttpServletResponse response,
            final String role) throws IOException {

        final String destination;

        if (User.ROLE_SELLER.equals(role)) {
            destination = SELLER_DASHBOARD;
        } else if (User.ROLE_ADMIN.equals(role)) {
            destination = ADMIN_DASHBOARD;
        } else {
            destination = BUYER_DASHBOARD;
        }

        response.sendRedirect(
                request.getContextPath() + destination);
    }

    /**
     * Gets the application database connection pool.
     *
     * @param request HTTP request
     * @return HikariCP data source
     * @throws ServletException when data source is unavailable
     */
    private HikariDataSource getDataSource(
            final HttpServletRequest request)
            throws ServletException {

        final Object dataSource = request
                .getServletContext()
                .getAttribute(
                        DatabaseListener.DATA_SOURCE_ATTRIBUTE);

        if (!(dataSource instanceof HikariDataSource)) {
            throw new ServletException(
                    "VR Mart database connection is unavailable.");
        }

        return (HikariDataSource) dataSource;
    }
}
