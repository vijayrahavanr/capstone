<%@ page contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>

<%@ page import="java.util.List" %>
<%@ page import="java.util.Map" %>
<%@ page import="com.vrmart.model.Order" %>
<%@ page import="com.vrmart.model.CartItem" %>
<%@ page import="com.vrmart.model.OrderServiceRequest" %>
<%@ page import="com.vrmart.model.ProductReview" %>

<%
    final Order order =
            (Order) request.getAttribute("order");

    final List<CartItem> orderItems =
            (List<CartItem>)
                    request.getAttribute("orderItems");

    final List<OrderServiceRequest> serviceRequests =
            (List<OrderServiceRequest>)
                    request.getAttribute("serviceRequests");

    final Map<Long, ProductReview> productReviews =
            (Map<Long, ProductReview>)
                    request.getAttribute("productReviews");

    final String message =
            request.getParameter("message");

    final String orderStatus =
            order == null ? "" : order.getStatus();

    final boolean canCancel =
            "PENDING".equals(orderStatus)
                    || "APPROVED".equals(orderStatus);

    final boolean canReturnOrReplace =
            "DELIVERED".equals(orderStatus);

    final boolean canNeedHelp =
            !"CANCELLED".equals(orderStatus);
%>

<!DOCTYPE html>
<html lang="en">

