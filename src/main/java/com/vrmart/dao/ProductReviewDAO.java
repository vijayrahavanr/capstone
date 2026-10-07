package com.vrmart.dao;

import com.vrmart.listener.DatabaseListener;
import com.vrmart.model.ProductReview;

import javax.servlet.ServletContext;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * Handles database operations for product reviews and ratings.
 */
public class ProductReviewDAO {

    /** SQL for creating a review. */
    private static final String CREATE_REVIEW_SQL =
            "INSERT INTO product_reviews "
                    + "(product_id, buyer_id, order_id, rating, review_text) "
                    + "VALUES (?, ?, ?, ?, ?) "
                    + "RETURNING id, product_id, buyer_id, order_id, rating, "
                    + "review_text, created_at, updated_at";

    /** SQL for finding a review by ID. */
    private static final String FIND_BY_ID_SQL =
            "SELECT pr.id, pr.product_id, pr.buyer_id, pr.order_id, "
                    + "pr.rating, pr.review_text, pr.created_at, "
                    + "pr.updated_at, p.name AS product_name, "
                    + "u.username AS buyer_name "
                    + "FROM product_reviews pr "
                    + "JOIN products p ON p.id = pr.product_id "
                    + "JOIN users u ON u.id = pr.buyer_id "
                    + "WHERE pr.id = ?";

    /** SQL for finding a buyer's product review. */
    private static final String FIND_BY_BUYER_PRODUCT_SQL =
            "SELECT pr.id, pr.product_id, pr.buyer_id, pr.order_id, "
                    + "pr.rating, pr.review_text, pr.created_at, "
                    + "pr.updated_at, p.name AS product_name, "
                    + "u.username AS buyer_name "
                    + "FROM product_reviews pr "
                    + "JOIN products p ON p.id = pr.product_id "
                    + "JOIN users u ON u.id = pr.buyer_id "
                    + "WHERE pr.buyer_id = ? AND pr.product_id = ?";

    /** SQL for finding a review for an order product. */
    private static final String FIND_BY_ORDER_PRODUCT_SQL =
            "SELECT pr.id, pr.product_id, pr.buyer_id, pr.order_id, "
                    + "pr.rating, pr.review_text, pr.created_at, "
                    + "pr.updated_at, p.name AS product_name, "
                    + "u.username AS buyer_name "
                    + "FROM product_reviews pr "
                    + "JOIN products p ON p.id = pr.product_id "
                    + "JOIN users u ON u.id = pr.buyer_id "
                    + "WHERE pr.buyer_id = ? "
                    + "AND pr.order_id = ? "
                    + "AND pr.product_id = ?";

    /** SQL for finding all reviews for a product. */
    private static final String FIND_BY_PRODUCT_SQL =
            "SELECT pr.id, pr.product_id, pr.buyer_id, pr.order_id, "
                    + "pr.rating, pr.review_text, pr.created_at, "
                    + "pr.updated_at, p.name AS product_name, "
                    + "u.username AS buyer_name "
                    + "FROM product_reviews pr "
                    + "JOIN products p ON p.id = pr.product_id "
                    + "JOIN users u ON u.id = pr.buyer_id "
                    + "WHERE pr.product_id = ? "
                    + "ORDER BY pr.created_at DESC";

    /** SQL for finding reviews belonging to a seller's products. */
    private static final String FIND_BY_SELLER_SQL =
            "SELECT pr.id, pr.product_id, pr.buyer_id, pr.order_id, "
                    + "pr.rating, pr.review_text, pr.created_at, "
                    + "pr.updated_at, p.name AS product_name, "
                    + "u.username AS buyer_name "
                    + "FROM product_reviews pr "
                    + "JOIN products p ON p.id = pr.product_id "
                    + "JOIN users u ON u.id = pr.buyer_id "
                    + "WHERE p.seller_id = ? "
                    + "ORDER BY pr.created_at DESC";

    /** SQL for updating a buyer's review. */
    private static final String UPDATE_REVIEW_SQL =
            "UPDATE product_reviews "
                    + "SET rating = ?, review_text = ?, "
                    + "updated_at = CURRENT_TIMESTAMP "
                    + "WHERE id = ? AND buyer_id = ? "
                    + "RETURNING id, product_id, buyer_id, order_id, rating, "
                    + "review_text, created_at, updated_at";

