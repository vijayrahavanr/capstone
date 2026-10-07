<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="javax.servlet.http.HttpSession"%>

<%
    final HttpSession sessionObj = request.getSession(false);

    if (sessionObj == null
            || sessionObj.getAttribute("user") == null) {
        response.sendRedirect(request.getContextPath() + "/login");
        return;
    }

    final String role = String.valueOf(
            sessionObj.getAttribute("role"));

    final String contextPath = request.getContextPath();

    final Object successObject =
            request.getAttribute("success");

    final Object errorObject =
            request.getAttribute("error");

    final String success = successObject != null
            ? successObject.toString()
            : "";

    final String error = errorObject != null
            ? errorObject.toString()
            : "";

    final boolean buyer =
            "BUYER".equalsIgnoreCase(role);
%>

<!DOCTYPE html>
<html lang="en">

<head>

    <meta charset="UTF-8">

    <meta name="viewport"
          content="width=device-width, initial-scale=1.0">

    <title>VR Mart | Change Password</title>

    <style>

        * {
            box-sizing: border-box;
            margin: 0;
            padding: 0;
        }

        body {
            font-family: Arial, Helvetica, sans-serif;
            background:
                radial-gradient(
                    circle at 80% 10%,
                    rgba(112, 75, 255, 0.10),
                    transparent 32%
                ),
                #070814;
            color: #f8f8ff;
            min-height: 100vh;
        }

        .app {
            display: flex;
            min-height: 100vh;
        }

        .sidebar {
            width: 270px;
            min-height: 100vh;
            position: fixed;
            left: 0;
            top: 0;
            bottom: 0;
            background: #0b0d1a;
            border-right: 1px solid #202236;
            padding: 28px 18px 20px;
            display: flex;
            flex-direction: column;
            z-index: 10;
        }

        .brand {
            display: flex;
            align-items: center;
            gap: 13px;
            padding: 2px 10px 36px;
        }

        .brand-logo {
            width: 47px;
            height: 47px;
            border-radius: 14px;
            display: flex;
            align-items: center;
            justify-content: center;
            background: linear-gradient(
                135deg,
                #7655ff,
                #9b65ff
            );
            color: white;
            font-size: 26px;
            font-weight: 900;
            box-shadow:
                0 8px 25px rgba(118, 85, 255, .25);
        }

        .brand-name {
            font-size: 19px;
            font-weight: 800;
            color: #f6f4ff;
        }

        .brand-name span {
            color: #9d7cff;
        }

        .menu-title {
            padding: 0 12px;
            margin: 0 0 12px;
            color: #69708d;
            font-size: 11px;
            font-weight: 800;
            letter-spacing: 1.2px;
            text-transform: uppercase;
        }

        .menu {
            display: flex;
            flex-direction: column;
            gap: 5px;
        }

        .menu a {
            min-height: 49px;
            padding: 0 16px;
            border-radius: 12px;
            display: flex;
            align-items: center;
            gap: 15px;
            color: #858ca7;
            text-decoration: none;
            font-size: 14px;
            font-weight: 600;
            transition: .2s ease;
        }

        .menu a:hover {
            color: #ffffff;
            background: rgba(118, 85, 255, .10);
        }

        .menu a.active {
            color: #ffffff;
            background: linear-gradient(
                90deg,
                rgba(118, 85, 255, .18),
                rgba(118, 85, 255, .08)
            );
            position: relative;
        }

        .menu a.active::before {
            content: "";
            position: absolute;
            left: 0;
            top: 10px;
            bottom: 10px;
            width: 3px;
            border-radius: 0 4px 4px 0;
            background: #a276ff;
        }

        .menu-icon {
            width: 18px;
            text-align: center;
            font-size: 15px;
            opacity: .9;
        }

        .sidebar-bottom {
            margin-top: auto;
        }

        .profile-mini {
            margin: 20px 2px 12px;
            padding: 14px;
            border: 1px solid #26293b;
            border-radius: 16px;
            background: rgba(255, 255, 255, .025);
            display: flex;
            align-items: center;
            gap: 12px;
        }

        .profile-avatar {
            width: 38px;
            height: 38px;
            border-radius: 12px;
            display: flex;
            align-items: center;
            justify-content: center;
            background: linear-gradient(
                135deg,
                #7655ff,
                #985cff
            );
            font-weight: 900;
            font-size: 17px;
        }

        .profile-info {
            min-width: 0;
        }

        .profile-name {
            color: #e8e8f3;
            font-size: 13px;
            font-weight: 700;
        }

        .profile-role {
            color: #727891;
            font-size: 11px;
            margin-top: 4px;
        }

        .logout {
            width: 100%;
            border: none;
            border-radius: 11px;
            padding: 11px;
            background: rgba(180, 39, 75, .10);
            color: #ff9cad;
            font-size: 12px;
            font-weight: 700;
            cursor: pointer;
            text-decoration: none;
            text-align: center;
            display: block;
        }

        .logout:hover {
            background: rgba(180, 39, 75, .18);
        }

        .main {
            width: calc(100% - 270px);
            margin-left: 270px;
            padding: 42px;
        }

        .page-header {
            max-width: 1120px;
            margin: 0 auto 28px;
            display: flex;
            justify-content: space-between;
            align-items: flex-end;
            gap: 25px;
        }

        .eyebrow {
            color: #7e8aac;
            font-size: 11px;
            font-weight: 800;
            letter-spacing: 1.2px;
            text-transform: uppercase;
            margin-bottom: 10px;
        }

        .page-header h1 {
            font-size: 31px;
            line-height: 1.15;
            font-weight: 850;
            margin-bottom: 9px;
        }

        .page-header p {
            color: #697493;
            font-size: 13px;
        }

        .back-btn {
            text-decoration: none;
            color: #c7cbe0;
            border: 1px solid #292c42;
            background: rgba(255, 255, 255, .025);
            border-radius: 12px;
            padding: 12px 17px;
            font-size: 12px;
            font-weight: 700;
            white-space: nowrap;
        }

        .back-btn:hover {
            border-color: #7454e9;
            color: #ffffff;
        }

        .content-card {
            max-width: 1120px;
            margin: 0 auto;
            border: 1px solid #25283a;
            border-radius: 20px;
            background: rgba(17, 19, 32, .82);
            box-shadow:
                0 25px 70px rgba(0, 0, 0, .20);
            overflow: hidden;
        }

        .card-header {
            padding: 28px 30px 24px;
            border-bottom: 1px solid #242738;
        }

        .card-header h2 {
            font-size: 17px;
            margin-bottom: 7px;
        }

        .card-header p {
            color: #697493;
            font-size: 12px;
            line-height: 1.5;
        }

        .form-area {
            max-width: 650px;
            padding: 30px;
        }

        .message {
            border-radius: 11px;
            padding: 13px 15px;
            margin-bottom: 24px;
            font-size: 13px;
            font-weight: 600;
        }

        .success {
            color: #8ef0ba;
            background: rgba(18, 128, 72, .13);
            border: 1px solid rgba(42, 181, 105, .30);
        }

        .error {
            color: #ff9fae;
            background: rgba(160, 34, 62, .13);
            border: 1px solid rgba(207, 65, 91, .30);
        }

        .field {
            margin-bottom: 21px;
        }

        .field label {
            display: block;
            color: #e9eaf4;
            font-size: 13px;
            font-weight: 700;
            margin-bottom: 9px;
        }

        .field input {
            width: 100%;
            height: 48px;
            border: 1px solid #30344a;
            border-radius: 11px;
            background: #0d0f1b;
            color: #f4f4ff;
            padding: 0 15px;
            font-size: 13px;
            outline: none;
            transition: .2s ease;
        }

        .field input::placeholder {
            color: #555d78;
        }

        .field input:focus {
            border-color: #7655ff;
            box-shadow:
                0 0 0 3px rgba(118, 85, 255, .10);
        }

        .hint {
            color: #626b88;
            font-size: 11px;
            margin-top: 8px;
        }

        .actions {
            display: flex;
            align-items: center;
            gap: 12px;
            margin-top: 27px;
        }

        .save-btn {
            border: none;
            border-radius: 11px;
            padding: 13px 23px;
            background: linear-gradient(
                135deg,
                #7655ff,
                #875cff
            );
            color: white;
            font-size: 13px;
            font-weight: 800;
            cursor: pointer;
            box-shadow:
                0 10px 25px rgba(118, 85, 255, .18);
        }

        .save-btn:hover {
            transform: translateY(-1px);
            box-shadow:
                0 13px 30px rgba(118, 85, 255, .25);
        }

        .cancel-btn {
            text-decoration: none;
            border: 1px solid #2c3044;
            border-radius: 11px;
            padding: 12px 20px;
            color: #a5abc1;
            font-size: 13px;
            font-weight: 700;
        }

        .cancel-btn:hover {
            color: white;
            border-color: #4a4f69;
        }

        .security-note {
            margin-top: 28px;
            padding: 18px;
            border: 1px solid #272a3d;
            border-radius: 13px;
            background: rgba(118, 85, 255, .035);
            display: flex;
            gap: 13px;
            align-items: flex-start;
        }

        .security-icon {
            width: 35px;
            height: 35px;
            border-radius: 10px;
            display: flex;
            align-items: center;
            justify-content: center;
            background: rgba(118, 85, 255, .12);
            font-size: 17px;
            flex-shrink: 0;
        }

        .security-note strong {
            display: block;
            color: #dfe1ee;
            font-size: 12px;
            margin-bottom: 5px;
        }

        .security-note span {
            color: #68718e;
            font-size: 11px;
            line-height: 1.5;
        }

        @media (max-width: 900px) {

            .sidebar {
                width: 230px;
            }

            .main {
                width: calc(100% - 230px);
                margin-left: 230px;
                padding: 30px 24px;
            }
        }

        @media (max-width: 700px) {

            .sidebar {
                position: relative;
                width: 100%;
                min-height: auto;
            }

            .app {
                display: block;
            }

            .main {
                width: 100%;
                margin-left: 0;
                padding: 25px 18px;
            }

            .page-header {
                align-items: flex-start;
                flex-direction: column;
            }

            .content-card {
                border-radius: 16px;
            }

            .form-area {
                padding: 22px;
            }
        }

    </style>

