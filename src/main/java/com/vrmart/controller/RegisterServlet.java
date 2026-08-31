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
import java.io.IOException;
import java.sql.SQLException;

/**
 * Handles VR Mart user registration requests.
 */
@WebServlet("/register")
public final class RegisterServlet extends HttpServlet {

    /** Serialization version. */
    private static final long serialVersionUID = 1L;

    /** Registration page. */
    private static final String REGISTER_PAGE = "/register.jsp";

    /** Buyer dashboard page. */
    private static final String LOGIN_PAGE = "/login.jsp";

    /**
     * Handles registration form submission.
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
        final String email = request.getParameter("email");
        final String phone = request.getParameter("phone");
        final String password = request.getParameter("password");
        final String confirmPassword =
                request.getParameter("confirmPassword");

        if (password == null
                || !password.equals(confirmPassword)) {
            request.setAttribute(
                    "error",
                    "Passwords do not match.");
            request.getRequestDispatcher(REGISTER_PAGE)
                    .forward(request, response);
            return;
        }

        try {
            final HikariDataSource dataSource =
                    getDataSource(request);

            final UserDAO userDAO = new UserDAO(dataSource);
            final AuthService authService =
                    new AuthService(userDAO);

            final User user = authService.register(
                    username,
                    email,
                    phone,
                    password);

            request.getSession(true).setAttribute(
                    "user",
                    user);

            response.sendRedirect(
                    request.getContextPath() + LOGIN_PAGE);

        } catch (IllegalArgumentException exception) {
            request.setAttribute(
                    "error",
                    exception.getMessage());
            request.getRequestDispatcher(REGISTER_PAGE)
                    .forward(request, response);

        } catch (SQLException exception) {
    exception.printStackTrace();

    request.setAttribute(
            "error",
            "Database error: " + exception.getMessage());

    request.getRequestDispatcher(REGISTER_PAGE)
            .forward(request, response);
}
    }

    /**
     * Handles direct GET requests to the registration endpoint.
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

        request.getRequestDispatcher(REGISTER_PAGE)
                .forward(request, response);
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
