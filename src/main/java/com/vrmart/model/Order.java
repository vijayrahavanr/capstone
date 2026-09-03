package com.vrmart.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Represents a VR Mart customer order.
 */
public final class Order {

    /** Order identifier. */
    private long id;

    /** Buyer identifier. */
    private long buyerId;

    /** Total order amount. */
    private BigDecimal totalAmount;

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

    /** Order update timestamp. */
    private LocalDateTime updatedAt;

    /**
     * Returns the order identifier.
     *
     * @return order identifier
     */
    public long getId() {
        return id;
    }

    /**
     * Sets the order identifier.
     *
     * @param value order identifier
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
     * Returns the total amount.
     *
     * @return total amount
     */
    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    /**
     * Sets the total amount.
     *
     * @param value total amount
     */
    public void setTotalAmount(final BigDecimal value) {
        totalAmount = value;
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
     * Returns the creation timestamp.
     *
     * @return creation timestamp
     */
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    /**
     * Sets the creation timestamp.
     *
     * @param value creation timestamp
     */
    public void setCreatedAt(final LocalDateTime value) {
        createdAt = value;
    }

    /**
     * Returns the update timestamp.
     *
     * @return update timestamp
     */
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    /**
     * Sets the update timestamp.
     *
     * @param value update timestamp
     */
    public void setUpdatedAt(final LocalDateTime value) {
        updatedAt = value;
    }
}
