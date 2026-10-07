<%@ page contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>

<%@ page import="com.vrmart.model.Product" %>
<%@ page import="com.vrmart.model.User" %>
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

    final List<Product> wishlist =
            (List<Product>) request.getAttribute("wishlist");

    final String removeMessage =
            (String) session.getAttribute(
                    "wishlistRemoveSuccess");

    if (removeMessage != null) {
        session.removeAttribute("wishlistRemoveSuccess");
    }
%>

<!DOCTYPE html>
<html lang="en">

<head>

    <meta charset="UTF-8">

    <meta name="viewport"
          content="width=device-width, initial-scale=1.0">

    <title>VR Mart | Wishlist</title>

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
            max-width: 1200px;
            margin: 0 auto;
            padding: 35px;
        }

        .topbar {
            display: flex;
            align-items: center;
            justify-content: space-between;
            margin-bottom: 40px;
        }

        .brand {
            font-size: 22px;
            font-weight: 800;
        }

        .brand span {
            color: #818cf8;
        }

        .top-links {
            display: flex;
            align-items: center;
            gap: 18px;
        }

        .back {
            color: #a5b4fc;
            text-decoration: none;
            font-size: 13px;
        }

        .back:hover {
            color: #ffffff;
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

        .heading p {
            margin-top: 9px;
            color: #7f899a;
            font-size: 14px;
        }

        .success {
            display: flex;
            align-items: center;
            justify-content: space-between;
            gap: 15px;
            margin-top: 25px;
            padding: 12px 14px;
            border-radius: 10px;
            color: #bbf7d0;
            background: rgba(34, 197, 94, 0.1);
            border: 1px solid rgba(34, 197, 94, 0.2);
            font-size: 13px;
        }

        .products {
            display: grid;
            grid-template-columns:
                repeat(3, minmax(0, 1fr));
            gap: 20px;
            margin-top: 30px;
        }

        .product {
            overflow: hidden;
            border-radius: 18px;
            background: rgba(255, 255, 255, 0.035);
            border: 1px solid rgba(255, 255, 255, 0.07);
        }

        .product-image {
            height: 210px;
            display: flex;
            align-items: center;
            justify-content: center;
            background: #111522;
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

        .content {
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

        .content h2 {
            margin-bottom: 8px;
            font-size: 18px;
        }

        .description {
            min-height: 42px;
            color: #7f899a;
            font-size: 12px;
            line-height: 1.6;
        }

        .seller {
            margin-top: 14px;
            color: #a5b4fc;
            font-size: 12px;
        }

        .price {
            margin-top: 8px;
            font-size: 17px;
            font-weight: 700;
        }

        .stock {
            margin-top: 7px;
            color: #687386;
            font-size: 11px;
        }

        .actions {
            display: grid;
            gap: 9px;
            margin-top: 17px;
        }

        .button {
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

        .button:hover {
            opacity: 0.9;
        }

        .remove {
            background: rgba(239, 68, 68, 0.12);
            border: 1px solid rgba(239, 68, 68, 0.2);
            color: #fca5a5;
        }

        .remove:hover {
            background: rgba(239, 68, 68, 0.2);
        }

        .disabled {
            cursor: not-allowed;
            opacity: 0.45;
        }

        .empty {
            margin-top: 30px;
            padding: 65px 25px;
            text-align: center;
            border-radius: 18px;
            background: rgba(255, 255, 255, 0.035);
            border: 1px solid rgba(255, 255, 255, 0.07);
        }

        .empty-icon {
            margin-bottom: 15px;
            font-size: 42px;
        }

        .empty h2 {
            font-size: 20px;
        }

        .empty p {
            margin-top: 8px;
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
                margin-bottom: 30px;
            }

            .heading h1 {
                font-size: 28px;
            }

            .success {
                align-items: flex-start;
                flex-direction: column;
            }
        }

    </style>

</head>

<body>

<main class="page">

    <header class="topbar">

        <div class="brand">
            VR <span>Mart</span>
        </div>

        <div class="top-links">

            <a class="back"
               href="<%= request.getContextPath() %>/products">
                ← Continue Shopping
            </a>

            <a class="back"
               href="<%= request.getContextPath() %>/buyer/dashboard.jsp">
                Dashboard
            </a>

        </div>

    </header>

    <section class="heading">

        <small>Buyer Portal</small>

        <h1>My Wishlist ♡</h1>

        <p>
            Products you saved for later.
        </p>

    </section>

    <% if (removeMessage != null) { %>

        <div class="success">
            <span>
                ✓ <%= removeMessage %>
            </span>
        </div>

    <% } %>

    <% if (wishlist == null || wishlist.isEmpty()) { %>

        <section class="empty">

            <div class="empty-icon">
                ♡
            </div>

            <h2>Your Wishlist is Empty</h2>

            <p>
                Save products you like and find them here later.
            </p>

        </section>

    <% } else { %>

        <section class="products">

            <% for (final Product product : wishlist) { %>

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

                    <div class="content">

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

                        <div class="seller">
                            Sold by:
                            <strong>
                                <%= product.getSellerName() == null
                                        ? "Seller"
                                        : product.getSellerName() %>
                            </strong>
                        </div>

                        <div class="price">
                            ₹<%= product.getPrice() %>
                        </div>

                        <div class="stock">
                            Stock:
                            <%= product.getStockQty() %>
                        </div>

                        <div class="actions">

                            <% if (product.getStockQty() > 0) { %>

                                <form method="post"
                                      action="<%= request.getContextPath() %>/buyer/cart/add">

                                    <input type="hidden"
                                           name="productId"
                                           value="<%= product.getId() %>">

                                    <input type="hidden"
                                           name="quantity"
                                           value="1">

                                    <button type="submit"
                                            class="button">

                                        Add to Cart

                                    </button>

                                </form>

                            <% } else { %>

                                <button type="button"
                                        class="button disabled"
                                        disabled>

                                    Out of Stock

                                </button>

                            <% } %>

                            <form method="post"
                                  action="<%= request.getContextPath() %>/buyer/wishlist">

                                <input type="hidden"
                                       name="productId"
                                       value="<%= product.getId() %>">

                                <input type="hidden"
                                       name="action"
                                       value="remove">

                                <button type="submit"
                                        class="button remove">

                                    Remove from Wishlist

                                </button>

                            </form>

                        </div>

                    </div>

                </article>

            <% } %>

        </section>

    <% } %>

</main>

</body>

</html>
