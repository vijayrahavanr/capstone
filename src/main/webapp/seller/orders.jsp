<%@ page contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>

<%@ page import="com.vrmart.model.SellerOrderItem" %>
<%@ page import="com.vrmart.model.User" %>
<%@ page import="java.time.format.DateTimeFormatter" %>
<%@ page import="java.util.List" %>

<%
    final User user =
            (User) session.getAttribute("user");

    if (user == null
            || !User.ROLE_SELLER.equals(
                    user.getRole())) {

        response.sendRedirect(
                request.getContextPath()
                        + "/seller/login");
        return;
    }

    final List<SellerOrderItem> orders =
            (List<SellerOrderItem>)
                    request.getAttribute(
                            "sellerOrders");

    final DateTimeFormatter formatter =
            DateTimeFormatter.ofPattern(
                    "dd MMM yyyy, hh:mm a");
%>

<!DOCTYPE html>
<html lang="en">

<head>

    <meta charset="UTF-8">

    <meta name="viewport"
          content="width=device-width, initial-scale=1.0">

    <title>Incoming Orders - VR Mart</title>

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
            color: #f8fafc;
        }

        .container {
            width: min(1200px, 92%);
            margin: 0 auto;
            padding: 42px 0;
        }

        .topbar {
            display: flex;
            justify-content: space-between;
            align-items: center;
            gap: 20px;
            margin-bottom: 35px;
        }

        .title-section h1 {
            margin: 0;
            font-size: 32px;
        }

        .title-section p {
            margin: 8px 0 0;
            color: #94a3b8;
        }

        .actions {
            display: flex;
            gap: 10px;
        }

        .button {
            display: inline-block;
            padding: 11px 18px;
            border-radius: 9px;
            text-decoration: none;
            font-size: 14px;
            font-weight: bold;
            background: #1e293b;
            color: #f8fafc;
            border: 1px solid #334155;
        }

        .button:hover {
            background: #334155;
        }

        .empty {
            text-align: center;
            padding: 70px 20px;
            border: 1px solid #263244;
            border-radius: 18px;
            background: #111827;
        }

        .empty h2 {
            margin-bottom: 10px;
        }

        .empty p {
            color: #94a3b8;
            margin-bottom: 25px;
        }

        .order-list {
            display: grid;
            gap: 18px;
        }

        .order-card {
            background: #111827;
            border: 1px solid #263244;
            border-radius: 18px;
            padding: 24px;
            box-shadow:
                0 12px 30px rgba(0, 0, 0, 0.20);
        }

        .order-header {
            display: flex;
            justify-content: space-between;
            align-items: flex-start;
            gap: 20px;
            padding-bottom: 18px;
            border-bottom: 1px solid #263244;
        }

        .order-id {
            font-size: 20px;
            font-weight: bold;
        }

        .order-date {
            color: #94a3b8;
            font-size: 13px;
            margin-top: 6px;
        }

        .status {
            display: inline-block;
            padding: 7px 12px;
            border-radius: 20px;
            background: #172554;
            color: #93c5fd;
            font-size: 12px;
            font-weight: bold;
        }

        .details {
            display: grid;
            grid-template-columns:
                repeat(4, 1fr);
            gap: 18px;
            margin-top: 20px;
        }

        .detail {
            background: #0f172a;
            border-radius: 12px;
            padding: 15px;
        }

        .label {
            display: block;
            color: #64748b;
            font-size: 12px;
            margin-bottom: 7px;
            text-transform: uppercase;
            letter-spacing: 0.5px;
        }

        .value {
            font-size: 15px;
            font-weight: 600;
            word-break: break-word;
        }

        .amount {
            color: #86efac;
        }

        .wide {
            grid-column: span 2;
        }

        .status-form {
            display: flex;
            gap: 8px;
            align-items: center;
        }

        .status-select {
            padding: 9px 12px;
            border-radius: 8px;
            border: 1px solid #334155;
            background: #0f172a;
            color: #f8fafc;
            font-size: 13px;
        }

        .update-button {
            padding: 9px 13px;
            border: 0;
            border-radius: 8px;
            background: #4f46e5;
            color: #ffffff;
            font-size: 13px;
            font-weight: bold;
            cursor: pointer;
        }

        .update-button:hover {
            background: #6366f1;
        }

        @media (max-width: 800px) {

            .topbar,
            .order-header {
                flex-direction: column;
            }

            .details {
                grid-template-columns:
                    repeat(2, 1fr);
            }

            .wide {
                grid-column: span 2;
            }
        }

        @media (max-width: 500px) {

            .details {
                grid-template-columns: 1fr;
            }

            .wide {
                grid-column: span 1;
            }

            .actions {
                width: 100%;
            }

            .button {
                flex: 1;
                text-align: center;
            }

            .status-form {
                width: 100%;
            }

            .status-select {
                flex: 1;
            }
        }

    </style>

