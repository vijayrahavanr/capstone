<%@ page contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>
<%@ page import="com.vrmart.model.Product" %>
<%@ page import="com.vrmart.model.User" %>
<%@ page import="java.util.List" %>

<%
    final List<Product> products =
            (List<Product>) request.getAttribute("products");

    final User user =
            (User) session.getAttribute("user");

    final String cartError =
            (String) session.getAttribute("cartError");

    if (cartError != null) {
        session.removeAttribute("cartError");
    }
%>

<!DOCTYPE html>
<html lang="en">

<head>

    <meta charset="UTF-8">

    <meta name="viewport"
          content="width=device-width, initial-scale=1.0">

    <title>VR Mart | Products</title>

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
            max-width: 1250px;
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

        .top-links {
            display: flex;
            align-items: center;
            gap: 20px;
        }

        .back,
        .cart-link {
            color: #a5b4fc;
            text-decoration: none;
            font-size: 13px;
        }

        .back:hover,
        .cart-link:hover {
            color: #ffffff;
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
            letter-spacing: -1px;
        }

        .heading p {
            margin-top: 9px;
            color: #7f899a;
            font-size: 14px;
        }

        .error {
            margin-bottom: 20px;
            padding: 12px 14px;
            border-radius: 10px;
            color: #fecaca;
            background: rgba(239, 68, 68, 0.1);
            border: 1px solid rgba(239, 68, 68, 0.2);
            font-size: 13px;
        }

        .products {
            display: grid;
            grid-template-columns:
                repeat(3, minmax(0, 1fr));
            gap: 20px;
        }

        .product {
            overflow: hidden;
            border-radius: 18px;
            background: rgba(255, 255, 255, 0.035);
            border: 1px solid rgba(255, 255, 255, 0.07);
            transition: 0.25s ease;
        }

        .product:hover {
            transform: translateY(-4px);
            border-color: rgba(129, 140, 248, 0.3);
        }

        .product-image {
            height: 210px;
            display: flex;
            align-items: center;
            justify-content: center;
            background:
                radial-gradient(
                    circle at 50% 30%,
                    rgba(99, 102, 241, 0.2),
                    transparent 60%
                ),
                #111522;
        }

        .product-image img {
            width: 100%;
            height: 100%;
            object-fit: cover;
        }

        .no-image {
            color: #687386;
            font-size: 13px;
        }

        .product-content {
            padding: 20px;
        }

        .category {
            display: inline-block;
            margin-bottom: 10px;
            padding: 5px 9px;
            border-radius: 999px;
            color: #a5b4fc;
            background: rgba(99, 102, 241, 0.1);
            font-size: 10px;
            font-weight: 700;
            text-transform: uppercase;
        }

        .product h2 {
            margin-bottom: 8px;
            font-size: 17px;
        }

        .description {
            min-height: 42px;
            margin-bottom: 15px;
            color: #7f899a;
            font-size: 12px;
            line-height: 1.6;
        }

        .product-footer {
            display: flex;
            align-items: center;
            justify-content: space-between;
        }

        .price {
            color: #ffffff;
            font-size: 18px;
            font-weight: 800;
        }

        .stock {
            color: #7f899a;
            font-size: 11px;
        }

        .cart-form {
            margin-top: 16px;
        }

        .cart-button {
            width: 100%;
            padding: 11px;
            border: 0;
            border-radius: 10px;
            cursor: pointer;
            color: #ffffff;
            background: linear-gradient(
                135deg,
                #6366f1,
                #8b5cf6
            );
            font-size: 12px;
            font-weight: 700;
        }

        .cart-button:hover {
            opacity: 0.9;
        }

        .cart-button.disabled {
            cursor: not-allowed;
            opacity: 0.5;
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

        @media (max-width: 900px) {

            .products {
                grid-template-columns: 1fr 1fr;
            }

        }

        @media (max-width: 600px) {

            .page {
                padding: 20px;
            }

            .products {
                grid-template-columns: 1fr;
            }

            .topbar {
                margin-bottom: 25px;
            }

            .heading h1 {
                font-size: 28px;
            }

            .top-links {
                gap: 10px;
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

        <div class="top-links">

            <% if (user != null
                    && User.ROLE_BUYER.equals(user.getRole())) { %>

                <a class="cart-link"
                   href="<%= request.getContextPath() %>/buyer/cart">
                    🛒 Cart
                </a>

            <% } %>

            <a class="back"
               href="<%= request.getContextPath() %>/buyer/dashboard.jsp">
                ← Dashboard
            </a>

        </div>

    </header>

    <section class="heading">

        <small>VR Mart Marketplace</small>

        <h1>Explore Products</h1>

        <p>
            Discover products available from trusted sellers.
        </p>

    </section>

    <% if (cartError != null) { %>

        <div class="error">
            <%= cartError %>
        </div>

    <% } %>

    <% if (products == null || products.isEmpty()) { %>

        <section class="empty">

            <h2>No products available yet</h2>

            <p>
                Products added by sellers will appear here.
            </p>

        </section>

    <% } else { %>

        <section class="products">

            <% for (Product product : products) { %>

                <article class="product">

                    <div class="product-image">

                        <% if (product.getImageUrl() != null
                                && !product.getImageUrl().isBlank()) { %>

                            <img
                                src="<%= product.getImageUrl() %>"
                                alt="<%= product.getName() %>">

                        <% } else { %>

                            <span class="no-image">
                                No image available
                            </span>

                        <% } %>

                    </div>

                    <div class="product-content">

                        <span class="category">
                            <%= product.getCategory() %>
                        </span>

                        <h2>
                            <%= product.getName() %>
                        </h2>

                        <p class="description">
                            <%= product.getDescription() == null
                                    ? "No description available."
                                    : product.getDescription() %>
                        </p>

                        <div class="product-footer">

                            <span class="price">
                                ₹<%= product.getPrice() %>
                            </span>

                            <span class="stock">
                                Stock:
                                <%= product.getStockQty() %>
                            </span>

                        </div>

                        <% if (product.getStockQty() > 0) { %>

                            <form method="post"
                                  action="<%= request.getContextPath() %>/buyer/cart/add"
                                  class="cart-form">

                                <input type="hidden"
                                       name="productId"
                                       value="<%= product.getId() %>">

                                <input type="hidden"
                                       name="quantity"
                                       value="1">

                                <button type="submit"
                                        class="cart-button">
                                    Add to Cart
                                </button>

                            </form>

                        <% } else { %>

                            <button type="button"
                                    class="cart-button disabled"
                                    disabled>
                                Out of Stock
                            </button>

                        <% } %>

                    </div>

                </article>

            <% } %>

        </section>

    <% } %>

</main>

</body>

</html>
