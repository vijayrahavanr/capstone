package com.vrmart.model;

import java.time.LocalDateTime;

/**
 * Represents a buyer service request for an order.
 */
public final class OrderServiceRequest {

    /** Request ID. */
    private long id;

    /** Order ID. */
    private long orderId;

    /** Buyer ID. */
    private long buyerId;

    /** Product ID. */
    private long productId;

    /** Product name. */
    private String productName;

    /** Buyer username. */
    private String buyerName;

    /** Request type. */
    private String requestType;

    /** Request reason. */
    private String reason;

    /** Request status. */
    private String status;

    /** Creation timestamp. */
    private LocalDateTime createdAt;

    /** Update timestamp. */
    private LocalDateTime updatedAt;

    /** @return request ID. */
    public long getId() {
        return id;
    }

    /** @param value request ID. */
    public void setId(final long value) {
        id = value;
    }

    /** @return order ID. */
    public long getOrderId() {
        return orderId;
    }

    /** @param value order ID. */
    public void setOrderId(final long value) {
        orderId = value;
    }

    /** @return buyer ID. */
    public long getBuyerId() {
        return buyerId;
    }

    /** @param value buyer ID. */
    public void setBuyerId(final long value) {
        buyerId = value;
    }

    /** @return product ID. */
    public long getProductId() {
        return productId;
    }

    /** @param value product ID. */
    public void setProductId(final long value) {
        productId = value;
    }

    /** @return product name. */
    public String getProductName() {
        return productName;
    }

    /** @param value product name. */
    public void setProductName(final String value) {
        productName = value;
    }

    /** @return buyer username. */
    public String getBuyerName() {
        return buyerName;
    }

    /** @param value buyer username. */
    public void setBuyerName(final String value) {
        buyerName = value;
    }

    /** @return request type. */
    public String getRequestType() {
        return requestType;
    }

    /** @param value request type. */
    public void setRequestType(final String value) {
        requestType = value;
    }

    /** @return request reason. */
    public String getReason() {
        return reason;
    }

    /** @param value request reason. */
    public void setReason(final String value) {
        reason = value;
    }

    /** @return request status. */
    public String getStatus() {
        return status;
    }

    /** @param value request status. */
    public void setStatus(final String value) {
        status = value;
    }

    /** @return creation timestamp. */
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    /** @param value creation timestamp. */
    public void setCreatedAt(final LocalDateTime value) {
        createdAt = value;
    }

    /** @return update timestamp. */
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    /** @param value update timestamp. */
    public void setUpdatedAt(final LocalDateTime value) {
        updatedAt = value;
    }
}
