package com.vrmart.dao;

import com.vrmart.model.Product;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Data access object for products.
 */
public final class ProductDAO {

    /** First SQL parameter. */
    private static final int PARAM_1 = 1;

    /** Second SQL parameter. */
    private static final int PARAM_2 = 2;

    /** Third SQL parameter. */
    private static final int PARAM_3 = 3;

    /** Fourth SQL parameter. */
    private static final int PARAM_4 = 4;

    /** Fifth SQL parameter. */
    private static final int PARAM_5 = 5;

    /** Sixth SQL parameter. */
    private static final int PARAM_6 = 6;

    /** Seventh SQL parameter. */
    private static final int PARAM_7 = 7;

    /** Eighth SQL parameter. */
    private static final int PARAM_8 = 8;

    /** SQL statement used to retrieve active products. */
    private static final String FIND_ALL_SQL =
            "SELECT p.id, p.seller_id, p.name, p.description, "
                    + "p.price, p.stock_qty, p.category, p.image_url, "
                    + "p.created_at, p.updated_at, "
                    + "u.username AS seller_name, "
                    + "COALESCE(a.assured, FALSE) AS is_vr_mart_assured "
                    + "FROM products p "
                    + "JOIN users u ON u.id = p.seller_id "
                    + "LEFT JOIN vr_mart_assurance a ON a.product_id = p.id "
                    + "WHERE p.is_active = TRUE "
                    + "ORDER BY p.created_at DESC";

    /** SQL statement used to retrieve active seller products. */
    private static final String FIND_BY_SELLER_SQL =
            "SELECT p.id, p.seller_id, p.name, p.description, "
                    + "p.price, p.stock_qty, p.category, p.image_url, "
                    + "p.created_at, p.updated_at, "
                    + "u.username AS seller_name, "
                    + "COALESCE(a.assured, FALSE) AS is_vr_mart_assured "
                    + "FROM products p "
                    + "JOIN users u ON u.id = p.seller_id "
                    + "LEFT JOIN vr_mart_assurance a ON a.product_id = p.id "
                    + "WHERE p.seller_id = ? "
                    + "AND p.is_active = TRUE "
                    + "ORDER BY p.created_at DESC";

    /** SQL statement used to retrieve one active seller product. */
    private static final String FIND_BY_ID_AND_SELLER_SQL =
            "SELECT p.id, p.seller_id, p.name, p.description, "
                    + "p.price, p.stock_qty, p.category, p.image_url, "
                    + "p.created_at, p.updated_at, "
                    + "u.username AS seller_name, "
                    + "COALESCE(a.assured, FALSE) AS is_vr_mart_assured "
                    + "FROM products p "
                    + "JOIN users u ON u.id = p.seller_id "
                    + "LEFT JOIN vr_mart_assurance a ON a.product_id = p.id "
                    + "WHERE p.id = ? "
                    + "AND p.seller_id = ? "
                    + "AND p.is_active = TRUE";

    /** SQL statement used to create a product. */
    private static final String CREATE_SQL =
            "INSERT INTO products "
                    + "(seller_id, name, description, price, stock_qty, "
                    + "category, image_url, is_active) "
                    + "VALUES (?, ?, ?, ?, ?, ?, ?, TRUE)";

    /** SQL statement used to update an active product. */
    private static final String UPDATE_SQL =
            "UPDATE products SET "
                    + "name = ?, description = ?, price = ?, stock_qty = ?, "
                    + "category = ?, image_url = ?, "
                    + "updated_at = CURRENT_TIMESTAMP "
                    + "WHERE id = ? AND seller_id = ? AND is_active = TRUE";

    /** SQL statement used to safely remove a product. */
    private static final String DELETE_SQL =
            "UPDATE products SET "
                    + "is_active = FALSE, "
                    + "updated_at = CURRENT_TIMESTAMP "
                    + "WHERE id = ? AND seller_id = ? AND is_active = TRUE";

    /** SQL statement used to retrieve product stock. */
    private static final String FIND_STOCK_SQL =
            "SELECT stock_qty "
                    + "FROM products "
                    + "WHERE id = ? AND is_active = TRUE";

    /** Database data source. */
    private final DataSource dataSource;

    /**
     * Creates a product data access object.
     *
     * @param source database data source
     */
    public ProductDAO(final DataSource source) {
        dataSource = source;
    }

    /**
     * Finds all active products.
     *
     * @return list of products
     * @throws SQLException when database access fails
     */
    public List<Product> findAll() throws SQLException {

        final List<Product> products = new ArrayList<>();

        try (Connection connection = dataSource.getConnection();
                PreparedStatement statement =
                        connection.prepareStatement(FIND_ALL_SQL);
                ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                products.add(mapProduct(resultSet));
            }
        }

