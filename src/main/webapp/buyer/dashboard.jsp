<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="com.vrmart.model.User" %>

<%
    User currentUser = (User) session.getAttribute("user");

    if (currentUser == null) {
        response.sendRedirect(
            request.getContextPath() + "/login"
        );
        return;
    }

    String username = currentUser.getUsername();
%>

<!DOCTYPE html>
<html lang="en">

<head>

    <meta charset="UTF-8">

    <meta name="viewport"
          content="width=device-width, initial-scale=1.0">

    <title>VR Mart | Dashboard</title>

    <style>

        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
        }

        :root {
            --bg: #070914;
            --surface: #0d1020;
            --surface-soft: #121628;
            --border: rgba(255, 255, 255, 0.08);
            --text: #f7f7fb;
            --muted: #858ca3;
            --muted-light: #aeb4c7;
            --primary: #7c5cff;
            --primary-light: #a78bfa;
            --cyan: #67e8f9;
            --success: #34d399;
        }

        html {
            scroll-behavior: smooth;
        }

        body {
            min-height: 100vh;
            background:
                radial-gradient(
                    circle at 75% 0%,
                    rgba(124, 92, 255, 0.13),
                    transparent 30%
                ),
                radial-gradient(
                    circle at 20% 100%,
                    rgba(103, 232, 249, 0.06),
                    transparent 25%
                ),
                var(--bg);
            color: var(--text);
            font-family:
                Inter,
                ui-sans-serif,
                -apple-system,
                BlinkMacSystemFont,
                "Segoe UI",
                sans-serif;
        }

        a {
            color: inherit;
        }

        .layout {
            min-height: 100vh;
        }

        /* =========================
           SIDEBAR
           ========================= */

        .sidebar {
            position: fixed;
            inset: 0 auto 0 0;
            width: 250px;
            padding: 25px 16px;
            display: flex;
            flex-direction: column;
            background:
                linear-gradient(
                    180deg,
                    rgba(14, 17, 32, 0.98),
                    rgba(9, 11, 22, 0.98)
                );
            border-right: 1px solid var(--border);
            z-index: 20;
        }

        .brand {
            display: flex;
            align-items: center;
            gap: 12px;
            padding: 4px 10px 34px;
        }

        .logo {
            width: 43px;
            height: 43px;
            display: grid;
            place-items: center;
            border-radius: 14px;
            background:
                linear-gradient(
                    135deg,
                    #635bff,
                    #9b5cff
                );
            font-size: 20px;
            font-weight: 900;
            box-shadow:
                0 12px 30px rgba(124, 92, 255, 0.32);
        }

        .brand-text {
            font-size: 19px;
            font-weight: 850;
            letter-spacing: -0.5px;
        }

        .brand-text span {
            color: var(--primary-light);
        }

        .menu-label {
            padding: 0 12px;
            margin-bottom: 9px;
            color: #50586d;
            font-size: 10px;
            font-weight: 800;
            letter-spacing: 1.5px;
            text-transform: uppercase;
        }

        .menu {
            display: flex;
            flex-direction: column;
            gap: 4px;
        }

        .menu a {
            position: relative;
            display: flex;
            align-items: center;
            gap: 12px;
            padding: 12px 13px;
            border-radius: 12px;
            color: #858da3;
            text-decoration: none;
            font-size: 13px;
            font-weight: 600;
            transition:
                background 0.2s ease,
                color 0.2s ease,
                transform 0.2s ease;
        }

        .menu a:hover {
            color: #ffffff;
            background: rgba(255, 255, 255, 0.045);
            transform: translateX(2px);
        }

        .menu a.active {
            color: #ffffff;
            background:
                linear-gradient(
                    90deg,
                    rgba(124, 92, 255, 0.18),
                    rgba(124, 92, 255, 0.06)
                );
        }

        .menu a.active::before {
            content: "";
            position: absolute;
            left: 0;
            top: 9px;
            bottom: 9px;
            width: 3px;
            border-radius: 0 4px 4px 0;
            background: var(--primary-light);
            box-shadow: 0 0 14px rgba(167, 139, 250, 0.7);
        }

        .menu-icon {
            width: 22px;
            text-align: center;
            font-size: 16px;
        }

        .sidebar-spacer {
            flex: 1;
        }

        .account-card {
            padding: 14px;
            margin: 0 2px 10px;
            border: 1px solid var(--border);
            border-radius: 15px;
            background: rgba(255, 255, 255, 0.035);
        }

        .account-row {
            display: flex;
            align-items: center;
            gap: 10px;
        }

        .avatar {
            width: 34px;
            height: 34px;
            display: grid;
            place-items: center;
            flex-shrink: 0;
            border-radius: 11px;
            background:
                linear-gradient(
                    135deg,
                    #6366f1,
                    #a855f7
                );
            font-size: 13px;
            font-weight: 800;
        }

        .account-name {
            overflow: hidden;
        }

        .account-name strong {
            display: block;
            overflow: hidden;
            margin-bottom: 3px;
            font-size: 12px;
            text-overflow: ellipsis;
            white-space: nowrap;
        }

        .account-name span {
            color: #687187;
            font-size: 10px;
        }

        .logout {
            display: block;
            padding: 10px;
            border-radius: 10px;
            color: #fca5a5;
            background: rgba(239, 68, 68, 0.055);
            text-align: center;
            text-decoration: none;
            font-size: 11px;
            font-weight: 700;
            transition: 0.2s ease;
        }

        .logout:hover {
            background: rgba(239, 68, 68, 0.11);
        }

        /* =========================
           MAIN
           ========================= */

        .main {
            min-height: 100vh;
            margin-left: 250px;
            padding: 27px 38px 50px;
        }

        .topbar {
            display: flex;
            align-items: center;
            justify-content: space-between;
            gap: 25px;
            margin-bottom: 30px;
        }

        .welcome small {
            color: #70798e;
            font-size: 10px;
            font-weight: 800;
            letter-spacing: 1.5px;
        }

        .welcome h1 {
            margin-top: 7px;
            font-size: 28px;
            font-weight: 800;
            letter-spacing: -1.1px;
        }

        .welcome h1 span {
            color: var(--primary-light);
        }

        .top-right {
            display: flex;
            align-items: center;
            gap: 10px;
        }

        .search {
            width: 240px;
            height: 42px;
            display: flex;
            align-items: center;
            gap: 9px;
            padding: 0 14px;
            border: 1px solid var(--border);
            border-radius: 12px;
            background: rgba(255, 255, 255, 0.035);
        }

        .search span {
            color: #71798d;
            font-size: 15px;
        }

        .search input {
            width: 100%;
            border: 0;
            outline: 0;
            background: transparent;
            color: #ffffff;
            font: inherit;
            font-size: 11px;
        }

        .search input::placeholder {
            color: #666f83;
        }

        .icon-button {
            width: 42px;
            height: 42px;
            display: grid;
            place-items: center;
            border: 1px solid var(--border);
            border-radius: 12px;
            background: rgba(255, 255, 255, 0.035);
            color: #aab1c2;
            text-decoration: none;
            font-size: 17px;
            transition: 0.2s ease;
        }

        .icon-button:hover {
            color: #ffffff;
            border-color: rgba(167, 139, 250, 0.3);
            background: rgba(124, 92, 255, 0.09);
        }

        /* =========================
           HERO
           ========================= */

        .hero {
            position: relative;
            min-height: 275px;
            overflow: hidden;
            padding: 37px;
            margin-bottom: 27px;
            border: 1px solid rgba(167, 139, 250, 0.15);
            border-radius: 24px;
            background:
                radial-gradient(
                    circle at 80% 20%,
                    rgba(167, 139, 250, 0.25),
                    transparent 28%
                ),
                radial-gradient(
                    circle at 100% 100%,
                    rgba(103, 232, 249, 0.08),
                    transparent 30%
                ),
                linear-gradient(
                    135deg,
                    rgba(99, 102, 241, 0.16),
                    rgba(139, 92, 246, 0.045)
                );
        }

        .hero::before {
            content: "";
            position: absolute;
            width: 360px;
            height: 360px;
            right: -100px;
            top: -160px;
            border: 1px solid rgba(255, 255, 255, 0.045);
            border-radius: 50%;
        }

        .hero::after {
            content: "";
            position: absolute;
            width: 220px;
            height: 220px;
            right: 35px;
            bottom: -145px;
            border: 1px solid rgba(255, 255, 255, 0.035);
            border-radius: 50%;
        }

        .hero-content {
            position: relative;
            z-index: 2;
            max-width: 650px;
        }

        .hero-label {
            display: inline-flex;
            align-items: center;
            gap: 7px;
            margin-bottom: 14px;
            color: #a5b4fc;
            font-size: 10px;
            font-weight: 800;
            letter-spacing: 1.7px;
            text-transform: uppercase;
        }

        .hero-dot {
            width: 6px;
            height: 6px;
            border-radius: 50%;
            background: var(--success);
            box-shadow: 0 0 10px rgba(52, 211, 153, 0.7);
        }

        .hero h2 {
            max-width: 600px;
            margin-bottom: 13px;
            font-size: 35px;
            line-height: 1.1;
            letter-spacing: -1.6px;
        }

        .hero h2 span {
            background:
                linear-gradient(
                    90deg,
                    #a5b4fc,
                    #c084fc,
                    #67e8f9
                );
            -webkit-background-clip: text;
            -webkit-text-fill-color: transparent;
            background-clip: text;
            color: transparent;
        }

        .hero p {
            max-width: 570px;
            color: #858da3;
            font-size: 13px;
            line-height: 1.75;
        }

        .hero-actions {
            display: flex;
            align-items: center;
            gap: 11px;
            margin-top: 21px;
        }

        .primary-button,
        .secondary-button {
            display: inline-flex;
            align-items: center;
            justify-content: center;
            min-height: 41px;
            padding: 0 17px;
            border-radius: 11px;
            text-decoration: none;
            font-size: 11px;
            font-weight: 800;
            transition: 0.2s ease;
        }

        .primary-button {
            color: #ffffff;
            background:
                linear-gradient(
                    135deg,
                    #6366f1,
                    #8b5cf6
                );
            box-shadow:
                0 10px 25px rgba(99, 102, 241, 0.22);
        }

        .primary-button:hover {
            transform: translateY(-2px);
            box-shadow:
                0 14px 30px rgba(99, 102, 241, 0.3);
        }

        .secondary-button {
            color: #aeb5c8;
            border: 1px solid var(--border);
            background: rgba(255, 255, 255, 0.035);
        }

        .secondary-button:hover {
            color: #ffffff;
            background: rgba(255, 255, 255, 0.06);
        }

        /* =========================
           SECTION HEADER
           ========================= */

        .section-header {
            display: flex;
            align-items: center;
            justify-content: space-between;
            margin-bottom: 14px;
        }

        .section-header h3 {
            font-size: 16px;
            letter-spacing: -0.3px;
        }

        .section-header p {
            margin-top: 4px;
            color: #626b80;
            font-size: 10px;
        }

        .view-link {
            color: #9182ff;
            font-size: 11px;
            font-weight: 700;
            text-decoration: none;
        }

        .view-link:hover {
            color: #b2a7ff;
        }

        /* =========================
           QUICK CARDS
           ========================= */

        .quick-grid {
            display: grid;
            grid-template-columns:
                repeat(4, minmax(0, 1fr));
            gap: 13px;
            margin-bottom: 28px;
        }

        .quick-card {
            position: relative;
            overflow: hidden;
            min-height: 145px;
            padding: 20px;
            border: 1px solid var(--border);
            border-radius: 17px;
            background:
                linear-gradient(
                    145deg,
                    rgba(255, 255, 255, 0.045),
                    rgba(255, 255, 255, 0.018)
                );
            transition:
                transform 0.25s ease,
                border-color 0.25s ease,
                background 0.25s ease;
        }

        .quick-card-link {
            display: block;
            color: inherit;
            text-decoration: none;
        }

        .quick-card::after {
            content: "";
            position: absolute;
            width: 90px;
            height: 90px;
            right: -45px;
            bottom: -45px;
            border-radius: 50%;
            background: rgba(124, 92, 255, 0.08);
        }

        .quick-card:hover {
            transform: translateY(-4px);
            border-color: rgba(167, 139, 250, 0.24);
            background:
                linear-gradient(
                    145deg,
                    rgba(124, 92, 255, 0.09),
                    rgba(255, 255, 255, 0.025)
                );
        }

        .quick-icon {
            width: 38px;
            height: 38px;
            display: grid;
            place-items: center;
            margin-bottom: 18px;
            border: 1px solid rgba(167, 139, 250, 0.12);
            border-radius: 11px;
            color: #b4a9ff;
            background: rgba(124, 92, 255, 0.1);
            font-size: 16px;
        }

        .quick-card h4 {
            margin-bottom: 6px;
            font-size: 13px;
        }

        .quick-card p {
            color: #666f83;
            font-size: 10px;
            line-height: 1.6;
        }

        /* =========================
           BOTTOM GRID
           ========================= */

        .bottom-grid {
            display: grid;
            grid-template-columns:
                minmax(0, 1.45fr)
                minmax(280px, 0.8fr);
            gap: 16px;
        }

        .panel {
            padding: 21px;
            border: 1px solid var(--border);
            border-radius: 18px;
            background:
                linear-gradient(
                    145deg,
                    rgba(255, 255, 255, 0.04),
                    rgba(255, 255, 255, 0.018)
                );
        }

        .panel-header {
            display: flex;
            align-items: center;
            justify-content: space-between;
            margin-bottom: 17px;
        }

        .panel-header h3 {
            font-size: 14px;
        }

        .panel-header span {
            color: #60697c;
            font-size: 10px;
        }

        /* =========================
           FEATURE ROWS
           ========================= */

        .feature-list {
            display: flex;
            flex-direction: column;
            gap: 10px;
        }

        .feature {
            display: flex;
            align-items: center;
            gap: 13px;
            padding: 12px;
            border: 1px solid rgba(255, 255, 255, 0.045);
            border-radius: 13px;
            background: rgba(255, 255, 255, 0.02);
        }

        .feature-icon {
            width: 39px;
            height: 39px;
            display: grid;
            place-items: center;
            flex-shrink: 0;
            border-radius: 11px;
            background:
                linear-gradient(
                    135deg,
                    rgba(99, 102, 241, 0.16),
                    rgba(168, 85, 247, 0.11)
                );
            color: #a5b4fc;
        }

        .feature-text {
            min-width: 0;
        }

        .feature-text strong {
            display: block;
            margin-bottom: 4px;
            font-size: 11px;
        }

        .feature-text span {
            color: #656e82;
            font-size: 9px;
        }

        .feature-arrow {
            margin-left: auto;
            color: #596276;
            font-size: 14px;
        }

        /* =========================
           STATUS PANEL
           ========================= */

        .status-card {
            padding: 17px;
            margin-bottom: 12px;
            border: 1px solid rgba(52, 211, 153, 0.11);
            border-radius: 14px;
            background: rgba(52, 211, 153, 0.035);
        }

        .status-top {
            display: flex;
            align-items: center;
            justify-content: space-between;
            margin-bottom: 10px;
        }

        .status-label {
            color: #8c95a9;
            font-size: 10px;
            font-weight: 700;
        }

        .status-live {
            display: flex;
            align-items: center;
            gap: 5px;
            color: #6ee7b7;
            font-size: 9px;
            font-weight: 800;
        }

        .status-live::before {
            content: "";
            width: 6px;
            height: 6px;
            border-radius: 50%;
            background: var(--success);
            box-shadow: 0 0 9px rgba(52, 211, 153, 0.7);
        }

        .status-title {
            margin-bottom: 5px;
            font-size: 13px;
        }

        .status-description {
            color: #687186;
            font-size: 9px;
            line-height: 1.6;
        }

        .account-info {
            display: flex;
            flex-direction: column;
            gap: 9px;
        }

        .info-row {
            display: flex;
            align-items: center;
            justify-content: space-between;
            padding-bottom: 9px;
            border-bottom: 1px solid rgba(255, 255, 255, 0.045);
        }

        .info-row:last-child {
            padding-bottom: 0;
            border-bottom: 0;
        }

        .info-row span:first-child {
            color: #646d81;
            font-size: 9px;
        }

        .info-row span:last-child {
            color: #aeb5c7;
            font-size: 9px;
            font-weight: 700;
        }

        /* =========================
           MOBILE
           ========================= */

        .mobile-top {
            display: none;
        }

        @media (max-width: 1100px) {

            .quick-grid {
                grid-template-columns:
                    repeat(2, minmax(0, 1fr));
            }

            .search {
                width: 190px;
            }
        }

        @media (max-width: 850px) {

            .sidebar {
                display: none;
            }

            .mobile-top {
                display: flex;
                align-items: center;
                justify-content: space-between;
                margin-bottom: 22px;
            }

            .main {
                width: 100%;
                margin-left: 0;
                padding: 22px;
            }

            .topbar {
                margin-bottom: 25px;
            }

            .bottom-grid {
                grid-template-columns: 1fr;
            }
        }

        @media (max-width: 620px) {

            .main {
                padding: 17px;
            }

            .topbar {
                align-items: flex-start;
            }

            .top-right {
                display: none;
            }

            .welcome h1 {
                font-size: 24px;
            }

            .hero {
                min-height: auto;
                padding: 26px;
            }

            .hero h2 {
                font-size: 28px;
            }

            .hero-actions {
                flex-direction: column;
                align-items: stretch;
            }

            .primary-button,
            .secondary-button {
                width: 100%;
            }

            .quick-grid {
                grid-template-columns: 1fr;
            }
        }

    </style>

