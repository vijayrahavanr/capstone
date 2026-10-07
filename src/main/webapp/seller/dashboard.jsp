<%@ page contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>

<%@ page import="com.vrmart.model.Product" %>
<%@ page import="com.vrmart.model.User" %>
<%@ page import="java.math.BigDecimal" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Map" %>

<%
    final User user = (User) session.getAttribute("user");

    if (user == null
            || !User.ROLE_SELLER.equals(user.getRole())) {

        response.sendRedirect(
                request.getContextPath() + "/seller/login");
        return;
    }

    final Integer totalProducts =
            (Integer) request.getAttribute("sellerTotalProducts");

    final Integer totalStock =
            (Integer) request.getAttribute("sellerTotalStock");

    final Integer lowStockCount =
            (Integer) request.getAttribute("sellerLowStockCount");

    final Integer productsSold =
            (Integer) request.getAttribute("sellerProductsSold");

    final BigDecimal revenue =
            (BigDecimal) request.getAttribute("sellerRevenue");

    final Integer totalOrders =
            (Integer) request.getAttribute("sellerTotalOrders");

    final Integer pendingOrders =
            (Integer) request.getAttribute("sellerPendingOrders");

    final Integer completedOrders =
            (Integer) request.getAttribute("sellerCompletedOrders");

    final Integer cancelledOrders =
            (Integer) request.getAttribute("sellerCancelledOrders");

    final Double performanceRate =
            (Double) request.getAttribute("sellerPerformanceRate");

    final BigDecimal averageOrderValue =
            (BigDecimal) request.getAttribute(
                    "sellerAverageOrderValue");

    final List<Product> lowStockProducts =
            (List<Product>) request.getAttribute(
                    "sellerLowStockProducts");

    final List<Map.Entry<String, Integer>> bestSellers =
            (List<Map.Entry<String, Integer>>) request.getAttribute(
                    "sellerBestSellers");

    final int displayTotalProducts =
            totalProducts == null ? 0 : totalProducts;

    final int displayTotalStock =
            totalStock == null ? 0 : totalStock;

    final int displayLowStockCount =
            lowStockCount == null ? 0 : lowStockCount;

    final int displayProductsSold =
            productsSold == null ? 0 : productsSold;

    final int displayTotalOrders =
            totalOrders == null ? 0 : totalOrders;

    final int displayPendingOrders =
            pendingOrders == null ? 0 : pendingOrders;

    final int displayCompletedOrders =
            completedOrders == null ? 0 : completedOrders;

    final int displayCancelledOrders =
            cancelledOrders == null ? 0 : cancelledOrders;

    final BigDecimal displayRevenue =
            revenue == null ? BigDecimal.ZERO : revenue;

    final BigDecimal displayAverageOrderValue =
            averageOrderValue == null
                    ? BigDecimal.ZERO
                    : averageOrderValue;

    final double displayPerformanceRate =
            performanceRate == null ? 0.0 : performanceRate;
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

        .analytics {
            display: grid;
            grid-template-columns:
                repeat(4, minmax(0, 1fr));
            gap: 15px;
            margin-top: 30px;
        }

        .stat {
            padding: 22px;
            border-radius: 18px;
            background: rgba(255, 255, 255, 0.035);
            border: 1px solid rgba(255, 255, 255, 0.07);
        }

        .stat-label {
            color: #7f899a;
            font-size: 11px;
            font-weight: 700;
            letter-spacing: 1px;
            text-transform: uppercase;
        }

        .stat-value {
            margin-top: 10px;
            font-size: 25px;
            font-weight: 800;
        }

        .stat-sub {
            margin-top: 7px;
            color: #818cf8;
            font-size: 11px;
        }

        .insights {
            display: grid;
            grid-template-columns:
                repeat(2, minmax(0, 1fr));
            gap: 20px;
            margin-top: 20px;
        }

        .panel {
            padding: 25px;
            border-radius: 18px;
            background: rgba(255, 255, 255, 0.035);
            border: 1px solid rgba(255, 255, 255, 0.07);
        }

        .panel h2 {
            font-size: 17px;
            margin-bottom: 18px;
        }

        .panel-subtitle {
            color: #7f899a;
            font-size: 12px;
            margin-top: -10px;
            margin-bottom: 18px;
        }

        .metric-row {
            display: flex;
            justify-content: space-between;
            align-items: center;
            padding: 13px 0;
            border-bottom: 1px solid
                rgba(255, 255, 255, 0.06);
        }

        .metric-row:last-child {
            border-bottom: none;
        }

        .metric-name {
            color: #b5bdcc;
            font-size: 13px;
        }

        .metric-value {
            font-size: 14px;
            font-weight: 700;
        }

        .low-stock-list,
        .best-seller-list {
            display: flex;
            flex-direction: column;
            gap: 10px;
        }

        .product-row {
            display: flex;
            justify-content: space-between;
            align-items: center;
            gap: 15px;
            padding: 13px 15px;
            border-radius: 12px;
            background: rgba(255, 255, 255, 0.025);
        }

        .product-name {
            font-size: 13px;
            font-weight: 700;
            overflow: hidden;
            text-overflow: ellipsis;
            white-space: nowrap;
        }

        .product-meta {
            margin-top: 4px;
            color: #7f899a;
            font-size: 11px;
        }

        .stock-badge {
            flex-shrink: 0;
            padding: 5px 9px;
            border-radius: 8px;
            background: rgba(248, 113, 113, 0.1);
            color: #fca5a5;
            font-size: 11px;
            font-weight: 700;
        }

        .sales-badge {
            flex-shrink: 0;
            padding: 5px 9px;
            border-radius: 8px;
            background: rgba(129, 140, 248, 0.1);
            color: #a5b4fc;
            font-size: 11px;
            font-weight: 700;
        }

        .empty {
            padding: 18px 0;
            color: #7f899a;
            font-size: 13px;
        }

        .actions {
            display: grid;
            grid-template-columns:
                repeat(3, minmax(0, 1fr));
            gap: 20px;
            margin-top: 25px;
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

        @media (max-width: 950px) {

            .analytics {
                grid-template-columns:
                    repeat(2, minmax(0, 1fr));
            }

            .insights {
                grid-template-columns: 1fr;
            }
        }

        @media (max-width: 850px) {

            .actions {
                grid-template-columns:
                    repeat(2, minmax(0, 1fr));
            }
        }

        @media (max-width: 600px) {

            .analytics {
                grid-template-columns: 1fr;
            }

            .actions {
                grid-template-columns: 1fr;
            }

            .page {
                padding: 30px 20px;
            }

            .welcome h1 {
                font-size: 28px;
            }

            .topbar {
                margin-bottom: 35px;
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

    <section class="analytics">

        <div class="stat">
            <div class="stat-label">
                Revenue
            </div>

            <div class="stat-value">
                ₹<%= displayRevenue %>
            </div>

            <div class="stat-sub">
                Delivered orders
            </div>
        </div>

        <div class="stat">
            <div class="stat-label">
                Products Sold
            </div>

            <div class="stat-value">
                <%= displayProductsSold %>
            </div>

            <div class="stat-sub">
                Units delivered
            </div>
        </div>

        <div class="stat">
            <div class="stat-label">
                Total Orders
            </div>

            <div class="stat-value">
                <%= displayTotalOrders %>
            </div>

            <div class="stat-sub">
                Active seller orders
            </div>
        </div>

        <div class="stat">
            <div class="stat-label">
                Products
            </div>

            <div class="stat-value">
                <%= displayTotalProducts %>
            </div>

            <div class="stat-sub">
                Total listings
            </div>
        </div>

    </section>

    <section class="insights">

        <div class="panel">

            <h2>Sales Overview</h2>

            <p class="panel-subtitle">
                Current seller marketplace performance.
            </p>

            <div class="metric-row">
                <span class="metric-name">
                    Pending Orders
                </span>

                <span class="metric-value">
                    <%= displayPendingOrders %>
                </span>
            </div>

            <div class="metric-row">
                <span class="metric-name">
                    Completed Orders
                </span>

                <span class="metric-value">
                    <%= displayCompletedOrders %>
                </span>
            </div>

            <div class="metric-row">
                <span class="metric-name">
                    Cancelled Orders
                </span>

                <span class="metric-value">
                    <%= displayCancelledOrders %>
                </span>
            </div>

            <div class="metric-row">
                <span class="metric-name">
                    Average Order Value
                </span>

                <span class="metric-value">
                    ₹<%= displayAverageOrderValue %>
                </span>
            </div>

            <div class="metric-row">
                <span class="metric-name">
                    Completion Rate
                </span>

                <span class="metric-value">
                    <%= String.format(
                            "%.1f",
                            displayPerformanceRate) %>%
                </span>
            </div>

        </div>

        <div class="panel">

            <h2>Inventory Overview</h2>

            <p class="panel-subtitle">
                Current stock position across your products.
            </p>

            <div class="metric-row">
                <span class="metric-name">
                    Total Products
                </span>

                <span class="metric-value">
                    <%= displayTotalProducts %>
                </span>
            </div>

            <div class="metric-row">
                <span class="metric-name">
                    Total Units In Stock
                </span>

                <span class="metric-value">
                    <%= displayTotalStock %>
                </span>
            </div>

            <div class="metric-row">
                <span class="metric-name">
                    Low Stock Products
                </span>

                <span class="metric-value">
                    <%= displayLowStockCount %>
                </span>
            </div>

        </div>

    </section>

    <section class="insights">

        <div class="panel">

            <h2>Best Sellers</h2>

            <p class="panel-subtitle">
                Your highest-selling products.
            </p>

            <div class="best-seller-list">

                <%
                    if (bestSellers == null
                            || bestSellers.isEmpty()) {
                %>

                    <div class="empty">
                        No completed sales yet.
                    </div>

                <%
                    } else {
                        for (Map.Entry<String, Integer> entry
                                : bestSellers) {
                %>

                    <div class="product-row">

                        <div>
                            <div class="product-name">
                                <%= entry.getKey() %>
                            </div>

                            <div class="product-meta">
                                Product performance
                            </div>
                        </div>

                        <div class="sales-badge">
                            <%= entry.getValue() %> sold
                        </div>

                    </div>

                <%
                        }
                    }
                %>

            </div>

        </div>

        <div class="panel">

            <h2>Low Stock</h2>

            <p class="panel-subtitle">
                Products that need inventory attention.
            </p>

            <div class="low-stock-list">

                <%
                    if (lowStockProducts == null
                            || lowStockProducts.isEmpty()) {
                %>

                    <div class="empty">
                        All products have healthy stock levels.
                    </div>

                <%
                    } else {
                        for (Product product
                                : lowStockProducts) {
                %>

                    <div class="product-row">

                        <div>
                            <div class="product-name">
                                <%= product.getName() %>
                            </div>

                            <div class="product-meta">
                                Inventory alert
                            </div>
                        </div>

                        <div class="stock-badge">
                            <%= product.getStockQty() %> left
                        </div>

                    </div>

                <%
                        }
                    }
                %>

            </div>

        </div>

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
           href="<%= request.getContextPath() %>/seller/products">

            <div class="icon">▣</div>

            <h2>View Products</h2>

            <p>
                Manage products added by you to VR Mart.
            </p>

        </a>

        <a class="card"
           href="<%= request.getContextPath() %>/seller/orders">

            <div class="icon">🛒</div>

            <h2>Incoming Orders</h2>

            <p>
                View orders received for your products.
            </p>

        </a>

        <a class="card"
           href="<%= request.getContextPath() %>/seller/service-requests">

            <div class="icon">⚙</div>

            <h2>Service Requests</h2>

            <p>
                Manage return, replacement and help requests
                for your products.
            </p>

        </a>

        <a class="card"
           href="<%= request.getContextPath() %>/seller/reviews">

            <div class="icon">★</div>

            <h2>Product Reviews</h2>

            <p>
                View ratings and reviews from buyers
                for your products.
            </p>

        </a>

        <a class="card"
           href="<%= request.getContextPath() %>/seller/settings.jsp">

            <div class="icon">⚙</div>

            <h2>Settings</h2>

            <p>
                Manage your seller account preferences
                and application settings.
            </p>

        </a>

        <a class="card"
           href="<%= request.getContextPath() %>/profile">

            <div class="icon">◎</div>

            <h2>My Profile</h2>

            <p>
                View and update your seller account information.
            </p>

        </a>

    </section>

</main>

</body>

</html>
