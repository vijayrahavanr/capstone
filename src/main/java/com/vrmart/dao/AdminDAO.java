package com.vrmart.dao;

import com.zaxxer.hikari.HikariDataSource;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Data access object for the VR Mart administrator.
 */
public final class AdminDAO {

    /** First SQL parameter. */
    private static final int PARAM_ONE = 1;

    /** Second SQL parameter. */
    private static final int PARAM_TWO = 2;

    /** Database connection pool. */
    private final HikariDataSource dataSource;

    /**
     * Creates the administrator DAO.
     *
     * @param source database connection pool
     */
    public AdminDAO(final HikariDataSource source) {
        dataSource = source;
    }

    /**
     * Returns all users.
     *
     * @return user rows
     * @throws SQLException when database access fails
     */
    public List<Map<String, Object>> findUsers()
            throws SQLException {

        return query(
                "SELECT id, username, email, phone, role, created_at "
                        + "FROM users ORDER BY id DESC",
                new String[] {
                    "id", "username", "email", "phone",
                    "role", "created_at"
                });
    }

    /**
     * Returns all products, including inactive products.
     *
     * @return product rows
     * @throws SQLException when database access fails
     */
    public List<Map<String, Object>> findProducts()
            throws SQLException {

        return query(
                "SELECT p.id, p.name, p.category, p.price, "
                        + "p.stock_qty, p.is_active, "
                        + "COALESCE(a.assured, FALSE) "
                        + "AS is_vr_mart_assured, "
                        + "u.username AS seller "
                        + "FROM products p "
                        + "JOIN users u ON u.id = p.seller_id "
                        + "LEFT JOIN vr_mart_assurance a "
                        + "ON a.product_id = p.id "
                        + "ORDER BY p.id DESC",
                new String[] {
                    "id", "name", "category", "price",
                    "stock_qty", "is_active",
                    "is_vr_mart_assured", "seller"
                });
    }

    /**
     * Returns all orders.
     *
     * @return order rows
     * @throws SQLException when database access fails
     */
    public List<Map<String, Object>> findOrders()
            throws SQLException {

        return query(
                "SELECT o.id, u.username AS buyer, "
                        + "o.total_amount, o.status, "
                        + "o.created_at, o.updated_at "
                        + "FROM orders o "
                        + "JOIN users u ON u.id = o.buyer_id "
                        + "ORDER BY o.id DESC",
                new String[] {
                    "id", "buyer", "total_amount",
                    "status", "created_at", "updated_at"
                });
    }

    /**
     * Returns all service requests.
     *
     * @return service request rows
     * @throws SQLException when database access fails
     */
    public List<Map<String, Object>> findServiceRequests()
            throws SQLException {

        return query(
                "SELECT r.id, r.order_id, u.username AS buyer, "
                        + "p.name AS product, r.request_type, "
                        + "r.status, r.created_at, r.updated_at "
                        + "FROM order_service_requests r "
                        + "JOIN users u ON u.id = r.buyer_id "
                        + "JOIN products p ON p.id = r.product_id "
                        + "ORDER BY r.id DESC",
                new String[] {
                    "id", "order_id", "buyer", "product",
                    "request_type", "status",
                    "created_at", "updated_at"
                });
    }

    /**
     * Returns all reviews.
     *
     * @return review rows
     * @throws SQLException when database access fails
     */
    public List<Map<String, Object>> findReviews()
            throws SQLException {

        return query(
                "SELECT r.id, "
                        + "COALESCE(u.username, 'Unknown Buyer') "
                        + "AS buyer, "
                        + "COALESCE(p.name, 'Unknown Product') "
                        + "AS product, "
                        + "r.rating, r.comment, r.created_at "
                        + "FROM reviews r "
                        + "LEFT JOIN users u ON u.id = r.buyer_id "
                        + "LEFT JOIN products p ON p.id = r.product_id "
                        + "ORDER BY r.id DESC",
                new String[] {
                    "id", "buyer", "product", "rating",
                    "comment", "created_at"
                });
    }

    /**
     * Returns marketplace counts.
     *
     * @return count map
     * @throws SQLException when database access fails
     */
    public Map<String, Long> getCounts()
            throws SQLException {

        final Map<String, Long> counts = new HashMap<>();

        counts.put("users", count("users"));
        counts.put("products", count("products"));
        counts.put("orders", count("orders"));
        counts.put("reviews", count("reviews"));
        counts.put(
                "serviceRequests",
                count("order_service_requests"));

        return counts;
    }

