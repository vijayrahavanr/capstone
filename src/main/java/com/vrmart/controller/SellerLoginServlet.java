package com.vrmart.controller;

import com.vrmart.dao.UserDAO;
import com.vrmart.dao.UserSettingsDAO;
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
 * Handles seller login requests.
 */
@WebServlet("/seller/login")
public final class SellerLoginServlet extends HttpServlet {

    /** Serialization version. */
    private static final long serialVersionUID = 1L;

    /** Seller login page. */
    private static final String LOGIN_PAGE = "/seller/login.jsp";

    /** Seller dashboard page. */
    private static final String SELLER_DASHBOARD =
            "/seller/dashboard.jsp";

    /**
     * Displays the seller login page.
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
     * Processes seller login.
     *
     * @param request HTTP request
     * @param response HTTP response
     * @throws ServletException when processing fails
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
            final HikariDataSource dataSource = getDataSource(request);
            final UserDAO userDAO = new UserDAO(dataSource);
            final AuthService authService = new AuthService(userDAO);
            final User user = authService.authenticate(username, password);

            if (!User.ROLE_SELLER.equals(user.getRole())) {
                request.setAttribute(
                        "error",
                        "This login is only for seller accounts.");
                request.getRequestDispatcher(LOGIN_PAGE)
                        .forward(request, response);
                return;
            }

            final HttpSession session = request.getSession(true);
            request.changeSessionId();
            session.setAttribute("user", user);
            session.setAttribute("role", User.ROLE_SELLER);

            final UserSettingsDAO settingsDAO =
                    new UserSettingsDAO(dataSource);
            session.setAttribute(
                    "userSettings",
                    settingsDAO.ensureSettings(user.getId()));

            response.sendRedirect(
                    request.getContextPath() + SELLER_DASHBOARD);
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
     * Gets the application database connection pool.
     *
     * @param request HTTP request
     * @return HikariCP data source
     * @throws ServletException when data source is unavailable
     */
    private HikariDataSource getDataSource(
            final HttpServletRequest request) throws ServletException {
        final Object dataSource = request.getServletContext()
                .getAttribute(DatabaseListener.DATA_SOURCE_ATTRIBUTE);
        if (!(dataSource instanceof HikariDataSource)) {
            throw new ServletException(
                    "VR Mart database connection is unavailable.");
        }
        return (HikariDataSource) dataSource;
    }
}