<head>

    <meta charset="UTF-8">

    <meta name="viewport"
          content="width=device-width, initial-scale=1.0">

    <title>VR Mart | Order Details</title>

    <style>

        * {
            box-sizing: border-box;
        }

        body {
            margin: 0;
            min-height: 100vh;
            font-family: Arial, Helvetica, sans-serif;
            background: linear-gradient(
                    135deg,
                    #0b1020,
                    #111827
            );
            color: #ffffff;
        }

        .container {
            width: 92%;
            max-width: 1100px;
            margin: 40px auto;
        }

        .topbar {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 30px;
        }

        .topbar h1 {
            margin: 0;
            font-size: 32px;
        }

        .button {
            display: inline-block;
            padding: 10px 18px;
            border-radius: 8px;
            background: #374151;
            color: #ffffff;
            text-decoration: none;
            font-size: 14px;
            font-weight: 600;
        }

        .button:hover {
            background: #4b5563;
        }

        .message {
            margin-bottom: 20px;
            padding: 14px 18px;
            border-radius: 10px;
            background: rgba(34, 197, 94, 0.15);
            border: 1px solid rgba(34, 197, 94, 0.35);
            color: #86efac;
        }

        .grid {
            display: grid;
            grid-template-columns:
                repeat(2, minmax(0, 1fr));
            gap: 20px;
        }

        .card {
            background: rgba(255, 255, 255, 0.045);
            border: 1px solid rgba(255, 255, 255, 0.08);
            border-radius: 16px;
            padding: 24px;
        }

        .wide {
            grid-column: 1 / -1;
        }

        .card h2 {
            margin: 0 0 22px;
            font-size: 20px;
        }

        .details {
            display: grid;
            gap: 14px;
        }

        .detail {
            display: flex;
            justify-content: space-between;
            gap: 20px;
            padding-bottom: 12px;
            border-bottom: 1px solid
                    rgba(255, 255, 255, 0.06);
        }

        .detail:last-child {
            border-bottom: 0;
            padding-bottom: 0;
        }

        .label {
            color: #8b95a7;
            font-size: 13px;
        }

        .value {
            text-align: right;
            font-size: 14px;
            font-weight: 600;
        }

        .status {
            display: inline-block;
            padding: 6px 12px;
            border-radius: 20px;
            background: rgba(129, 140, 248, 0.16);
            color: #a5b4fc;
            font-size: 12px;
            font-weight: 700;
        }

        .items {
            display: grid;
            gap: 14px;
        }

        .item {
            padding: 18px;
            border-radius: 12px;
            background: rgba(255, 255, 255, 0.035);
            border: 1px solid
                    rgba(255, 255, 255, 0.06);
        }

        .product-name {
            font-size: 16px;
            font-weight: 700;
            margin-bottom: 6px;
        }

        .seller {
            color: #9ca3af;
            font-size: 13px;
            margin-bottom: 14px;
        }

        .quantity {
            color: #a5b4fc;
            font-size: 13px;
        }

        .price {
            color: #d1d5db;
            font-size: 14px;
            margin-top: 8px;
        }

        .total {
            margin-top: 20px;
            padding-top: 18px;
            border-top: 1px solid
                    rgba(255, 255, 255, 0.08);
            display: flex;
            justify-content: space-between;
            align-items: center;
        }

        .total-label {
            color: #9ca3af;
            font-size: 14px;
        }

        .total-value {
            font-size: 22px;
            font-weight: 800;
        }

        /* Review section */

        .review-section {
            margin-top: 18px;
            padding-top: 16px;
            border-top: 1px solid
                    rgba(255, 255, 255, 0.08);
        }

        .review-title {
            margin-bottom: 10px;
            color: #f9fafb;
            font-size: 14px;
            font-weight: 700;
        }

        .review-stars {
            margin-bottom: 8px;
            color: #fbbf24;
            font-size: 18px;
            letter-spacing: 2px;
        }

        .review-text {
            margin-bottom: 12px;
            color: #cbd5e1;
            font-size: 13px;
            line-height: 1.5;
        }

        .review-date {
            margin-bottom: 12px;
            color: #6b7280;
            font-size: 11px;
        }

        .review-button {
            display: inline-block;
            padding: 9px 15px;
            border-radius: 8px;
            background: #6366f1;
            color: #ffffff;
            text-decoration: none;
            font-size: 12px;
            font-weight: 700;
        }

        .review-button:hover {
            background: #4f46e5;
        }

        .review-note {
            color: #8b95a7;
            font-size: 12px;
        }

        .actions {
            display: grid;
            gap: 18px;
        }

        .action-box {
            padding: 18px;
            border-radius: 12px;
            background: rgba(255, 255, 255, 0.035);
            border: 1px solid
                    rgba(255, 255, 255, 0.06);
        }

        .action-box h3 {
            margin: 0 0 8px;
            font-size: 16px;
        }

        .action-box p {
            margin: 0 0 15px;
            color: #8b95a7;
            font-size: 13px;
            line-height: 1.5;
        }

        .action-form {
            display: grid;
            gap: 12px;
        }

        select,
        textarea {
            width: 100%;
            padding: 11px 12px;
            border-radius: 8px;
            border: 1px solid
                    rgba(255, 255, 255, 0.12);
            background: #111827;
            color: #ffffff;
            font-family: inherit;
            font-size: 13px;
            outline: none;
        }

        select:focus,
        textarea:focus {
            border-color: #818cf8;
        }

        textarea {
            min-height: 90px;
            resize: vertical;
        }

        .submit {
            width: fit-content;
            padding: 10px 18px;
            border: 0;
            border-radius: 8px;
            background: #6366f1;
            color: #ffffff;
            font-weight: 700;
            cursor: pointer;
        }

        .submit:hover {
            background: #4f46e5;
        }

        .submit:disabled {
            background: #374151;
            color: #6b7280;
            cursor: not-allowed;
            opacity: 0.65;
        }

        select:disabled,
        textarea:disabled {
            background: #1f2937;
            color: #6b7280;
            cursor: not-allowed;
            opacity: 0.65;
        }

        .cancel {
            background: #dc2626;
        }

        .cancel:hover {
            background: #b91c1c;
        }

        .history {
            display: grid;
            gap: 14px;
        }

        .history-item {
            padding: 16px;
            border-radius: 10px;
            background: rgba(255, 255, 255, 0.035);
            border: 1px solid
                    rgba(255, 255, 255, 0.06);
        }

        .history-top {
            display: flex;
            justify-content: space-between;
            gap: 15px;
            margin-bottom: 10px;
        }

        .history-type {
            font-weight: 700;
        }

        .history-status {
            color: #a5b4fc;
            font-size: 12px;
            font-weight: 700;
        }

        .history-product {
            color: #9ca3af;
            font-size: 13px;
            margin-bottom: 8px;
        }

        .history-reason {
            color: #d1d5db;
            font-size: 13px;
            line-height: 1.5;
        }

        .blocked-message {
            display: none;
            margin-top: 10px;
            padding: 10px 12px;
            border-radius: 8px;
            background: rgba(239, 68, 68, 0.10);
            border: 1px solid rgba(239, 68, 68, 0.25);
            color: #fca5a5;
            font-size: 12px;
        }

        .empty {
            color: #8b95a7;
            font-size: 13px;
        }

        @media (max-width: 700px) {

            .grid {
                grid-template-columns: 1fr;
            }

            .wide {
                grid-column: auto;
            }

            .topbar {
                align-items: flex-start;
                flex-direction: column;
                gap: 15px;
            }

            .detail {
                flex-direction: column;
                gap: 5px;
            }

            .value {
                text-align: left;
            }

            .total {
                gap: 15px;
            }
        }

    </style>

