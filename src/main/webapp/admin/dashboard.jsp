<%@ page contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>

<%@ page import="java.math.BigDecimal" %>
<%@ page import="java.util.HashMap" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Map" %>
<%@ page import="com.vrmart.model.User" %>

<%
    final User admin = (User) session.getAttribute("user");

    if (admin == null
            || !User.ROLE_ADMIN.equals(admin.getRole())) {
        response.sendRedirect(
                request.getContextPath() + "/admin/login");
        return;
    }

    final Map<String, Long> counts =
            (Map<String, Long>) request.getAttribute("counts");

    final List<Map<String, Object>> users =
            (List<Map<String, Object>>) request.getAttribute("users");

    final List<Map<String, Object>> products =
            (List<Map<String, Object>>) request.getAttribute("products");

    final List<Map<String, Object>> orders =
            (List<Map<String, Object>>) request.getAttribute("orders");

    final List<Map<String, Object>> serviceRequests =
            (List<Map<String, Object>>)
                    request.getAttribute("serviceRequests");

    final List<Map<String, Object>> reviews =
            (List<Map<String, Object>>)
                    request.getAttribute("reviews");

    final Map<String, Object> analytics =
            (Map<String, Object>) request.getAttribute("analytics");

    final Map<String, Object> safeAnalytics =
            analytics == null
                    ? new HashMap<String, Object>()
                    : analytics;

    final BigDecimal totalRevenue =
            safeAnalytics.get("totalRevenue") instanceof BigDecimal
                    ? (BigDecimal) safeAnalytics.get("totalRevenue")
                    : BigDecimal.ZERO;

    final BigDecimal averageOrderValue =
            safeAnalytics.get("averageOrderValue")
                    instanceof BigDecimal
                    ? (BigDecimal)
                            safeAnalytics.get("averageOrderValue")
                    : BigDecimal.ZERO;

    final long productsSold =
            safeAnalytics.get("productsSold") instanceof Number
                    ? ((Number)
                            safeAnalytics.get("productsSold")).longValue()
                    : 0L;

    final long activeProducts =
            safeAnalytics.get("activeProducts") instanceof Number
                    ? ((Number)
                            safeAnalytics.get("activeProducts")).longValue()
                    : 0L;

    final long lowStockProducts =
            safeAnalytics.get("lowStockProducts") instanceof Number
                    ? ((Number)
                            safeAnalytics.get("lowStockProducts")).longValue()
                    : 0L;

    final long outOfStockProducts =
            safeAnalytics.get("outOfStockProducts") instanceof Number
                    ? ((Number)
                            safeAnalytics.get("outOfStockProducts"))
                            .longValue()
                    : 0L;

    final long buyers =
            safeAnalytics.get("buyers") instanceof Number
                    ? ((Number) safeAnalytics.get("buyers")).longValue()
                    : 0L;

    final long sellers =
            safeAnalytics.get("sellers") instanceof Number
                    ? ((Number) safeAnalytics.get("sellers")).longValue()
                    : 0L;

    final long admins =
            safeAnalytics.get("admins") instanceof Number
                    ? ((Number) safeAnalytics.get("admins")).longValue()
                    : 0L;

    final Map<String, Long> orderStatusStats =
            safeAnalytics.get("orderStatusStats")
                    instanceof Map
                    ? (Map<String, Long>)
                            safeAnalytics.get("orderStatusStats")
                    : new HashMap<String, Long>();

    final Map<String, Long> categoryStats =
            safeAnalytics.get("categoryStats")
                    instanceof Map
                    ? (Map<String, Long>)
                            safeAnalytics.get("categoryStats")
                    : new HashMap<String, Long>();

    final Map<String, BigDecimal> monthlyRevenue =
            safeAnalytics.get("monthlyRevenue")
                    instanceof Map
                    ? (Map<String, BigDecimal>)
                            safeAnalytics.get("monthlyRevenue")
                    : new HashMap<String, BigDecimal>();

    final long pendingOrders =
            orderStatusStats.get("PENDING") == null
                    ? 0L
                    : orderStatusStats.get("PENDING");

    final long approvedOrders =
            orderStatusStats.get("APPROVED") == null
                    ? 0L
                    : orderStatusStats.get("APPROVED");

    final long shippedOrders =
            orderStatusStats.get("SHIPPED") == null
                    ? 0L
                    : orderStatusStats.get("SHIPPED");

    final long deliveredOrders =
            orderStatusStats.get("DELIVERED") == null
                    ? 0L
                    : orderStatusStats.get("DELIVERED");

    final long cancelledOrders =
            orderStatusStats.get("CANCELLED") == null
                    ? 0L
                    : orderStatusStats.get("CANCELLED");

    long maxCategorySales = 0L;

    for (Long value : categoryStats.values()) {
        if (value != null && value > maxCategorySales) {
            maxCategorySales = value;
        }
    }

    BigDecimal maxMonthlyRevenue = BigDecimal.ZERO;

    for (BigDecimal value : monthlyRevenue.values()) {
        if (value != null
                && value.compareTo(maxMonthlyRevenue) > 0) {
            maxMonthlyRevenue = value;
        }
    }
