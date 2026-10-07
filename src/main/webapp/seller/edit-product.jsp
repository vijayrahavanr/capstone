<%@ page contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>
<%@ page import="com.vrmart.model.Product" %>
<%@ page import="com.vrmart.model.User" %>

<%
    final User user = (User) session.getAttribute("user");

    if (user == null
            || !User.ROLE_SELLER.equals(user.getRole())) {

        response.sendRedirect(
                request.getContextPath() + "/seller/login");
        return;
    }

    final Product product =
            (Product) request.getAttribute("product");

    final String error =
            (String) request.getAttribute("error");

    if (product == null) {
        response.sendRedirect(
                request.getContextPath() + "/seller/products");
        return;
    }
%>

<!DOCTYPE html>
<html lang="en">
<head>

    <meta charset="UTF-8">

    <meta name="viewport"
          content="width=device-width, initial-scale=1.0">

    <title>VR Mart | Modify Product</title>

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
            max-width: 760px;
            margin: 0 auto;
            padding: 40px 25px;
        }

        .topbar {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 35px;
        }

        .brand {
            font-size: 21px;
            font-weight: 800;
        }

        .brand span {
            color: #818cf8;
        }

        .back {
            color: #a5b4fc;
            text-decoration: none;
            font-size: 13px;
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
            font-size: 14px;
        }

        .form-card {
            margin-top: 30px;
            padding: 28px;
            border-radius: 18px;
            background: rgba(255, 255, 255, 0.035);
            border: 1px solid rgba(255, 255, 255, 0.07);
        }

        .field {
            margin-bottom: 20px;
        }

        label {
            display: block;
            margin-bottom: 8px;
            color: #c7cedb;
            font-size: 12px;
            font-weight: 700;
        }

        input,
        textarea,
        select {
            width: 100%;
            padding: 13px 14px;
            border: 1px solid rgba(255, 255, 255, 0.1);
            border-radius: 10px;
            outline: none;
            background: #111522;
            color: #ffffff;
            font-size: 13px;
        }

        textarea {
            min-height: 100px;
            resize: vertical;
        }

        input:focus,
        textarea:focus,
        select:focus {
            border-color: #818cf8;
        }

        .row {
            display: grid;
            grid-template-columns: 1fr 1fr;
            gap: 16px;
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

        .preview {
            width: 100%;
            height: 220px;
            margin-bottom: 20px;
            border-radius: 12px;
            overflow: hidden;
            background: #111522;
            display: flex;
            align-items: center;
            justify-content: center;
        }

        .preview img {
            width: 100%;
            height: 100%;
            object-fit: cover;
        }

        .no-image {
            color: #687386;
            font-size: 12px;
        }

        button {
            width: 100%;
            padding: 14px;
            border: 0;
            border-radius: 10px;
            cursor: pointer;
            color: #ffffff;
            background: linear-gradient(
                135deg,
                #6366f1,
                #8b5cf6
            );
            font-size: 14px;
            font-weight: 700;
        }

        @media (max-width: 600px) {

            .row {
                grid-template-columns: 1fr;
            }

            .form-card {
                padding: 20px;
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

        <a class="back"
           href="<%= request.getContextPath() %>/seller/products">
            ← My Products
        </a>

    </header>

    <section class="heading">

        <small>Seller Portal</small>

        <h1>Modify Product</h1>

        <p>
            Update your product information.
        </p>

    </section>

    <section class="form-card">

        <% if (error != null) { %>

            <div class="error">
                <%= error %>
            </div>

        <% } %>

        <div class="preview">

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

        <form method="post"
              action="<%= request.getContextPath() %>/seller/products/edit">

            <input type="hidden"
                   name="id"
                   value="<%= product.getId() %>">

            <div class="field">

                <label for="name">
                    Product Name
                </label>

                <input
                    id="name"
                    name="name"
                    type="text"
                    maxlength="150"
                    value="<%= product.getName() %>"
                    required>

            </div>

            <div class="field">

                <label for="description">
                    Description
                </label>

                <textarea
                    id="description"
                    name="description"
                    maxlength="1000"><%= product.getDescription() != null
                            ? product.getDescription()
                            : "" %></textarea>

            </div>

            <div class="row">

                <div class="field">

                    <label for="price">
                        Price
                    </label>

                    <input
                        id="price"
                        name="price"
                        type="number"
                        step="0.01"
                        min="0"
                        value="<%= product.getPrice() %>"
                        required>

                </div>

                <div class="field">

                    <label for="stockQty">
                        Stock Quantity
                    </label>

                    <input
                        id="stockQty"
                        name="stockQty"
                        type="number"
                        min="0"
                        value="<%= product.getStockQty() %>"
                        required>

                </div>

            </div>

            <div class="field">

                <label for="category">
                    Category
                </label>

                <select
                    id="category"
                    name="category"
                    required>
                    <option value="">Select category</option>
                    <option value="Electronics"
                        <%= "Electronics".equals(
                                product.getCategory())
                                ? "selected" : "" %>>
                        Electronics
                    </option>
                    <option value="Fashion"
                        <%= "Fashion".equals(product.getCategory())
                                ? "selected" : "" %>>
                        Fashion
                    </option>
                    <option value="Home & Kitchen"
                        <%= "Home & Kitchen".equals(
                                product.getCategory())
                                ? "selected" : "" %>>
                        Home &amp; Kitchen
                    </option>
                    <option value="Beauty"
                        <%= "Beauty".equals(product.getCategory())
                                ? "selected" : "" %>>
                        Beauty
                    </option>
                    <option value="Grocery"
                        <%= "Grocery".equals(product.getCategory())
                                ? "selected" : "" %>>
                        Grocery
                    </option>
                    <option value="Sports & Fitness"
                        <%= "Sports & Fitness".equals(
                                product.getCategory())
                                ? "selected" : "" %>>
                        Sports &amp; Fitness
                    </option>
                    <option value="Books"
                        <%= "Books".equals(product.getCategory())
                                ? "selected" : "" %>>
                        Books
                    </option>
                    <option value="Toys"
                        <%= "Toys".equals(product.getCategory())
                                ? "selected" : "" %>>
                        Toys
                    </option>
                    <option value="Accessories"
                        <%= "Accessories".equals(
                                product.getCategory())
                                ? "selected" : "" %>>
                        Accessories
                    </option>
                </select>

            </div>

            <div class="field">

                <label for="imageUrl">
                    Image URL
                </label>

                <input
                    id="imageUrl"
                    name="imageUrl"
                    type="url"
                    maxlength="1000"
                    value="<%= product.getImageUrl() != null
                            ? product.getImageUrl()
                            : "" %>"
                    placeholder="https://example.com/product.jpg">

            </div>

            <button type="submit">
                Save Changes
            </button>

        </form>

    </section>

</main>

</body>
</html>
