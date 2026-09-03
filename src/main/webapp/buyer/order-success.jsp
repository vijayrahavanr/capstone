<%@ page contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>

<%@ page import="com.vrmart.model.Order" %>
<%@ page import="com.vrmart.model.User" %>
<%@ page import="java.time.LocalDateTime" %>
<%@ page import="java.time.format.DateTimeFormatter" %>

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

    final Order order =
            (Order) session.getAttribute("lastOrder");

    if (order == null) {
        response.sendRedirect(
                request.getContextPath()
                        + "/products");
        return;
    }

    final DateTimeFormatter formatter =
            DateTimeFormatter.ofPattern(
                    "dd MMM yyyy, hh:mm a");

    final LocalDateTime createdAt =
            order.getCreatedAt() == null
                    ? LocalDateTime.now()
                    : order.getCreatedAt();

    final LocalDateTime estimatedDelivery =
            createdAt.plusDays(5);
%>

<!DOCTYPE html>
<html lang="en">

<head>

    <meta charset="UTF-8">

    <meta name="viewport"
          content="width=device-width, initial-scale=1.0">

    <title>Order Successful - VR Mart</title>

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
        }

        .page {
            width: min(1000px, 92%);
            margin: 0 auto;
            padding: 45px 0;
        }

        .success-card {
            background: #11141d;
            border: 1px solid #272d3a;
            border-radius: 22px;
            padding: 45px 38px;
            text-align: center;
        }

        .check {
            width: 82px;
            height: 82px;
            margin: 0 auto 28px;
            border-radius: 50%;
            display: flex;
            align-items: center;
            justify-content: center;
            font-size: 45px;
            color: #6ee7a0;
            background: #0d3022;
            border: 1px solid #176b47;
        }

        .brand {
            color: #818cf8;
            font-size: 14px;
            font-weight: 800;
            letter-spacing: 2px;
            margin-bottom: 14px;
        }

        h1 {
            margin: 0;
            font-size: 36px;
        }

        .subtitle {
            margin: 12px 0 35px;
            color: #8290a6;
            font-size: 15px;
        }

        .details {
            display: grid;
            grid-template-columns:
                repeat(2, minmax(0, 1fr));
            gap: 18px;
            text-align: left;
        }

        .detail {
            padding: 20px;
            border-radius: 15px;
            background: #0f1420;
            border: 1px solid #202838;
        }

        .label {
            display: block;
            color: #66748c;
            font-size: 12px;
            text-transform: uppercase;
            letter-spacing: 0.7px;
            margin-bottom: 9px;
        }

        .value {
            font-size: 16px;
            font-weight: 700;
            word-break: break-word;
        }

        .amount {
            color: #818cf8;
            font-size: 22px;
        }

        .status {
            color: #93c5fd;
        }

        .wide {
            grid-column: span 2;
        }

        .delivery-note {
            margin-top: 18px;
            padding: 20px;
            border-radius: 15px;
            text-align: left;
            background: #15182b;
            border: 1px solid #333876;
        }

        .delivery-note .label {
            color: #818cf8;
        }

        .delivery-note strong {
            font-size: 16px;
        }

        .buttons {
            display: flex;
            justify-content: center;
            gap: 12px;
            margin-top: 32px;
        }

        .button {
            display: inline-block;
            padding: 13px 22px;
            border-radius: 10px;
            text-decoration: none;
            font-size: 14px;
            font-weight: 700;
            background: #4f46e5;
            color: #ffffff;
        }

        .button:hover {
            background: #6366f1;
        }

        .secondary {
            background: #1e293b;
            border: 1px solid #334155;
        }

        .secondary:hover {
            background: #334155;
        }

        @media (max-width: 650px) {

            .success-card {
                padding: 35px 20px;
            }

            h1 {
                font-size: 28px;
            }

            .details {
                grid-template-columns: 1fr;
            }

            .wide {
                grid-column: span 1;
            }

            .buttons {
                flex-direction: column;
            }

            .button {
                text-align: center;
            }
        }

    </style>

</head>

<body>

<div class="page">

    <div class="success-card">

        <div class="check">
            ✓
        </div>

        <div class="brand">
            VR MART
        </div>

        <h1>
            Order Placed Successfully!
        </h1>

        <p class="subtitle">
            Thank you for shopping with VR Mart.
            Your order has been confirmed.
        </p>

        <div class="details">

            <!-- ORDER ID -->

            <div class="detail">

                <span class="label">
                    Order ID
                </span>

                <span class="value">
                    #<%= order.getId() %>
                </span>

            </div>

            <!-- STATUS -->

            <div class="detail">

                <span class="label">
                    Order Status
                </span>

                <span class="value status">
                    <%= order.getStatus() %>
                </span>

            </div>

            <!-- TOTAL -->

            <div class="detail">

                <span class="label">
                    Total Amount
                </span>

                <span class="value amount">
                    &#8377;<%= order.getTotalAmount() %>
                </span>

            </div>

            <!-- PAYMENT -->

            <div class="detail">

                <span class="label">
                    Payment
                </span>

                <span class="value">
                    <%= order.getPaymentMethod() == null
                            ? "Not available"
                            : order.getPaymentMethod() %>
                </span>

            </div>

            <!-- ADDRESS -->

            <div class="detail wide">

                <span class="label">
                    Delivery Address
                </span>

                <span class="value">
                    <%= order.getDeliveryAddress() == null
                            || order.getDeliveryAddress()
                                .isEmpty()
                            ? "Address not available"
                            : order.getDeliveryAddress() %>
                </span>

            </div>

            <!-- LANDMARK -->

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

            <!-- ESTIMATED DELIVERY -->

            <div class="detail wide">

                <span class="label">
                    Estimated Delivery
                </span>

                <span class="value">
                    <%= estimatedDelivery.format(formatter) %>
                </span>

            </div>

        </div>

        <div class="delivery-note">

            <span class="label">
                Delivery Update
            </span>

            <strong>
                Your order will be delivered within
                3–5 business days.
            </strong>

        </div>

        <div class="buttons">

            <a class="button"
               href="<%= request.getContextPath() %>/products">
                Continue Shopping
            </a>

            <a class="button secondary"
               href="<%= request.getContextPath() %>/buyer/orders">
                View My Orders
            </a>

        </div>

    </div>

</div>

</body>

</html>
