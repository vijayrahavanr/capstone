package com.vrmart.dao;

import com.vrmart.model.CartItem;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Data access object for buyer shopping carts.
 */
public final class CartDAO {

    /** First prepared statement parameter. */
    private static final int PARAM_ONE = 1;

    /** Second prepared statement parameter. */
    private static final int PARAM_TWO = 2;

    /** Third prepared statement parameter. */
    private static final int PARAM_THREE = 3;

    /** SQL query for finding all items for a buyer. */
    private static final String FIND_BY_BUYER_SQL =
            "SELECT c.id, c.buyer_id, c.product_id, "
                    + "c.quantity, p.name, p.price, "
                    + "p.stock_qty, p.image_url, "
                    + "u.username AS seller_name "
                    + "FROM cart_items c "
                    + "JOIN products p ON p.id = c.product_id "
                    + "JOIN users u ON u.id = p.seller_id "
                    + "WHERE c.buyer_id = ? "
                    + "ORDER BY c.created_at DESC";

    /** SQL query for inserting a cart item. */
    private static final String INSERT_SQL =
            "INSERT INTO cart_items "
                    + "(buyer_id, product_id, quantity) "
                    + "VALUES (?, ?, ?)";

    /** SQL query for finding an existing cart item. */
    private static final String FIND_ITEM_SQL =
            "SELECT quantity "
                    + "FROM cart_items "
                    + "WHERE buyer_id = ? "
                    + "AND product_id = ?";

    /** SQL query for updating cart quantity. */
    private static final String UPDATE_SQL =
            "UPDATE cart_items "
                    + "SET quantity = ?, updated_at = CURRENT_TIMESTAMP "
                    + "WHERE buyer_id = ? "
                    + "AND product_id = ?";

    /** SQL query for deleting a cart item. */
    private static final String DELETE_SQL =
            "DELETE FROM cart_items "
                    + "WHERE buyer_id = ? "
                    + "AND product_id = ?";

    /** Database connection pool. */
    private final DataSource dataSource;

    /**
     * Creates a cart data access object.
     *
     * @param source database connection pool
     */
    public CartDAO(final DataSource source) {
        dataSource = source;
    }

    /**
     * Finds all cart items belonging to a buyer.
     *
     * @param buyerId buyer identifier
     * @return list of cart items
     * @throws SQLException if a database error occurs
     */
    public List<CartItem> findByBuyer(
            final long buyerId) throws SQLException {

        final List<CartItem> items = new ArrayList<>();

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
                    items.add(
                            mapCartItem(resultSet));
                }
            }
        }

        return items;
    }

    /**
     * Adds a product to the cart.
     *
     * @param buyerId buyer identifier
     * @param productId product identifier
     * @param quantity quantity to add
     * @throws SQLException if a database error occurs
     */
    public void addItem(
            final long buyerId,
            final long productId,
            final int quantity) throws SQLException {

        if (quantity <= 0) {
            throw new IllegalArgumentException(
                    "Quantity must be greater than zero.");
        }

        final Integer existingQuantity =
                findExistingQuantity(
                        buyerId,
                        productId);

        if (existingQuantity == null) {

            try (Connection connection =
                         dataSource.getConnection();
                    PreparedStatement statement =
                            connection.prepareStatement(
                                    INSERT_SQL)) {

                statement.setLong(
                        PARAM_ONE,
                        buyerId);

                statement.setLong(
                        PARAM_TWO,
                        productId);

                statement.setInt(
                        PARAM_THREE,
                        quantity);

                statement.executeUpdate();
            }

        } else {

            updateQuantity(
                    buyerId,
                    productId,
                    existingQuantity + quantity);
        }
    }

    /**
     * Updates the quantity of a cart item.
     *
     * @param buyerId buyer identifier
     * @param productId product identifier
     * @param quantity new quantity
     * @throws SQLException if a database error occurs
     */
    public void updateQuantity(
            final long buyerId,
            final long productId,
            final int quantity) throws SQLException {

        if (quantity <= 0) {
            removeItem(
                    buyerId,
                    productId);
            return;
        }

        try (Connection connection =
                     dataSource.getConnection();
                PreparedStatement statement =
                        connection.prepareStatement(
                                UPDATE_SQL)) {

            statement.setInt(
                    PARAM_ONE,
                    quantity);

            statement.setLong(
                    PARAM_TWO,
                    buyerId);

            statement.setLong(
                    PARAM_THREE,
                    productId);

            statement.executeUpdate();
        }
    }

    /**
     * Removes a product from the cart.
     *
     * @param buyerId buyer identifier
     * @param productId product identifier
     * @throws SQLException if a database error occurs
     */
    public void removeItem(
            final long buyerId,
            final long productId) throws SQLException {

        try (Connection connection =
                     dataSource.getConnection();
                PreparedStatement statement =
                        connection.prepareStatement(
                                DELETE_SQL)) {

            statement.setLong(
                    PARAM_ONE,
                    buyerId);

            statement.setLong(
                    PARAM_TWO,
                    productId);

            statement.executeUpdate();
        }
    }

    /**
     * Finds the existing quantity of a cart item.
     *
     * @param buyerId buyer identifier
     * @param productId product identifier
     * @return existing quantity, or null when not found
     * @throws SQLException if a database error occurs
     */
    private Integer findExistingQuantity(
            final long buyerId,
            final long productId) throws SQLException {

        try (Connection connection =
                     dataSource.getConnection();
                PreparedStatement statement =
                        connection.prepareStatement(
                                FIND_ITEM_SQL)) {

            statement.setLong(
                    PARAM_ONE,
                    buyerId);

            statement.setLong(
                    PARAM_TWO,
                    productId);

            try (ResultSet resultSet =
                         statement.executeQuery()) {

                if (resultSet.next()) {
                    return resultSet.getInt(
                            PARAM_ONE);
                }
            }
        }

        return null;
    }

    /**
     * Maps a database result row to a cart item.
     *
     * @param resultSet database result set
     * @return mapped cart item
     * @throws SQLException if a database error occurs
     */
    private CartItem mapCartItem(
            final ResultSet resultSet) throws SQLException {

        final CartItem item = new CartItem();

        item.setId(
                resultSet.getLong("id"));

        item.setBuyerId(
                resultSet.getLong("buyer_id"));

        item.setProductId(
                resultSet.getLong("product_id"));

        item.setQuantity(
                resultSet.getInt("quantity"));

        item.setProductName(
                resultSet.getString("name"));

        item.setSellerName(
                resultSet.getString("seller_name"));

        item.setProductPrice(
                resultSet.getBigDecimal("price"));

        item.setStockQty(
                resultSet.getInt("stock_qty"));

        item.setImageUrl(
                resultSet.getString("image_url"));

        return item;
    }
}
