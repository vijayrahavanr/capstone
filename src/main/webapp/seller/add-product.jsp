<%@ page contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>
<%@ page import="com.vrmart.model.User" %>

<%
    final User user = (User) session.getAttribute("user");

    if (user == null
            || !User.ROLE_SELLER.equals(user.getRole())) {
        response.sendRedirect(
                request.getContextPath() + "/login");
        return;
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

    <title>VR Mart | Add Product</title>

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
           href="<%= request.getContextPath() %>/seller/dashboard.jsp">
            ← Dashboard
        </a>

    </header>

    <section class="heading">

        <small>Seller Portal</small>

        <h1>Add Product</h1>

        <p>
            Add a product to the VR Mart marketplace.
        </p>

    </section>

    <section class="form-card">

        <% if (error != null) { %>

            <div class="error">
                <%= error %>
            </div>

        <% } %>

        <form method="post"
              action="<%= request.getContextPath() %>/seller/products">

            <div class="field">

                <label for="name">Product Name</label>

                <input id="name"
                       name="name"
                       type="text"
                       maxlength="150"
                       required>

            </div>

            <div class="field">

                <label for="description">Description</label>

                <textarea id="description"
                          name="description"
                          maxlength="1000"></textarea>

            </div>

            <div class="row">

                <div class="field">

                    <label for="price">Price</label>

                    <input id="price"
                           name="price"
                           type="number"
                           step="0.01"
                           min="0"
                           required>

                </div>

                <div class="field">

                    <label for="stockQty">Stock Quantity</label>

                    <input id="stockQty"
                           name="stockQty"
                           type="number"
                           min="0"
                           required>

                </div>

            </div>

            <div class="field">

                <label for="category">Category</label>

                <input id="category"
                       name="category"
                       type="text"
                       maxlength="100"
                       placeholder="Example: Electronics"
                       required>

            </div>

            <div class="field">

                <label for="imageUrl">Image URL</label>

                <input id="imageUrl"
                       name="imageUrl"
                       type="url"
                       maxlength="1000"
                       placeholder="https://example.com/product.jpg">

            </div>

            <button type="submit">
                Add Product
            </button>

        </form>

    </section>

</main>

</body>
</html>