</head>

<body>

<div class="container">

    <div class="topbar">

        <div class="title-section">

            <h1>Incoming Orders</h1>

            <p>
                Orders containing your products
            </p>

        </div>

        <div class="actions">

            <a class="button"
               href="<%= request.getContextPath() %>/seller/dashboard.jsp">
                Dashboard
            </a>

            <a class="button"
               href="<%= request.getContextPath() %>/seller/products">
                My Products
            </a>

        </div>

    </div>

    <%
        if (orders == null || orders.isEmpty()) {
    %>

        <div class="empty">

            <h2>No Incoming Orders</h2>

            <p>
                You haven't received any orders for
                your products yet.
            </p>

            <a class="button"
               href="<%= request.getContextPath() %>/seller/products">
                View My Products
            </a>

        </div>

    <%
        } else {
    %>

        <div class="order-list">

            <%
                for (SellerOrderItem order : orders) {
            %>

                <div class="order-card">

                    <div class="order-header">

                        <div>

                            <div class="order-id">
                                Order #<%= order.getOrderId() %>
                            </div>

                            <div class="order-date">
                                <%= order.getCreatedAt() == null
                                        ? "Date unavailable"
                                        : order.getCreatedAt()
                                            .format(formatter) %>
                            </div>

                        </div>

                        <span class="status">
                            <%= order.getStatus() %>
                        </span>

                    </div>

                    <div class="details">

                        <div class="detail">

                            <span class="label">
                                Buyer ID
                            </span>

                            <span class="value">
                                #<%= order.getBuyerId() %>
                            </span>

                        </div>

                        <div class="detail">

                            <span class="label">
                                Product
                            </span>

                            <span class="value">
                                <%= order.getProductName() %>
                            </span>

                        </div>

                        <div class="detail">

                            <span class="label">
                                Quantity
                            </span>

                            <span class="value">
                                <%= order.getQuantity() %>
                            </span>

                        </div>

                        <div class="detail">

                            <span class="label">
                                Unit Price
                            </span>

                            <span class="value">
                                &#8377;<%= order.getUnitPrice() %>
                            </span>

                        </div>

                        <div class="detail">

                            <span class="label">
                                Order Value
                            </span>

                            <span class="value amount">
                                &#8377;<%= order.getLineTotal() %>
                            </span>

                        </div>

                        <div class="detail">

                            <span class="label">
                                Payment
                            </span>

                            <span class="value">
                                <%= order.getPaymentMethod() %>
                            </span>

                        </div>

                        <div class="detail wide">

                            <span class="label">
                                Delivery Address
                            </span>

                            <span class="value">
                                <%= order.getDeliveryAddress() %>
                            </span>

                        </div>

                        <div class="detail wide">

                            <span class="label">
                                Landmark
                            </span>

                            <span class="value">
                                <%= order.getDeliveryLandmark() == null
                                        || order.getDeliveryLandmark()
                                            .isEmpty()
                                        ? "Not provided"
                                        : order.getDeliveryLandmark() %>
                            </span>

                        </div>

                        <div class="detail wide">

                            <span class="label">
                                Update Status
                            </span>

                            <form class="status-form"
                                  method="post"
                                  action="<%= request.getContextPath() %>/seller/orders/status">

                                <input type="hidden"
                                       name="orderId"
                                       value="<%= order.getOrderId() %>">

                                <select class="status-select"
                                        name="status">

                                    <option value="PENDING"
                                            <%= "PENDING".equals(
                                                    order.getStatus())
                                                    ? "selected" : "" %>>
                                        PENDING
                                    </option>

                                    <option value="CONFIRMED"
                                            <%= "CONFIRMED".equals(
                                                    order.getStatus())
                                                    ? "selected" : "" %>>
                                        CONFIRMED
                                    </option>

                                    <option value="PROCESSING"
                                            <%= "PROCESSING".equals(
                                                    order.getStatus())
                                                    ? "selected" : "" %>>
                                        PROCESSING
                                    </option>

                                    <option value="SHIPPED"
                                            <%= "SHIPPED".equals(
                                                    order.getStatus())
                                                    ? "selected" : "" %>>
                                        SHIPPED
                                    </option>

                                    <option value="DELIVERED"
                                            <%= "DELIVERED".equals(
                                                    order.getStatus())
                                                    ? "selected" : "" %>>
                                        DELIVERED
                                    </option>

                                </select>

                                <button class="update-button"
                                        type="submit">
                                    Update
                                </button>

                            </form>

                        </div>

                    </div>

                </div>

            <%
                }
            %>

        </div>

    <%
        }
    %>

</div>

</body>

</html>
