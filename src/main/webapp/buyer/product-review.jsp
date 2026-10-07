<%@ page contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>
<%@ page import="com.vrmart.model.ProductReview" %>
<%@ page import="com.vrmart.model.User" %>

<%
    final User currentUser =
            (User) session.getAttribute("user");

    if (currentUser == null
            || !User.ROLE_BUYER.equals(currentUser.getRole())) {
        response.sendRedirect(
                request.getContextPath() + "/login");
        return;
    }

    final ProductReview review =
            (ProductReview) request.getAttribute("review");

    final String orderId =
            request.getParameter("orderId");

    final String productId =
            request.getParameter("productId");

    final String productNameAttribute =
            (String) request.getAttribute("productName");

    final String productName =
            productNameAttribute != null
                    && !productNameAttribute.trim().isEmpty()
                    ? productNameAttribute
                    : "Product";

    final boolean editing = review != null;

    final int selectedRating =
            review != null ? review.getRating() : 5;

    final String reviewText =
            review != null
                    && review.getReviewText() != null
                    ? review.getReviewText()
                    : "";

    final String errorMessage =
            (String) request.getAttribute("error");

    final String successMessage =
            (String) request.getAttribute("success");
%>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport"
          content="width=device-width, initial-scale=1.0">

    <title>
        <%= editing ? "Edit Review" : "Write Review" %>
        - VR Mart
    </title>

    <style>
        * {
            box-sizing: border-box;
        }

        body {
            margin: 0;
            min-height: 100vh;
            font-family: Arial, sans-serif;
            background:
                linear-gradient(135deg, #0b1020, #111827);
            color: #f3f4f6;
        }

        .page {
            width: 100%;
            max-width: 900px;
            margin: 0 auto;
            padding: 40px 20px 60px;
        }

        .back-link {
            display: inline-block;
            margin-bottom: 25px;
            color: #a5b4fc;
            text-decoration: none;
            font-size: 15px;
        }

        .back-link:hover {
            text-decoration: underline;
        }

        .card {
            background: rgba(17, 24, 39, 0.96);
            border: 1px solid #374151;
            border-radius: 18px;
            padding: 32px;
            box-shadow: 0 20px 45px rgba(0, 0, 0, 0.35);
        }

        .heading {
            margin-bottom: 28px;
        }

        .heading h1 {
            margin: 0 0 8px;
            font-size: 30px;
        }

        .heading p {
            margin: 0;
            color: #9ca3af;
            font-size: 15px;
        }

        .product-box {
            background: #1f2937;
            border: 1px solid #374151;
            border-radius: 14px;
            padding: 20px;
            margin-bottom: 28px;
        }

        .product-label {
            color: #9ca3af;
            font-size: 13px;
            margin-bottom: 7px;
        }

        .product-name {
            font-size: 21px;
            font-weight: bold;
            color: #ffffff;
        }

        .field {
            margin-bottom: 26px;
        }

        .field-label {
            display: block;
            margin-bottom: 12px;
            font-size: 15px;
            font-weight: bold;
        }

        .stars {
            display: flex;
            flex-direction: row-reverse;
            justify-content: flex-end;
            gap: 6px;
        }

        .stars input {
            display: none;
        }

        .stars label {
            font-size: 38px;
            color: #4b5563;
            cursor: pointer;
            transition: color 0.15s ease;
            line-height: 1;
        }

        .stars label:hover,
        .stars label:hover ~ label {
            color: #fbbf24;
        }

        .stars input:checked ~ label {
            color: #fbbf24;
        }

        textarea {
            width: 100%;
            min-height: 170px;
            resize: vertical;
            padding: 15px;
            border-radius: 12px;
            border: 1px solid #4b5563;
            background: #111827;
            color: #ffffff;
            font-family: Arial, sans-serif;
            font-size: 15px;
            line-height: 1.5;
            outline: none;
        }

        textarea:focus {
            border-color: #818cf8;
            box-shadow:
                0 0 0 2px rgba(129, 140, 248, 0.15);
        }

        textarea::placeholder {
            color: #6b7280;
        }

        .hint {
            margin-top: 8px;
            color: #6b7280;
            font-size: 13px;
        }

        .message {
            padding: 13px 15px;
            border-radius: 10px;
            margin-bottom: 22px;
            font-size: 14px;
        }

        .error {
            background: rgba(127, 29, 29, 0.35);
            border: 1px solid #991b1b;
            color: #fecaca;
        }

        .success {
            background: rgba(20, 83, 45, 0.35);
            border: 1px solid #166534;
            color: #bbf7d0;
        }

        .actions {
            display: flex;
            gap: 12px;
            align-items: center;
            margin-top: 30px;
        }

        .submit-button {
            border: none;
            border-radius: 10px;
            padding: 13px 25px;
            background: #6366f1;
            color: #ffffff;
            font-size: 15px;
            font-weight: bold;
            cursor: pointer;
        }

        .submit-button:hover {
            background: #4f46e5;
        }

        .cancel-button {
            display: inline-block;
            padding: 12px 22px;
            border-radius: 10px;
            border: 1px solid #4b5563;
            color: #d1d5db;
            text-decoration: none;
            font-size: 15px;
        }

        .cancel-button:hover {
            background: #1f2937;
        }

        .info {
            margin-top: 25px;
            padding-top: 20px;
            border-top: 1px solid #374151;
            color: #9ca3af;
            font-size: 13px;
            line-height: 1.6;
        }

        @media (max-width: 600px) {
            .page {
                padding: 25px 14px 40px;
            }

            .card {
                padding: 22px;
            }

            .heading h1 {
                font-size: 25px;
            }

            .stars label {
                font-size: 32px;
            }

            .actions {
                flex-direction: column;
                align-items: stretch;
            }

            .submit-button,
            .cancel-button {
                width: 100%;
                text-align: center;
            }
        }
    </style>
</head>

<body>

<div class="page">

    <a class="back-link"
       href="<%= request.getContextPath() %>/buyer/order-details?orderId=<%= orderId %>">
        &larr; Back to Order Details
    </a>

    <div class="card">

        <div class="heading">
            <h1>
                <%= editing ? "Edit Your Review" : "Write a Review" %>
            </h1>

            <p>
                <%= editing
                        ? "Update your rating and review for this product."
                        : "Share your experience with this product." %>
            </p>
        </div>

        <% if (errorMessage != null
                && !errorMessage.trim().isEmpty()) { %>

            <div class="message error">
                <%= errorMessage %>
            </div>

        <% } %>

        <% if (successMessage != null
                && !successMessage.trim().isEmpty()) { %>

            <div class="message success">
                <%= successMessage %>
            </div>

        <% } %>

        <div class="product-box">
            <div class="product-label">
                PRODUCT
            </div>

            <div class="product-name">
                <%= productName %>
            </div>
        </div>

        <form method="post"
              action="<%= request.getContextPath() %>/buyer/product-review">

            <input type="hidden"
                   name="orderId"
                   value="<%= orderId %>">

            <input type="hidden"
                   name="productId"
                   value="<%= productId %>">

            <div class="field">

                <label class="field-label">
                    Your Rating
                </label>

                <div class="stars">

                    <input type="radio"
                           id="star5"
                           name="rating"
                           value="5"
                           <%= selectedRating == 5
                                   ? "checked" : "" %>>

                    <label for="star5"
                           title="5 stars">
                        &#9733;
                    </label>

                    <input type="radio"
                           id="star4"
                           name="rating"
                           value="4"
                           <%= selectedRating == 4
                                   ? "checked" : "" %>>

                    <label for="star4"
                           title="4 stars">
                        &#9733;
                    </label>

                    <input type="radio"
                           id="star3"
                           name="rating"
                           value="3"
                           <%= selectedRating == 3
                                   ? "checked" : "" %>>

                    <label for="star3"
                           title="3 stars">
                        &#9733;
                    </label>

                    <input type="radio"
                           id="star2"
                           name="rating"
                           value="2"
                           <%= selectedRating == 2
                                   ? "checked" : "" %>>

                    <label for="star2"
                           title="2 stars">
                        &#9733;
                    </label>

                    <input type="radio"
                           id="star1"
                           name="rating"
                           value="1"
                           <%= selectedRating == 1
                                   ? "checked" : "" %>>

                    <label for="star1"
                           title="1 star">
                        &#9733;
                    </label>

                </div>

            </div>

            <div class="field">

                <label class="field-label"
                       for="reviewText">
                    Your Review
                </label>

                <textarea id="reviewText"
                          name="reviewText"
                          maxlength="1000"
                          minlength="3"
                          required
                          placeholder="Tell us about your experience with this product..."><%= reviewText %></textarea>

                <div class="hint">
                    Write between 3 and 1000 characters.
                </div>

            </div>

            <div class="actions">

                <button type="submit"
                        class="submit-button">
                    <%= editing
                            ? "Update Review"
                            : "Submit Review" %>
                </button>

                <a class="cancel-button"
                   href="<%= request.getContextPath() %>/buyer/order-details?orderId=<%= orderId %>">
                    Cancel
                </a>

            </div>

        </form>

        <div class="info">
            Reviews can be submitted only for products from
            successfully delivered orders.
        </div>

    </div>

</div>

</body>
</html>
