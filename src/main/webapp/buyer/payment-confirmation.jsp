<%@ page contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>
<%@ page import="com.vrmart.model.CartItem" %>
<%@ page import="java.math.BigDecimal" %>
<%@ page import="java.util.List" %>

<%
    final List<CartItem> cartItems =
            (List<CartItem>) request.getAttribute("cartItems");

    final String customerName =
            (String) request.getAttribute("customerName");

    final String customerPhone =
            (String) request.getAttribute("customerPhone");

    final String address =
            (String) request.getAttribute("address");

    final String landmark =
            (String) request.getAttribute("landmark");

    final String paymentMethod =
            (String) request.getAttribute("paymentMethod");

    BigDecimal total = BigDecimal.ZERO;

    if (cartItems != null) {
        for (CartItem item : cartItems) {
            total = total.add(item.getSubtotal());
        }
    }

    String paymentLabel = paymentMethod;

    if ("COD".equals(paymentMethod)) {
        paymentLabel = "Cash on Delivery";
    } else if ("UPI".equals(paymentMethod)) {
        paymentLabel = "UPI";
    } else if ("CARD".equals(paymentMethod)) {
        paymentLabel = "Card";
    }
%>

<!DOCTYPE html>
<html lang="en">

<head>

    <meta charset="UTF-8">

    <meta name="viewport"
          content="width=device-width, initial-scale=1.0">

    <title>Confirm Payment | VR Mart</title>

    <style>

        * {
            box-sizing: border-box;
            margin: 0;
            padding: 0;
        }

        body {
            min-height: 100vh;
            font-family: Arial, Helvetica, sans-serif;
            background:
                radial-gradient(
                    circle at 15% 20%,
                    rgba(99, 102, 241, 0.20),
                    transparent 30%
                ),
                radial-gradient(
                    circle at 85% 80%,
                    rgba(168, 85, 247, 0.16),
                    transparent 30%
                ),
                #080b14;
            color: #ffffff;
        }

        .page {
            width: 100%;
            max-width: 1050px;
            margin: 0 auto;
            padding: 35px 20px;
        }

        .topbar {
            display: flex;
            align-items: center;
            justify-content: space-between;
            margin-bottom: 35px;
        }

        .brand {
            display: flex;
            align-items: center;
            gap: 12px;
        }

        .logo {
            width: 45px;
            height: 45px;
            display: flex;
            align-items: center;
            justify-content: center;
            border-radius: 13px;
            background: linear-gradient(
                135deg,
                #6366f1,
                #8b5cf6
            );
            font-size: 20px;
            font-weight: 800;
        }

        .brand-name {
            font-size: 20px;
            font-weight: 800;
        }

        .brand-name span {
            color: #818cf8;
        }

        .back {
            color: #a5b4fc;
            text-decoration: none;
            font-size: 13px;
        }

        .heading {
            margin-bottom: 25px;
        }

        .heading small {
            color: #818cf8;
            font-size: 11px;
            font-weight: 700;
            letter-spacing: 1.5px;
            text-transform: uppercase;
        }

        .heading h1 {
            margin-top: 8px;
            font-size: 32px;
        }

        .heading p {
            margin-top: 8px;
            color: #7f899a;
            font-size: 13px;
        }

        .layout {
            display: grid;
            grid-template-columns: 1.35fr 1fr;
            gap: 20px;
        }

        .card {
            padding: 25px;
            border-radius: 18px;
            background: rgba(17, 20, 31, 0.88);
            border: 1px solid rgba(255, 255, 255, 0.08);
            box-shadow: 0 25px 60px rgba(0, 0, 0, 0.25);
        }

        .card h2 {
            margin-bottom: 20px;
            font-size: 18px;
        }

        .detail {
            margin-bottom: 17px;
        }

        .detail:last-child {
            margin-bottom: 0;
        }

        .label {
            margin-bottom: 6px;
            color: #727d90;
            font-size: 10px;
            font-weight: 700;
            text-transform: uppercase;
            letter-spacing: 0.7px;
        }

        .value {
            color: #e5e7eb;
            font-size: 13px;
            line-height: 1.5;
            word-break: break-word;
        }

        .payment-box {
            margin-top: 22px;
            padding: 15px;
            border-radius: 12px;
            background: rgba(99, 102, 241, 0.09);
            border: 1px solid rgba(129, 140, 248, 0.18);
        }

        .payment-box .value {
            color: #c7d2fe;
            font-weight: 700;
        }

        .item-list {
            display: grid;
            gap: 12px;
        }

        .item {
            display: flex;
            align-items: center;
            justify-content: space-between;
            gap: 15px;
            padding-bottom: 12px;
            border-bottom: 1px solid rgba(255, 255, 255, 0.06);
        }

        .item:last-child {
            border-bottom: none;
        }

        .item-name {
            color: #e5e7eb;
            font-size: 13px;
            font-weight: 700;
        }

        .item-qty {
            margin-top: 4px;
            color: #687386;
            font-size: 10px;
        }

        .item-price {
            color: #ffffff;
            font-size: 13px;
            font-weight: 700;
            white-space: nowrap;
        }

        .total-row {
            display: flex;
            align-items: center;
            justify-content: space-between;
            margin-top: 20px;
            padding-top: 18px;
            border-top: 1px solid rgba(255, 255, 255, 0.08);
        }

        .total-label {
            color: #8992a3;
            font-size: 13px;
        }

        .total {
            font-size: 24px;
            font-weight: 800;
        }

        .mock-note {
            margin-top: 18px;
            padding: 13px;
            border-radius: 10px;
            background: rgba(34, 197, 94, 0.07);
            border: 1px solid rgba(34, 197, 94, 0.14);
            color: #a7f3d0;
            font-size: 11px;
            line-height: 1.5;
        }

        .confirm {
            width: 100%;
            margin-top: 20px;
            padding: 14px 18px;
            border: none;
            border-radius: 11px;
            background: linear-gradient(
                135deg,
                #6366f1,
                #8b5cf6
            );
            color: #ffffff;
            font-size: 13px;
            font-weight: 700;
            cursor: pointer;
        }

        .confirm:hover {
            opacity: 0.92;
        }

        .cancel {
            display: block;
            margin-top: 13px;
            text-align: center;
            color: #7f899a;
            font-size: 11px;
            text-decoration: none;
        }

        .cancel:hover {
            color: #a5b4fc;
        }

        .secure {
            margin-top: 14px;
            color: #5f6879;
            text-align: center;
            font-size: 10px;
        }

        @media (max-width: 750px) {

            .page {
                padding: 25px 16px;
            }

            .layout {
                grid-template-columns: 1fr;
            }

            .heading h1 {
                font-size: 27px;
            }

        }

    </style>

