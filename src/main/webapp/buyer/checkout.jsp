<%@ page contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>

<%@ page import="com.vrmart.model.CartItem" %>
<%@ page import="com.vrmart.model.User" %>
<%@ page import="java.math.BigDecimal" %>
<%@ page import="java.util.List" %>

<%
    final User user =
            (User) session.getAttribute("user");

    if (user == null
            || !User.ROLE_BUYER.equals(user.getRole())) {

        response.sendRedirect(
                request.getContextPath() + "/login");
        return;
    }

    final List<CartItem> cartItems =
            (List<CartItem>) request.getAttribute("cartItems");

    BigDecimal total = BigDecimal.ZERO;

    if (cartItems != null) {
        for (CartItem item : cartItems) {
            total = total.add(item.getSubtotal());
        }
    }

    final String error =
            (String) request.getAttribute("error");
%>

<!DOCTYPE html>
<html lang="en">

<head>

    <meta charset="UTF-8">

    <meta name="viewport"
          content="width=device-width, initial-scale=1.0">

    <title>VR Mart | Checkout</title>

    <style>

        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
        }

        body {
            min-height: 100vh;
            font-family: Arial, Helvetica, sans-serif;
            background: #080b14;
            color: #ffffff;
        }

        .page {
            max-width: 1100px;
            margin: 0 auto;
            padding: 35px;
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
            width: 44px;
            height: 44px;
            display: flex;
            align-items: center;
            justify-content: center;
            border-radius: 13px;
            font-size: 20px;
            font-weight: 800;
            background: linear-gradient(
                135deg,
                #6366f1,
                #8b5cf6
            );
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
            margin-bottom: 28px;
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
            font-size: 34px;
        }

        .layout {
            display: grid;
            grid-template-columns: 1.5fr 1fr;
            gap: 22px;
        }

        .card {
            padding: 24px;
            border-radius: 18px;
            background: rgba(255, 255, 255, 0.035);
            border: 1px solid rgba(255, 255, 255, 0.07);
        }

        .card h2 {
            margin-bottom: 20px;
            font-size: 18px;
        }

        .field {
            margin-bottom: 18px;
        }

        .field label {
            display: block;
            margin-bottom: 8px;
            color: #aeb6c7;
            font-size: 12px;
            font-weight: 700;
        }

        .field input,
        .field textarea,
        .field select {
            width: 100%;
            padding: 13px 14px;
            border: 1px solid rgba(255, 255, 255, 0.1);
            border-radius: 10px;
            outline: none;
            background: #111522;
            color: #ffffff;
            font-family: inherit;
            font-size: 13px;
        }

        .field textarea {
            min-height: 100px;
            resize: vertical;
        }

        .field input:focus,
        .field textarea:focus,
        .field select:focus {
            border-color: #6366f1;
        }

        .error {
            margin-bottom: 18px;
            padding: 12px 14px;
            border-radius: 10px;
            background: rgba(239, 68, 68, 0.12);
            border: 1px solid rgba(239, 68, 68, 0.25);
            color: #fca5a5;
            font-size: 12px;
        }

        .payment-note {
            margin-top: -8px;
            margin-bottom: 18px;
            color: #687386;
            font-size: 10px;
            line-height: 1.5;
        }

        .order-items {
            display: grid;
            gap: 12px;
        }

        .order-item {
            display: flex;
            align-items: center;
            justify-content: space-between;
            gap: 15px;
            padding-bottom: 12px;
            border-bottom: 1px solid rgba(255, 255, 255, 0.06);
        }

        .order-item:last-child {
            border-bottom: none;
        }

        .item-name {
            font-size: 13px;
            font-weight: 700;
        }

        .item-quantity {
            margin-top: 4px;
            color: #687386;
            font-size: 10px;
        }

        .item-price {
            font-size: 13px;
            font-weight: 700;
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
            color: #7f899a;
            font-size: 13px;
        }

        .total {
            font-size: 23px;
            font-weight: 800;
        }

        .place-order {
            width: 100%;
            margin-top: 20px;
            padding: 14px 20px;
            border: none;
            border-radius: 10px;
            background: #6366f1;
            color: #ffffff;
            font-size: 13px;
            font-weight: 700;
            cursor: pointer;
            transition: 0.2s ease;
        }

        .place-order:hover {
            background: #4f46e5;
            transform: translateY(-1px);
        }

        .secure {
            margin-top: 12px;
            text-align: center;
            color: #687386;
            font-size: 10px;
        }

        @media (max-width: 750px) {

            .page {
                padding: 20px;
            }

            .layout {
                grid-template-columns: 1fr;
            }

            .heading h1 {
                font-size: 28px;
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
           href="<%= request.getContextPath() %>/buyer/cart">
            ← Cart
        </a>

    </header>

    <section class="heading">

        <small>Secure Checkout</small>

        <h1>Complete Your Order</h1>

    </section>

    <form method="post"
          action="<%= request.getContextPath() %>/buyer/checkout">

        <div class="layout">

            <section class="card">

                <h2>Delivery Details</h2>

                <% if (error != null && !error.isBlank()) { %>

                    <div class="error">
                        <%= error %>
                    </div>

                <% } %>

                <div class="field">

                    <label for="address">
                        Delivery Address
                    </label>

                    <textarea
                        id="address"
                        name="address"
                        placeholder="Enter your complete delivery address"
                        required></textarea>

                </div>

                <div class="field">

                    <label for="landmark">
                        Landmark
                    </label>

                    <input
                        type="text"
                        id="landmark"
                        name="landmark"
                        placeholder="Nearby landmark (optional)">

                </div>

                <div class="field">

                    <label for="paymentMethod">
                        Payment Method
                    </label>

                    <select
                        id="paymentMethod"
                        name="paymentMethod"
                        required>

                        <option value="">
                            Select payment method
                        </option>

                        <option value="COD">
                            Cash on Delivery
                        </option>

                        <option value="UPI">
                            UPI
                        </option>

                        <option value="CARD">
                            Card
                        </option>

                    </select>

                </div>

                <div class="payment-note">
                    Payment is simulated for this project.
                    No real payment will be processed.
                </div>

                <button
                    type="submit"
                    class="place-order">
                    Place Order
                </button>

                <div class="secure">
                    🔒 Your order is securely processed by VR Mart.
                </div>

            </section>

            <section class="card">

                <h2>Order Summary</h2>

                <div class="order-items">

                    <% if (cartItems != null) {
                        for (CartItem item : cartItems) { %>

                        <div class="order-item">

                            <div>

                                <div class="item-name">
                                    <%= item.getProductName() %>
                                </div>

                                <div class="item-quantity">
                                    Qty: <%= item.getQuantity() %>
                                </div>

                            </div>

                            <div class="item-price">
                                ₹<%= item.getSubtotal() %>
                            </div>

                        </div>

                    <% }
                    } %>

                </div>

                <div class="total-row">

                    <span class="total-label">
                        Total
                    </span>

                    <span class="total">
                        ₹<%= total %>
                    </span>

                </div>

            </section>

        </div>

    </form>

</main>

</body>

</html>
