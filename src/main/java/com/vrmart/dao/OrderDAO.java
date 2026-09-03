package com.vrmart.dao;

import com.vrmart.model.CartItem;
import com.vrmart.model.Order;
import com.vrmart.model.SellerOrderItem;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * Data access object for buyer and seller orders.
 */
public final class OrderDAO {

    /** Pending order status. */
    private static final String STATUS_PENDING = "PENDING";

    /** SQL for creating an order. */
    private static final String CREATE_ORDER_SQL =
            "INSERT INTO orders "
                    + "(buyer_id, total_amount, status, "
                    + "delivery_address, delivery_landmark, "
                    + "payment_method) "
                    + "VALUES (?, ?, ?, ?, ?, ?) "
                    + "RETURNING id, buyer_id, total_amount, "
                    + "status, delivery_address, "
                    + "delivery_landmark, payment_method, "
                    + "created_at, updated_at";

    /** SQL for creating an order item. */
    private static final String CREATE_ORDER_ITEM_SQL =
            "INSERT INTO order_items "
                    + "(order_id, product_id, quantity, unit_price) "
                    + "VALUES (?, ?, ?, ?)";

    /** SQL for reducing stock. */
    private static final String UPDATE_STOCK_SQL =
            "UPDATE products "
                    + "SET stock_qty = stock_qty - ? "
                    + "WHERE id = ? "
                    + "AND stock_qty >= ?";

    /** SQL for clearing cart. */
    private static final String CLEAR_CART_SQL =
            "DELETE FROM cart_items "
                    + "WHERE buyer_id = ?";

    /** SQL for finding buyer orders. */
    private static final String FIND_BY_BUYER_SQL =
            "SELECT id, buyer_id, total_amount, status, "
                    + "delivery_address, delivery_landmark, "
                    + "payment_method, created_at, updated_at "
                    + "FROM orders "
                    + "WHERE buyer_id = ? "
                    + "ORDER BY created_at DESC";

    /** SQL for finding seller incoming orders. */
    private static final String FIND_BY_SELLER_SQL =
            "SELECT o.id AS order_id, "
                    + "o.buyer_id, "
                    + "oi.product_id, "
                    + "p.name AS product_name, "
                    + "oi.quantity, "
                    + "oi.unit_price, "
                    + "(oi.quantity * oi.unit_price) AS line_total, "
                    + "o.status, "
                    + "o.delivery_address, "
                    + "o.delivery_landmark, "
                    + "o.payment_method, "
                    + "o.created_at "
                    + "FROM orders o "
                    + "JOIN order_items oi "
                    + "ON oi.order_id = o.id "
                    + "JOIN products p "
                    + "ON p.id = oi.product_id "
                    + "WHERE p.seller_id = ? "
                    + "ORDER BY o.created_at DESC, o.id DESC";

    /** SQL for updating order status. */
    private static final String UPDATE_STATUS_SQL =
            "UPDATE orders "
                    + "SET status = ?, "
                    + "updated_at = CURRENT_TIMESTAMP "
                    + "WHERE id = ?";

    /** First parameter. */
    private static final int PARAM_ONE = 1;

    /** Second parameter. */
    private static final int PARAM_TWO = 2;

    /** Third parameter. */
    private static final int PARAM_THREE = 3;

    /** Fourth parameter. */
    private static final int PARAM_FOUR = 4;

    /** Fifth parameter. */
    private static final int PARAM_FIVE = 5;

    /** Sixth parameter. */
    private static final int PARAM_SIX = 6;

    /** Database data source. */
    private final DataSource dataSource;

    /**
     * Creates an order DAO.
     *
     * @param source application data source
     */
    public OrderDAO(final DataSource source) {
        dataSource = source;
    }

    /**
     * Creates an order from the buyer cart.
     *
     * @param buyerId buyer identifier
     * @param cartItems cart items
     * @param deliveryAddress delivery address
     * @param deliveryLandmark delivery landmark
     * @param paymentMethod payment method
     * @return created order
     * @throws SQLException when database operation fails
     */
    public Order createOrder(
            final long buyerId,
            final List<CartItem> cartItems,
            final String deliveryAddress,
            final String deliveryLandmark,
            final String paymentMethod)
            throws SQLException {

        if (cartItems == null || cartItems.isEmpty()) {
            throw new SQLException(
                    "Cannot create an empty order.");
        }

        try (Connection connection =
                     dataSource.getConnection()) {

            connection.setAutoCommit(false);

            try {
                final BigDecimal totalAmount =
                        calculateTotal(cartItems);

                final Order order =
                        insertOrder(
                                connection,
                                buyerId,
                                totalAmount,
                                deliveryAddress,
                                deliveryLandmark,
                                paymentMethod);

                insertOrderItems(
                        connection,
                        order.getId(),
                        cartItems);

                updateStock(
                        connection,
                        cartItems);

                clearCart(
                        connection,
                        buyerId);

                connection.commit();

                return order;

            } catch (SQLException exception) {
                connection.rollback();
                throw exception;
            }
        }
    }

