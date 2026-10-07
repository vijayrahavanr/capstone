package com.vrmart.model;

import java.time.LocalDateTime;

/**
 * Represents a buyer review and rating for a purchased product.
 */
public class ProductReview {

    /** Review database identifier. */
    private long id;

    /** Reviewed product identifier. */
    private long productId;

    /** Buyer identifier. */
    private long buyerId;

    /** Related order identifier. */
    private long orderId;

    /** Product display name. */
    private String productName;

    /** Buyer display name. */
    private String buyerName;

    /** Rating value from one to five. */
    private int rating;

    /** Review text written by the buyer. */
    private String reviewText;

    /** Review creation timestamp. */
    private LocalDateTime createdAt;

    /** Review last update timestamp. */
    private LocalDateTime updatedAt;

    /**
     * Gets the review ID.
     *
     * @return review ID
     */
    public long getId() {
        return id;
    }

    /**
     * Sets the review ID.
     *
     * @param idValue review ID
     */
    public void setId(final long idValue) {
        this.id = idValue;
    }

    /**
     * Gets the product ID.
     *
     * @return product ID
     */
    public long getProductId() {
        return productId;
    }

    /**
     * Sets the product ID.
     *
     * @param productIdValue product ID
     */
    public void setProductId(final long productIdValue) {
        this.productId = productIdValue;
    }

    /**
     * Gets the buyer ID.
     *
     * @return buyer ID
     */
    public long getBuyerId() {
        return buyerId;
    }

    /**
     * Sets the buyer ID.
     *
     * @param buyerIdValue buyer ID
     */
    public void setBuyerId(final long buyerIdValue) {
        this.buyerId = buyerIdValue;
    }

    /**
     * Gets the order ID.
     *
     * @return order ID
     */
    public long getOrderId() {
        return orderId;
    }

    /**
     * Sets the order ID.
     *
     * @param orderIdValue order ID
     */
    public void setOrderId(final long orderIdValue) {
        this.orderId = orderIdValue;
    }

    /**
     * Gets the product name.
     *
     * @return product name
     */
    public String getProductName() {
        return productName;
    }

    /**
     * Sets the product name.
     *
     * @param productNameValue product name
     */
    public void setProductName(final String productNameValue) {
        this.productName = productNameValue;
    }

    /**
     * Gets the buyer name.
     *
     * @return buyer name
     */
    public String getBuyerName() {
        return buyerName;
    }

    /**
     * Sets the buyer name.
     *
     * @param buyerNameValue buyer name
     */
    public void setBuyerName(final String buyerNameValue) {
        this.buyerName = buyerNameValue;
    }

    /**
     * Gets the rating.
     *
     * @return rating from one to five
     */
    public int getRating() {
        return rating;
    }

    /**
     * Sets the rating.
     *
     * @param ratingValue rating from one to five
     */
    public void setRating(final int ratingValue) {
        this.rating = ratingValue;
    }

    /**
     * Gets the review text.
     *
     * @return review text
     */
    public String getReviewText() {
        return reviewText;
    }

    /**
     * Sets the review text.
     *
     * @param reviewTextValue review text
     */
    public void setReviewText(final String reviewTextValue) {
        this.reviewText = reviewTextValue;
    }

    /**
     * Gets the creation timestamp.
     *
     * @return creation timestamp
     */
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    /**
     * Sets the creation timestamp.
     *
     * @param createdAtValue creation timestamp
     */
    public void setCreatedAt(final LocalDateTime createdAtValue) {
        this.createdAt = createdAtValue;
    }

    /**
     * Gets the update timestamp.
     *
     * @return update timestamp
     */
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    /**
     * Sets the update timestamp.
     *
     * @param updatedAtValue update timestamp
     */
    public void setUpdatedAt(final LocalDateTime updatedAtValue) {
        this.updatedAt = updatedAtValue;
    }
}