</head>

<body>

<div class="container">

    <div class="topbar">

        <h1>Order Details</h1>

        <a class="button"
           href="<%= request.getContextPath() %>/buyer/orders">
            Back to My Orders
        </a>

    </div>

    <% if (message != null && !message.trim().isEmpty()) { %>

        <div class="message">
            <%= message %>
        </div>

    <% } %>

    <% if (order != null) { %>

        <div class="grid">

            <!-- ORDER INFORMATION -->

            <div class="card">

                <h2>Order Information</h2>

                <div class="details">

                    <div class="detail">
                        <div class="label">Order ID</div>
                        <div class="value">
                            #<%= order.getId() %>
                        </div>
                    </div>

                    <div class="detail">
                        <div class="label">Order Date</div>
                        <div class="value">
                            <%= order.getCreatedAt() %>
                        </div>
                    </div>

                    <div class="detail">
                        <div class="label">Status</div>
                        <div class="value">
                            <span class="status">
                                <%= order.getStatus() %>
                            </span>
                        </div>
                    </div>

                    <div class="detail">
                        <div class="label">Payment</div>
                        <div class="value">
                            <%= order.getPaymentMethod() %>
                        </div>
                    </div>

                </div>

            </div>


            <!-- CUSTOMER INFORMATION -->

            <div class="card">

                <h2>Customer Information</h2>

                <div class="details">

                    <div class="detail">
                        <div class="label">Name</div>
                        <div class="value">
                            <%= order.getCustomerName() == null
                                    ? "-"
                                    : order.getCustomerName() %>
                        </div>
                    </div>

                    <div class="detail">
                        <div class="label">Phone</div>
                        <div class="value">
                            <%= order.getCustomerPhone() == null
                                    ? "-"
                                    : order.getCustomerPhone() %>
                        </div>
                    </div>

                </div>

            </div>


            <!-- DELIVERY -->

            <div class="card wide">

                <h2>Delivery</h2>

                <div class="details">

                    <div class="detail">

                        <div class="label">
                            Delivery Address
                        </div>

                        <div class="value">
                            <%= order.getDeliveryAddress() == null
                                    ? "-"
                                    : order.getDeliveryAddress() %>
                        </div>

                    </div>

                    <div class="detail">

                        <div class="label">Landmark</div>

                        <div class="value">
                            <%= order.getDeliveryLandmark() == null
                                    || order.getDeliveryLandmark()
                                            .trim().isEmpty()
                                    ? "-"
                                    : order.getDeliveryLandmark() %>
                        </div>

                    </div>

                </div>

            </div>


            <!-- PRODUCTS -->

            <div class="card wide">

                <h2>Products</h2>

                <div class="items">

                    <%
                        if (orderItems != null
                                && !orderItems.isEmpty()) {

                            for (CartItem item : orderItems) {

                                final ProductReview review =
                                        productReviews == null
                                                ? null
                                                : productReviews.get(
                                                        item.getProductId());
                    %>

                        <div class="item">

                            <div class="product-name">
                                <%= item.getProductName() %>
                            </div>

                            <div class="seller">
                                Sold by:
                                <%= item.getSellerName() == null
                                        ? "-"
                                        : item.getSellerName() %>
                            </div>

                            <div class="quantity">
                                Quantity:
                                <%= item.getQuantity() %>
                            </div>

                            <div class="price">
                                Unit Price:
                                ₹<%= String.format(
                                        "%.2f",
                                        item.getProductPrice()) %>
                            </div>

                            <div class="price">
                                Subtotal:
                                ₹<%= String.format(
                                        "%.2f",
                                        item.getSubtotal()) %>
                            </div>


                            <% if ("DELIVERED".equals(orderStatus)) { %>

                                <!-- REVIEW & RATING -->

                                <div class="review-section">

                                    <div class="review-title">
                                        Review & Rating
                                    </div>

                                    <% if (review != null) { %>

                                        <div class="review-stars">

                                            <%
                                                for (int star = 1;
                                                     star <= 5;
                                                     star++) {
                                                    if (star <=
                                                            review.getRating()) {
                                            %>
                                                ★
                                            <%
                                                    } else {
                                            %>
                                                ☆
                                            <%
                                                    }
                                                }
                                            %>

                                        </div>

                                        <div class="review-text">
                                            <%= review.getReviewText() %>
                                        </div>

                                        <% if (review.getUpdatedAt() != null) { %>

                                            <div class="review-date">
                                                Last updated:
                                                <%= review.getUpdatedAt() %>
                                            </div>

                                        <% } %>

                                        <a class="review-button"
                                           href="<%= request.getContextPath() %>/buyer/product-review?orderId=<%= order.getId() %>&productId=<%= item.getProductId() %>">
                                            Edit Review
                                        </a>

                                    <% } else { %>

                                        <div class="review-note">
                                            You can rate and review this
                                            delivered product.
                                        </div>

                                        <br>

                                        <a class="review-button"
                                           href="<%= request.getContextPath() %>/buyer/product-review?orderId=<%= order.getId() %>&productId=<%= item.getProductId() %>">
                                            Write Review
                                        </a>

                                    <% } %>

                                </div>

                            <% } %>

                        </div>

                    <%
                            }

                        } else {
                    %>

                        <div class="empty">
                            No products found for this order.
                        </div>

                    <%
                        }
                    %>

                </div>

                <div class="total">

                    <div class="total-label">
                        Total Amount
                    </div>

                    <div class="total-value">
                        ₹<%= String.format(
                                "%.2f",
                                order.getTotalAmount()) %>
                    </div>

                </div>

            </div>


            <!-- ORDER ACTIONS -->

            <div class="card wide">

                <h2>Order Actions</h2>

                <div class="actions">

                    <% if (canCancel) { %>

                        <div class="action-box">

                            <h3>Cancel Order</h3>

                            <p>
                                Cancel this order before it is shipped.
                            </p>

                            <form class="action-form"
                                  method="post"
                                  action="<%= request.getContextPath() %>/orders/cancel">

                                <input type="hidden"
                                       name="orderId"
                                       value="<%= order.getId() %>">

                                <button class="submit cancel"
                                        type="submit">
                                    Cancel Order
                                </button>

                            </form>

                        </div>

                    <% } %>


                    <% if (canNeedHelp) { %>

                        <div class="action-box">

                            <h3>Need Help</h3>

                            <p>
                                Select the product and tell us
                                how we can help.
                            </p>

                            <form class="action-form"
                                  method="post"
                                  action="<%= request.getContextPath() %>/buyer/service-request">

                                <input type="hidden"
                                       name="orderId"
                                       value="<%= order.getId() %>">

                                <input type="hidden"
                                       name="requestType"
                                       value="NEED_HELP">

                                <select name="productId"
                                        required>

                                    <option value="">
                                        Select Product
                                    </option>

                                    <%
                                        if (orderItems != null) {
                                            for (CartItem item
                                                    : orderItems) {
                                    %>

                                        <option value="<%= item.getProductId() %>">
                                            <%= item.getProductName() %>
                                            -
                                            <%= item.getSellerName() == null
                                                    ? "Seller"
                                                    : item.getSellerName() %>
                                        </option>

                                    <%
                                            }
                                        }
                                    %>

                                </select>

                                <textarea name="reason"
                                          placeholder="Tell us your issue..."
                                          required></textarea>

                                <button class="submit"
                                        type="submit">
                                    Submit Help Request
                                </button>

                            </form>

                        </div>

                    <% } %>


                    <% if (canReturnOrReplace) { %>

                        <!-- RETURN -->

                        <div class="action-box">

                            <h3>Return Product</h3>

                            <p>
                                Select the delivered product
                                you want to return.
                            </p>

                            <form id="returnForm"
                                  class="action-form"
                                  method="post"
                                  action="<%= request.getContextPath() %>/buyer/service-request">

                                <input type="hidden"
                                       name="orderId"
                                       value="<%= order.getId() %>">

                                <input type="hidden"
                                       name="requestType"
                                       value="RETURN">

                                <select id="returnProduct"
                                        name="productId"
                                        required>

                                    <option value="">
                                        Select Product
                                    </option>

                                    <%
                                        if (orderItems != null) {
                                            for (CartItem item
                                                    : orderItems) {
                                    %>

                                        <option value="<%= item.getProductId() %>">
                                            <%= item.getProductName() %>
                                            -
                                            <%= item.getSellerName() == null
                                                    ? "Seller"
                                                    : item.getSellerName() %>
                                        </option>

                                    <%
                                            }
                                        }
                                    %>

                                </select>

                                <textarea id="returnReason"
                                          name="reason"
                                          placeholder="Enter return reason..."
                                          required></textarea>

                                <button id="returnButton"
                                        class="submit"
                                        type="submit">
                                    Submit Return Request
                                </button>

                            </form>

                            <div id="returnBlockedMessage"
                                 class="blocked-message">
                                A Return or Replacement request is already
                                active for this product.
                            </div>

                        </div>


                        <!-- REPLACEMENT -->

                        <div class="action-box">

                            <h3>Replacement Product</h3>

                            <p>
                                Select the delivered product
                                you want to replace.
                            </p>

                            <form id="replacementForm"
                                  class="action-form"
                                  method="post"
                                  action="<%= request.getContextPath() %>/buyer/service-request">

                                <input type="hidden"
                                       name="orderId"
                                       value="<%= order.getId() %>">

                                <input type="hidden"
                                       name="requestType"
                                       value="REPLACEMENT">

                                <select id="replacementProduct"
                                        name="productId"
                                        required>

                                    <option value="">
                                        Select Product
                                    </option>

                                    <%
                                        if (orderItems != null) {
                                            for (CartItem item
                                                    : orderItems) {
                                    %>

                                        <option value="<%= item.getProductId() %>">
                                            <%= item.getProductName() %>
                                            -
                                            <%= item.getSellerName() == null
                                                    ? "Seller"
                                                    : item.getSellerName() %>
                                        </option>

                                    <%
                                            }
                                        }
                                    %>

                                </select>

                                <textarea id="replacementReason"
                                          name="reason"
                                          placeholder="Enter replacement reason..."
                                          required></textarea>

                                <button id="replacementButton"
                                        class="submit"
                                        type="submit">
                                    Submit Replacement Request
                                </button>

                            </form>

                            <div id="replacementBlockedMessage"
                                 class="blocked-message">
                                A Return or Replacement request is already
                                active for this product.
                            </div>

                        </div>

                    <% } %>

                </div>

            </div>


            <!-- SERVICE REQUEST HISTORY -->

            <div class="card wide">

                <h2>Service Request History</h2>

                <%
                    if (serviceRequests != null
                            && !serviceRequests.isEmpty()) {
                %>

                    <div class="history">

                        <%
                            for (OrderServiceRequest serviceRequest
                                    : serviceRequests) {
                        %>

                            <div class="history-item">

                                <div class="history-top">

                                    <div class="history-type">
                                        <%= serviceRequest.getRequestType() %>
                                    </div>

                                    <div class="history-status">
                                        <%= serviceRequest.getStatus() %>
                                    </div>

                                </div>

                                <div class="history-product">
                                    Product:
                                    <%= serviceRequest.getProductName() == null
                                            ? "-"
                                            : serviceRequest.getProductName() %>
                                </div>

                                <div class="history-reason">
                                    <%= serviceRequest.getReason() %>
                                </div>

                            </div>

                        <%
                            }
                        %>

                    </div>

                <%
                    } else {
                %>

                    <div class="empty">
                        No service requests for this order.
                    </div>

                <%
                    }
                %>

            </div>

        </div>

    <% } else { %>

        <div class="card">

            <div class="empty">
                Order details could not be loaded.
            </div>

        </div>

    <% } %>