        return products;
    }

    /**
     * Finds active products belonging to a seller.
     *
     * @param sellerId seller identifier
     * @return list of seller products
     * @throws SQLException when database access fails
     */
    public List<Product> findBySeller(
            final long sellerId) throws SQLException {

        final List<Product> products = new ArrayList<>();

        try (Connection connection = dataSource.getConnection();
                PreparedStatement statement =
                        connection.prepareStatement(
                                FIND_BY_SELLER_SQL)) {

            statement.setLong(PARAM_1, sellerId);

            try (ResultSet resultSet = statement.executeQuery()) {

                while (resultSet.next()) {
                    products.add(mapProduct(resultSet));
                }
            }
        }

        return products;
    }

    /**
     * Finds an active product belonging to a specific seller.
     *
     * @param productId product identifier
     * @param sellerId seller identifier
     * @return product or null
     * @throws SQLException when database access fails
     */
    public Product findByIdAndSeller(
            final long productId,
            final long sellerId) throws SQLException {

        try (Connection connection = dataSource.getConnection();
                PreparedStatement statement =
                        connection.prepareStatement(
                                FIND_BY_ID_AND_SELLER_SQL)) {

            statement.setLong(PARAM_1, productId);
            statement.setLong(PARAM_2, sellerId);

            try (ResultSet resultSet = statement.executeQuery()) {

                if (resultSet.next()) {
                    return mapProduct(resultSet);
                }
            }
        }

        return null;
    }

    /**
     * Creates a new product.
     *
     * @param product product to create
     * @throws SQLException when database insertion fails
     */
    public void create(final Product product) throws SQLException {

        try (Connection connection = dataSource.getConnection();
                PreparedStatement statement =
                        connection.prepareStatement(CREATE_SQL)) {

            statement.setLong(
                    PARAM_1,
                    product.getSellerId());

            statement.setString(
                    PARAM_2,
                    product.getName());

            statement.setString(
                    PARAM_3,
                    product.getDescription());

            statement.setBigDecimal(
                    PARAM_4,
                    product.getPrice());

            statement.setInt(
                    PARAM_5,
                    product.getStockQty());

            statement.setString(
                    PARAM_6,
                    product.getCategory());

            statement.setString(
                    PARAM_7,
                    product.getImageUrl());

            statement.executeUpdate();
        }
    }

    /**
     * Updates an active seller product.
     *
     * @param product product to update
     * @return true when the product was updated
     * @throws SQLException when database update fails
     */
    public boolean update(final Product product)
            throws SQLException {

        try (Connection connection = dataSource.getConnection();
                PreparedStatement statement =
                        connection.prepareStatement(UPDATE_SQL)) {

            statement.setString(
                    PARAM_1,
                    product.getName());

            statement.setString(
                    PARAM_2,
                    product.getDescription());

            statement.setBigDecimal(
                    PARAM_3,
                    product.getPrice());

            statement.setInt(
                    PARAM_4,
                    product.getStockQty());

            statement.setString(
                    PARAM_5,
                    product.getCategory());

            statement.setString(
                    PARAM_6,
                    product.getImageUrl());

            statement.setLong(
                    PARAM_7,
                    product.getId());

            statement.setLong(
                    PARAM_8,
                    product.getSellerId());

            return statement.executeUpdate() > 0;
        }
    }

    /**
     * Safely removes a seller product.
     *
     * <p>The product is marked inactive instead of being physically
     * deleted. This preserves historical order records.</p>
     *
     * @param productId product identifier
     * @param sellerId seller identifier
     * @return true when removed, false when not found or already removed
     * @throws SQLException when database update fails
     */
    public boolean delete(
            final long productId,
            final long sellerId) throws SQLException {

        try (Connection connection = dataSource.getConnection();
                PreparedStatement statement =
                        connection.prepareStatement(DELETE_SQL)) {

            statement.setLong(PARAM_1, productId);
            statement.setLong(PARAM_2, sellerId);

            return statement.executeUpdate() > 0;
        }
    }

    /**
     * Finds available stock quantity.
     *
     * @param productId product identifier
     * @return stock quantity or null
     * @throws SQLException when database access fails
     */
    public Integer findStock(final long productId)
            throws SQLException {

        try (Connection connection = dataSource.getConnection();
                PreparedStatement statement =
                        connection.prepareStatement(FIND_STOCK_SQL)) {

            statement.setLong(PARAM_1, productId);

            try (ResultSet resultSet = statement.executeQuery()) {

                if (resultSet.next()) {
                    return resultSet.getInt("stock_qty");
                }
            }
        }

        return null;
    }

    /**
     * Maps a database row into a product.
     *
     * @param resultSet database result set
     * @return mapped product
     * @throws SQLException when database value cannot be read
     */
    private Product mapProduct(
            final ResultSet resultSet) throws SQLException {

        final Product product = new Product();

        product.setId(
                resultSet.getLong("id"));

        product.setSellerId(
                resultSet.getLong("seller_id"));

        product.setSellerName(
                resultSet.getString("seller_name"));

        product.setName(
                resultSet.getString("name"));

        product.setDescription(
                resultSet.getString("description"));

        product.setPrice(
                resultSet.getBigDecimal("price"));

        product.setStockQty(
                resultSet.getInt("stock_qty"));

        product.setCategory(
                resultSet.getString("category"));

        product.setImageUrl(
                resultSet.getString("image_url"));

        product.setVrMartAssured(
                resultSet.getBoolean("is_vr_mart_assured"));

        if (resultSet.getTimestamp("created_at") != null) {
            product.setCreatedAt(
                    resultSet.getTimestamp("created_at")
                            .toLocalDateTime());
        }

        if (resultSet.getTimestamp("updated_at") != null) {
            product.setUpdatedAt(
                    resultSet.getTimestamp("updated_at")
                            .toLocalDateTime());
        }

        return product;
    }
}