    /** SQL for deleting a buyer's review. */
    private static final String DELETE_REVIEW_SQL =
            "DELETE FROM product_reviews "
                    + "WHERE id = ? AND buyer_id = ?";

    /** SQL for calculating product rating. */
    private static final String PRODUCT_RATING_SQL =
            "SELECT COALESCE(AVG(rating), 0) AS average_rating, "
                    + "COUNT(*) AS review_count "
                    + "FROM product_reviews "
                    + "WHERE product_id = ?";

    /** SQL for validating a delivered order product. */
    private static final String DELIVERED_ORDER_PRODUCT_SQL =
            "SELECT COUNT(*) "
                    + "FROM order_items oi "
                    + "JOIN orders o ON o.id = oi.order_id "
                    + "WHERE o.id = ? "
                    + "AND o.buyer_id = ? "
                    + "AND o.status = 'DELIVERED' "
                    + "AND oi.product_id = ?";

    /** SQL for validating review ownership. */
    private static final String REVIEW_OWNERSHIP_SQL =
            "SELECT COUNT(*) "
                    + "FROM product_reviews "
                    + "WHERE id = ? AND buyer_id = ?";

    /** First prepared-statement parameter position. */
    private static final int PARAM_ONE = 1;

    /** Second prepared-statement parameter position. */
    private static final int PARAM_TWO = 2;

    /** Third prepared-statement parameter position. */
    private static final int PARAM_THREE = 3;

    /** Fourth prepared-statement parameter position. */
    private static final int PARAM_FOUR = 4;

    /** Fifth prepared-statement parameter position. */
    private static final int PARAM_FIVE = 5;

    /** Servlet context containing the application's data source. */
    private final ServletContext servletContext;

    /**
     * Creates a product review DAO.
     *
     * @param context application servlet context
     */
    public ProductReviewDAO(final ServletContext context) {
        this.servletContext = context;
    }

    /**
     * Creates a new product review.
     *
     * @param reviewValue review to create
     * @return created review or null
     * @throws SQLException when a database error occurs
     */
    public ProductReview createReview(
            final ProductReview reviewValue) throws SQLException {

        try (Connection connection = getConnection();
                PreparedStatement statement =
                        connection.prepareStatement(CREATE_REVIEW_SQL)) {

            statement.setLong(
                    PARAM_ONE, reviewValue.getProductId());
            statement.setLong(
                    PARAM_TWO, reviewValue.getBuyerId());
            statement.setLong(
                    PARAM_THREE, reviewValue.getOrderId());
            statement.setInt(
                    PARAM_FOUR, reviewValue.getRating());
            statement.setString(
                    PARAM_FIVE, reviewValue.getReviewText());

            try (ResultSet result = statement.executeQuery()) {
                if (result.next()) {
                    return mapReview(result);
                }
            }
        }

        return null;
    }

    /**
     * Finds a review by ID.
     *
     * @param reviewIdValue review ID
     * @return matching review or null
     * @throws SQLException when a database error occurs
     */
    public ProductReview findById(
            final long reviewIdValue) throws SQLException {

        try (Connection connection = getConnection();
                PreparedStatement statement =
                        connection.prepareStatement(FIND_BY_ID_SQL)) {

            statement.setLong(PARAM_ONE, reviewIdValue);

            try (ResultSet result = statement.executeQuery()) {
                if (result.next()) {
                    return mapReview(result);
                }
            }
        }

        return null;
    }

    /**
     * Finds a buyer review for a product.
     *
     * @param buyerIdValue buyer ID
     * @param productIdValue product ID
     * @return matching review or null
     * @throws SQLException when a database error occurs
     */
    public ProductReview findByBuyerAndProduct(
            final long buyerIdValue,
            final long productIdValue) throws SQLException {

        try (Connection connection = getConnection();
                PreparedStatement statement =
                        connection.prepareStatement(
                                FIND_BY_BUYER_PRODUCT_SQL)) {

            statement.setLong(PARAM_ONE, buyerIdValue);
            statement.setLong(PARAM_TWO, productIdValue);

            try (ResultSet result = statement.executeQuery()) {
                if (result.next()) {
                    return mapReview(result);
                }
            }
        }

        return null;
    }