</div>


<script>

    const activeProducts = new Set();

    <% if (serviceRequests != null) {

        for (OrderServiceRequest serviceRequest
                : serviceRequests) {

            final String requestType =
                    serviceRequest.getRequestType();

            final String requestStatus =
                    serviceRequest.getStatus();

            if (("RETURN".equals(requestType)
                    || "REPLACEMENT".equals(requestType))
                    && ("REQUESTED".equals(requestStatus)
                    || "APPROVED".equals(requestStatus))) {
    %>

        activeProducts.add(
            "<%= serviceRequest.getProductId() %>"
        );

    <%
            }
        }
    } %>


    function updateReturnReplacementState() {

        const returnProduct =
            document.getElementById("returnProduct");

        const replacementProduct =
            document.getElementById("replacementProduct");

        const returnButton =
            document.getElementById("returnButton");

        const replacementButton =
            document.getElementById(
                "replacementButton"
            );

        const returnReason =
            document.getElementById("returnReason");

        const replacementReason =
            document.getElementById(
                "replacementReason"
            );

        const returnBlockedMessage =
            document.getElementById(
                "returnBlockedMessage"
            );

        const replacementBlockedMessage =
            document.getElementById(
                "replacementBlockedMessage"
            );

        if (!returnProduct
                || !replacementProduct
                || !returnButton
                || !replacementButton) {
            return;
        }

        const returnActive =
            activeProducts.has(returnProduct.value);

        const replacementActive =
            activeProducts.has(
                replacementProduct.value
            );

        returnButton.disabled = returnActive;
        returnProduct.disabled = returnActive;

        if (returnReason) {
            returnReason.disabled = returnActive;
        }

        replacementButton.disabled =
                replacementActive;

        replacementProduct.disabled =
                replacementActive;

        if (replacementReason) {
            replacementReason.disabled =
                    replacementActive;
        }

        if (returnBlockedMessage) {
            returnBlockedMessage.style.display =
                    returnActive ? "block" : "none";
        }

        if (replacementBlockedMessage) {
            replacementBlockedMessage.style.display =
                    replacementActive ? "block" : "none";
        }
    }


    const returnProduct =
            document.getElementById("returnProduct");

    const replacementProduct =
            document.getElementById(
                    "replacementProduct"
            );


    if (returnProduct) {

        returnProduct.addEventListener(
                "change",
                updateReturnReplacementState
        );

    }


    if (replacementProduct) {

        replacementProduct.addEventListener(
                "change",
                updateReturnReplacementState
        );

    }


    updateReturnReplacementState();

</script>

</body>

</html>
