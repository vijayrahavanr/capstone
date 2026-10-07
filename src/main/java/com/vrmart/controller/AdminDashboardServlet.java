package com.vrmart.controller;

import com.vrmart.dao.AdminDAO;
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
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Loads the VR Mart administrator control center and analytics.
 */
@WebServlet("/admin/dashboard")
public final class AdminDashboardServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(
            final HttpServletRequest request,
            final HttpServletResponse response)
            throws ServletException, IOException {

        final HttpSession session = request.getSession(false);

        if (!isAdmin(session)) {
            response.sendRedirect(
                    request.getContextPath() + "/admin/login");
            return;
        }

        try {
            final HikariDataSource dataSource =
                    getDataSource(request);

            final AdminDAO adminDAO =
                    new AdminDAO(dataSource);

            request.setAttribute(
                    "counts",
                    adminDAO.getCounts());

            request.setAttribute(
                    "users",
                    adminDAO.findUsers());

            request.setAttribute(
                    "products",
                    adminDAO.findProducts());

            request.setAttribute(
                    "orders",
                    adminDAO.findOrders());

            request.setAttribute(
                    "serviceRequests",
                    adminDAO.findServiceRequests());

            request.setAttribute(
                    "reviews",
                    findAllProductReviews(dataSource));

            request.setAttribute(
                    "analytics",
                    adminDAO.getAnalytics());

            request.getRequestDispatcher(
                    "/admin/dashboard.jsp")
                    .forward(request, response);

        } catch (Exception exception) {
            throw new ServletException(
                    "Unable to load administrator dashboard.",
                    exception);
        }
    }

    /**
     * Loads product reviews from the product_reviews table.
     *
     * @param dataSource database connection pool
     * @return review rows
     * @throws Exception when database access fails
     */
    private List<Map<String, Object>> findAllProductReviews(
            final HikariDataSource dataSource)
            throws Exception {

        final List<Map<String, Object>> reviews =
                new ArrayList<>();

        final String sql =
                "SELECT pr.id, "
                        + "u.username AS buyer, "
                        + "p.name AS product, "
                        + "pr.rating, "
                        + "pr.review_text AS comment, "
                        + "pr.created_at "
                        + "FROM product_reviews pr "
                        + "JOIN users u ON u.id = pr.buyer_id "
                        + "JOIN products p ON p.id = pr.product_id "
                        + "ORDER BY pr.created_at DESC";

        try (Connection connection =
                     dataSource.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql);
             ResultSet result =
                     statement.executeQuery()) {

            while (result.next()) {
                final Map<String, Object> row =
                        new HashMap<>();

                row.put(
                        "id",
                        result.getLong("id"));

                row.put(
                        "buyer",
                        result.getString("buyer"));

                row.put(
                        "product",
                        result.getString("product"));

                row.put(
                        "rating",
                        result.getInt("rating"));

                row.put(
                        "comment",
                        result.getString("comment"));

                row.put(
                        "created_at",
                        result.getTimestamp("created_at"));

                reviews.add(row);
            }
        }

        return reviews;
    }

    /**
     * Checks administrator authentication.
     *
     * @param session current session
     * @return true when the session belongs to an administrator
     */
    private boolean isAdmin(final HttpSession session) {

        if (session == null) {
            return false;
        }

        final Object userObject =
                session.getAttribute("user");

        return userObject instanceof User user
                && User.ROLE_ADMIN.equals(user.getRole())
                && user.getId() != null;
    }

    /**
     * Gets the application database connection pool.
     *
     * @param request servlet request
     * @return configured Hikari data source
     * @throws ServletException when unavailable
     */
    private HikariDataSource getDataSource(
            final HttpServletRequest request)
            throws ServletException {

        final Object source =
                request.getServletContext()
                        .getAttribute(
                                DatabaseListener.DATA_SOURCE_ATTRIBUTE);

        if (!(source instanceof HikariDataSource)) {
            throw new ServletException(
                    "VR Mart database connection is unavailable.");
        }

        return (HikariDataSource) source;
    }
}
