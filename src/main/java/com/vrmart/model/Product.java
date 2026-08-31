package com.vrmart.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Represents a product available in VR Mart.
 */
public final class Product {

    /** Product identifier. */
    private long productId;

    /** Seller identifier. */
    private long sellerId;

    /** Product name. */
    private String productName;

    /** Product description. */
    private String productDescription;

    /** Product price. */
    private BigDecimal productPrice;

    /** Available stock quantity. */
    private int productStockQty;

    /** Product category. */
    private String productCategory;

    /** Product image URL. */
    private String productImageUrl;

    /** Product creation timestamp. */
    private LocalDateTime productCreatedAt;

    /** Product update timestamp. */
    private LocalDateTime productUpdatedAt;

    /**
     * Creates an empty product.
     */
    public Product() {
        // Default constructor.
    }

    /**
     * Returns the product identifier.
     *
     * @return product identifier
     */
    public long getId() {
        return productId;
    }

    /**
     * Sets the product identifier.
     *
     * @param value product identifier
     */
    public void setId(final long value) {
        productId = value;
    }

    /**
     * Returns the seller identifier.
     *
     * @return seller identifier
     */
    public long getSellerId() {
        return sellerId;
    }

    /**
     * Sets the seller identifier.
     *
     * @param value seller identifier
     */
    public void setSellerId(final long value) {
        sellerId = value;
    }

    /**
     * Returns the product name.
     *
     * @return product name
     */
    public String getName() {
        return productName;
    }

    /**
     * Sets the product name.
     *
     * @param value product name
     */
    public void setName(final String value) {
        productName = value;
    }

    /**
     * Returns the product description.
     *
     * @return product description
     */
    public String getDescription() {
        return productDescription;
    }

    /**
     * Sets the product description.
     *
     * @param value product description
     */
    public void setDescription(final String value) {
        productDescription = value;
    }

    /**
     * Returns the product price.
     *
     * @return product price
     */
    public BigDecimal getPrice() {
        return productPrice;
    }

    /**
     * Sets the product price.
     *
     * @param value product price
     */
    public void setPrice(final BigDecimal value) {
        productPrice = value;
    }

    /**
     * Returns the available stock quantity.
     *
     * @return stock quantity
     */
    public int getStockQty() {
        return productStockQty;
    }

    /**
     * Sets the available stock quantity.
     *
     * @param value stock quantity
     */
    public void setStockQty(final int value) {
        productStockQty = value;
    }

    /**
     * Returns the product category.
     *
     * @return product category
     */
    public String getCategory() {
        return productCategory;
    }

    /**
     * Sets the product category.
     *
     * @param value product category
     */
    public void setCategory(final String value) {
        productCategory = value;
    }

    /**
     * Returns the product image URL.
     *
     * @return image URL
     */
    public String getImageUrl() {
        return productImageUrl;
    }

    /**
     * Sets the product image URL.
     *
     * @param value image URL
     */
    public void setImageUrl(final String value) {
        productImageUrl = value;
    }

    /**
     * Returns the creation timestamp.
     *
     * @return creation timestamp
     */
    public LocalDateTime getCreatedAt() {
        return productCreatedAt;
    }

    /**
     * Sets the creation timestamp.
     *
     * @param value creation timestamp
     */
    public void setCreatedAt(final LocalDateTime value) {
        productCreatedAt = value;
    }

    /**
     * Returns the update timestamp.
     *
     * @return update timestamp
     */
    public LocalDateTime getUpdatedAt() {
        return productUpdatedAt;
    }

    /**
     * Sets the update timestamp.
     *
     * @param value update timestamp
     */
    public void setUpdatedAt(final LocalDateTime value) {
        productUpdatedAt = value;
    }
}