</head>

<body>

<div class="layout">

    <!-- =========================
         SIDEBAR
         ========================= -->

    <aside class="sidebar">

        <div class="brand">

            <div class="logo">V</div>

            <div class="brand-text">
                VR <span>Mart</span>
            </div>

        </div>

        <div class="menu-label">
            Marketplace
        </div>

        <nav class="menu">

            <a href="#" class="active">
                <span class="menu-icon">⌂</span>
                Dashboard
            </a>

            <a href="<%= request.getContextPath() %>/products">
                <span class="menu-icon">⌕</span>
                Explore Products
            </a>

            <a href="#">
                <span class="menu-icon">♡</span>
                Wishlist
            </a>

            <a href="#">
                <span class="menu-icon">▣</span>
                My Orders
            </a>

            <!-- CHANGED: Cart now opens buyer cart -->
            <a href="<%= request.getContextPath() %>/buyer/cart">
                <span class="menu-icon">🛒</span>
                Cart
            </a>

        </nav>

        <div class="menu-label"
             style="margin-top: 29px;">
            Account
        </div>

        <nav class="menu">

            <a href="#">
                <span class="menu-icon">◎</span>
                Profile
            </a>

            <a href="#">
                <span class="menu-icon">⚙</span>
                Settings
            </a>

        </nav>

        <div class="sidebar-spacer"></div>

        <div class="account-card">

            <div class="account-row">

                <div class="avatar">
                    <%= username.substring(0, 1).toUpperCase() %>
                </div>

                <div class="account-name">

                    <strong>
                        <%= username %>
                    </strong>

                    <span>
                        Buyer account
                    </span>

                </div>

            </div>

        </div>

        <a class="logout"
           href="<%= request.getContextPath() %>/logout">
            Sign out
        </a>

    </aside>


    <!-- =========================
         MAIN CONTENT
         ========================= -->

    <main class="main">

        <div class="mobile-top">

            <div class="brand">

                <div class="logo">V</div>

                <div class="brand-text">
                    VR <span>Mart</span>
                </div>

            </div>

            <a class="icon-button"
               href="<%= request.getContextPath() %>/logout">
                ↪
            </a>

        </div>


        <!-- TOP BAR -->

        <header class="topbar">

            <div class="welcome">

                <small>
                    BUYER DASHBOARD
                </small>

                <h1>
                    Welcome back,
                    <span><%= username %></span>
                </h1>

            </div>

            <div class="top-right">

                <div class="search">

                    <span>⌕</span>

                    <input
                        type="text"
                        placeholder="Search products...">

                </div>

                <a href="#"
                   class="icon-button"
                   aria-label="Wishlist">
                    ♡
                </a>

                <!-- CHANGED: Top-right cart now opens buyer cart -->
                <a href="<%= request.getContextPath() %>/buyer/cart"
                   class="icon-button"
                   aria-label="Cart">
                    🛒
                </a>

            </div>

        </header>


        <!-- HERO -->

        <section class="hero">

            <div class="hero-content">

                <div class="hero-label">

                    <span class="hero-dot"></span>

                    VR Mart Marketplace

                </div>

                <h2>
                    Discover products
                    <span>worth bringing home.</span>
                </h2>

                <p>
                    Explore products from trusted sellers,
                    discover new deals and manage your
                    shopping experience from one place.
                </p>

                <div class="hero-actions">

                    <a href="<%= request.getContextPath() %>/products"
                       class="primary-button">
                        Explore marketplace&nbsp; →
                    </a>

                    <a href="#"
                       class="secondary-button">
                        View my orders
                    </a>

                </div>

            </div>

        </section>


        <!-- QUICK ACCESS -->

        <div class="section-header">

            <div>

                <h3>
                    Quick access
                </h3>

                <p>
                    Everything you need, one click away
                </p>

            </div>

            <a href="<%= request.getContextPath() %>/products"
               class="view-link">
                View all →
            </a>

        </div>


        <section class="quick-grid">

            <!-- EXPLORE PRODUCTS -->

            <a class="quick-card quick-card-link"
               href="<%= request.getContextPath() %>/products">

                <div class="quick-icon">
                    ⌕
                </div>

                <h4>
                    Explore Products
                </h4>

                <p>
                    Browse the latest products
                    available on VR Mart.
                </p>

            </a>


            <!-- WISHLIST -->

            <article class="quick-card">

                <div class="quick-icon">
                    ♡
                </div>

                <h4>
                    Wishlist
                </h4>

                <p>
                    Keep your favourite products
                    saved for later.
                </p>

            </article>


            <!-- MY ORDERS -->

            <article class="quick-card">

                <div class="quick-icon">
                    ▣
                </div>

                <h4>
                    My Orders
                </h4>

                <p>
                    Track purchases and access
                    your complete order history.
                </p>

            </article>


            <!-- CHANGED: SHOPPING CART QUICK CARD -->

            <a class="quick-card quick-card-link"
               href="<%= request.getContextPath() %>/buyer/cart">

                <div class="quick-icon">
                    🛒
                </div>

                <h4>
                    Shopping Cart
                </h4>

                <p>
                    Review your selected products
                    before checkout.
                </p>

            </a>

        </section>


        <!-- LOWER CONTENT -->

        <section class="bottom-grid">

            <!-- MARKETPLACE FEATURES -->

            <div class="panel">

                <div class="panel-header">

                    <h3>
                        Your marketplace
                    </h3>

                    <span>
                        VR MART
                    </span>

                </div>


                <div class="feature-list">

                    <div class="feature">

                        <div class="feature-icon">
                            ✦
                        </div>

                        <div class="feature-text">

                            <strong>
                                Discover new products
                            </strong>

                            <span>
                                Browse products across multiple categories.
                            </span>

                        </div>

                        <div class="feature-arrow">
                            →
                        </div>

                    </div>


                    <div class="feature">

                        <div class="feature-icon">
                            ◈
                        </div>

                        <div class="feature-text">

                            <strong>
                                Shop from trusted sellers
                            </strong>

                            <span>
                                Find products offered through the VR Mart marketplace.
                            </span>

                        </div>

                        <div class="feature-arrow">
                            →
                        </div>

                    </div>


                    <div class="feature">

                        <div class="feature-icon">
                            ✓
                        </div>

                        <div class="feature-text">

                            <strong>
                                Manage your purchases
                            </strong>

                            <span>
                                Keep your shopping activity organised in one place.
                            </span>

                        </div>

                        <div class="feature-arrow">
                            →
                        </div>

                    </div>

                </div>

            </div>


            <!-- ACCOUNT STATUS -->

            <div class="panel">

                <div class="panel-header">

                    <h3>
                        Account overview
                    </h3>

                    <span>
                        ACTIVE
                    </span>

                </div>


                <div class="status-card">

                    <div class="status-top">

                        <span class="status-label">
                            Account status
                        </span>

                        <span class="status-live">
                            Active
                        </span>

                    </div>

                    <div class="status-title">
                        Your account is ready
                    </div>

                    <div class="status-description">
                        You can explore the marketplace
                        and manage your shopping experience.
                    </div>

                </div>


                <div class="account-info">

                    <div class="info-row">

                        <span>
                            Username
                        </span>

                        <span>
                            <%= username %>
                        </span>

                    </div>

                    <div class="info-row">

                        <span>
                            Account type
                        </span>

                        <span>
                            Buyer
                        </span>

                    </div>

                    <div class="info-row">

                        <span>
                            Marketplace
                        </span>

                        <span>
                            VR Mart
                        </span>

                    </div>

                </div>

            </div>

        </section>

    </main>

</div>

</body>

</html>
