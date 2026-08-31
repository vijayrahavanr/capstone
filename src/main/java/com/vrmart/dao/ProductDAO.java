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

    /** Prepared statement parameter for seller ID. */
    private static final int PARAM_SELLER_ID = 1;

    /** Prepared statement parameter for product name. */
    private static final int PARAM_NAME = 2;

    /** Prepared statement parameter for description. */
    private static final int PARAM_DESCRIPTION = 3;

    /** Prepared statement parameter for price. */
    private static final int PARAM_PRICE = 4;

    /** Prepared statement parameter for stock quantity. */
    private static final int PARAM_STOCK_QTY = 5;

    /** Prepared statement parameter for category. */
    private static final int PARAM_CATEGORY = 6;

    /** Prepared statement parameter for image URL. */
    private static final int PARAM_IMAGE_URL = 7;

    /** SQL statement used to retrieve all products. */
    private static final String FIND_ALL_SQL =
            "SELECT id, seller_id, name, description, price, stock_qty, "
                    + "category, image_url, created_at, updated_at "
                    + "FROM products "
                    + "ORDER BY created_at DESC";

    /** SQL statement used to create a product. */
    private static final String CREATE_SQL =
            "INSERT INTO products "
                    + "(seller_id, name, description, price, stock_qty, "
                    + "category, image_url) "
                    + "VALUES (?, ?, ?, ?, ?, ?, ?)";

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
     * Finds all products.
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
                    PARAM_SELLER_ID,
                    product.getSellerId());

            statement.setString(
                    PARAM_NAME,
                    product.getName());

            statement.setString(
                    PARAM_DESCRIPTION,
                    product.getDescription());

            statement.setBigDecimal(
                    PARAM_PRICE,
                    product.getPrice());

            statement.setInt(
                    PARAM_STOCK_QTY,
                    product.getStockQty());

            statement.setString(
                    PARAM_CATEGORY,
                    product.getCategory());

            statement.setString(
                    PARAM_IMAGE_URL,
                    product.getImageUrl());

            statement.executeUpdate();
        }
    }

    /**
     * Converts a database row into a product.
     *
     * @param resultSet database result set
     * @return mapped product
     * @throws SQLException when a database value cannot be read
     */
    private Product mapProduct(final ResultSet resultSet)
            throws SQLException {

        final Product product = new Product();

        product.setId(resultSet.getLong("id"));
        product.setSellerId(resultSet.getLong("seller_id"));
        product.setName(resultSet.getString("name"));
        product.setDescription(
                resultSet.getString("description"));
        product.setPrice(resultSet.getBigDecimal("price"));
        product.setStockQty(resultSet.getInt("stock_qty"));
        product.setCategory(resultSet.getString("category"));
        product.setImageUrl(resultSet.getString("image_url"));

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