%>

<!DOCTYPE html>
<html lang="en">
<head>

<meta charset="UTF-8">

<meta name="viewport"
      content="width=device-width, initial-scale=1.0">

<title>VR Mart | Admin Control Center</title>

<style>

* {
    box-sizing: border-box;
}

body {
    margin: 0;
    min-height: 100vh;
    font-family: Arial, Helvetica, sans-serif;
    background: #070914;
    color: #ffffff;
}

.layout {
    display: flex;
    min-height: 100vh;
}

.sidebar {
    position: fixed;
    inset: 0 auto 0 0;
    width: 245px;
    padding: 25px 16px;
    border-right: 1px solid rgba(255,255,255,.08);
    background: #0b0e1b;
}

.brand {
    display: flex;
    align-items: center;
    gap: 11px;
    margin-bottom: 36px;
    padding: 4px 10px;
    font-size: 19px;
    font-weight: 900;
}

.logo {
    width: 42px;
    height: 42px;
    display: grid;
    place-items: center;
    border-radius: 13px;
    background: linear-gradient(135deg,#635bff,#9b5cff);
}

.brand span {
    color: #a78bfa;
}

.label {
    margin: 0 12px 9px;
    color: #50586d;
    font-size: 10px;
    font-weight: 800;
    letter-spacing: 1.5px;
    text-transform: uppercase;
}

.menu a {
    display: block;
    padding: 12px 13px;
    border-radius: 11px;
    color: #8b93a8;
    text-decoration: none;
    font-size: 12px;
    font-weight: 700;
}

.menu a:hover,
.menu a.active {
    color: #ffffff;
    background: rgba(124,92,255,.11);
}

.admin-box {
    position: absolute;
    left: 16px;
    right: 16px;
    bottom: 22px;
}

.account {
    padding: 13px;
    margin-bottom: 10px;
    border: 1px solid rgba(255,255,255,.08);
    border-radius: 14px;
    background: rgba(255,255,255,.035);
}

.account strong {
    display: block;
    margin-bottom: 4px;
    font-size: 12px;
}

.account span {
    color: #6f788b;
    font-size: 10px;
}

.logout {
    display: block;
    padding: 10px;
    border-radius: 10px;
    color: #fca5a5;
    background: rgba(239,68,68,.06);
    text-align: center;
    text-decoration: none;
    font-size: 11px;
    font-weight: 800;
}

.main {
    width: calc(100% - 245px);
    margin-left: 245px;
    padding: 34px 36px 60px;
}

.top {
    display: flex;
    justify-content: space-between;
    align-items: flex-end;
    gap: 20px;
    margin-bottom: 25px;
}

.eyebrow {
    color: #818cf8;
    font-size: 10px;
    font-weight: 800;
    letter-spacing: 1.5px;
}

h1 {
    margin: 8px 0 7px;
    font-size: 31px;
}

.subtitle {
    margin: 0;
    color: #737d91;
    font-size: 12px;
}

.assured {
    padding: 9px 13px;
    border: 1px solid rgba(52,211,153,.20);
    border-radius: 999px;
    color: #6ee7b7;
    background: rgba(52,211,153,.06);
    font-size: 10px;
    font-weight: 800;
}

.stats {
    display: grid;
    grid-template-columns: repeat(5,1fr);
    gap: 12px;
    margin-bottom: 23px;
}

.stat {
    padding: 18px;
    border: 1px solid rgba(255,255,255,.08);
    border-radius: 15px;
    background: rgba(255,255,255,.035);
}

.stat span {
    color: #6f788b;
    font-size: 9px;
    font-weight: 700;
}

.stat strong {
    display: block;
    margin-top: 7px;
    font-size: 24px;
}

.analytics-grid {
    display: grid;
    grid-template-columns: repeat(4,1fr);
    gap: 12px;
    margin-bottom: 20px;
}

.analytics-card {
    padding: 18px;
    border: 1px solid rgba(124,92,255,.15);
    border-radius: 15px;
    background:
        linear-gradient(
            135deg,
            rgba(124,92,255,.09),
            rgba(255,255,255,.025)
        );
}

.analytics-card .title {
    color: #737d91;
    font-size: 9px;
    font-weight: 700;
    text-transform: uppercase;
    letter-spacing: .7px;
}

.analytics-card strong {
    display: block;
    margin-top: 8px;
    font-size: 23px;
}

.analytics-card small {
    display: block;
    margin-top: 6px;
    color: #596276;
    font-size: 9px;
}

.analytics-two {
    display: grid;
    grid-template-columns: 1fr 1fr;
    gap: 20px;
    margin-bottom: 20px;
}

.analytics-three {
    display: grid;
    grid-template-columns: 1fr 1fr 1fr;
    gap: 20px;
    margin-bottom: 20px;
}

.analytics-panel {
    padding: 19px;
    border: 1px solid rgba(255,255,255,.08);
    border-radius: 17px;
    background: rgba(255,255,255,.025);
    overflow: hidden;
}

.analytics-panel h2 {
    margin: 0 0 16px;
    font-size: 15px;
}

.status-row {
    display: flex;
    align-items: center;
    justify-content: space-between;
    gap: 12px;
    padding: 10px 0;
    border-bottom: 1px solid rgba(255,255,255,.05);
}

.status-row:last-child {
    border-bottom: 0;
}

.status-name {
    color: #aeb6c7;
    font-size: 10px;
    font-weight: 700;
}

.status-value {
    color: #ffffff;
    font-size: 11px;
    font-weight: 800;
}

.progress {
    flex: 1;
    height: 6px;
    overflow: hidden;
    border-radius: 999px;
    background: rgba(255,255,255,.06);
}

.progress span {
    display: block;
    height: 100%;
    min-width: 2px;
    border-radius: 999px;
    background: linear-gradient(90deg,#635bff,#a78bfa);
}

.category-row {
    margin-bottom: 14px;
}

.category-head {
    display: flex;
    justify-content: space-between;
    margin-bottom: 6px;
}

.category-head span {
    color: #aeb6c7;
    font-size: 10px;
}

.category-head strong {
    color: #ffffff;
    font-size: 10px;
}

.revenue-chart {
    display: flex;
    align-items: flex-end;
    gap: 9px;
    height: 190px;
    padding-top: 10px;
    overflow-x: auto;
}

.revenue-column {
    min-width: 42px;
    height: 100%;
    display: flex;
    flex-direction: column;
    justify-content: flex-end;
    align-items: center;
    gap: 7px;
}

.revenue-bar-area {
    width: 25px;
    height: 145px;
    display: flex;
    align-items: flex-end;
}

.revenue-bar {
    width: 100%;
    min-height: 3px;
    border-radius: 7px 7px 3px 3px;
    background: linear-gradient(180deg,#9b5cff,#635bff);
}

.revenue-month {
    color: #737d91;
    font-size: 8px;
    white-space: nowrap;
}

.revenue-value {
    color: #a78bfa;
    font-size: 8px;
    white-space: nowrap;
}

.inventory-box {
    display: grid;
    grid-template-columns: repeat(3,1fr);
    gap: 9px;
}

.inventory-item {
    padding: 14px 10px;
    border-radius: 12px;
    background: rgba(255,255,255,.035);
    text-align: center;
}

.inventory-item strong {
    display: block;
    font-size: 20px;
}

.inventory-item span {
    display: block;
    margin-top: 5px;
    color: #737d91;
    font-size: 8px;
    font-weight: 700;
    text-transform: uppercase;
}

.section {
    margin-bottom: 20px;
    padding: 19px;
    border: 1px solid rgba(255,255,255,.08);
    border-radius: 17px;
    background: rgba(255,255,255,.025);
    overflow-x: auto;
}

.section h2 {
    margin: 0 0 14px;
    font-size: 15px;
}

table {
    width: 100%;
    min-width: 760px;
    border-collapse: collapse;
}

th,
td {
    padding: 10px 8px;
    border-bottom: 1px solid rgba(255,255,255,.06);
    text-align: left;
    vertical-align: middle;
    font-size: 10px;
}

th {
    color: #737d91;
    font-size: 9px;
    text-transform: uppercase;
}

td {
    color: #c4cad6;
}

.badge {
    display: inline-block;
    padding: 5px 8px;
    border-radius: 999px;
    background: rgba(124,92,255,.10);
    color: #b4a9ff;
    font-size: 8px;
    font-weight: 800;
}

.assured-mini {
    display: inline-block;
    margin-left: 5px;
    padding: 4px 7px;
    border-radius: 999px;
    color: #6ee7b7;
    background: rgba(52,211,153,.07);
    font-size: 8px;
    font-weight: 800;
}

.inactive {
    color: #fca5a5;
}

select,
button {
    padding: 7px;
    border: 1px solid rgba(255,255,255,.10);
    border-radius: 8px;
    background: #111522;
    color: #ffffff;
    font-size: 9px;
}

button {
    cursor: pointer;
    color: #c4b5fd;
    background: rgba(124,92,255,.12);
    font-weight: 800;
}

.inline-form {
    display: inline-block;
    margin: 0 3px 3px 0;
}

.action-group {
    display: flex;
    gap: 8px;
    align-items: center;
    flex-wrap: wrap;
}

.delete-button {
    border: 1px solid rgba(255,90,90,.35);
    color: #ff8b8b;
}

.delete-button:hover {
    border-color: rgba(255,90,90,.65);
}

.empty {
    padding: 25px;
    color: #596276;
    text-align: center;
    font-size: 10px;
}

@media (max-width: 1100px) {

    .stats {
        grid-template-columns: repeat(3,1fr);
    }

    .analytics-grid {
        grid-template-columns: repeat(2,1fr);
    }

    .analytics-three {
        grid-template-columns: 1fr 1fr;
    }
}

@media (max-width: 850px) {

    .sidebar {
        position: static;
        width: 100%;
    }

    .layout {
        display: block;
    }

    .admin-box {
        position: static;
        margin-top: 25px;
    }

    .main {
        width: 100%;
        margin-left: 0;
        padding: 25px 16px 45px;
    }

    .stats {
        grid-template-columns: 1fr 1fr;
    }

    .analytics-two,
    .analytics-three {
        grid-template-columns: 1fr;
    }
}

@media (max-width: 600px) {

    .stats,
    .analytics-grid {
        grid-template-columns: 1fr;
    }

    .top {
        align-items: flex-start;
        flex-direction: column;
    }

    h1 {
        font-size: 26px;
    }

    .inventory-box {
        grid-template-columns: 1fr;
    }
}

</style>
</head>

<body>

<div class="layout">

<aside class="sidebar">

    <div class="brand">
        <div class="logo">V</div>
        <div>VR <span>Mart</span></div>
    </div>

    <div class="label">Administration</div>

    <nav class="menu">
        <a class="active" href="#overview">Control Center</a>
        <a href="#analytics">Analytics</a>
        <a href="#users">Users</a>
        <a href="#products">Products</a>
        <a href="#orders">Orders</a>
        <a href="#services">Service Requests</a>
        <a href="#reviews">Reviews</a>
    </nav>

    <div class="admin-box">

        <div class="account">
            <strong><%= admin.getUsername() %></strong>
            <span>VR Mart Administrator</span>
        </div>

        <a class="logout"
           href="<%= request.getContextPath() %>/logout">
            Sign out
        </a>

    </div>

</aside>

<main class="main">

<header class="top" id="overview">

    <div>
        <div class="eyebrow">VR MART ADMINISTRATION</div>

        <h1>Control Center</h1>

        <p class="subtitle">
            Full marketplace visibility and administration.
        </p>
    </div>

    <div class="assured">
        ✓ VR Mart Assured
    </div>

</header>


<section class="stats">

    <div class="stat">
        <span>Users</span>
        <strong><%= counts.get("users") %></strong>
    </div>

    <div class="stat">
        <span>Products</span>
        <strong><%= counts.get("products") %></strong>
    </div>

    <div class="stat">
        <span>Orders</span>
        <strong><%= counts.get("orders") %></strong>
    </div>

    <div class="stat">
        <span>Service Requests</span>
        <strong><%= counts.get("serviceRequests") %></strong>
    </div>

    <div class="stat">
        <span>Reviews</span>
        <strong><%= counts.get("reviews") %></strong>
    </div>

</section>


<section id="analytics">

    <div class="analytics-grid">

        <div class="analytics-card">
            <div class="title">Total Revenue</div>

            <strong>
                ₹<%= totalRevenue.setScale(
                        2,
                        java.math.RoundingMode.HALF_UP) %>
            </strong>

            <small>Delivered orders</small>
        </div>

        <div class="analytics-card">
            <div class="title">Products Sold</div>

            <strong>
                <%= productsSold %>
            </strong>

            <small>Delivered units</small>
        </div>

        <div class="analytics-card">
            <div class="title">Average Order</div>

            <strong>
                ₹<%= averageOrderValue.setScale(
                        2,
                        java.math.RoundingMode.HALF_UP) %>
            </strong>

            <small>Delivered order average</small>
        </div>

        <div class="analytics-card">
            <div class="title">Active Products</div>

            <strong>
                <%= activeProducts %>
            </strong>

            <small>Currently active listings</small>
        </div>

    </div>


    <div class="analytics-two">

        <div class="analytics-panel">

            <h2>Order Status Overview</h2>

            <%
                final long totalStatusOrders =
                        pendingOrders
                        + approvedOrders
                        + shippedOrders
                        + deliveredOrders
                        + cancelledOrders;
            %>

            <div class="status-row">
                <span class="status-name">Pending</span>

                <div class="progress">
                    <span style="width:
                        <%= totalStatusOrders == 0
                            ? 0
                            : (pendingOrders * 100
                                / totalStatusOrders) %>%">
                    </span>
                </div>

                <span class="status-value">
                    <%= pendingOrders %>
                </span>
            </div>

            <div class="status-row">
                <span class="status-name">Approved</span>

                <div class="progress">
                    <span style="width:
                        <%= totalStatusOrders == 0
                            ? 0
                            : (approvedOrders * 100
                                / totalStatusOrders) %>%">
                    </span>
                </div>

                <span class="status-value">
                    <%= approvedOrders %>
                </span>
            </div>

            <div class="status-row">
                <span class="status-name">Shipped</span>

                <div class="progress">
                    <span style="width:
                        <%= totalStatusOrders == 0
                            ? 0
                            : (shippedOrders * 100
                                / totalStatusOrders) %>%">
                    </span>
                </div>

                <span class="status-value">
                    <%= shippedOrders %>
                </span>
            </div>

            <div class="status-row">
                <span class="status-name">Delivered</span>

                <div class="progress">
                    <span style="width:
                        <%= totalStatusOrders == 0
                            ? 0
                            : (deliveredOrders * 100
                                / totalStatusOrders) %>%">
                    </span>
                </div>

                <span class="status-value">
                    <%= deliveredOrders %>
                </span>
            </div>

            <div class="status-row">
                <span class="status-name">Cancelled</span>

                <div class="progress">
                    <span style="width:
                        <%= totalStatusOrders == 0
                            ? 0
                            : (cancelledOrders * 100
                                / totalStatusOrders) %>%">
                    </span>
                </div>

                <span class="status-value">
                    <%= cancelledOrders %>
                </span>
            </div>

        </div>


        <div class="analytics-panel">

            <h2>User Statistics</h2>

            <div class="status-row">
                <span class="status-name">Buyers</span>

                <span class="status-value">
                    <%= buyers %>
                </span>
            </div>

            <div class="status-row">
                <span class="status-name">Sellers</span>

                <span class="status-value">
                    <%= sellers %>
                </span>
            </div>

            <div class="status-row">
                <span class="status-name">Administrators</span>

                <span class="status-value">
                    <%= admins %>
                </span>
            </div>

            <div class="status-row">
                <span class="status-name">Total Accounts</span>

                <span class="status-value">
                    <%= buyers + sellers + admins %>
                </span>
            </div>

        </div>

    </div>


    <div class="analytics-two">

        <div class="analytics-panel">

            <h2>Category Sales</h2>

            <%
                if (categoryStats.isEmpty()) {
            %>

                <div class="empty">
                    No delivered sales available yet.
                </div>

            <%
                } else {

                    for (Map.Entry<String, Long> entry
                            : categoryStats.entrySet()) {

                        final String category =
                                entry.getKey();

                        final long sold =
                                entry.getValue() == null
                                        ? 0L
                                        : entry.getValue();

                        final long width =
                                maxCategorySales == 0
                                        ? 0L
                                        : sold * 100
                                            / maxCategorySales;
            %>

                <div class="category-row">

                    <div class="category-head">

                        <span>
                            <%= category %>
                        </span>

                        <strong>
                            <%= sold %> sold
                        </strong>

                    </div>

                    <div class="progress">

                        <span style="width:<%= width %>%">
                        </span>

                    </div>

                </div>

            <%
                    }
                }
            %>

        </div>


        <div class="analytics-panel">

            <h2>Monthly Revenue</h2>

            <%
                if (monthlyRevenue.isEmpty()) {
            %>

                <div class="empty">
                    No delivered revenue available yet.
                </div>

            <%
                } else {
            %>

            <div class="revenue-chart">

            <%
                    for (Map.Entry<String, BigDecimal> entry
                            : monthlyRevenue.entrySet()) {

                        final BigDecimal revenue =
                                entry.getValue() == null
                                        ? BigDecimal.ZERO
                                        : entry.getValue();

                        int height = 0;

                        if (maxMonthlyRevenue.compareTo(
                                BigDecimal.ZERO) > 0) {

                            height =
                                    revenue.multiply(
                                            BigDecimal.valueOf(100))
                                            .divide(
                                                maxMonthlyRevenue,
                                                0,
                                                java.math.RoundingMode.DOWN)
                                            .intValue();
                        }
            %>

                <div class="revenue-column">

                    <div class="revenue-value">
                        ₹<%= revenue.setScale(
                                2,
                                java.math.RoundingMode.HALF_UP) %>
                    </div>

                    <div class="revenue-bar-area">

                        <div class="revenue-bar"
                             style="height:<%= Math.max(
                                 height, 3) %>%">
                        </div>

                    </div>

                    <div class="revenue-month">
                        <%= entry.getKey() %>
                    </div>

                </div>

            <%
                    }
            %>

            </div>

            <%
                }
            %>

        </div>

    </div>


    <div class="analytics-three">

        <div class="analytics-panel">

            <h2>Inventory Overview</h2>

            <div class="inventory-box">

                <div class="inventory-item">
                    <strong>
                        <%= activeProducts %>
                    </strong>

                    <span>Active</span>
                </div>

                <div class="inventory-item">
                    <strong>
                        <%= lowStockProducts %>
                    </strong>

                    <span>Low Stock</span>
                </div>

                <div class="inventory-item">
                    <strong>
                        <%= outOfStockProducts %>
                    </strong>

                    <span>Out of Stock</span>
                </div>

            </div>

        </div>


        <div class="analytics-panel">

            <h2>Marketplace Overview</h2>

            <div class="status-row">
                <span class="status-name">
                    Users
                </span>

                <span class="status-value">
                    <%= counts.get("users") %>
                </span>
            </div>

            <div class="status-row">
                <span class="status-name">
                    Products
                </span>

                <span class="status-value">
                    <%= counts.get("products") %>
                </span>
            </div>

            <div class="status-row">
                <span class="status-name">
                    Orders
                </span>

                <span class="status-value">
                    <%= counts.get("orders") %>
                </span>
            </div>

            <div class="status-row">
                <span class="status-name">
                    Reviews
                </span>

                <span class="status-value">
                    <%= counts.get("reviews") %>
                </span>
            </div>

        </div>


        <div class="analytics-panel">

            <h2>Sales Snapshot</h2>

            <div class="status-row">
                <span class="status-name">
                    Delivered Orders
                </span>

                <span class="status-value">
                    <%= deliveredOrders %>
                </span>
            </div>

            <div class="status-row">
                <span class="status-name">
                    Units Sold
                </span>

                <span class="status-value">
                    <%= productsSold %>
                </span>
            </div>

            <div class="status-row">
                <span class="status-name">
                    Revenue
                </span>

                <span class="status-value">
                    ₹<%= totalRevenue.setScale(
                            2,
                            java.math.RoundingMode.HALF_UP) %>
                </span>
            </div>

            <div class="status-row">
                <span class="status-name">
                    Average Order
                </span>

                <span class="status-value">
                    ₹<%= averageOrderValue.setScale(
                            2,
                            java.math.RoundingMode.HALF_UP) %>
                </span>
            </div>

        </div>

    </div>

</section>


<section class="section" id="users">

<h2>User Management</h2>

<table>

<tr>
    <th>ID</th>
    <th>User</th>
    <th>Email</th>
    <th>Role</th>
    <th>Action</th>
</tr>

<%
    for (Map<String, Object> row : users) {

        final long rowUserId =
                ((Number) row.get("id")).longValue();
%>

<tr>

<td>
    <%= row.get("id") %>
</td>

<td>
    <%= row.get("username") %>
</td>

<td>
    <%= row.get("email") %>
</td>

<td>
    <span class="badge">
        <%= row.get("role") %>
    </span>
</td>

<td>

<%
        if (admin.getId() != null
                && admin.getId().longValue() == rowUserId) {
%>

    <span>Current Admin</span>

<%
        } else {
%>

    <div class="action-group">

        <form class="inline-form"
              method="post"
              action="<%= request.getContextPath() %>/admin/action">

            <input type="hidden"
                   name="action"
                   value="userRole">

            <input type="hidden"
                   name="userId"
                   value="<%= row.get("id") %>">

            <select name="role">

                <option value="BUYER"
                    <%= "BUYER".equals(row.get("role"))
                            ? "selected" : "" %>>
                    BUYER
                </option>

                <option value="SELLER"
                    <%= "SELLER".equals(row.get("role"))
                            ? "selected" : "" %>>
                    SELLER
                </option>

                <option value="ADMIN"
                    <%= "ADMIN".equals(row.get("role"))
                            ? "selected" : "" %>>
                    ADMIN
                </option>

            </select>

            <button type="submit">
                Update
            </button>

        </form>


        <form class="inline-form"
              method="post"
              action="<%= request.getContextPath() %>/admin/action"
              onsubmit="return confirm(
                  'Delete this user account?');">

            <input type="hidden"
                   name="action"
                   value="deleteUser">

            <input type="hidden"
                   name="userId"
                   value="<%= row.get("id") %>">

            <button type="submit"
                    class="delete-button">
                Delete
            </button>

        </form>

    </div>

<%
        }
%>

</td>

</tr>

<%
    }
%>

</table>

</section>


<section class="section" id="products">

<h2>Product Management &amp; VR Mart Assurance</h2>

<table>

<tr>
    <th>ID</th>
    <th>Product</th>
    <th>Category</th>
    <th>Seller</th>
    <th>Price</th>
    <th>Stock</th>
    <th>State</th>
    <th>Assurance</th>
    <th>Action</th>
</tr>

<%
    for (Map<String, Object> row : products) {
%>

<tr>

<td>
    <%= row.get("id") %>
</td>

<td>

    <%= row.get("name") %>

<%
    if (Boolean.TRUE.equals(
            row.get("is_vr_mart_assured"))) {
%>

    <span class="assured-mini">
        VR Mart Assured
    </span>

<%
    }
%>

</td>

<td>
    <%= row.get("category") %>
</td>

<td>
    <%= row.get("seller") %>
</td>

<td>
    ₹<%= row.get("price") %>
</td>

<td>
    <%= row.get("stock_qty") %>
</td>

<td class="<%= Boolean.TRUE.equals(
        row.get("is_active"))
        ? "" : "inactive" %>">

    <%= Boolean.TRUE.equals(row.get("is_active"))
            ? "ACTIVE"
            : "INACTIVE" %>

</td>

<td>

    <span class="badge">
        <%= Boolean.TRUE.equals(
                row.get("is_vr_mart_assured"))
                ? "ASSURED"
                : "NOT ASSURED" %>
    </span>

</td>

<td>

<form class="inline-form"
      method="post"
      action="<%= request.getContextPath() %>/admin/action">

    <input type="hidden"
           name="action"
           value="productActive">

    <input type="hidden"
           name="productId"
           value="<%= row.get("id") %>">

    <input type="hidden"
           name="active"
           value="<%= !Boolean.TRUE.equals(
               row.get("is_active")) %>">

    <button type="submit">

        <%= Boolean.TRUE.equals(row.get("is_active"))
                ? "Disable"
                : "Enable" %>

    </button>