    /**
     * Finds a buyer review for a specific order product.
     *
     * @param buyerIdValue buyer ID
     * @param orderIdValue order ID
     * @param productIdValue product ID
     * @return matching review or null
     * @throws SQLException when a database error occurs
     */
    public ProductReview findByOrderAndProduct(
            final long buyerIdValue,
            final long orderIdValue,
            final long productIdValue) throws SQLException {

        try (Connection connection = getConnection();
                PreparedStatement statement =
                        connection.prepareStatement(
                                FIND_BY_ORDER_PRODUCT_SQL)) {

            statement.setLong(PARAM_ONE, buyerIdValue);
            statement.setLong(PARAM_TWO, orderIdValue);
            statement.setLong(PARAM_THREE, productIdValue);

            try (ResultSet result = statement.executeQuery()) {
                if (result.next()) {
                    return mapReview(result);
                }
            }
        }

        return null;
    }

    /**
     * Finds all reviews for a product.
     *
     * @param productIdValue product ID
     * @return product reviews
     * @throws SQLException when a database error occurs
     */
    public List<ProductReview> findByProduct(
            final long productIdValue) throws SQLException {

        List<ProductReview> reviews = new ArrayList<>();

        try (Connection connection = getConnection();
                PreparedStatement statement =
                        connection.prepareStatement(
                                FIND_BY_PRODUCT_SQL)) {

            statement.setLong(PARAM_ONE, productIdValue);

            try (ResultSet result = statement.executeQuery()) {
                while (result.next()) {
                    reviews.add(mapReview(result));
                }
            }
        }

        return reviews;
    }

    /**
     * Finds reviews for products owned by a seller.
     *
     * @param sellerIdValue seller ID
     * @return seller product reviews
     * @throws SQLException when a database error occurs
     */
    public List<ProductReview> findBySeller(
            final long sellerIdValue) throws SQLException {

        List<ProductReview> reviews = new ArrayList<>();

        try (Connection connection = getConnection();
                PreparedStatement statement =
                        connection.prepareStatement(FIND_BY_SELLER_SQL)) {

            statement.setLong(PARAM_ONE, sellerIdValue);

            try (ResultSet result = statement.executeQuery()) {
                while (result.next()) {
                    reviews.add(mapReview(result));
                }
            }
        }

        return reviews;
    }

    /**
     * Updates a review belonging to a buyer.
     *
     * @param reviewIdValue review ID
     * @param buyerIdValue buyer ID
     * @param ratingValue new rating
     * @param reviewTextValue new review text
     * @return updated review or null
     * @throws SQLException when a database error occurs
     */
    public ProductReview updateReview(
            final long reviewIdValue,
            final long buyerIdValue,
            final int ratingValue,
            final String reviewTextValue) throws SQLException {

        try (Connection connection = getConnection();
                PreparedStatement statement =
                        connection.prepareStatement(
                                UPDATE_REVIEW_SQL)) {

            statement.setInt(PARAM_ONE, ratingValue);
            statement.setString(PARAM_TWO, reviewTextValue);
            statement.setLong(PARAM_THREE, reviewIdValue);
            statement.setLong(PARAM_FOUR, buyerIdValue);

            try (ResultSet result = statement.executeQuery()) {
                if (result.next()) {
                    return mapReview(result);
                }
            }
        }

        return null;
    }

    /**
     * Deletes a review belonging to a buyer.
     *
     * @param reviewIdValue review ID
     * @param buyerIdValue buyer ID
     * @return true when deleted
     * @throws SQLException when a database error occurs
     */
    public boolean deleteReview(
            final long reviewIdValue,
            final long buyerIdValue) throws SQLException {

        try (Connection connection = getConnection();
                PreparedStatement statement =
                        connection.prepareStatement(DELETE_REVIEW_SQL)) {

            statement.setLong(PARAM_ONE, reviewIdValue);
            statement.setLong(PARAM_TWO, buyerIdValue);

            return statement.executeUpdate() > 0;
        }
    }

