package com.vrmart.model;

import java.math.BigDecimal;

/**
 * Represents an item in a buyer shopping cart.
 */
public final class CartItem {

    /** Cart item identifier. */
    private long id;

    /** Buyer identifier. */
    private long buyerId;

    /** Product identifier. */
    private long productId;

    /** Product name. */
    private String productName;

    /** Seller username. */
    private String sellerName;

    /** Product price. */
    private BigDecimal productPrice;

    /** Available product stock. */
    private int stockQty;

    /** Cart quantity. */
    private int quantity;

    /** Product image URL. */
    private String imageUrl;

    /**
     * Returns the cart item identifier.
     *
     * @return cart item identifier
     */
    public long getId() {
        return id;
    }

    /**
     * Sets the cart item identifier.
     *
     * @param value cart item identifier
     */
    public void setId(final long value) {
        id = value;
    }

    /**
     * Returns the buyer identifier.
     *
     * @return buyer identifier
     */
    public long getBuyerId() {
        return buyerId;
    }

    /**
     * Sets the buyer identifier.
     *
     * @param value buyer identifier
     */
    public void setBuyerId(final long value) {
        buyerId = value;
    }

    /**
     * Returns the product identifier.
     *
     * @return product identifier
     */
    public long getProductId() {
        return productId;
    }

    /**
     * Sets the product identifier.
     *
     * @param value product identifier
     */
    public void setProductId(final long value) {
        productId = value;
    }

    /**
     * Returns the product name.
     *
     * @return product name
     */
    public String getProductName() {
        return productName;
    }

    /**
     * Sets the product name.
     *
     * @param value product name
     */
    public void setProductName(final String value) {
        productName = value;
    }

    /**
     * Returns the seller username.
     *
     * @return seller username
     */
    public String getSellerName() {
        return sellerName;
    }

    /**
     * Sets the seller username.
     *
     * @param value seller username
     */
    public void setSellerName(final String value) {
        sellerName = value;
    }

    /**
     * Returns the product price.
     *
     * @return product price
     */
    public BigDecimal getProductPrice() {
        return productPrice;
    }

    /**
     * Sets the product price.
     *
     * @param value product price
     */
    public void setProductPrice(final BigDecimal value) {
        productPrice = value;
    }

    /**
     * Returns the available stock quantity.
     *
     * @return stock quantity
     */
    public int getStockQty() {
        return stockQty;
    }

    /**
     * Sets the available stock quantity.
     *
     * @param value stock quantity
     */
    public void setStockQty(final int value) {
        stockQty = value;
    }

    /**
     * Returns the cart quantity.
     *
     * @return cart quantity
     */
    public int getQuantity() {
        return quantity;
    }

    /**
     * Sets the cart quantity.
     *
     * @param value cart quantity
     */
    public void setQuantity(final int value) {
        quantity = value;
    }

    /**
     * Returns the product image URL.
     *
     * @return image URL
     */
    public String getImageUrl() {
        return imageUrl;
    }

    /**
     * Sets the product image URL.
     *
     * @param value image URL
     */
    public void setImageUrl(final String value) {
        imageUrl = value;
    }

    /**
     * Calculates the cart item subtotal.
     *
     * @return item subtotal
     */
    public BigDecimal getSubtotal() {
        return productPrice.multiply(
                BigDecimal.valueOf(quantity));
    }
}
