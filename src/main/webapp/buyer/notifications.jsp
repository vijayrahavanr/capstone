<%@ page contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>
<%@ page import="java.util.List" %>
<%@ page import="com.vrmart.model.Order" %>
<%@ page import="com.vrmart.model.OrderServiceRequest" %>
<%@ page import="com.vrmart.model.User" %>
<%@ page import="com.vrmart.model.UserSettings" %>

<%
    final User currentUser =
            (User) session.getAttribute("user");

    if (currentUser == null
            || !User.ROLE_BUYER.equals(currentUser.getRole())) {
        response.sendRedirect(
                request.getContextPath() + "/buyer/login");
        return;
    }

    final List<Order> orders =
            (List<Order>) request.getAttribute("orders");

    final List<OrderServiceRequest> serviceRequests =
            (List<OrderServiceRequest>)
                    request.getAttribute("serviceRequests");

    UserSettings currentSettings =
            (UserSettings) session.getAttribute("userSettings");

    final boolean darkMode = currentSettings == null
            || currentSettings.isDarkMode();
%>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport"
          content="width=device-width, initial-scale=1.0">

    <title>Notifications | VR Mart</title>

    <%@ include file="/includes/theme.jspf" %>

    <style>
        * {
            box-sizing: border-box;
        }

        body {
            margin: 0;
            min-height: 100vh;
            font-family: Arial, Helvetica, sans-serif;
            background: #080b14;
            color: #ffffff;
            padding: 32px 20px;
        }

        body.vrmart-light {
            background: #f4f5fb;
            color: #15182a;
        }

        .container {
            width: min(900px, 100%);
            margin: 0 auto;
        }

        .top {
            display: flex;
            align-items: center;
            justify-content: space-between;
            gap: 16px;
            margin-bottom: 28px;
        }

        h1 {
            margin: 0 0 6px;
            font-size: 30px;
        }

        .subtitle {
            margin: 0;
            color: #8f98aa;
        }

        body.vrmart-light .subtitle {
            color: #687087;
        }

        .back {
            color: #a5b4fc;
            text-decoration: none;
            font-weight: 700;
        }

        body.vrmart-light .back {
            color: #4f46b5;
        }

        .list {
            display: grid;
            gap: 14px;
        }

        .notification {
            padding: 20px;
            border-radius: 18px;
            background: #11141f;
            border: 1px solid rgba(255, 255, 255, 0.09);
        }

        body.vrmart-light .notification {
            background: #ffffff;
            border-color: rgba(20, 24, 45, 0.10);
        }

        .title {
            margin: 0 0 7px;
            font-size: 16px;
            font-weight: 700;
        }

        .message {
            margin: 0;
            color: #aab3c8;
            line-height: 1.5;
        }

        body.vrmart-light .message {
            color: #687087;
        }

        .time {
            margin-top: 10px;
            font-size: 12px;
            color: #737d92;
        }

        .empty {
            padding: 45px 20px;
            text-align: center;
            border-radius: 18px;
            background: #11141f;
            color: #8f98aa;
        }

        body.vrmart-light .empty {
            background: #ffffff;
            color: #687087;
        }

        .status {
            display: inline-block;
            margin-top: 10px;
            padding: 5px 9px;
            border-radius: 999px;
            font-size: 11px;
            font-weight: 700;
            text-transform: uppercase;
        }
    </style>
</head>

<body class="<%= darkMode ? "" : "vrmart-light" %>">

<div class="container">

    <div class="top">
        <div>
            <h1>Notifications</h1>
            <p class="subtitle">
                Order and service request updates
            </p>
        </div>

        <a
            class="back"
            href="<%= request.getContextPath() %>/buyer/dashboard.jsp">
            ← Dashboard
        </a>
    </div>

    <div class="list">

        <%
            boolean hasNotifications =
                    orders != null && !orders.isEmpty();

            if (hasNotifications) {
                for (Order order : orders) {
        %>

        <div class="notification">
            <p class="title">
                Order #<%= order.getId() %> update
            </p>

            <p class="message">
                Your order status is now
                <strong><%= order.getStatus() %></strong>.
            </p>

            <span class="status">
                <%= order.getStatus() %>
            </span>

            <div class="time">
                <%= order.getUpdatedAt() != null
                        ? order.getUpdatedAt()
                        : order.getCreatedAt() %>
            </div>
        </div>

        <%
                }
            }

            if (serviceRequests != null) {
                for (OrderServiceRequest serviceRequest
                        : serviceRequests) {
        %>

        <div class="notification">
            <p class="title">
                Service request update
            </p>

            <p class="message">
                Order #<%= serviceRequest.getOrderId() %>
                · <%= serviceRequest.getRequestType() %>
                for <%= serviceRequest.getProductName() %>
                is now
                <strong><%= serviceRequest.getStatus() %></strong>.
            </p>

            <span class="status">
                <%= serviceRequest.getStatus() %>
            </span>

            <div class="time">
                <%= serviceRequest.getUpdatedAt() != null
                        ? serviceRequest.getUpdatedAt()
                        : serviceRequest.getCreatedAt() %>
            </div>
        </div>

        <%
                }
            }

            if (!hasNotifications
                    && (serviceRequests == null
                    || serviceRequests.isEmpty())) {
        %>

        <div class="empty">
            No notifications yet.
        </div>

        <%
            }
        %>

    </div>
</div>

</body>
</html>