    /**
     * Gets average rating and review count for a product.
     *
     * @param productIdValue product ID
     * @return array containing average rating and review count
     * @throws SQLException when a database error occurs
     */
    public double[] findProductRating(
            final long productIdValue) throws SQLException {

        try (Connection connection = getConnection();
                PreparedStatement statement =
                        connection.prepareStatement(
                                PRODUCT_RATING_SQL)) {

            statement.setLong(PARAM_ONE, productIdValue);

            try (ResultSet result = statement.executeQuery()) {
                if (result.next()) {
                    double averageRating =
                            result.getDouble("average_rating");
                    double reviewCount =
                            result.getDouble("review_count");

                    return new double[] {
                        averageRating,
                        reviewCount
                    };
                }
            }
        }

        return new double[] {0.0, 0.0};
    }

    /**
     * Checks whether a delivered buyer order contains a product.
     *
     * @param orderIdValue order ID
     * @param buyerIdValue buyer ID
     * @param productIdValue product ID
     * @return true when product belongs to delivered order
     * @throws SQLException when a database error occurs
     */
    public boolean isDeliveredOrderProduct(
            final long orderIdValue,
            final long buyerIdValue,
            final long productIdValue) throws SQLException {

        try (Connection connection = getConnection();
                PreparedStatement statement =
                        connection.prepareStatement(
                                DELIVERED_ORDER_PRODUCT_SQL)) {

            statement.setLong(PARAM_ONE, orderIdValue);
            statement.setLong(PARAM_TWO, buyerIdValue);
            statement.setLong(PARAM_THREE, productIdValue);

            try (ResultSet result = statement.executeQuery()) {
                return result.next()
                        && result.getInt(PARAM_ONE) > 0;
            }
        }
    }

    /**
     * Checks whether a review belongs to a buyer.
     *
     * @param reviewIdValue review ID
     * @param buyerIdValue buyer ID
     * @return true when review belongs to buyer
     * @throws SQLException when a database error occurs
     */
    public boolean isReviewOwnedByBuyer(
            final long reviewIdValue,
            final long buyerIdValue) throws SQLException {

        try (Connection connection = getConnection();
                PreparedStatement statement =
                        connection.prepareStatement(
                                REVIEW_OWNERSHIP_SQL)) {

            statement.setLong(PARAM_ONE, reviewIdValue);
            statement.setLong(PARAM_TWO, buyerIdValue);

            try (ResultSet result = statement.executeQuery()) {
                return result.next()
                        && result.getInt(PARAM_ONE) > 0;
            }
        }
    }

    /**
     * Obtains a database connection from the application data source.
     *
     * @return database connection
     * @throws SQLException when connection cannot be obtained
     */
    private Connection getConnection() throws SQLException {
        Object dataSource = servletContext.getAttribute(
                DatabaseListener.DATA_SOURCE_ATTRIBUTE);

        if (!(dataSource
                instanceof javax.sql.DataSource)) {
            throw new SQLException(
                    "Application data source is not available.");
        }

        return ((javax.sql.DataSource) dataSource).getConnection();
    }

    /**
     * Maps a result row to a ProductReview object.
     *
     * @param resultValue result set
     * @return mapped product review
     * @throws SQLException when a column cannot be read
     */
    private ProductReview mapReview(
            final ResultSet resultValue) throws SQLException {

        ProductReview review = new ProductReview();

        review.setId(resultValue.getLong("id"));
        review.setProductId(
                resultValue.getLong("product_id"));
        review.setBuyerId(
                resultValue.getLong("buyer_id"));
        review.setOrderId(
                resultValue.getLong("order_id"));
        review.setRating(
                resultValue.getInt("rating"));
        review.setReviewText(
                resultValue.getString("review_text"));

        try {
            review.setProductName(
                    resultValue.getString("product_name"));
        } catch (SQLException ignored) {
            // Product name is optional for insert/update queries.
        }

        try {
            review.setBuyerName(
                    resultValue.getString("buyer_name"));
        } catch (SQLException ignored) {
            // Buyer name is optional for insert/update queries.
        }

        Timestamp createdAt =
                resultValue.getTimestamp("created_at");
        Timestamp updatedAt =
                resultValue.getTimestamp("updated_at");

        if (createdAt != null) {
            review.setCreatedAt(
                    createdAt.toLocalDateTime());
        }

        if (updatedAt != null) {
            review.setUpdatedAt(
                    updatedAt.toLocalDateTime());
        }

        return review;
    }
}