</head>

<body>

<main class="page">

    <header class="topbar">

        <div class="brand">

            <div class="logo">
                V
            </div>

            <div class="brand-name">
                VR <span>Mart</span>
            </div>

        </div>

        <a class="back"
           href="<%= request.getContextPath() %>/buyer/checkout">
            ← Back to Checkout
        </a>

    </header>

    <section class="heading">

        <small>Mock Payment</small>

        <h1>Confirm Your Order</h1>

        <p>
            Review your order details before placing the order.
        </p>

    </section>

    <div class="layout">

        <section class="card">

            <h2>Delivery Details</h2>

            <div class="detail">

                <div class="label">
                    Customer Name
                </div>

                <div class="value">
                    <%= customerName %>
                </div>

            </div>

            <div class="detail">

                <div class="label">
                    Phone Number
                </div>

                <div class="value">
                    <%= customerPhone %>
                </div>

            </div>

            <div class="detail">

                <div class="label">
                    Delivery Address
                </div>

                <div class="value">
                    <%= address %>
                </div>

            </div>

            <% if (landmark != null
                    && !landmark.isBlank()) { %>

                <div class="detail">

                    <div class="label">
                        Landmark
                    </div>

                    <div class="value">
                        <%= landmark %>
                    </div>

                </div>

            <% } %>

            <div class="payment-box">

                <div class="label">
                    Payment Method
                </div>

                <div class="value">
                    <%= paymentLabel %>
                </div>

            </div>

            <div class="mock-note">
                This is a mock payment confirmation.
                No real payment will be processed.
            </div>

        </section>

        <section class="card">

            <h2>Order Summary</h2>

            <div class="item-list">

                <% if (cartItems != null) {

                    for (CartItem item : cartItems) { %>

                        <div class="item">

                            <div>

                                <div class="item-name">
                                    <%= item.getProductName() %>
                                </div>

                                <div class="item-qty">
                                    Qty: <%= item.getQuantity() %>
                                </div>

                            </div>

                            <div class="item-price">
                                ₹<%= item.getSubtotal() %>
                            </div>

                        </div>

                <%  }
                } %>

            </div>

            <div class="total-row">

                <span class="total-label">
                    Total Amount
                </span>

                <span class="total">
                    ₹<%= total %>
                </span>

            </div>

            <form method="post"
                  action="<%= request.getContextPath() %>/buyer/payment-confirmation">

                <button
                    type="submit"
                    class="confirm">
                    Confirm Payment &amp; Place Order
                </button>

            </form>

            <a class="cancel"
               href="<%= request.getContextPath() %>/buyer/checkout">
                Cancel and return to checkout
            </a>

            <div class="secure">
                🔒 Secure VR Mart checkout
            </div>

        </section>

    </div>

</main>

</body>

</html>
