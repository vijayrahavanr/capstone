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
    public static final String STATUS_PENDING = "PENDING";

    /** Approved order status. */
    public static final String STATUS_APPROVED = "APPROVED";

    /** Shipped order status. */
    public static final String STATUS_SHIPPED = "SHIPPED";

    /** Delivered order status. */
    public static final String STATUS_DELIVERED = "DELIVERED";

    /** Cancelled order status. */
    public static final String STATUS_CANCELLED = "CANCELLED";

    /** SQL for creating an order. */
    private static final String CREATE_ORDER_SQL =
            "INSERT INTO orders "
                    + "(buyer_id, customer_name, customer_phone, "
                    + "total_amount, status, delivery_address, "
                    + "delivery_landmark, payment_method) "
                    + "VALUES (?, ?, ?, ?, ?, ?, ?, ?) "
                    + "RETURNING id, buyer_id, customer_name, "
                    + "customer_phone, total_amount, status, "
                    + "delivery_address, delivery_landmark, "
                    + "payment_method, created_at, updated_at";

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

    /** SQL for restoring stock after cancellation. */
    private static final String RESTORE_STOCK_SQL =
            "UPDATE products p "
                    + "SET stock_qty = p.stock_qty + oi.quantity "
                    + "FROM order_items oi "
                    + "WHERE oi.order_id = ? "
                    + "AND p.id = oi.product_id";

    /** SQL for clearing cart. */
    private static final String CLEAR_CART_SQL =
            "DELETE FROM cart_items "
                    + "WHERE buyer_id = ?";

    /** SQL for finding buyer orders. */
    private static final String FIND_BY_BUYER_SQL =
            "SELECT id, buyer_id, customer_name, customer_phone, "
                    + "total_amount, status, delivery_address, "
                    + "delivery_landmark, payment_method, "
                    + "created_at, updated_at "
                    + "FROM orders "
                    + "WHERE buyer_id = ? "
                    + "ORDER BY created_at DESC";

    /** SQL for finding order items. */
    private static final String FIND_ITEMS_BY_ORDER_SQL =
            "SELECT oi.product_id, "
                    + "p.name AS product_name, "
                    + "oi.quantity, "
                    + "oi.unit_price AS product_price, "
                    + "u.username AS seller_name "
                    + "FROM order_items oi "
                    + "JOIN orders o "
                    + "ON o.id = oi.order_id "
                    + "JOIN products p "
                    + "ON p.id = oi.product_id "
                    + "JOIN users u "
                    + "ON u.id = p.seller_id "
                    + "WHERE oi.order_id = ? "
                    + "AND o.buyer_id = ? "
                    + "ORDER BY oi.product_id";

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

    /** SQL for seller status update. */
    private static final String UPDATE_SELLER_STATUS_SQL =
            "UPDATE orders "
                    + "SET status = ?, "
                    + "updated_at = CURRENT_TIMESTAMP "
                    + "WHERE id = ? "
                    + "AND EXISTS ("
                    + "SELECT 1 "
                    + "FROM order_items oi "
                    + "JOIN products p "
                    + "ON p.id = oi.product_id "
                    + "WHERE oi.order_id = orders.id "
                    + "AND p.seller_id = ?"
                    + ") "
                    + "AND ("
                    + "(status = 'PENDING' AND ? = 'APPROVED') "
                    + "OR "
                    + "(status = 'APPROVED' AND ? = 'SHIPPED') "
                    + "OR "
                    + "(status = 'SHIPPED' AND ? = 'DELIVERED')"
                    + ")";

    /** SQL for buyer cancellation. */
    private static final String CANCEL_BUYER_SQL =
            "UPDATE orders "
                    + "SET status = 'CANCELLED', "
                    + "updated_at = CURRENT_TIMESTAMP "
                    + "WHERE id = ? "
                    + "AND buyer_id = ? "
                    + "AND status IN ('PENDING', 'APPROVED')";

    /** SQL for seller cancellation. */
    private static final String CANCEL_SELLER_SQL =
            "UPDATE orders "
                    + "SET status = 'CANCELLED', "
                    + "updated_at = CURRENT_TIMESTAMP "
                    + "WHERE id = ? "
                    + "AND status IN ('PENDING', 'APPROVED') "
                    + "AND EXISTS ("
                    + "SELECT 1 "
                    + "FROM order_items oi "
                    + "JOIN products p "
                    + "ON p.id = oi.product_id "
                    + "WHERE oi.order_id = orders.id "
                    + "AND p.seller_id = ?"
                    + ")";

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

    /** Seventh parameter. */
    private static final int PARAM_SEVEN = 7;

    /** Eighth parameter. */
    private static final int PARAM_EIGHT = 8;

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
     * @param customerName customer name
     * @param customerPhone customer phone
     * @param deliveryAddress delivery address
     * @param deliveryLandmark delivery landmark
     * @param paymentMethod payment method
     * @return created order
     * @throws SQLException when database operation fails
     */
    public Order createOrder(
            final long buyerId,
            final List<CartItem> cartItems,
            final String customerName,
            final String customerPhone,
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

                final Order orderData =
                        new Order();

                orderData.setBuyerId(buyerId);
                orderData.setCustomerName(customerName);
                orderData.setCustomerPhone(customerPhone);
                orderData.setTotalAmount(totalAmount);
                orderData.setStatus(STATUS_PENDING);
                orderData.setDeliveryAddress(
                        deliveryAddress);
                orderData.setDeliveryLandmark(
                        deliveryLandmark);
                orderData.setPaymentMethod(
                        paymentMethod);

                final Order order =
                        insertOrder(
                                connection,
                                orderData);

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
     * Finds products belonging to a buyer order.
     *
     * @param orderId order identifier
     * @param buyerId buyer identifier
     * @return order items
     * @throws SQLException when database operation fails
     */
    public List<CartItem> findItemsByOrder(
            final long orderId,
            final long buyerId)
            throws SQLException {

        final List<CartItem> items =
                new ArrayList<>();

        try (Connection connection =
                     dataSource.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(
                             FIND_ITEMS_BY_ORDER_SQL)) {

            statement.setLong(
                    PARAM_ONE,
                    orderId);

            statement.setLong(
                    PARAM_TWO,
                    buyerId);

            try (ResultSet resultSet =
                         statement.executeQuery()) {

                while (resultSet.next()) {

                    final CartItem item =
                            new CartItem();

                    item.setProductId(
                            resultSet.getLong(
                                    "product_id"));

                    item.setProductName(
                            resultSet.getString(
                                    "product_name"));

                    item.setQuantity(
                            resultSet.getInt(
                                    "quantity"));

                    item.setProductPrice(
                            resultSet.getBigDecimal(
                                    "product_price"));

                    item.setSellerName(
                            resultSet.getString(
                                    "seller_name"));

                    items.add(item);
                }
            }
        }

        return items;
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
     * Updates seller order status using the valid workflow.
     *
     * @param orderId order identifier
     * @param sellerId seller identifier
     * @param status new status
     * @return true when updated
     * @throws SQLException when database operation fails
     */
    public boolean updateStatus(
            final long orderId,
            final long sellerId,
            final String status)
            throws SQLException {

        try (Connection connection =
                     dataSource.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(
                             UPDATE_SELLER_STATUS_SQL)) {

            statement.setString(
                    PARAM_ONE,
                    status);

            statement.setLong(
                    PARAM_TWO,
                    orderId);

            statement.setLong(
                    PARAM_THREE,
                    sellerId);

            statement.setString(
                    PARAM_FOUR,
                    status);

            statement.setString(
                    PARAM_FIVE,
                    status);

            statement.setString(
                    PARAM_SIX,
                    status);

            return statement.executeUpdate() > 0;
        }
    }

    /**
     * Cancels a buyer order and restores stock.
     *
     * @param orderId order identifier
     * @param buyerId buyer identifier
     * @return true when cancelled
     * @throws SQLException when database operation fails
     */
    public boolean cancelByBuyer(
            final long orderId,
            final long buyerId)
            throws SQLException {

        try (Connection connection =
                     dataSource.getConnection()) {

            connection.setAutoCommit(false);

            try {
                final int updatedRows =
                        cancelBuyerOrder(
                                connection,
                                orderId,
                                buyerId);

                if (updatedRows == 0) {
                    connection.rollback();
                    return false;
                }

                restoreStock(
                        connection,
                        orderId);

                connection.commit();
                return true;

            } catch (SQLException exception) {
                connection.rollback();
                throw exception;
            }
        }
    }

    /**
     * Cancels a seller order and restores stock.
     *
     * @param orderId order identifier
     * @param sellerId seller identifier
     * @return true when cancelled
     * @throws SQLException when database operation fails
     */
    public boolean cancelBySeller(
            final long orderId,
            final long sellerId)
            throws SQLException {

        try (Connection connection =
                     dataSource.getConnection()) {

            connection.setAutoCommit(false);

            try {
                final int updatedRows =
                        cancelSellerOrder(
                                connection,
                                orderId,
                                sellerId);

                if (updatedRows == 0) {
                    connection.rollback();
                    return false;
                }

                restoreStock(
                        connection,
                        orderId);

                connection.commit();
                return true;

            } catch (SQLException exception) {
                connection.rollback();
                throw exception;
            }
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
     * @param order order data
     * @return created order
     * @throws SQLException when insertion fails
     */
    private Order insertOrder(
            final Connection connection,
            final Order order)
            throws SQLException {

        try (PreparedStatement statement =
                     connection.prepareStatement(
                             CREATE_ORDER_SQL)) {

            statement.setLong(
                    PARAM_ONE,
                    order.getBuyerId());

            statement.setString(
                    PARAM_TWO,
                    order.getCustomerName());

            statement.setString(
                    PARAM_THREE,
                    order.getCustomerPhone());

            statement.setBigDecimal(
                    PARAM_FOUR,
                    order.getTotalAmount());

            statement.setString(
                    PARAM_FIVE,
                    order.getStatus());

            statement.setString(
                    PARAM_SIX,
                    order.getDeliveryAddress());

            statement.setString(
                    PARAM_SEVEN,
                    order.getDeliveryLandmark());

            statement.setString(
                    PARAM_EIGHT,
                    order.getPaymentMethod());

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
     * Cancels a buyer order.
     *
     * @param connection database connection
     * @param orderId order identifier
     * @param buyerId buyer identifier
     * @return number of updated rows
     * @throws SQLException when update fails
     */
    private int cancelBuyerOrder(
            final Connection connection,
            final long orderId,
            final long buyerId)
            throws SQLException {

        try (PreparedStatement statement =
                     connection.prepareStatement(
                             CANCEL_BUYER_SQL)) {

            statement.setLong(
                    PARAM_ONE,
                    orderId);

            statement.setLong(
                    PARAM_TWO,
                    buyerId);

            return statement.executeUpdate();
        }
    }

    /**
     * Cancels a seller order.
     *
     * @param connection database connection
     * @param orderId order identifier
     * @param sellerId seller identifier
     * @return number of updated rows
     * @throws SQLException when update fails
     */
    private int cancelSellerOrder(
            final Connection connection,
            final long orderId,
            final long sellerId)
            throws SQLException {

        try (PreparedStatement statement =
                     connection.prepareStatement(
                             CANCEL_SELLER_SQL)) {

            statement.setLong(
                    PARAM_ONE,
                    orderId);

            statement.setLong(
                    PARAM_TWO,
                    sellerId);

            return statement.executeUpdate();
        }
    }

    /**
     * Restores stock for a cancelled order.
     *
     * @param connection database connection
     * @param orderId order identifier
     * @throws SQLException when stock update fails
     */
    private void restoreStock(
            final Connection connection,
            final long orderId)
            throws SQLException {

        try (PreparedStatement statement =
                     connection.prepareStatement(
                             RESTORE_STOCK_SQL)) {

            statement.setLong(
                    PARAM_ONE,
                    orderId);

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

        order.setCustomerName(
                resultSet.getString(
                        "customer_name"));

        order.setCustomerPhone(
                resultSet.getString(
                        "customer_phone"));

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