</form>


<form class="inline-form"
      method="post"
      action="<%= request.getContextPath() %>/admin/action">

    <input type="hidden"
           name="action"
           value="productAssurance">

    <input type="hidden"
           name="productId"
           value="<%= row.get("id") %>">

    <input type="hidden"
           name="assured"
           value="<%= !Boolean.TRUE.equals(
               row.get("is_vr_mart_assured")) %>">

    <button type="submit">

        <%= Boolean.TRUE.equals(
                row.get("is_vr_mart_assured"))
                ? "Remove Assurance"
                : "Assure Product" %>

    </button>

</form>


<%
    if (!Boolean.TRUE.equals(row.get("is_active"))) {
%>

<form class="inline-form"
      method="post"
      action="<%= request.getContextPath() %>/admin/action"
      onsubmit="return confirm(
          'Permanently delete this inactive product?');">

    <input type="hidden"
           name="action"
           value="deleteProduct">

    <input type="hidden"
           name="productId"
           value="<%= row.get("id") %>">

    <button type="submit"
            class="delete-button">

        Delete Product

    </button>

</form>

<%
    }
%>

</td>

</tr>

<%
    }
%>

</table>

</section>


<section class="section" id="orders">

<h2>Order Management</h2>

<table>

<tr>
    <th>ID</th>
    <th>Buyer</th>
    <th>Total</th>
    <th>Status</th>
    <th>Action</th>
