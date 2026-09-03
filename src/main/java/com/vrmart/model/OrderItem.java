package com.vrmart.model;

import java.math.BigDecimal;

/**
 * Represents an item belonging to a VR Mart order.
 */
public final class OrderItem {

    /** Order item identifier. */
    private long id;

    /** Order identifier. */
    private long orderId;

    /** Product identifier. */
    private long productId;

    /** Ordered quantity. */
    private int quantity;

    /** Price of one product at the time of ordering. */
    private BigDecimal unitPrice;

    /** Product name for display. */
    private String productName;

    /**
     * Returns the order item identifier.
     *
     * @return order item identifier
     */
    public long getId() {
        return id;
    }

    /**
     * Sets the order item identifier.
     *
     * @param value order item identifier
     */
    public void setId(final long value) {
        id = value;
    }

    /**
     * Returns the order identifier.
     *
     * @return order identifier
     */
    public long getOrderId() {
        return orderId;
    }

    /**
     * Sets the order identifier.
     *
     * @param value order identifier
     */
    public void setOrderId(final long value) {
        orderId = value;
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
     * Returns the ordered quantity.
     *
     * @return ordered quantity
     */
    public int getQuantity() {
        return quantity;
    }

    /**
     * Sets the ordered quantity.
     *
     * @param value ordered quantity
     */
    public void setQuantity(final int value) {
        quantity = value;
    }

    /**
     * Returns the unit price.
     *
     * @return unit price
     */
    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    /**
     * Sets the unit price.
     *
     * @param value unit price
     */
    public void setUnitPrice(final BigDecimal value) {
        unitPrice = value;
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
     * Calculates the subtotal for this order item.
     *
     * @return item subtotal
     */
    public BigDecimal getSubtotal() {
        return unitPrice.multiply(
                BigDecimal.valueOf(quantity));
    }
}