    /**
     * Finds orders belonging to a buyer.
     *
     * @param buyerId buyer identifier
     * @return buyer orders
     * @throws SQLException when database operation fails
     */
    public List<Order> findByBuyer(
            final long buyerId) throws SQLException {

        final List<Order> orders =
                new ArrayList<>();

        try (Connection connection =
                     dataSource.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(
                             FIND_BY_BUYER_SQL)) {

            statement.setLong(
                    PARAM_ONE,
                    buyerId);

            try (ResultSet resultSet =
                         statement.executeQuery()) {

                while (resultSet.next()) {
                    orders.add(
                            mapOrder(resultSet));
                }
            }
        }

        return orders;
    }

    /**
     * Finds incoming orders for a seller.
     *
     * @param sellerId seller identifier
     * @return seller order items
     * @throws SQLException when database operation fails
     */
    public List<SellerOrderItem> findBySeller(
            final long sellerId) throws SQLException {

        final List<SellerOrderItem> orders =
                new ArrayList<>();

        try (Connection connection =
                     dataSource.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(
                             FIND_BY_SELLER_SQL)) {

            statement.setLong(
                    PARAM_ONE,
                    sellerId);

            try (ResultSet resultSet =
                         statement.executeQuery()) {

                while (resultSet.next()) {
                    orders.add(
                            mapSellerOrderItem(
                                    resultSet));
                }
            }
        }

        return orders;
    }

    /**
     * Updates the status of an order.
     *
     * @param orderId order identifier
     * @param status new status
     * @return true when the order was updated
     * @throws SQLException when database operation fails
     */
    public boolean updateStatus(
            final long orderId,
            final String status)
            throws SQLException {

        try (Connection connection =
                     dataSource.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(
                             UPDATE_STATUS_SQL)) {

            statement.setString(
                    PARAM_ONE,
                    status);

            statement.setLong(
                    PARAM_TWO,
                    orderId);

            return statement.executeUpdate() > 0;
        }
    }

    /**
     * Calculates the total cart value.
     *
     * @param cartItems cart items
     * @return total amount
     */
    private BigDecimal calculateTotal(
            final List<CartItem> cartItems) {

        BigDecimal total = BigDecimal.ZERO;

        for (CartItem item : cartItems) {
            total = total.add(
                    item.getSubtotal());
        }

        return total;
    }

    /**
     * Inserts the main order record.
     *
     * @param connection database connection
     * @param buyerId buyer identifier
     * @param totalAmount total amount
     * @param deliveryAddress delivery address
     * @param deliveryLandmark delivery landmark
     * @param paymentMethod payment method
     * @return created order
     * @throws SQLException when insertion fails
     */
    private Order insertOrder(
            final Connection connection,
            final long buyerId,
            final BigDecimal totalAmount,
            final String deliveryAddress,
            final String deliveryLandmark,
            final String paymentMethod)
            throws SQLException {

        try (PreparedStatement statement =
                     connection.prepareStatement(
                             CREATE_ORDER_SQL)) {

            statement.setLong(
                    PARAM_ONE,
                    buyerId);

            statement.setBigDecimal(
                    PARAM_TWO,
                    totalAmount);

            statement.setString(
                    PARAM_THREE,
                    STATUS_PENDING);

            statement.setString(
                    PARAM_FOUR,
                    deliveryAddress);

            statement.setString(
                    PARAM_FIVE,
                    deliveryLandmark);

            statement.setString(
                    PARAM_SIX,
                    paymentMethod);

            try (ResultSet resultSet =
                         statement.executeQuery()) {

                if (!resultSet.next()) {
                    throw new SQLException(
                            "Unable to create order.");
                }

                return mapOrder(resultSet);
            }
        }
    }

