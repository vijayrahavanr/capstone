<%@ page contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>

<%@ page import="com.vrmart.model.Order" %>
<%@ page import="com.vrmart.model.User" %>
<%@ page import="java.time.format.DateTimeFormatter" %>
<%@ page import="java.util.List" %>

<%
    final User user =
            (User) session.getAttribute("user");

    if (user == null
            || !User.ROLE_BUYER.equals(
                    user.getRole())) {

        response.sendRedirect(
                request.getContextPath()
                        + "/buyer/login");
        return;
    }

    final List<Order> orders =
            (List<Order>)
                    request.getAttribute("orders");

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

    <title>My Orders - VR Mart</title>

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
            width: min(1100px, 92%);
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

        h1 {
            margin: 0;
            font-size: 32px;
        }

        .subtitle {
            margin-top: 8px;
            color: #94a3b8;
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

        .order-list {
            display: grid;
            gap: 18px;
        }

        .order-card {
            background: #111827;
            border: 1px solid #263244;
            border-radius: 18px;
            padding: 24px;
        }

        .header {
            display: flex;
            justify-content: space-between;
            gap: 20px;
            padding-bottom: 18px;
            border-bottom: 1px solid #263244;
        }

        .order-id {
            font-size: 20px;
            font-weight: bold;
        }

        .date {
            margin-top: 6px;
            color: #94a3b8;
            font-size: 13px;
        }

        .status {
            height: fit-content;
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
                repeat(2, 1fr);
            gap: 15px;
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
            font-size: 11px;
            text-transform: uppercase;
            margin-bottom: 7px;
        }

        .value {
            font-size: 14px;
            font-weight: 600;
            word-break: break-word;
        }

        .amount {
            color: #86efac;
            font-size: 18px;
        }

        .wide {
            grid-column: span 2;
        }

        .empty {
            text-align: center;
            padding: 70px 20px;
            background: #111827;
            border: 1px solid #263244;
            border-radius: 18px;
        }

        .empty p {
            color: #94a3b8;
            margin: 10px 0 25px;
        }

        @media (max-width: 650px) {

            .topbar,
            .header {
                flex-direction: column;
            }

            .details {
                grid-template-columns: 1fr;
            }

            .wide {
                grid-column: span 1;
            }
        }

    </style>

</head>

<body>

<div class="container">

    <div class="topbar">

        <div>
            <h1>My Orders</h1>

            <p class="subtitle">
                Track your VR Mart orders
            </p>
        </div>

        <a class="button"
           href="<%= request.getContextPath() %>/products">
            Continue Shopping
        </a>

    </div>

    <%
        if (orders == null || orders.isEmpty()) {
    %>

        <div class="empty">

            <h2>No Orders Yet</h2>

            <p>
                You haven't placed any orders yet.
            </p>

            <a class="button"
               href="<%= request.getContextPath() %>/products">
                Start Shopping
            </a>

        </div>

    <%
        } else {
    %>

        <div class="order-list">

            <%
                for (Order order : orders) {
            %>

                <div class="order-card">

                    <div class="header">

                        <div>

                            <div class="order-id">
                                Order #<%= order.getId() %>
                            </div>

                            <div class="date">

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
                                Order Total
                            </span>

                            <span class="value amount">
                                &#8377;<%= order.getTotalAmount() %>
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