</tr>

<%
    for (Map<String, Object> row : orders) {
%>

<tr>

<td>
    #<%= row.get("id") %>
</td>

<td>
    <%= row.get("buyer") %>
</td>

<td>
    ₹<%= row.get("total_amount") %>
</td>

<td>
    <span class="badge">
        <%= row.get("status") %>
    </span>
</td>

<td>

<form class="inline-form"
      method="post"
      action="<%= request.getContextPath() %>/admin/action">

    <input type="hidden"
           name="action"
           value="orderStatus">

    <input type="hidden"
           name="orderId"
           value="<%= row.get("id") %>">

    <select name="status">

        <option value="PENDING">
            PENDING
        </option>

        <option value="APPROVED">
            APPROVED
        </option>

        <option value="SHIPPED">
            SHIPPED
        </option>

        <option value="DELIVERED">
            DELIVERED
        </option>

        <option value="CANCELLED">
            CANCELLED
        </option>

    </select>

    <button type="submit">
        Update
    </button>

</form>

</td>

</tr>

<%
    }
%>

</table>

</section>


<section class="section" id="services">

<h2>Service Requests</h2>

<table>

<tr>
    <th>ID</th>
    <th>Order</th>
    <th>Buyer</th>
    <th>Product</th>
    <th>Type</th>
    <th>Status</th>
</tr>

<%
    for (Map<String, Object> row : serviceRequests) {
%>

<tr>

<td>
    #<%= row.get("id") %>
</td>

<td>
    #<%= row.get("order_id") %>
</td>

<td>
    <%= row.get("buyer") %>
</td>

<td>
    <%= row.get("product") %>
</td>

<td>
    <%= row.get("request_type") %>
</td>

<td>
    <span class="badge">
        <%= row.get("status") %>
    </span>
</td>

</tr>

<%
    }
%>

</table>

</section>


<section class="section" id="reviews">

<h2>Reviews &amp; Ratings</h2>

<table>

<tr>
    <th>ID</th>
    <th>Buyer</th>
    <th>Product</th>
    <th>Rating</th>
    <th>Comment</th>
</tr>

<%
    for (Map<String, Object> row : reviews) {
%>

<tr>

<td>
    #<%= row.get("id") %>
</td>

<td>
    <%= row.get("buyer") %>
</td>

<td>
    <%= row.get("product") %>
</td>

<td>
    ★ <%= row.get("rating") %>/5
</td>

<td>
    <%= row.get("comment") %>
</td>

</tr>

<%
    }
%>

</table>

</section>


</main>

</div>

</body>
</html>