    /**
     * Returns administrator analytics based on marketplace data.
     *
     * @return analytics map
     * @throws SQLException when database access fails
     */
    public Map<String, Object> getAnalytics()
            throws SQLException {

        final Map<String, Object> analytics = new HashMap<>();

        analytics.put("totalRevenue", getTotalRevenue());
        analytics.put("productsSold", getProductsSold());
        analytics.put("averageOrderValue", getAverageOrderValue());
        analytics.put("activeProducts", getActiveProducts());
        analytics.put("outOfStockProducts", getOutOfStockProducts());
        analytics.put("lowStockProducts", getLowStockProducts());
        analytics.put("buyers", countUsersByRole("BUYER"));
        analytics.put("sellers", countUsersByRole("SELLER"));
        analytics.put("admins", countUsersByRole("ADMIN"));
        analytics.put(
                "orderStatusStats",
                getOrderStatusStats());
        analytics.put(
                "categoryStats",
                getCategoryStats());
        analytics.put(
                "monthlyRevenue",
                getMonthlyRevenue());

        return analytics;
    }

    /**
     * Returns total revenue from delivered orders.
     *
     * @return delivered order revenue
     * @throws SQLException when database access fails
     */
    private BigDecimal getTotalRevenue()
            throws SQLException {

        final String sql =
                "SELECT COALESCE(SUM(total_amount), 0) "
                        + "FROM orders "
                        + "WHERE status = 'DELIVERED'";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            if (resultSet.next()) {
                final BigDecimal value =
                        resultSet.getBigDecimal(PARAM_ONE);

                return value == null
                        ? BigDecimal.ZERO
                        : value;
            }

            return BigDecimal.ZERO;
        }
    }

    /**
     * Returns total quantity sold from delivered orders.
     *
     * @return number of delivered units
     * @throws SQLException when database access fails
     */
    private long getProductsSold()
            throws SQLException {

        final String sql =
                "SELECT COALESCE(SUM(oi.quantity), 0) "
                        + "FROM order_items oi "
                        + "JOIN orders o ON o.id = oi.order_id "
                        + "WHERE o.status = 'DELIVERED'";

        return getLongValue(sql);
    }

    /**
     * Returns average value of delivered orders.
     *
     * @return average delivered order value
     * @throws SQLException when database access fails
     */
    private BigDecimal getAverageOrderValue()
            throws SQLException {

        final String sql =
                "SELECT COALESCE(AVG(total_amount), 0) "
                        + "FROM orders "
                        + "WHERE status = 'DELIVERED'";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            if (resultSet.next()) {
                final BigDecimal value =
                        resultSet.getBigDecimal(PARAM_ONE);

                return value == null
                        ? BigDecimal.ZERO
                        : value;
            }

            return BigDecimal.ZERO;
        }
    }

    /**
     * Returns the number of active products.
     *
     * @return active product count
     * @throws SQLException when database access fails
     */
    private long getActiveProducts()
            throws SQLException {

        return getLongValue(
                "SELECT COUNT(*) FROM products "
                        + "WHERE is_active = TRUE");
    }

    /**
     * Returns the number of out-of-stock products.
     *
     * @return out-of-stock product count
     * @throws SQLException when database access fails
     */
    private long getOutOfStockProducts()
            throws SQLException {

        return getLongValue(
                "SELECT COUNT(*) FROM products "
                        + "WHERE stock_qty <= 0");
    }

    /**
     * Returns the number of low-stock products.
     *
     * @return low-stock product count
     * @throws SQLException when database access fails
     */
    private long getLowStockProducts()
            throws SQLException {

        return getLongValue(
                "SELECT COUNT(*) FROM products "
                        + "WHERE stock_qty > 0 AND stock_qty <= 5");
    }

    /**
     * Returns the number of users for a role.
     *
     * @param role user role
     * @return user count
     * @throws SQLException when database access fails
     */
    private long countUsersByRole(final String role)
            throws SQLException {

        final String sql =
                "SELECT COUNT(*) FROM users WHERE role = ?";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setString(PARAM_ONE, role);

            try (ResultSet resultSet =
                         statement.executeQuery()) {

                if (resultSet.next()) {
                    return resultSet.getLong(PARAM_ONE);
                }
            }
        }

        return 0L;
    }

    /**
     * Returns order counts grouped by status.
     *
     * @return order status statistics
     * @throws SQLException when database access fails
     */
    private Map<String, Long> getOrderStatusStats()
            throws SQLException {

        final Map<String, Long> stats = new HashMap<>();

        final String sql =
                "SELECT status, COUNT(*) AS total "
                        + "FROM orders "
                        + "GROUP BY status "
                        + "ORDER BY status";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                stats.put(
                        resultSet.getString("status"),
                        resultSet.getLong("total"));
            }
        }

        return stats;
    }

    /**
     * Returns delivered sales grouped by product category.
     *
     * @return category statistics
     * @throws SQLException when database access fails
     */
    private Map<String, Long> getCategoryStats()
            throws SQLException {

        final Map<String, Long> stats = new HashMap<>();

        final String sql =
                "SELECT COALESCE(p.category, 'Uncategorized') "
                        + "AS category, "
                        + "COALESCE(SUM(oi.quantity), 0) AS sold "
                        + "FROM order_items oi "
                        + "JOIN orders o ON o.id = oi.order_id "
                        + "JOIN products p ON p.id = oi.product_id "
                        + "WHERE o.status = 'DELIVERED' "
                        + "GROUP BY p.category "
                        + "ORDER BY sold DESC";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                stats.put(
                        resultSet.getString("category"),
                        resultSet.getLong("sold"));
            }
        }

        return stats;
    }

    /**
     * Returns delivered revenue grouped by month.
     *
     * @return monthly revenue statistics
     * @throws SQLException when database access fails
     */
    private Map<String, BigDecimal> getMonthlyRevenue()
            throws SQLException {

        final Map<String, BigDecimal> revenue =
                new HashMap<>();

        final String sql =
                "SELECT TO_CHAR(created_at, 'YYYY-MM') AS month, "
                        + "COALESCE(SUM(total_amount), 0) AS revenue "
                        + "FROM orders "
                        + "WHERE status = 'DELIVERED' "
                        + "GROUP BY TO_CHAR(created_at, 'YYYY-MM') "
                        + "ORDER BY month";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                final BigDecimal value =
                        resultSet.getBigDecimal("revenue");

                revenue.put(
                        resultSet.getString("month"),
                        value == null ? BigDecimal.ZERO : value);
            }
        }

        return revenue;
    }

    /**
     * Reads one numeric value from a query.
     *
     * @param sql SQL query
     * @return numeric result
     * @throws SQLException when database access fails
     */
    private long getLongValue(final String sql)
            throws SQLException {

        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            if (resultSet.next()) {
                return resultSet.getLong(PARAM_ONE);
            }
        }

        return 0L;
    }

    /**
     * Changes a user's role.
     *
     * @param userId user identifier
     * @param role new role
     * @return true when changed
     * @throws SQLException when database access fails
     */
    public boolean updateUserRole(
            final long userId,
            final String role)
            throws SQLException {

        final String sql =
                "UPDATE users SET role = ?, "
                        + "updated_at = CURRENT_TIMESTAMP "
                        + "WHERE id = ?";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setString(PARAM_ONE, role);
            statement.setLong(PARAM_TWO, userId);

            return statement.executeUpdate() > 0;
        }
    }

    /**
     * Deletes a buyer or seller account without deleting the current admin.
     * Seller accounts that have products already used in orders are protected
     * to preserve order history.
     *
     * @param userId user identifier
     * @param currentAdminId current administrator identifier
     * @return true when the account is deleted
     * @throws SQLException when database access fails
     */
    public boolean deleteUser(
            final long userId,
            final long currentAdminId)
            throws SQLException {

        if (userId == currentAdminId) {
            return false;
        }

        final String roleSql =
                "SELECT role FROM users WHERE id = ?";

        final String sellerOrdersSql =
                "SELECT COUNT(*) FROM order_items oi "
                        + "JOIN products p ON p.id = oi.product_id "
                        + "WHERE p.seller_id = ?";

        final String deleteRequestsSql =
                "DELETE FROM order_service_requests "
                        + "WHERE buyer_id = ?";

        final String deleteSettingsSql =
                "DELETE FROM user_settings WHERE user_id = ?";

        final String deleteWishlistSql =
                "DELETE FROM wishlists WHERE buyer_id = ?";

        final String deleteCartSql =
                "DELETE FROM cart_items WHERE buyer_id = ?";

        final String deleteUserSql =
                "DELETE FROM users WHERE id = ?";

        try (Connection connection = dataSource.getConnection()) {

            connection.setAutoCommit(false);

            try {
                String role = null;

                try (PreparedStatement statement =
                             connection.prepareStatement(roleSql)) {

                    statement.setLong(PARAM_ONE, userId);

                    try (ResultSet resultSet =
                                 statement.executeQuery()) {

                        if (resultSet.next()) {
                            role = resultSet.getString(PARAM_ONE);
                        }
                    }
                }

                if (role == null || "ADMIN".equals(role)) {
                    connection.rollback();
                    return false;
                }

                if ("SELLER".equals(role)) {
                    try (PreparedStatement statement =
                                 connection.prepareStatement(
                                         sellerOrdersSql)) {

                        statement.setLong(PARAM_ONE, userId);

                        try (ResultSet resultSet =
                                     statement.executeQuery()) {

                            if (resultSet.next()
                                    && resultSet.getLong(PARAM_ONE) > 0) {
                                connection.rollback();
                                return false;
                            }
                        }
                    }
                }

                executeDelete(
                        connection, deleteRequestsSql, userId);
                executeDelete(
                        connection, deleteSettingsSql, userId);
                executeDelete(
                        connection, deleteWishlistSql, userId);
                executeDelete(
                        connection, deleteCartSql, userId);

                try (PreparedStatement statement =
                             connection.prepareStatement(deleteUserSql)) {

                    statement.setLong(PARAM_ONE, userId);

                    final boolean deleted =
                            statement.executeUpdate() > 0;

                    if (deleted) {
                        connection.commit();
                    } else {
                        connection.rollback();
                    }

                    return deleted;
                }
            } catch (SQLException exception) {
                connection.rollback();
                throw exception;
            } finally {
                connection.setAutoCommit(true);
            }
        }
    }

    /**
     * Executes a delete statement for one user.
     *
     * @param connection database connection
     * @param sql delete SQL
     * @param userId user identifier
     * @throws SQLException when database access fails
     */
    private void executeDelete(
            final Connection connection,
            final String sql,
            final long userId)
            throws SQLException {

        try (PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setLong(PARAM_ONE, userId);
            statement.executeUpdate();
        }
    }

    /**
     * Permanently removes an inactive product when it has no order history.
     *
     * @param productId product identifier
     * @return true when the product is deleted
     * @throws SQLException when database access fails
     */
    public boolean deleteInactiveProduct(
            final long productId)
            throws SQLException {

        final String stateSql =
                "SELECT is_active FROM products WHERE id = ?";

        final String orderSql =
                "SELECT COUNT(*) FROM order_items "
                        + "WHERE product_id = ?";

        final String deleteAssuranceSql =
                "DELETE FROM vr_mart_assurance "
                        + "WHERE product_id = ?";

        final String deleteRequestsSql =
                "DELETE FROM order_service_requests "
                        + "WHERE product_id = ?";

        final String deleteReviewsSql =
                "DELETE FROM reviews WHERE product_id = ?";

        final String deleteWishlistSql =
                "DELETE FROM wishlists WHERE product_id = ?";

        final String deleteCartSql =
                "DELETE FROM cart_items WHERE product_id = ?";

        final String deleteProductSql =
                "DELETE FROM products "
                        + "WHERE id = ? AND is_active = FALSE";

        try (Connection connection = dataSource.getConnection()) {

            connection.setAutoCommit(false);

            try {
                if (!isInactiveProduct(
                        connection, stateSql, productId)) {
                    connection.rollback();
                    return false;
                }

                if (hasOrderHistory(
                        connection, orderSql, productId)) {
                    connection.rollback();
                    return false;
                }

                executeProductDelete(
                        connection, deleteAssuranceSql, productId);
                executeProductDelete(
                        connection, deleteRequestsSql, productId);
                executeProductDelete(
                        connection, deleteReviewsSql, productId);
                executeProductDelete(
                        connection, deleteWishlistSql, productId);
                executeProductDelete(
                        connection, deleteCartSql, productId);

                try (PreparedStatement statement =
                             connection.prepareStatement(
                                     deleteProductSql)) {

                    statement.setLong(PARAM_ONE, productId);

                    final boolean deleted =
                            statement.executeUpdate() > 0;

                    if (deleted) {
                        connection.commit();
                    } else {
                        connection.rollback();
                    }

                    return deleted;
                }
            } catch (SQLException exception) {
                connection.rollback();
                throw exception;
            } finally {
                connection.setAutoCommit(true);
            }
        }
    }

    /**
     * Checks whether a product exists and is inactive.
     *
     * @param connection database connection
     * @param sql state query
     * @param productId product identifier
     * @return true when inactive
     * @throws SQLException when database access fails
     */
    private boolean isInactiveProduct(
            final Connection connection,
            final String sql,
            final long productId)
            throws SQLException {

        try (PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setLong(PARAM_ONE, productId);

            try (ResultSet resultSet =
                         statement.executeQuery()) {

                return resultSet.next()
                        && !resultSet.getBoolean(PARAM_ONE);
            }
        }
    }

    /**
     * Checks whether a product is present in an order.
     *
     * @param connection database connection
     * @param sql order query
     * @param productId product identifier
     * @return true when order history exists
     * @throws SQLException when database access fails
     */
    private boolean hasOrderHistory(
            final Connection connection,
            final String sql,
            final long productId)
            throws SQLException {

        try (PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setLong(PARAM_ONE, productId);

            try (ResultSet resultSet =
                         statement.executeQuery()) {

                return resultSet.next()
                        && resultSet.getLong(PARAM_ONE) > 0;
            }
        }
    }

    /**
     * Deletes product-linked supporting data.
     *
     * @param connection database connection
     * @param sql delete SQL
     * @param productId product identifier
     * @throws SQLException when database access fails
     */
    private void executeProductDelete(
            final Connection connection,
            final String sql,
            final long productId)
            throws SQLException {

        try (PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setLong(PARAM_ONE, productId);
            statement.executeUpdate();
        }
    }

    /**
     * Enables or disables a product.
     *
     * @param productId product identifier
     * @param active active state
     * @return true when changed
     * @throws SQLException when database access fails
     */
    public boolean updateProductActive(
            final long productId,
            final boolean active)
            throws SQLException {

        final String sql =
                "UPDATE products SET is_active = ?, "
                        + "updated_at = CURRENT_TIMESTAMP "
                        + "WHERE id = ?";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setBoolean(PARAM_ONE, active);
            statement.setLong(PARAM_TWO, productId);

            return statement.executeUpdate() > 0;
        }
    }

    /**
     * Gives or removes VR Mart assurance.
     *
     * @param productId product identifier
     * @param adminId administrator identifier
     * @param assured assurance state
     * @return true when changed
     * @throws SQLException when database access fails
     */
    public boolean updateProductAssurance(
            final long productId,
            final long adminId,
            final boolean assured)
            throws SQLException {

        final String deleteSql =
                "DELETE FROM vr_mart_assurance "
                        + "WHERE product_id = ?";

        final String upsertSql =
                "INSERT INTO vr_mart_assurance "
                        + "(product_id, assured, assured_by) "
                        + "VALUES (?, TRUE, ?) "
                        + "ON CONFLICT (product_id) "
                        + "DO UPDATE SET assured = TRUE, "
                        + "assured_by = EXCLUDED.assured_by, "
                        + "assured_at = CURRENT_TIMESTAMP";

        try (Connection connection = dataSource.getConnection()) {

            if (!assured) {
                try (PreparedStatement statement =
                             connection.prepareStatement(deleteSql)) {

                    statement.setLong(PARAM_ONE, productId);
                    return statement.executeUpdate() > 0;
                }
            }

            try (PreparedStatement statement =
                         connection.prepareStatement(upsertSql)) {

                statement.setLong(PARAM_ONE, productId);
                statement.setLong(PARAM_TWO, adminId);

                return statement.executeUpdate() > 0;
            }
        }
    }

    /**
     * Updates an order status.
     *
     * @param orderId order identifier
     * @param status new status
     * @return true when changed
     * @throws SQLException when database access fails
     */
    public boolean updateOrderStatus(
            final long orderId,
            final String status)
            throws SQLException {

        final String sql =
                "UPDATE orders SET status = ?, "
                        + "updated_at = CURRENT_TIMESTAMP "
                        + "WHERE id = ?";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setString(PARAM_ONE, status);
            statement.setLong(PARAM_TWO, orderId);

            return statement.executeUpdate() > 0;
        }
    }

    /**
     * Counts rows in a trusted table.
     *
     * @param tableName trusted table name
     * @return row count
     * @throws SQLException when database access fails
     */
    private long count(final String tableName)
            throws SQLException {

        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(
                             "SELECT COUNT(*) FROM " + tableName);
             ResultSet resultSet =
                     statement.executeQuery()) {

            if (resultSet.next()) {
                return resultSet.getLong(PARAM_ONE);
            }

            return 0L;
        }
    }

    /**
     * Executes a read-only query.
     *
     * @param sql SQL query
     * @param columns selected column names
     * @return result rows
     * @throws SQLException when database access fails
     */
    private List<Map<String, Object>> query(
            final String sql,
            final String[] columns)
            throws SQLException {

        final List<Map<String, Object>> rows =
                new ArrayList<>();

        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql);
             ResultSet resultSet =
                     statement.executeQuery()) {

            while (resultSet.next()) {
                final Map<String, Object> row =
                        new HashMap<>();

                for (final String column : columns) {
                    row.put(
                            column,
                            resultSet.getObject(column));
                }

                rows.add(row);
            }
        }

        return rows;
    }
}
