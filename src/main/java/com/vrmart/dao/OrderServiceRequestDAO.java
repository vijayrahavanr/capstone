package com.vrmart.dao;

import com.vrmart.model.OrderServiceRequest;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Data access object for buyer order service requests.
 */
public final class OrderServiceRequestDAO {

    /** First SQL parameter. */
    private static final int PARAM_ONE = 1;

    /** Second SQL parameter. */
    private static final int PARAM_TWO = 2;

    /** Third SQL parameter. */
    private static final int PARAM_THREE = 3;

    /** Fourth SQL parameter. */
    private static final int PARAM_FOUR = 4;

    /** Fifth SQL parameter. */
    private static final int PARAM_FIVE = 5;

    /** SQL for creating a service request. */
    private static final String INSERT_SQL =
            "INSERT INTO order_service_requests "
                    + "(order_id, buyer_id, product_id, request_type, reason) "
                    + "VALUES (?, ?, ?, ?, ?) "
                    + "RETURNING id, order_id, buyer_id, product_id, "
                    + "request_type, reason, status, created_at, updated_at";

    /** SQL for finding requests belonging to an order. */
    private static final String FIND_BY_ORDER_SQL =
            "SELECT osr.id, osr.order_id, osr.buyer_id, "
                    + "osr.product_id, p.name AS product_name, "
                    + "osr.request_type, osr.reason, osr.status, "
                    + "osr.created_at, osr.updated_at "
                    + "FROM order_service_requests osr "
                    + "JOIN products p ON p.id = osr.product_id "
                    + "WHERE osr.order_id = ? "
                    + "AND osr.buyer_id = ? "
                    + "ORDER BY osr.created_at DESC";

    /** SQL for checking buyer order status. */
    private static final String FIND_ORDER_SQL =
            "SELECT o.status "
                    + "FROM orders o "
                    + "WHERE o.id = ? AND o.buyer_id = ?";

    /** SQL for checking product belongs to buyer order. */
    private static final String FIND_ORDER_PRODUCT_SQL =
            "SELECT oi.product_id "
                    + "FROM order_items oi "
                    + "JOIN orders o ON o.id = oi.order_id "
                    + "WHERE oi.order_id = ? "
                    + "AND oi.product_id = ? "
                    + "AND o.buyer_id = ?";

    /** SQL for checking active return or replacement. */
    private static final String FIND_ACTIVE_RETURN_REPLACEMENT_SQL =
            "SELECT request_type "
                    + "FROM order_service_requests "
                    + "WHERE order_id = ? "
                    + "AND buyer_id = ? "
                    + "AND product_id = ? "
                    + "AND request_type IN ('RETURN', 'REPLACEMENT') "
                    + "AND status IN ('REQUESTED', 'APPROVED') "
                    + "ORDER BY created_at DESC "
                    + "LIMIT 1";

    /** SQL for seller service requests. */
    private static final String FIND_BY_SELLER_SQL =
            "SELECT osr.id, osr.order_id, osr.buyer_id, "
                    + "osr.product_id, p.name AS product_name, "
                    + "buyer.username AS buyer_name, "
                    + "osr.request_type, osr.reason, osr.status, "
                    + "osr.created_at, osr.updated_at "
                    + "FROM order_service_requests osr "
                    + "JOIN products p ON p.id = osr.product_id "
                    + "JOIN users buyer ON buyer.id = osr.buyer_id "
                    + "WHERE p.seller_id = ? "
                    + "ORDER BY osr.created_at DESC";

    /** SQL for updating seller request status. */
    private static final String UPDATE_SELLER_STATUS_SQL =
            "UPDATE order_service_requests osr "
                    + "SET status = ?, updated_at = CURRENT_TIMESTAMP "
                    + "FROM products p "
                    + "WHERE osr.id = ? "
                    + "AND osr.product_id = p.id "
                    + "AND p.seller_id = ?";

    /** Database data source. */
    private final DataSource dataSource;

    /**
     * Creates the DAO.
     *
     * @param source database data source
     */
    public OrderServiceRequestDAO(final DataSource source) {
        this.dataSource = source;
    }

    /**
     * Creates a service request.
     *
     * @param request service request
     * @return saved service request
     * @throws Exception when the database operation fails
     */
    public OrderServiceRequest create(
            final OrderServiceRequest request) throws Exception {

        try (Connection connection =
                     dataSource.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(INSERT_SQL)) {

            statement.setLong(
                    PARAM_ONE,
                    request.getOrderId());

            statement.setLong(
                    PARAM_TWO,
                    request.getBuyerId());

            statement.setLong(
                    PARAM_THREE,
                    request.getProductId());

            statement.setString(
                    PARAM_FOUR,
                    request.getRequestType());

            statement.setString(
                    PARAM_FIVE,
                    request.getReason());

            try (ResultSet resultSet =
                         statement.executeQuery()) {

                if (!resultSet.next()) {
                    throw new IllegalStateException(
                            "Unable to create service request.");
                }

                return mapCreatedRequest(resultSet);
            }
        }
    }

    /**
     * Finds service requests for a buyer's order.
     *
     * @param orderId order ID
     * @param buyerId buyer ID
     * @return service requests
     * @throws Exception when the database operation fails
     */
    public List<OrderServiceRequest> findByOrder(
            final long orderId,
            final long buyerId) throws Exception {

        final List<OrderServiceRequest> requests =
                new ArrayList<>();

        try (Connection connection =
                     dataSource.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(
                             FIND_BY_ORDER_SQL)) {

            statement.setLong(PARAM_ONE, orderId);
            statement.setLong(PARAM_TWO, buyerId);

            try (ResultSet resultSet =
                         statement.executeQuery()) {

                while (resultSet.next()) {
                    requests.add(mapRequest(resultSet));
                }
            }
        }

        return requests;
    }

