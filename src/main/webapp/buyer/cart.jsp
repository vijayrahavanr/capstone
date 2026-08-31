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
%>

<!DOCTYPE html>
<html lang="en">

<head>

    <meta charset="UTF-8">

    <meta name="viewport"
          content="width=device-width, initial-scale=1.0">

    <title>VR Mart | Cart</title>

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

        .cart {
            display: grid;
            gap: 15px;
        }

        .item {
            display: grid;
            grid-template-columns: 100px 1fr auto;
            gap: 20px;
            align-items: center;
            padding: 18px;
            border-radius: 16px;
            background: rgba(255, 255, 255, 0.035);
            border: 1px solid rgba(255, 255, 255, 0.07);
        }

        .item-image {
            width: 100px;
            height: 80px;
            display: flex;
            align-items: center;
            justify-content: center;
            overflow: hidden;
            border-radius: 10px;
            background: #111522;
        }

        .item-image img {
            width: 100%;
            height: 100%;
            object-fit: cover;
        }

        .no-image {
            color: #687386;
            font-size: 11px;
        }

        .item-info h2 {
            margin-bottom: 7px;
            font-size: 16px;
        }

        .item-price {
            color: #818cf8;
            font-size: 13px;
        }

        .quantity-controls {
            display: flex;
            align-items: center;
            gap: 8px;
            margin-top: 12px;
        }

        .quantity-form {
            display: inline;
        }

        .quantity-button {
            width: 30px;
            height: 30px;
            display: flex;
            align-items: center;
            justify-content: center;
            border: 1px solid rgba(255, 255, 255, 0.1);
            border-radius: 8px;
            background: rgba(255, 255, 255, 0.05);
            color: #ffffff;
            cursor: pointer;
            font-size: 16px;
            font-weight: 700;
        }

        .quantity-button:hover {
            background: rgba(124, 92, 255, 0.18);
            border-color: rgba(167, 139, 250, 0.35);
        }

        .quantity-value {
            min-width: 30px;
            text-align: center;
            font-size: 13px;
            font-weight: 700;
        }

        .stock-text {
            margin-top: 6px;
            color: #687386;
            font-size: 10px;
        }

        .subtotal {
            margin-bottom: 8px;
            font-size: 16px;
            font-weight: 800;
            text-align: right;
        }

        .remove {
            color: #fca5a5;
            text-decoration: none;
            font-size: 11px;
        }

        .remove:hover {
            color: #fecaca;
        }

        .summary {
            margin-top: 25px;
            padding: 22px;
            border-radius: 16px;
            background: rgba(255, 255, 255, 0.035);
            border: 1px solid rgba(255, 255, 255, 0.07);
        }

        .summary-row {
            display: flex;
            justify-content: space-between;
            align-items: center;
        }

        .summary-label {
            color: #7f899a;
            font-size: 13px;
        }

        .total {
            font-size: 24px;
            font-weight: 800;
        }

        .empty {
            padding: 60px 20px;
            text-align: center;
            border-radius: 18px;
            background: rgba(255, 255, 255, 0.035);
            border: 1px solid rgba(255, 255, 255, 0.07);
        }

        .empty h2 {
            margin-bottom: 8px;
            font-size: 20px;
        }

        .empty p {
            color: #7f899a;
            font-size: 13px;
        }

        @media (max-width: 600px) {

            .page {
                padding: 20px;
            }

            .item {
                grid-template-columns: 80px 1fr;
            }

            .item-image {
                width: 80px;
                height: 70px;
            }

            .item-total {
                grid-column: 2;
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
           href="<%= request.getContextPath() %>/products">
            ← Products
        </a>

    </header>

    <section class="heading">

        <small>Buyer Cart</small>

        <h1>Your Cart</h1>

    </section>

    <% if (cartItems == null || cartItems.isEmpty()) { %>

        <section class="empty">

            <h2>Your cart is empty</h2>

            <p>
                Add products from the marketplace to continue.
            </p>

        </section>

    <% } else { %>

        <section class="cart">

            <% for (CartItem item : cartItems) { %>

                <article class="item">

                    <div class="item-image">

                        <% if (item.getImageUrl() != null
                                && !item.getImageUrl().isBlank()) { %>

                            <img
                                src="<%= item.getImageUrl() %>"
                                alt="<%= item.getProductName() %>">

                        <% } else { %>

                            <span class="no-image">
                                No image
                            </span>

                        <% } %>

                    </div>

                    <div class="item-info">

                        <h2>
                            <%= item.getProductName() %>
                        </h2>

                        <div class="item-price">
                            ₹<%= item.getProductPrice() %>
                        </div>

                        <div class="quantity-controls">

                            <form
                                class="quantity-form"
                                method="post"
                                action="<%= request.getContextPath() %>/buyer/cart/update">

                                <input
                                    type="hidden"
                                    name="productId"
                                    value="<%= item.getProductId() %>">

                                <input
                                    type="hidden"
                                    name="action"
                                    value="decrease">

                                <button
                                    class="quantity-button"
                                    type="submit"
                                    <%= item.getQuantity() <= 1
                                            ? "disabled" : "" %>>
                                    −
                                </button>

                            </form>

                            <span class="quantity-value">
                                <%= item.getQuantity() %>
                            </span>

                            <form
                                class="quantity-form"
                                method="post"
                                action="<%= request.getContextPath() %>/buyer/cart/update">

                                <input
                                    type="hidden"
                                    name="productId"
                                    value="<%= item.getProductId() %>">

                                <input
                                    type="hidden"
                                    name="action"
                                    value="increase">

                                <button
                                    class="quantity-button"
                                    type="submit"
                                    <%= item.getQuantity()
                                            >= item.getStockQty()
                                            ? "disabled" : "" %>>
                                    +
                                </button>

                            </form>

                        </div>

                        <div class="stock-text">
                            Stock available:
                            <%= item.getStockQty() %>
                        </div>

                    </div>

                    <div class="item-total">

                        <div class="subtotal">
                            ₹<%= item.getSubtotal() %>
                        </div>

                        <a
                            class="remove"
                            href="<%= request.getContextPath() %>/buyer/cart/remove?productId=<%= item.getProductId() %>">
                            Remove
                        </a>

                    </div>

                </article>

            <% } %>

        </section>

        <section class="summary">

            <div class="summary-row">

                <span class="summary-label">
                    Cart Total
                </span>

                <span class="total">
                    ₹<%= total %>
                </span>

            </div>

        </section>

    <% } %>

</main>

</body>
</html>
