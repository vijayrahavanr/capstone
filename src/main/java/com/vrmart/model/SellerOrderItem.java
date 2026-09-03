package com.vrmart.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Represents an order item received by a seller.
 */
public final class SellerOrderItem {

    /** Order identifier. */
    private long orderId;

    /** Buyer identifier. */
    private long buyerId;

    /** Product identifier. */
    private long productId;

    /** Product name. */
    private String productName;

    /** Ordered quantity. */
    private int quantity;

    /** Price of one product unit. */
    private BigDecimal unitPrice;

    /** Total value of this order item. */
    private BigDecimal lineTotal;

    /** Current order status. */
    private String status;

    /** Delivery address. */
    private String deliveryAddress;

    /** Delivery landmark. */
    private String deliveryLandmark;

    /** Payment method. */
    private String paymentMethod;

    /** Order creation timestamp. */
    private LocalDateTime createdAt;

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
     * Returns the ordered quantity.
     *
     * @return quantity
     */
    public int getQuantity() {
        return quantity;
    }

    /**
     * Sets the ordered quantity.
     *
     * @param value quantity
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
     * Returns the line total.
     *
     * @return line total
     */
    public BigDecimal getLineTotal() {
        return lineTotal;
    }

    /**
     * Sets the line total.
     *
     * @param value line total
     */
    public void setLineTotal(final BigDecimal value) {
        lineTotal = value;
    }

    /**
     * Returns the order status.
     *
     * @return order status
     */
    public String getStatus() {
        return status;
    }

    /**
     * Sets the order status.
     *
     * @param value order status
     */
    public void setStatus(final String value) {
        status = value;
    }

    /**
     * Returns the delivery address.
     *
     * @return delivery address
     */
    public String getDeliveryAddress() {
        return deliveryAddress;
    }

    /**
     * Sets the delivery address.
     *
     * @param value delivery address
     */
    public void setDeliveryAddress(final String value) {
        deliveryAddress = value;
    }

    /**
     * Returns the delivery landmark.
     *
     * @return delivery landmark
     */
    public String getDeliveryLandmark() {
        return deliveryLandmark;
    }

    /**
     * Sets the delivery landmark.
     *
     * @param value delivery landmark
     */
    public void setDeliveryLandmark(final String value) {
        deliveryLandmark = value;
    }

    /**
     * Returns the payment method.
     *
     * @return payment method
     */
    public String getPaymentMethod() {
        return paymentMethod;
    }

    /**
     * Sets the payment method.
     *
     * @param value payment method
     */
    public void setPaymentMethod(final String value) {
        paymentMethod = value;
    }

    /**
     * Returns the order creation timestamp.
     *
     * @return creation timestamp
     */
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    /**
     * Sets the order creation timestamp.
     *
     * @param value creation timestamp
     */
    public void setCreatedAt(final LocalDateTime value) {
        createdAt = value;
    }
}
