<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="com.vrmart.model.User" %>

<%
    final User user = (User) session.getAttribute("user");

    if (user == null || !User.ROLE_SELLER.equals(user.getRole())) {
        response.sendRedirect(
                request.getContextPath() + "/seller/login");
        return;
    }
%>

<!DOCTYPE html>
<html lang="en">

<head>

    <meta charset="UTF-8">

    <meta name="viewport"
          content="width=device-width, initial-scale=1.0">

    <title>VR Mart | Seller Dashboard</title>

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
            padding: 45px 30px;
        }

        .topbar {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 50px;
        }

        .brand {
            font-size: 22px;
            font-weight: 800;
        }

        .brand span {
            color: #818cf8;
        }

        .logout {
            color: #a5b4fc;
            text-decoration: none;
            font-size: 13px;
        }

        .logout:hover {
            color: #c4b5fd;
            text-decoration: underline;
        }

        .welcome small {
            color: #818cf8;
            font-size: 11px;
            font-weight: 700;
            letter-spacing: 1.5px;
            text-transform: uppercase;
        }

        .welcome h1 {
            margin-top: 10px;
            font-size: 34px;
        }

        .welcome p {
            margin-top: 10px;
            color: #7f899a;
            font-size: 14px;
        }

        .actions {
            display: grid;
            grid-template-columns:
                repeat(2, minmax(0, 1fr));
            gap: 20px;
            margin-top: 35px;
        }

        .card {
            padding: 28px;
            border-radius: 18px;
            text-decoration: none;
            color: #ffffff;
            background: rgba(255, 255, 255, 0.035);
            border: 1px solid rgba(255, 255, 255, 0.07);
            transition: 0.25s ease;
        }

        .card:hover {
            transform: translateY(-4px);
            border-color: rgba(129, 140, 248, 0.4);
        }

        .icon {
            font-size: 28px;
            margin-bottom: 18px;
        }

        .card h2 {
            font-size: 18px;
            margin-bottom: 8px;
        }

        .card p {
            color: #7f899a;
            font-size: 13px;
            line-height: 1.6;
        }

        @media (max-width: 600px) {

            .actions {
                grid-template-columns: 1fr;
            }

            .page {
                padding: 30px 20px;
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

        <a class="logout"
           href="<%= request.getContextPath() %>/logout">
            Logout
        </a>

    </header>

    <section class="welcome">

        <small>Seller Portal</small>

        <h1>
            Welcome, <%= user.getUsername() %>
        </h1>

        <p>
            Manage your products and marketplace listings.
        </p>

    </section>

    <section class="actions">

        <a class="card"
           href="<%= request.getContextPath() %>/seller/add-product.jsp">

            <div class="icon">＋</div>

            <h2>Add Product</h2>

            <p>
                Add a new product to the VR Mart marketplace.
            </p>

        </a>

        <a class="card"
           href="<%= request.getContextPath() %>/products">

            <div class="icon">▣</div>

            <h2>View Products</h2>

            <p>
                View products currently available in VR Mart.
            </p>

        </a>

    </section>

</main>

</body>

</html>
