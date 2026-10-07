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

    /** Customer name. */
    private String customerName;

    /** Customer phone number. */
    private String customerPhone;

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
     * Returns customer name.
     *
     * @return customer name
     */
    public String getCustomerName() {
        return customerName;
    }

    /**
     * Sets customer name.
     *
     * @param value customer name
     */
    public void setCustomerName(final String value) {
        customerName = value;
    }

    /**
     * Returns customer phone number.
     *
     * @return customer phone number
     */
    public String getCustomerPhone() {
        return customerPhone;
    }

    /**
     * Sets customer phone number.
     *
     * @param value customer phone number
     */
    public void setCustomerPhone(final String value) {
        customerPhone = value;
    }

    /**
     * Returns total amount.
     *
     * @return total amount
     */
    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    /**
     * Sets total amount.
     *
     * @param value total amount
     */
    public void setTotalAmount(final BigDecimal value) {
        totalAmount = value;
    }

    /**
     * Returns order status.
     *
     * @return order status
     */
    public String getStatus() {
        return status;
    }

    /**
     * Sets order status.
     *
     * @param value order status
     */
    public void setStatus(final String value) {
        status = value;
    }

    /**
     * Returns delivery address.
     *
     * @return delivery address
     */
    public String getDeliveryAddress() {
        return deliveryAddress;
    }

    /**
     * Sets delivery address.
     *
     * @param value delivery address
     */
    public void setDeliveryAddress(final String value) {
        deliveryAddress = value;
    }

    /**
     * Returns delivery landmark.
     *
     * @return delivery landmark
     */
    public String getDeliveryLandmark() {
        return deliveryLandmark;
    }

    /**
     * Sets delivery landmark.
     *
     * @param value delivery landmark
     */
    public void setDeliveryLandmark(final String value) {
        deliveryLandmark = value;
    }

    /**
     * Returns payment method.
     *
     * @return payment method
     */
    public String getPaymentMethod() {
        return paymentMethod;
    }

    /**
     * Sets payment method.
     *
     * @param value payment method
     */
    public void setPaymentMethod(final String value) {
        paymentMethod = value;
    }

    /**
     * Returns creation timestamp.
     *
     * @return creation timestamp
     */
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    /**
     * Sets creation timestamp.
     *
     * @param value creation timestamp
     */
    public void setCreatedAt(final LocalDateTime value) {
        createdAt = value;
    }

    /**
     * Returns update timestamp.
     *
     * @return update timestamp
     */
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    /**
     * Sets update timestamp.
     *
     * @param value update timestamp
     */
    public void setUpdatedAt(final LocalDateTime value) {
        updatedAt = value;
    }
}
