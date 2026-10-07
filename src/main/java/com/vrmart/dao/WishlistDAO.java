package com.vrmart.dao;

import com.vrmart.model.Product;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/**
 * Handles buyer wishlist database operations.
 */
public final class WishlistDAO {

    /** Data source. */
    private final DataSource dataSource;

    /** Finds all wishlist products for a buyer. */
    private static final String FIND_BY_BUYER_SQL =
            "SELECT p.id, p.name, p.description, p.category, "
                    + "p.price, p.stock_qty, p.image_url, "
                    + "p.seller_id, u.username AS seller_name "
                    + "FROM wishlists w "
                    + "JOIN products p ON p.id = w.product_id "
                    + "JOIN users u ON u.id = p.seller_id "
                    + "WHERE w.buyer_id = ? "
                    + "ORDER BY w.created_at DESC";

    /** Adds a product to the wishlist. */
    private static final String ADD_SQL =
            "INSERT INTO wishlists (buyer_id, product_id) "
                    + "VALUES (?, ?) "
                    + "ON CONFLICT (buyer_id, product_id) "
                    + "DO NOTHING";

    /** Removes a product from the wishlist. */
    private static final String REMOVE_SQL =
            "DELETE FROM wishlists "
                    + "WHERE buyer_id = ? "
                    + "AND product_id = ?";

    /** Checks whether a product is already wishlisted. */
    private static final String EXISTS_SQL =
            "SELECT 1 FROM wishlists "
                    + "WHERE buyer_id = ? "
                    + "AND product_id = ?";

    /**
     * Creates the DAO.
     *
     * @param source configured data source
     */
    public WishlistDAO(final DataSource source) {
        this.dataSource = source;
    }

    /**
     * Finds all wishlist products for a buyer.
     *
     * @param buyerId buyer ID
     * @return wishlist products
     * @throws Exception when database access fails
     */
    public List<Product> findByBuyer(final long buyerId)
            throws Exception {

        final List<Product> products =
                new ArrayList<>();

        try (Connection connection =
                     dataSource.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(
                             FIND_BY_BUYER_SQL)) {

            statement.setLong(1, buyerId);

            try (ResultSet result =
                         statement.executeQuery()) {

                while (result.next()) {
                    products.add(mapProduct(result));
                }
            }
        }

        return products;
    }

    /**
     * Adds a product to a buyer wishlist.
     *
     * @param buyerId buyer ID
     * @param productId product ID
     * @throws Exception when database access fails
     */
    public void add(final long buyerId,
                    final long productId)
            throws Exception {

        try (Connection connection =
                     dataSource.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(ADD_SQL)) {

            statement.setLong(1, buyerId);
            statement.setLong(2, productId);
            statement.executeUpdate();
        }
    }

    /**
     * Removes a product from a buyer wishlist.
     *
     * @param buyerId buyer ID
     * @param productId product ID
     * @throws Exception when database access fails
     */
    public void remove(final long buyerId,
                       final long productId)
            throws Exception {

        try (Connection connection =
                     dataSource.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(REMOVE_SQL)) {

            statement.setLong(1, buyerId);
            statement.setLong(2, productId);
            statement.executeUpdate();
        }
    }

    /**
     * Checks whether a product is wishlisted.
     *
     * @param buyerId buyer ID
     * @param productId product ID
     * @return true when already wishlisted
     * @throws Exception when database access fails
     */
    public boolean exists(final long buyerId,
                          final long productId)
            throws Exception {

        try (Connection connection =
                     dataSource.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(EXISTS_SQL)) {

            statement.setLong(1, buyerId);
            statement.setLong(2, productId);

            try (ResultSet result =
                         statement.executeQuery()) {

                return result.next();
            }
        }
    }

    /**
     * Maps a result row to a product.
     *
     * @param result database result
     * @return mapped product
     * @throws Exception when result access fails
     */
    private Product mapProduct(final ResultSet result)
            throws Exception {

        final Product product = new Product();

        product.setId(result.getLong("id"));
        product.setName(result.getString("name"));
        product.setDescription(
                result.getString("description"));
        product.setCategory(result.getString("category"));
        product.setPrice(result.getBigDecimal("price"));
        product.setStockQty(
                result.getInt("stock_qty"));
        product.setImageUrl(
                result.getString("image_url"));
        product.setSellerId(
                result.getLong("seller_id"));
        product.setSellerName(
                result.getString("seller_name"));

        return product;
    }
}
