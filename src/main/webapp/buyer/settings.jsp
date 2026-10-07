<%@ page contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>
<%@ page import="com.vrmart.model.User" %>
<%@ page import="com.vrmart.model.UserSettings" %>

<%
    final User currentUser =
            (User) session.getAttribute("user");

    if (currentUser == null
            || !User.ROLE_BUYER.equals(currentUser.getRole())) {

        response.sendRedirect(
                request.getContextPath() + "/login");
        return;
    }

    final String username = currentUser.getUsername();

    UserSettings currentSettings =
            (UserSettings) session.getAttribute("userSettings");

    if (currentSettings == null) {
        currentSettings = new UserSettings(currentUser.getId());
        currentSettings.setOrderUpdates(true);
        currentSettings.setEmailNotifications(true);
        currentSettings.setProductRecommendations(true);
        currentSettings.setDarkMode(true);
    }

    final String successMessage =
            request.getParameter("success");
%>

<!DOCTYPE html>
<html lang="en">

<head>

    <meta charset="UTF-8">

    <meta name="viewport"
          content="width=device-width, initial-scale=1.0">

    <title>VR Mart | Settings</title>

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
            --primary: #7c5cff;
            --primary-light: #a78bfa;
            --success: #34d399;
        }

        body {
            min-height: 100vh;
            background:
                radial-gradient(
                    circle at 75% 0%,
                    rgba(124, 92, 255, 0.13),
                    transparent 30%
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
            transition:
                background 0.3s ease,
                color 0.3s ease;
        }

        body.light-mode {
            --bg: #f4f5fb;
            --surface: #ffffff;
            --surface-soft: #eef0f8;
            --border: rgba(20, 24, 45, 0.10);
            --text: #15182a;
            --muted: #687087;
            --primary: #6d4df6;
            --primary-light: #6347df;
            --success: #059669;

            background:
                radial-gradient(
                    circle at 75% 0%,
                    rgba(124, 92, 255, 0.12),
                    transparent 30%
                ),
                var(--bg);
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

        body.light-mode .sidebar {
            background:
                linear-gradient(
                    180deg,
                    #ffffff,
                    #f5f6fb
                );
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
            color: #ffffff;
            font-size: 20px;
            font-weight: 900;
            box-shadow:
                0 12px 30px rgba(124, 92, 255, 0.32);
        }

        .brand-text {
            color: var(--text);
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

        body.light-mode .menu-label {
            color: #8991a5;
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
            transition: 0.2s ease;
        }

        body.light-mode .menu a {
            color: #687087;
        }

        .menu a:hover {
            color: #ffffff;
            background: rgba(124, 92, 255, 0.09);
            transform: translateX(2px);
        }

        body.light-mode .menu a:hover {
            color: #25283b;
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

        body.light-mode .menu a.active {
            color: #35266f;
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
            box-shadow:
                0 0 14px rgba(167, 139, 250, 0.7);
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

        body.light-mode .account-card {
            background: rgba(124, 92, 255, 0.045);
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
            color: #ffffff;
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
            color: var(--text);
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
        }

        /* =========================
           MAIN
           ========================= */

        .main {
            min-height: 100vh;
            margin-left: 250px;
            padding: 32px 40px 50px;
        }

        .page-header {
            display: flex;
            align-items: flex-end;
            justify-content: space-between;
            gap: 20px;
            margin-bottom: 28px;
        }

        .page-header small {
            color: #70798e;
            font-size: 10px;
            font-weight: 800;
            letter-spacing: 1.5px;
        }

        body.light-mode .page-header small {
            color: #70788e;
        }

        .page-header h1 {
            margin-top: 8px;
            color: var(--text);
            font-size: 30px;
            letter-spacing: -1.2px;
        }

        .page-header p {
            margin-top: 7px;
            color: #70798e;
            font-size: 12px;
        }

        .back-link {
            display: inline-flex;
            align-items: center;
            min-height: 40px;
            padding: 0 16px;
            border: 1px solid var(--border);
            border-radius: 11px;
            color: var(--muted-light, #aeb5c7);
            background: rgba(255, 255, 255, 0.035);
            text-decoration: none;
            font-size: 11px;
            font-weight: 700;
        }

        body.light-mode .back-link {
            color: #555d72;
            background: rgba(255, 255, 255, 0.7);
        }

        .back-link:hover {
            color: #ffffff;
            background: rgba(124, 92, 255, 0.10);
        }

        body.light-mode .back-link:hover {
            color: #35266f;
        }

        /* =========================
           SETTINGS CARD
           ========================= */

        .settings-card {
            max-width: 850px;
            padding: 24px;
            border: 1px solid var(--border);
            border-radius: 20px;
            background:
                linear-gradient(
                    145deg,
                    rgba(255, 255, 255, 0.045),
                    rgba(255, 255, 255, 0.018)
                );
        }

        body.light-mode .settings-card {
            background:
                linear-gradient(
                    145deg,
                    rgba(255, 255, 255, 0.95),
                    rgba(255, 255, 255, 0.75)
                );
        }

        .section-title {
            margin-bottom: 17px;
        }

        .section-title h2 {
            color: var(--text);
            font-size: 15px;
        }

        .section-title p {
            margin-top: 5px;
            color: #697287;
            font-size: 10px;
            line-height: 1.6;
        }

        .setting-row {
            display: flex;
            align-items: center;
            justify-content: space-between;
            gap: 20px;
            padding: 17px 0;
            border-bottom:
                1px solid rgba(255, 255, 255, 0.055);
        }

        body.light-mode .setting-row {
            border-bottom-color:
                rgba(20, 24, 45, 0.08);
        }

        .setting-row:last-of-type {
            border-bottom: 0;
        }

        .setting-info {
            min-width: 0;
        }

        .setting-info h3 {
            margin-bottom: 5px;
            color: var(--text);
            font-size: 12px;
        }

        .setting-info p {
            color: #697287;
            font-size: 9px;
            line-height: 1.6;
        }

        /* =========================
           TOGGLE
           ========================= */

        .toggle {
            position: relative;
            width: 45px;
            height: 24px;
            flex-shrink: 0;
        }

        .toggle input {
            width: 0;
            height: 0;
            opacity: 0;
        }

        .slider {
            position: absolute;
            inset: 0;
            border-radius: 20px;
            background: #292e42;
            cursor: pointer;
            transition: 0.25s ease;
        }

        .slider::before {
            content: "";
            position: absolute;
            width: 18px;
            height: 18px;
            left: 3px;
            top: 3px;
            border-radius: 50%;
            background: #9299aa;
            transition: 0.25s ease;
        }

        .toggle input:checked + .slider {
            background: var(--primary);
        }

        .toggle input:checked + .slider::before {
            transform: translateX(21px);
            background: #ffffff;
        }

        /* =========================
           ACTIONS
           ========================= */

        .actions {
            display: flex;
            align-items: center;
            gap: 10px;
            margin-top: 22px;
        }

        .save-button {
            min-height: 42px;
            padding: 0 19px;
            border: 0;
            border-radius: 11px;
            color: #ffffff;
            background:
                linear-gradient(
                    135deg,
                    #6366f1,
                    #8b5cf6
                );
            font-size: 11px;
            font-weight: 800;
            cursor: pointer;
            box-shadow:
                0 10px 25px rgba(99, 102, 241, 0.22);
        }

        .reset-button {
            min-height: 42px;
            padding: 0 17px;
            border: 1px solid var(--border);
            border-radius: 11px;
            color: #aeb5c7;
            background: rgba(255, 255, 255, 0.035);
            font-size: 11px;
            font-weight: 700;
            cursor: pointer;
        }

        body.light-mode .reset-button {
            color: #596177;
            background: rgba(255, 255, 255, 0.7);
        }

        .message {
            display: none;
            margin-top: 15px;
            padding: 11px 13px;
            border:
                1px solid rgba(52, 211, 153, 0.14);
            border-radius: 10px;
            color: #6ee7b7;
            background: rgba(52, 211, 153, 0.045);
            font-size: 10px;
        }

        body.light-mode .message {
            color: #047857;
            background: rgba(16, 185, 129, 0.07);
        }

        .security-card {
            margin-top: 18px;
            padding: 20px 22px;
            border: 1px solid var(--border);
            border-radius: 16px;
            background: rgba(124, 92, 255, 0.045);
        }

        .security-content {
            display: flex;
            align-items: center;
            justify-content: space-between;
            gap: 20px;
        }

        .security-left {
            display: flex;
            align-items: center;
            gap: 14px;
        }

        .security-icon {
            width: 44px;
            height: 44px;
            display: grid;
            place-items: center;
            flex-shrink: 0;
            border-radius: 13px;
            background: rgba(124, 92, 255, 0.12);
            font-size: 20px;
        }

        .security-info h3 {
            color: var(--text);
            font-size: 14px;
        }

        .security-info p {
            max-width: 620px;
            margin-top: 5px;
            color: var(--muted);
            font-size: 11px;
            line-height: 1.6;
        }

        .password-link {
            display: inline-flex;
            align-items: center;
            justify-content: center;
            min-height: 40px;
            padding: 0 15px;
            border: 1px solid rgba(124, 92, 255, 0.25);
            border-radius: 10px;
            color: var(--primary-light);
            background: rgba(124, 92, 255, 0.08);
            text-decoration: none;
            font-size: 11px;
            font-weight: 800;
            white-space: nowrap;
        }

        .password-link:hover {
            background: rgba(124, 92, 255, 0.15);
        }

        /* =========================
           MOBILE
           ========================= */

        .mobile-top {
            display: none;
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
        }

        @media (max-width: 600px) {

            .main {
                padding: 17px;
            }

            .page-header {
                align-items: flex-start;
                flex-direction: column;
            }

            .page-header h1 {
                font-size: 25px;
            }

            .settings-card {
                padding: 18px;
            }

            .security-content {
                align-items: stretch;
                flex-direction: column;
            }

            .password-link {
                width: 100%;
            }

            .setting-row {
                align-items: flex-start;
            }

            .actions {
                flex-direction: column;
                align-items: stretch;
            }

            .save-button,
            .reset-button {
                width: 100%;
            }
        }

    </style>

    <%@ include file="/includes/theme.jspf" %>
</head>

<body class="<%= currentSettings.isDarkMode() ? "" : "vrmart-light" %>">

<div class="layout">

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

            <a href="<%= request.getContextPath() %>/buyer/dashboard.jsp">
                <span class="menu-icon">⌂</span>
                Dashboard
            </a>

            <a href="<%= request.getContextPath() %>/products">
                <span class="menu-icon">⌕</span>
                Explore Products
            </a>

            <a href="<%= request.getContextPath() %>/buyer/wishlist">
                <span class="menu-icon">♡</span>
                Wishlist
            </a>

            <a href="<%= request.getContextPath() %>/buyer/orders">
                <span class="menu-icon">▣</span>
                My Orders
            </a>

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

            <a href="<%= request.getContextPath() %>/profile">
                <span class="menu-icon">◎</span>
                Profile
            </a>

            <a href="<%= request.getContextPath() %>/buyer/settings.jsp"
               class="active">
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


    <main class="main">

        <div class="mobile-top">

            <div class="brand">

                <div class="logo">V</div>

                <div class="brand-text">
                    VR <span>Mart</span>
                </div>

            </div>

            <a class="back-link"
               href="<%= request.getContextPath() %>/buyer/dashboard.jsp">
                ← Dashboard
            </a>

        </div>


        <header class="page-header">

            <div>

                <small>
                    ACCOUNT SETTINGS
                </small>

                <h1>
                    Settings
                </h1>

                <p>
                    Manage your VR Mart preferences.
                </p>

            </div>

            <a class="back-link"
               href="<%= request.getContextPath() %>/buyer/dashboard.jsp">
                ← Back to Dashboard
            </a>

        </header>


        <section class="settings-card">

            <div class="section-title">

                <h2>
                    Preferences
                </h2>

                <p>
                    Choose how VR Mart keeps you updated
                    and personalises your shopping experience.
                </p>

            </div>


            <form
                method="post"
                action="<%= request.getContextPath() %>/settings"
                class="settings-form">

            <div class="setting-row">

                <div class="setting-info">

                    <h3>
                        Order Updates
                    </h3>

                    <p>
                        Receive updates when your order
                        status changes.
                    </p>

                </div>

                <label class="toggle">

                    <input
                        type="checkbox"
                        id="orderUpdates"
                        name="orderUpdates"
                        <%= currentSettings.isOrderUpdates()
                                ? "checked" : "" %>>

                    <span class="slider"></span>

                </label>

            </div>


            <div class="setting-row">

                <div class="setting-info">

                    <h3>
                        Email Notifications
                    </h3>

                    <p>
                        Allow VR Mart to send important
                        account and shopping notifications.
                    </p>

                </div>

                <label class="toggle">

                    <input
                        type="checkbox"
                        id="emailNotifications"
                        name="emailNotifications"
                        <%= currentSettings.isEmailNotifications()
                                ? "checked" : "" %>>

                    <span class="slider"></span>

                </label>

            </div>


            <div class="setting-row">

                <div class="setting-info">

                    <h3>
                        Product Recommendations
                    </h3>

                    <p>
                        Show personalised product
                        recommendations based on your activity.
                    </p>

                </div>

                <label class="toggle">

                    <input
                        type="checkbox"
                        id="recommendations"
                        name="recommendations"
                        <%= currentSettings.isProductRecommendations()
                                ? "checked" : "" %>>

                    <span class="slider"></span>

                </label>

            </div>


            <div class="setting-row">

                <div class="setting-info">

                    <h3>
                        Dark Mode
                    </h3>

                    <p>
                        Switch between dark and light
                        appearance for VR Mart.
                    </p>

                </div>

                <label class="toggle">

                    <input
                        type="checkbox"
                        id="darkMode"
                        name="darkMode"
                        <%= currentSettings.isDarkMode()
                                ? "checked" : "" %>>

                    <span class="slider"></span>

                </label>

            </div>


            <div class="actions">
                <button
                    type="submit"
                    name="action"
                    value="save"
                    class="save-button">
                    Save Settings
                </button>

                <button
                    type="submit"
                    name="action"
                    value="reset"
                    class="reset-button">
                    Reset
                </button>

            </div>

            </form>

            <div class="security-card">
                <div class="security-content">
                    <div class="security-left">
                        <div class="security-icon">🔐</div>
                        <div class="security-info">
                            <h3>Change Password</h3>
                            <p>Update your VR Mart account password securely. Your current password is required.</p>
                        </div>
                    </div>
                    <a class="password-link"
                       href="<%= request.getContextPath() %>/change-password">
                        Change Password →
                    </a>
                </div>
            </div>

            <% if (successMessage != null
                    && !successMessage.trim().isEmpty()) { %>
                <div class="message" style="display:block;">
                    ✓ <%= successMessage.replace("+", " ") %>
                </div>
            <% } %>

        </section>

    </main>

</div>


<script>
    document.addEventListener(
        "DOMContentLoaded",
        function () {
            const darkMode =
                document.getElementById("darkMode");

            darkMode.addEventListener(
                "change",
                function () {
                    document.body.classList.toggle(
                        "light-mode",
                        !this.checked
                    );
                    document.body.classList.toggle(
                        "vrmart-light",
                        !this.checked
                    );
                }
            );
        }
    );
</script>

</body>

</html>