    /**
     * Inserts order items.
     *
     * @param connection database connection
     * @param orderId order identifier
     * @param cartItems cart items
     * @throws SQLException when insertion fails
     */
    private void insertOrderItems(
            final Connection connection,
            final long orderId,
            final List<CartItem> cartItems)
            throws SQLException {

        try (PreparedStatement statement =
                     connection.prepareStatement(
                             CREATE_ORDER_ITEM_SQL)) {

            for (CartItem item : cartItems) {

                statement.setLong(
                        PARAM_ONE,
                        orderId);

                statement.setLong(
                        PARAM_TWO,
                        item.getProductId());

                statement.setInt(
                        PARAM_THREE,
                        item.getQuantity());

                statement.setBigDecimal(
                        PARAM_FOUR,
                        item.getProductPrice());

                statement.addBatch();
            }

            statement.executeBatch();
        }
    }

    /**
     * Updates product stock.
     *
     * @param connection database connection
     * @param cartItems cart items
     * @throws SQLException when stock is insufficient
     */
    private void updateStock(
            final Connection connection,
            final List<CartItem> cartItems)
            throws SQLException {

        try (PreparedStatement statement =
                     connection.prepareStatement(
                             UPDATE_STOCK_SQL)) {

            for (CartItem item : cartItems) {

                statement.setInt(
                        PARAM_ONE,
                        item.getQuantity());

                statement.setLong(
                        PARAM_TWO,
                        item.getProductId());

                statement.setInt(
                        PARAM_THREE,
                        item.getQuantity());

                final int updatedRows =
                        statement.executeUpdate();

                if (updatedRows == 0) {
                    throw new SQLException(
                            "Insufficient stock for product: "
                                    + item.getProductId());
                }
            }
        }
    }

    /**
     * Clears the buyer cart.
     *
     * @param connection database connection
     * @param buyerId buyer identifier
     * @throws SQLException when deletion fails
     */
    private void clearCart(
            final Connection connection,
            final long buyerId)
            throws SQLException {

        try (PreparedStatement statement =
                     connection.prepareStatement(
                             CLEAR_CART_SQL)) {

            statement.setLong(
                    PARAM_ONE,
                    buyerId);

            statement.executeUpdate();
        }
    }

    /**
     * Maps a result to an Order model.
     *
     * @param resultSet database result
     * @return mapped order
     * @throws SQLException when reading result fails
     */
    private Order mapOrder(
            final ResultSet resultSet)
            throws SQLException {

        final Order order = new Order();

        order.setId(
                resultSet.getLong("id"));

        order.setBuyerId(
                resultSet.getLong("buyer_id"));

        order.setTotalAmount(
                resultSet.getBigDecimal(
                        "total_amount"));

        order.setStatus(
                resultSet.getString("status"));

        order.setDeliveryAddress(
                resultSet.getString(
                        "delivery_address"));

        order.setDeliveryLandmark(
                resultSet.getString(
                        "delivery_landmark"));

        order.setPaymentMethod(
                resultSet.getString(
                        "payment_method"));

        final Timestamp createdAt =
                resultSet.getTimestamp(
                        "created_at");

        if (createdAt != null) {
            order.setCreatedAt(
                    createdAt.toLocalDateTime());
        }

        final Timestamp updatedAt =
                resultSet.getTimestamp(
                        "updated_at");

        if (updatedAt != null) {
            order.setUpdatedAt(
                    updatedAt.toLocalDateTime());
        }

        return order;
    }

    /**
     * Maps a result to a seller order item.
     *
     * @param resultSet database result
     * @return mapped seller order item
     * @throws SQLException when reading result fails
     */
    private SellerOrderItem mapSellerOrderItem(
            final ResultSet resultSet)
            throws SQLException {

        final SellerOrderItem item =
                new SellerOrderItem();

        item.setOrderId(
                resultSet.getLong("order_id"));

        item.setBuyerId(
                resultSet.getLong("buyer_id"));

        item.setProductId(
                resultSet.getLong("product_id"));

        item.setProductName(
                resultSet.getString(
                        "product_name"));

        item.setQuantity(
                resultSet.getInt("quantity"));

        item.setUnitPrice(
                resultSet.getBigDecimal(
                        "unit_price"));

        item.setLineTotal(
                resultSet.getBigDecimal(
                        "line_total"));

        item.setStatus(
                resultSet.getString("status"));

        item.setDeliveryAddress(
                resultSet.getString(
                        "delivery_address"));

        item.setDeliveryLandmark(
                resultSet.getString(
                        "delivery_landmark"));

        item.setPaymentMethod(
                resultSet.getString(
                        "payment_method"));

        final Timestamp createdAt =
                resultSet.getTimestamp(
                        "created_at");

        if (createdAt != null) {
            item.setCreatedAt(
                    createdAt.toLocalDateTime());
        }

        return item;
    }
}
