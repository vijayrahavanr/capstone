<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="com.vrmart.model.Product" %>
<%@ page import="com.vrmart.model.User" %>
<%@ page import="java.util.List" %>

<%
    final User user = (User) session.getAttribute("user");

    if (user == null
            || !User.ROLE_SELLER.equals(user.getRole())) {
        response.sendRedirect(
                request.getContextPath() + "/seller/login");
        return;
    }

    final List<Product> products =
            (List<Product>) request.getAttribute("products");

    String message =
            (String) request.getAttribute("message");

    String error =
            (String) request.getAttribute("error");

    if (message == null) {
        message = request.getParameter("message");
    }

    if (error == null) {
        error = request.getParameter("error");
    }
%>

<!DOCTYPE html>
<html lang="en">

<head>

    <meta charset="UTF-8">

    <meta name="viewport"
          content="width=device-width, initial-scale=1.0">

    <title>VR Mart | My Products</title>

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
            max-width: 1150px;
            margin: 0 auto;
            padding: 40px 25px;
        }

        .topbar {
            display: flex;
            align-items: center;
            justify-content: space-between;
            margin-bottom: 45px;
        }

        .brand {
            font-size: 22px;
            font-weight: 800;
        }

        .brand span {
            color: #818cf8;
        }

        .top-actions {
            display: flex;
            align-items: center;
            gap: 20px;
        }

        .back,
        .logout {
            color: #a5b4fc;
            text-decoration: none;
            font-size: 13px;
        }

        .back:hover,
        .logout:hover {
            color: #c4b5fd;
        }

        .heading {
            display: flex;
            align-items: flex-end;
            justify-content: space-between;
            gap: 20px;
            margin-bottom: 30px;
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

        .add-button {
            padding: 12px 18px;
            border-radius: 10px;
            color: #ffffff;
            text-decoration: none;
            background: linear-gradient(
                135deg,
                #6366f1,
                #8b5cf6
            );
            font-size: 13px;
            font-weight: 700;
            white-space: nowrap;
        }

        .message,
        .error {
            margin-bottom: 20px;
            padding: 13px 15px;
            border-radius: 10px;
            font-size: 13px;
        }

        .message {
            color: #bbf7d0;
            background: rgba(34, 197, 94, 0.10);
            border: 1px solid rgba(34, 197, 94, 0.20);
        }

        .error {
            color: #fecaca;
            background: rgba(239, 68, 68, 0.10);
            border: 1px solid rgba(239, 68, 68, 0.20);
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
        }

        .product-image {
            width: 100%;
            height: 210px;
            display: flex;
            align-items: center;
            justify-content: center;
            overflow: hidden;
            background: #111522;
        }

        .product-image img {
            width: 100%;
            height: 100%;
            object-fit: cover;
        }

        .no-image {
            color: #687386;
            font-size: 12px;
        }

        .product-content {
            padding: 20px;
        }

        .category {
            color: #818cf8;
            font-size: 10px;
            font-weight: 700;
            letter-spacing: 1px;
            text-transform: uppercase;
        }

        .product h2 {
            margin-top: 8px;
            font-size: 18px;
        }

        .description {
            min-height: 42px;
            margin-top: 8px;
            color: #7f899a;
            font-size: 12px;
            line-height: 1.6;
        }

        .details {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-top: 18px;
            padding-top: 15px;
            border-top: 1px solid rgba(255, 255, 255, 0.07);
        }

        .price {
            color: #a5b4fc;
            font-size: 17px;
            font-weight: 800;
        }

        .stock {
            color: #8f98aa;
            font-size: 11px;
        }

        .actions {
            display: flex;
            gap: 9px;
            margin-top: 18px;
        }

        .edit-button,
        .delete-button {
            flex: 1;
            padding: 10px;
            border-radius: 9px;
            text-align: center;
            text-decoration: none;
            font-size: 12px;
            font-weight: 700;
        }

        .edit-button {
            color: #c7d2fe;
            background: rgba(99, 102, 241, 0.15);
            border: 1px solid rgba(129, 140, 248, 0.20);
        }

        .delete-button {
            color: #fecaca;
            background: rgba(239, 68, 68, 0.10);
            border: 1px solid rgba(239, 68, 68, 0.18);
            font-family: inherit;
            cursor: pointer;
        }

        .edit-button:hover {
            background: rgba(99, 102, 241, 0.25);
        }

        .delete-button:hover {
            background: rgba(239, 68, 68, 0.18);
        }

        .empty {
            padding: 70px 25px;
            text-align: center;
            border-radius: 18px;
            background: rgba(255, 255, 255, 0.035);
            border: 1px solid rgba(255, 255, 255, 0.07);
        }

        .empty h2 {
            margin-bottom: 9px;
            font-size: 20px;
        }

        .empty p {
            color: #7f899a;
            font-size: 13px;
            margin-bottom: 22px;
        }

        @media (max-width: 900px) {
            .products {
                grid-template-columns:
                    repeat(2, minmax(0, 1fr));
            }
        }

        @media (max-width: 600px) {

            .page {
                padding: 25px 18px;
            }

            .heading {
                align-items: flex-start;
                flex-direction: column;
            }

            .products {
                grid-template-columns: 1fr;
            }

            .top-actions {
                gap: 10px;
            }

            .back {
                display: none;
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

        <div class="top-actions">

            <a class="back"
               href="<%= request.getContextPath() %>/seller/dashboard.jsp">
                ← Dashboard
            </a>

            <a class="logout"
               href="<%= request.getContextPath() %>/logout">
                Logout
            </a>

        </div>

    </header>

    <section class="heading">

        <div>

            <small>Seller Portal</small>

            <h1>My Products</h1>

            <p>
                Manage your VR Mart marketplace products.
            </p>

        </div>

        <a class="add-button"
           href="<%= request.getContextPath() %>/seller/add-product.jsp">
            + Add Product
        </a>

    </section>

    <% if (message != null && !message.isBlank()) { %>

        <div class="message">
            <%= message %>
        </div>

    <% } %>

    <% if (error != null && !error.isBlank()) { %>

        <div class="error">
            <%= error %>
        </div>

    <% } %>

    <% if (products == null || products.isEmpty()) { %>

        <section class="empty">

            <h2>No products yet</h2>

            <p>
                Add your first product to the VR Mart marketplace.
            </p>

            <a class="add-button"
               href="<%= request.getContextPath() %>/seller/add-product.jsp">
                + Add Product
            </a>

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
                                No image
                            </span>

                        <% } %>

                    </div>

                    <div class="product-content">

                        <div class="category">
                            <%= product.getCategory() %>
                        </div>

                        <h2>
                            <%= product.getName() %>
                        </h2>

                        <p class="description">

                            <%= product.getDescription() != null
                                    && !product.getDescription().isBlank()
                                    ? product.getDescription()
                                    : "No description available." %>

                        </p>

                        <div class="details">

                            <span class="price">
                                ₹<%= product.getPrice() %>
                            </span>

                            <span class="stock">
                                Stock: <%= product.getStockQty() %>
                            </span>

                        </div>

                        <div class="actions">

                            <a
                                    class="edit-button"
                                    href="<%= request.getContextPath() %>/seller/products/edit?id=<%= product.getId() %>">
                                Modify
                            </a>

                            <form
                                    method="post"
                                    action="<%= request.getContextPath() %>/seller/products/delete"
                                    style="flex: 1;"
                                    onsubmit="return confirm('Remove this product from VR Mart?');">

                                <input
                                        type="hidden"
                                        name="id"
                                        value="<%= product.getId() %>">

                                <button
                                        type="submit"
                                        class="delete-button">
                                    Remove
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
