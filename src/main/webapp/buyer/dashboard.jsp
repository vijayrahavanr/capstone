<%@ page contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>
<%@ page import="com.vrmart.model.Product" %>
<%@ page import="com.vrmart.model.User" %>
<%@ page import="java.util.List" %>
<%
    final User currentUser =
            (User) session.getAttribute("user");
    if (currentUser == null
            || !User.ROLE_BUYER.equals(currentUser.getRole())) {
        response.sendRedirect(
                request.getContextPath() + "/login"
        );
        return;
    }
    final String username = currentUser.getUsername();
    if (request.getAttribute("dashboardLoaded") == null) {
        response.sendRedirect(
                request.getContextPath() + "/buyer/dashboard");
        return;
    }
    final List<Product> recommendedProducts =
            (List<Product>) request.getAttribute("recommendedProducts");
    final String recommendationMessage =
            (String) request.getAttribute("recommendationMessage");
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
            position: relative;
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
        .search:focus-within {
            border-color: rgba(167, 139, 250, 0.35);
            background: rgba(255, 255, 255, 0.045);
        }
        .search > span {
            color: #71798d;
            font-size: 15px;
            flex-shrink: 0;
        }
        .search form {
            width: 100%;
            height: 100%;
            display: flex;
            align-items: center;
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
        .search-suggestions {
            position: absolute;
            top: calc(100% + 8px);
            left: 0;
            right: 0;
            z-index: 100;
            display: none;
            overflow: hidden;
            border: 1px solid rgba(255, 255, 255, 0.09);
            border-radius: 13px;
            background: #111528;
            box-shadow:
                0 18px 45px rgba(0, 0, 0, 0.45);
        }
        .suggestion-item {
            width: 100%;
            display: flex;
            flex-direction: column;
            gap: 3px;
            padding: 11px 13px;
            border: 0;
            border-bottom: 1px solid
                rgba(255, 255, 255, 0.05);
            background: transparent;
            color: #ffffff;
            text-align: left;
            cursor: pointer;
        }
        .suggestion-item:last-child {
            border-bottom: 0;
        }
        .suggestion-item:hover {
            background:
                rgba(124, 92, 255, 0.12);
        }
        .suggestion-item strong {
            font-size: 11px;
            font-weight: 700;
        }
        .suggestion-item span {
            color: #727b91;
            font-size: 9px;
        }
        .suggestion-empty {
            padding: 12px 13px;
            color: #727b91;
            font-size: 10px;
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
            border-color:
                rgba(167, 139, 250, 0.3);
            background:
                rgba(124, 92, 255, 0.09);
        }
        .hero {
            position: relative;
            min-height: 275px;
            overflow: hidden;
            padding: 37px;
            margin-bottom: 27px;
            border:
                1px solid rgba(167, 139, 250, 0.15);
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
            border:
                1px solid rgba(255, 255, 255, 0.045);
            border-radius: 50%;
        }
        .hero::after {
            content: "";
            position: absolute;
            width: 220px;
            height: 220px;
            right: 35px;
            bottom: -145px;
            border:
                1px solid rgba(255, 255, 255, 0.035);
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
            box-shadow:
                0 0 10px rgba(52, 211, 153, 0.7);
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
            border: 0;
            background: transparent;
            color: var(--primary-light);
            cursor: pointer;
            font: inherit;
            font-size: 11px;
            font-weight: 700;
        }
        .view-link:hover {
            color: #b2a7ff;
        }
        .quick-grid {
            display: grid;
            grid-template-columns:
                repeat(4, minmax(0, 1fr));
            gap: 13px;
            margin-bottom: 28px;
        }
        .quick-extra {
            display: none;
        }
        .quick-extra.show {
            display: grid;
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
            border-color:
                rgba(167, 139, 250, 0.24);
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
            border:
                1px solid rgba(167, 139, 250, 0.12);
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
            border:
                1px solid rgba(255, 255, 255, 0.045);
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
        .status-card {
            padding: 17px;
            margin-bottom: 12px;
            border:
                1px solid rgba(52, 211, 153, 0.11);
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
            box-shadow:
                0 0 9px rgba(52, 211, 153, 0.7);
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
            border-bottom:
                1px solid rgba(255, 255, 255, 0.045);
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
        .mobile-top {
            display: none;
        }
        .recommendations {
            margin: 30px 0 34px;
        }

        .recommendations-header {
            display: flex;
            align-items: flex-end;
            justify-content: space-between;
            gap: 18px;
            margin-bottom: 16px;
        }

        .recommendations-header h3 {
            font-size: 18px;
            font-weight: 800;
            letter-spacing: -0.3px;
        }

        .recommendations-header p {
            margin-top: 5px;
            color: var(--muted);
            font-size: 11px;
        }

        .recommendation-grid {
            display: grid;
            grid-template-columns: repeat(3, minmax(0, 1fr));
            gap: 14px;
        }

        .recommendation-card {
            display: flex;
            min-width: 0;
            min-height: 132px;
            gap: 14px;
            padding: 13px;
            border: 1px solid var(--border);
            border-radius: 15px;
            background: rgba(255, 255, 255, 0.025);
            color: inherit;
            text-decoration: none;
            transition: 0.2s ease;
        }

        .recommendation-card:hover {
            transform: translateY(-2px);
            border-color: rgba(167, 139, 250, 0.32);
            background: rgba(124, 92, 255, 0.06);
        }

        .recommendation-image {
            width: 88px;
            height: 106px;
            flex-shrink: 0;
            overflow: hidden;
            border-radius: 11px;
            background: rgba(255, 255, 255, 0.05);
        }

        .recommendation-image img {
            width: 100%;
            height: 100%;
            object-fit: cover;
        }

        .recommendation-no-image {
            height: 100%;
            display: flex;
            align-items: center;
            justify-content: center;
            color: var(--muted);
            font-size: 10px;
        }

        .recommendation-content {
            min-width: 0;
            display: flex;
            flex-direction: column;
            justify-content: space-between;
        }

        .recommendation-category {
            color: var(--primary-light);
            font-size: 9px;
            font-weight: 800;
            letter-spacing: 0.7px;
            text-transform: uppercase;
        }

        .recommendation-content h4 {
            margin-top: 6px;
            overflow: hidden;
            font-size: 12px;
            font-weight: 750;
            line-height: 1.35;
            text-overflow: ellipsis;
            white-space: nowrap;
        }

        .recommendation-content p {
            margin-top: 6px;
            overflow: hidden;
            color: var(--muted);
            font-size: 9px;
            line-height: 1.45;
            display: -webkit-box;
            -webkit-box-orient: vertical;
            -webkit-line-clamp: 2;
        }

        .recommendation-meta {
            display: flex;
            align-items: center;
            justify-content: space-between;
            gap: 8px;
            margin-top: 8px;
        }

        .recommendation-price {
            color: var(--text);
            font-size: 12px;
            font-weight: 800;
        }

        .recommendation-stock {
            color: var(--success);
            font-size: 9px;
            font-weight: 700;
        }

        .recommendation-empty {
            padding: 18px;
            border: 1px dashed var(--border);
            border-radius: 14px;
            color: var(--muted);
            font-size: 11px;
        }

        @media (max-width: 1100px) {
            .recommendation-grid {
                grid-template-columns: repeat(2, minmax(0, 1fr));
            }

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
            .recommendation-grid {
                grid-template-columns: 1fr;
            }

            .recommendation-card {
                min-height: 118px;
            }

            .recommendation-image {
                width: 76px;
                height: 94px;
            }

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
            <a href="<%= request.getContextPath() %>/buyer/categories.jsp">
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
            <a href="<%= request.getContextPath() %>/settings">
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
            <a class="icon-button"
               href="<%= request.getContextPath() %>/logout">
                ↪
            </a>
        </div>
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
                    <form
                        method="get"
                        action="<%= request.getContextPath() %>/products"
                        autocomplete="off">
                        <input
                            id="dashboardSearch"
                            type="text"
                            name="search"
                            placeholder="Search products..."
                            aria-label="Search products"
                            autocomplete="off">
                    </form>
                    <div
                        id="searchSuggestions"
                        class="search-suggestions">
                    </div>
                </div>
                <a
                    href="<%= request.getContextPath() %>/buyer/wishlist"
                    class="icon-button"
                    aria-label="Wishlist">
                    ♡
                </a>
                <a
                    href="<%= request.getContextPath() %>/buyer/cart"
                    class="icon-button"
                    aria-label="Cart">
                    🛒
                </a>
                <a
                    href="<%= request.getContextPath() %>/buyer/notifications"
                    class="icon-button"
                    aria-label="Notifications">
                    🔔
                </a>
            </div>
        </header>
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
                    <a
                        href="<%= request.getContextPath() %>/buyer/categories.jsp"
                        class="primary-button">
                        Explore marketplace&nbsp; →
                    </a>
                    <a
                        href="<%= request.getContextPath() %>/buyer/orders"
                        class="secondary-button">
                        View my orders
                    </a>
                </div>
            </div>
        </section>
        <section class="recommendations" aria-labelledby="recommendations-title">
            <div class="recommendations-header">
                <div>
                    <h3 id="recommendations-title">Recommended for You</h3>
                    <p><%= recommendationMessage != null
                            ? recommendationMessage
                            : "Fresh picks from the VR Mart marketplace." %></p>
                </div>
                <a
                    class="view-link"
                    href="<%= request.getContextPath() %>/buyer/categories.jsp">
                    Explore all →
                </a>
            </div>
            <% if (recommendedProducts != null
                    && !recommendedProducts.isEmpty()) { %>
                <div class="recommendation-grid">
                    <% for (Product product : recommendedProducts) { %>
                        <a
                            class="recommendation-card"
                            href="<%= request.getContextPath() %>/products?search=<%= java.net.URLEncoder.encode(product.getName(), "UTF-8") %>">
                            <div class="recommendation-image">
                                <% if (product.getImageUrl() != null
                                        && !product.getImageUrl().trim().isEmpty()) { %>
                                    <img
                                        src="<%= product.getImageUrl() %>"
                                        alt="<%= product.getName() %>">
                                <% } else { %>
                                    <div
                                        class="recommendation-no-image">
                                        No image
                                    </div>
                                <% } %>
                            </div>
                            <div class="recommendation-content">
                                <div>
                                    <div class="recommendation-category">
                                        <%= product.getCategory() == null
                                                ? "Product"
                                                : product.getCategory() %>
                                    </div>
                                    <h4><%= product.getName() %></h4>
                                    <p><%= product.getDescription() == null
                                            ? "Explore this product on VR Mart."
                                            : product.getDescription() %></p>
                                </div>
                                <div class="recommendation-meta">
                                    <span class="recommendation-price">
                                        ₹<%= product.getPrice() %>
                                    </span>
                                    <span class="recommendation-stock">
                                        In stock
                                    </span>
                                </div>
                            </div>
                        </a>
                    <% } %>
                </div>
            <% } else { %>
                <div class="recommendation-empty">
                    No recommendations are available right now.
                    Explore the marketplace to discover products.
                </div>
            <% } %>
        </section>
        <div class="section-header" id="quick-access">
            <div>
                <h3>
                    Quick access
                </h3>
                <p>
                    Everything you need, one click away
                </p>
            </div>
            <button
                type="button"
                class="view-link"
                onclick="toggleQuickActions()"
                aria-expanded="false"
                id="quick-actions-toggle">
                View all →
            </button>
        </div>
        <section class="quick-grid">
            <a
                class="quick-card quick-card-link"
                href="<%= request.getContextPath() %>/buyer/categories.jsp">
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
            <a
                class="quick-card quick-card-link"
                href="<%= request.getContextPath() %>/buyer/wishlist">
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
            </a>
            <a
                class="quick-card quick-card-link"
                href="<%= request.getContextPath() %>/buyer/orders">
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
            </a>
            <a
                class="quick-card quick-card-link"
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
        <section class="quick-grid quick-extra"
                 id="extra-quick-actions">
            <a
                class="quick-card quick-card-link"
                href="<%= request.getContextPath() %>/profile">
                <div class="quick-icon">
                    ◉
                </div>
                <h4>
                    My Profile
                </h4>
                <p>
                    View and manage your personal
                    account details.
                </p>
            </a>
            <a
                class="quick-card quick-card-link"
                href="<%= request.getContextPath() %>/settings">
                <div class="quick-icon">
                    ⚙
                </div>
                <h4>
                    Settings
                </h4>
                <p>
                    Manage notifications,
                    recommendations and theme.
                </p>
            </a>
            <a
                class="quick-card quick-card-link"
                href="<%= request.getContextPath() %>/buyer/notifications">
                <div class="quick-icon">
                    ♢
                </div>
                <h4>
                    Notifications
                </h4>
                <p>
                    Check your latest order
                    and account updates.
                </p>
            </a>
            <a
                class="quick-card quick-card-link"
                href="<%= request.getContextPath() %>/buyer/categories.jsp">
                <div class="quick-icon">
                    ▦
                </div>
                <h4>
                    Shop by Category
                </h4>
                <p>
                    Explore Electronics, Fashion
                    and other categories.
                </p>
            </a>
        </section>
        <section class="bottom-grid">
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
                                Browse products across
                                multiple categories.
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
                                Find products offered through
                                the VR Mart marketplace.
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
                                Keep your shopping activity
                                organised in one place.
                            </span>
                        </div>
                        <div class="feature-arrow">
                            →
                        </div>
                    </div>
                </div>
            </div>
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
<script>
    document.addEventListener(
        "DOMContentLoaded",
        function () {
            const searchInput =
                document.getElementById(
                    "dashboardSearch"
                );
            const suggestionBox =
                document.getElementById(
                    "searchSuggestions"
                );
            const searchForm =
                searchInput.closest("form");
            const contextPath =
                "<%= request.getContextPath() %>";
            let requestNumber = 0;
            let debounceTimer = null;
            searchInput.addEventListener(
                "input",
                function () {
                    const value =
                        searchInput.value.trim();
                    requestNumber++;
                    const currentRequest =
                        requestNumber;
                    clearTimeout(debounceTimer);
                    suggestionBox.innerHTML = "";
                    if (value.length === 0) {
                        suggestionBox.style.display =
                            "none";
                        return;
                    }
                    debounceTimer =
                        setTimeout(
                            function () {
                                fetch(
                                    contextPath
                                    + "/products/suggestions?search="
                                    + encodeURIComponent(value)
                                )
                                    .then(
                                        function (response) {
                                            if (!response.ok) {
                                                throw new Error(
                                                    "Suggestion request failed."
                                                );
                                            }
                                            return response.json();
                                        }
                                    )
                                    .then(
                                        function (products) {
                                            if (
                                                currentRequest
                                                !== requestNumber
                                            ) {
                                                return;
                                            }
                                            suggestionBox.innerHTML =
                                                "";
                                            if (
                                                !products
                                                || products.length
                                                === 0
                                            ) {
                                                const empty =
                                                    document.createElement(
                                                        "div"
                                                    );
                                                empty.className =
                                                    "suggestion-empty";
                                                empty.textContent =
                                                    "No products found";
                                                suggestionBox.appendChild(
                                                    empty
                                                );
                                                suggestionBox.style.display =
                                                    "block";
                                                return;
                                            }
                                            products.forEach(
                                                function (product) {
                                                    const item =
                                                        document.createElement(
                                                            "button"
                                                        );
                                                    item.type =
                                                        "button";
                                                    item.className =
                                                        "suggestion-item";
                                                    const name =
                                                        document.createElement(
                                                            "strong"
                                                        );
                                                    name.textContent =
                                                        product.name;
                                                    const category =
                                                        document.createElement(
                                                            "span"
                                                        );
                                                    category.textContent =
                                                        product.category
                                                        || "";
                                                    item.appendChild(
                                                        name
                                                    );
                                                    item.appendChild(
                                                        category
                                                    );
                                                    item.addEventListener(
                                                        "click",
                                                        function () {
                                                            searchInput.value =
                                                                product.name;
                                                            suggestionBox.style.display =
                                                                "none";
                                                            searchForm.submit();
                                                        }
                                                    );
                                                    suggestionBox.appendChild(
                                                        item
                                                    );
                                                }
                                            );
                                            suggestionBox.style.display =
                                                "block";
                                        }
                                    )
                                    .catch(
                                        function () {
                                            suggestionBox.innerHTML =
                                                "";
                                            suggestionBox.style.display =
                                                "none";
                                        }
                                    );
                            },
                            150
                        );
                }
            );
            searchInput.addEventListener(
                "keydown",
                function (event) {
                    if (event.key === "Escape") {
                        suggestionBox.style.display =
                            "none";
                    }
                }
            );
            document.addEventListener(
                "click",
                function (event) {
                    if (
                        !searchInput.contains(
                            event.target
                        )
                        && !suggestionBox.contains(
                            event.target
                        )
                    ) {
                        suggestionBox.style.display =
                            "none";
                    }
                }
            );
        }
    );
</script>
<script>
    function toggleQuickActions() {
        const extra =
            document.getElementById(
                "extra-quick-actions"
            );
        const toggle =
            document.getElementById(
                "quick-actions-toggle"
            );
        const expanded =
            extra.classList.toggle("show");
        toggle.setAttribute(
            "aria-expanded",
            expanded
        );
        toggle.textContent =
            expanded
                ? "Show less ↑"
                : "View all →";
    }
</script>
</body>
</html>