    /**
     * Finds the status of a buyer's order.
     *
     * @param orderId order ID
     * @param buyerId buyer ID
     * @return order status or null
     * @throws Exception when the database operation fails
     */
    public String findOrderStatus(
            final long orderId,
            final long buyerId) throws Exception {

        try (Connection connection =
                     dataSource.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(
                             FIND_ORDER_SQL)) {

            statement.setLong(PARAM_ONE, orderId);
            statement.setLong(PARAM_TWO, buyerId);

            try (ResultSet resultSet =
                         statement.executeQuery()) {

                if (resultSet.next()) {
                    return resultSet.getString("status");
                }
            }
        }

        return null;
    }

    /**
     * Checks whether a product belongs to the buyer's order.
     *
     * @param orderId order ID
     * @param productId product ID
     * @param buyerId buyer ID
     * @return true when product belongs to order
     * @throws Exception when the database operation fails
     */
    public boolean isProductInBuyerOrder(
            final long orderId,
            final long productId,
            final long buyerId) throws Exception {

        try (Connection connection =
                     dataSource.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(
                             FIND_ORDER_PRODUCT_SQL)) {

            statement.setLong(PARAM_ONE, orderId);
            statement.setLong(PARAM_TWO, productId);
            statement.setLong(PARAM_THREE, buyerId);

            try (ResultSet resultSet =
                         statement.executeQuery()) {

                return resultSet.next();
            }
        }
    }

    /**
     * Finds an active return or replacement request.
     *
     * @param orderId order ID
     * @param buyerId buyer ID
     * @param productId product ID
     * @return active request type or null
     * @throws Exception when the database operation fails
     */
    public String findActiveReturnOrReplacement(
            final long orderId,
            final long buyerId,
            final long productId) throws Exception {

        try (Connection connection =
                     dataSource.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(
                             FIND_ACTIVE_RETURN_REPLACEMENT_SQL)) {

            statement.setLong(PARAM_ONE, orderId);
            statement.setLong(PARAM_TWO, buyerId);
            statement.setLong(PARAM_THREE, productId);

            try (ResultSet resultSet =
                         statement.executeQuery()) {

                if (resultSet.next()) {
                    return resultSet.getString("request_type");
                }
            }
        }

        return null;
    }

    /**
     * Finds service requests belonging to a seller.
     *
     * @param sellerId seller ID
     * @return seller service requests
     * @throws Exception when the database operation fails
     */
    public List<OrderServiceRequest> findBySeller(
            final long sellerId) throws Exception {

        final List<OrderServiceRequest> requests =
                new ArrayList<>();

        try (Connection connection =
                     dataSource.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(
                             FIND_BY_SELLER_SQL)) {

            statement.setLong(PARAM_ONE, sellerId);

            try (ResultSet resultSet =
                         statement.executeQuery()) {

                while (resultSet.next()) {
                    requests.add(mapRequest(resultSet));
                }
            }
        }

        return requests;
    }

    /**
     * Updates a service request status for its seller.
     *
     * @param requestId request ID
     * @param sellerId seller ID
     * @param status new status
     * @return true when the request was updated
     * @throws Exception when the database operation fails
     */
    public boolean updateSellerStatus(
            final long requestId,
            final long sellerId,
            final String status) throws Exception {

        try (Connection connection =
                     dataSource.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(
                             UPDATE_SELLER_STATUS_SQL)) {

            statement.setString(PARAM_ONE, status);
            statement.setLong(PARAM_TWO, requestId);
            statement.setLong(PARAM_THREE, sellerId);

            return statement.executeUpdate() > 0;
        }
    }

    /**
     * Maps a newly created request.
     *
     * @param resultSet database result
     * @return mapped service request
     * @throws Exception when reading the result fails
     */
    private OrderServiceRequest mapCreatedRequest(
            final ResultSet resultSet) throws Exception {

        final OrderServiceRequest request =
                new OrderServiceRequest();

        request.setId(resultSet.getLong("id"));
        request.setOrderId(
                resultSet.getLong("order_id"));
        request.setBuyerId(
                resultSet.getLong("buyer_id"));
        request.setProductId(
                resultSet.getLong("product_id"));
        request.setRequestType(
                resultSet.getString("request_type"));
        request.setReason(
                resultSet.getString("reason"));
        request.setStatus(
                resultSet.getString("status"));

        if (resultSet.getTimestamp("created_at") != null) {
            request.setCreatedAt(
                    resultSet.getTimestamp("created_at")
                            .toLocalDateTime());
        }

        if (resultSet.getTimestamp("updated_at") != null) {
            request.setUpdatedAt(
                    resultSet.getTimestamp("updated_at")
                            .toLocalDateTime());
        }

        return request;
    }

    /**
     * Maps a request containing product information.
     *
     * @param resultSet database result
     * @return mapped service request
     * @throws Exception when reading the result fails
     */
    private OrderServiceRequest mapRequest(
            final ResultSet resultSet) throws Exception {

        final OrderServiceRequest request =
                mapCreatedRequest(resultSet);

        request.setProductName(
                resultSet.getString("product_name"));

        try {
            request.setBuyerName(
                    resultSet.getString("buyer_name"));
        } catch (SQLException exception) {
            request.setBuyerName(null);
        }

        return request;
    }
}