</head>

<body>

<div class="app">

    <aside class="sidebar">

        <div class="brand">

            <div class="brand-logo">
                V
            </div>

            <div class="brand-name">
                VR <span>Mart</span>
            </div>

        </div>

        <div class="menu-title">
            Marketplace
        </div>

        <nav class="menu">

            <% if (buyer) { %>

                <a href="<%= contextPath %>/buyer/dashboard">
                    <span class="menu-icon">⌂</span>
                    Dashboard
                </a>

                <a href="<%= contextPath %>/buyer/categories.jsp">
                    <span class="menu-icon">⌕</span>
                    Explore Products
                </a>

                <a href="<%= contextPath %>/buyer/wishlist">
                    <span class="menu-icon">♡</span>
                    Wishlist
                </a>

                <a href="<%= contextPath %>/buyer/orders">
                    <span class="menu-icon">▣</span>
                    My Orders
                </a>

                <a href="<%= contextPath %>/buyer/cart">
                    <span class="menu-icon">🛒</span>
                    Cart
                </a>

            <% } else { %>

                <a href="<%= contextPath %>/seller/dashboard">
                    <span class="menu-icon">⌂</span>
                    Dashboard
                </a>

                <a href="<%= contextPath %>/seller/products">
                    <span class="menu-icon">▣</span>
                    Products
                </a>

                <a href="<%= contextPath %>/seller/orders">
                    <span class="menu-icon">▤</span>
                    Orders
                </a>

                <a href="<%= contextPath %>/seller/service-requests">
                    <span class="menu-icon">◈</span>
                    Service Requests
                </a>

            <% } %>

        </nav>

        <div class="menu-title" style="margin-top: 28px;">
            Account
        </div>

        <nav class="menu">

            <a href="<%= contextPath %>/profile">
                <span class="menu-icon">◎</span>
                Profile
            </a>

            <a href="<%= contextPath %>/settings"
               class="active">
                <span class="menu-icon">⚙</span>
                Settings
            </a>

        </nav>

        <div class="sidebar-bottom">

            <div class="profile-mini">

                <div class="profile-avatar">
                    V
                </div>

                <div class="profile-info">

                    <div class="profile-name">
                        <%= buyer ? "VR Buyer" : "VR Seller" %>
                    </div>

                    <div class="profile-role">
                        <%= buyer
                            ? "Buyer account"
                            : "Seller account" %>
                    </div>

                </div>

            </div>

            <a class="logout"
               href="<%= contextPath %>/logout">
                Sign out
            </a>

        </div>

    </aside>

    <main class="main">

        <header class="page-header">

            <div>

                <div class="eyebrow">
                    ACCOUNT SECURITY
                </div>

                <h1>
                    Change Password
                </h1>

                <p>
                    Update your VR Mart account password securely.
                </p>

            </div>

            <a class="back-btn"
               href="<%= contextPath %>/settings">
                ← Back to Settings
            </a>

        </header>

        <section class="content-card">

            <div class="card-header">

                <h2>
                    Password &amp; Security
                </h2>

                <p>
                    Choose a strong password to keep your
                    VR Mart account secure.
                </p>

            </div>

            <div class="form-area">

                <% if (!success.isEmpty()) { %>

                    <div class="message success">
                        ✓ <%= success %>
                    </div>

                <% } %>

                <% if (!error.isEmpty()) { %>

                    <div class="message error">
                        ⚠ <%= error %>
                    </div>

                <% } %>

                <form method="post"
                      action="<%= contextPath %>/change-password">

                    <div class="field">

                        <label for="currentPassword">
                            Current Password
                        </label>

                        <input
                            type="password"
                            id="currentPassword"
                            name="currentPassword"
                            placeholder="Enter your current password"
                            autocomplete="current-password"
                            required>

                    </div>

                    <div class="field">

                        <label for="newPassword">
                            New Password
                        </label>

                        <input
                            type="password"
                            id="newPassword"
                            name="newPassword"
                            placeholder="Enter your new password"
                            minlength="8"
                            autocomplete="new-password"
                            required>

                        <div class="hint">
                            Password must contain at least 8 characters.
                        </div>

                    </div>

                    <div class="field">

                        <label for="confirmPassword">
                            Confirm New Password
                        </label>

                        <input
                            type="password"
                            id="confirmPassword"
                            name="confirmPassword"
                            placeholder="Re-enter your new password"
                            minlength="8"
                            autocomplete="new-password"
                            required>

                    </div>

                    <div class="actions">

                        <button type="submit"
                                class="save-btn">
                            Change Password
                        </button>

                        <a class="cancel-btn"
                           href="<%= contextPath %>/settings">
                            Cancel
                        </a>

                    </div>

                </form>

                <div class="security-note">

                    <div class="security-icon">
                        🔐
                    </div>

                    <div>

                        <strong>
                            Keep your password secure
                        </strong>

                        <span>
                            Never share your VR Mart password with
                            anyone. Use a unique password that you
                            do not use on other websites.
                        </span>

                    </div>

                </div>

            </div>

        </section>

    </main>

</div>

</body>
</html>
