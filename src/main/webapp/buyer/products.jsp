<%@ page contentType="text/html; charset=UTF-8"

         pageEncoding="UTF-8" %>

<%@ page import="com.vrmart.model.Product" %>

<%@ page import="com.vrmart.model.User" %>

<%@ page import="java.util.ArrayList" %>

<%@ page import="java.util.HashSet" %>

<%@ page import="java.util.List" %>

<%@ page import="java.util.Locale" %>

<%@ page import="java.util.Set" %>

<%

    final List<Product> products =

            (List<Product>) request.getAttribute("products");

    final User user =

            (User) session.getAttribute("user");

    final String searchTerm =

            request.getAttribute("searchTerm") == null

                    ? ""

                    : request.getAttribute("searchTerm").toString();

    final String selectedCategory =

            request.getAttribute("category") == null

                    ? ""

                    : request.getAttribute("category").toString();

    final List<String> categories =

            (List<String>) request.getAttribute("categories");

    final String minPrice =

            request.getAttribute("minPrice") == null

                    ? ""

                    : request.getAttribute("minPrice").toString();

    final String maxPrice =

            request.getAttribute("maxPrice") == null

                    ? ""

                    : request.getAttribute("maxPrice").toString();

    final String availability =

            request.getAttribute("availability") == null

                    ? ""

                    : request.getAttribute("availability").toString();

    final String sort =

            request.getAttribute("sort") == null

                    ? ""

                    : request.getAttribute("sort").toString();

final String cartError =

            (String) session.getAttribute("cartError");

    if (cartError != null) {

        session.removeAttribute("cartError");

    }

    final String wishlistSuccess =

            (String) session.getAttribute("wishlistSuccess");

    if (wishlistSuccess != null) {

        session.removeAttribute("wishlistSuccess");

    }

    final Set<String> displayedProducts =

            new HashSet<>();

%>

<!DOCTYPE html>

<html lang="en">

<head>

    <meta charset="UTF-8">

    <meta name="viewport"

          content="width=device-width, initial-scale=1.0">

    <title>VR Mart | Products</title>

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

            max-width: 1250px;

            margin: 0 auto;

            padding: 35px;

        }

        .topbar {

            display: flex;

            align-items: center;

            justify-content: space-between;

            margin-bottom: 35px;

        }

        .brand {

            display: flex;

            align-items: center;

            gap: 12px;

        }

        .logo {

            width: 44px;

            height: 44px;

            display: flex;

            align-items: center;

            justify-content: center;

            border-radius: 13px;

            font-size: 20px;

            font-weight: 800;

            background: linear-gradient(

                135deg,

                #6366f1,

                #8b5cf6

            );

        }

        .brand-name {

            font-size: 20px;

            font-weight: 800;

        }

        .brand-name span {

            color: #818cf8;

        }

        .top-links {

            display: flex;

            align-items: center;

            gap: 20px;

            flex-wrap: wrap;

        }

        .back,

        .cart-link {

            color: #a5b4fc;

            text-decoration: none;

            font-size: 13px;

        }

        .back:hover,

        .cart-link:hover {

            color: #ffffff;

        }

        .heading {

            margin-bottom: 28px;

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

            letter-spacing: -1px;

        }

        .heading p {

            margin-top: 9px;

            color: #7f899a;

            font-size: 14px;

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

        .wishlist-success {

            display: flex;

            align-items: center;

            justify-content: space-between;

            gap: 15px;

            margin-bottom: 20px;

            padding: 12px 14px;

            border-radius: 10px;

            color: #bbf7d0;

            background: rgba(34, 197, 94, 0.1);

            border: 1px solid rgba(34, 197, 94, 0.2);

            font-size: 13px;

        }

        .wishlist-success a {

            color: #c4b5fd;

            text-decoration: none;

            font-weight: 700;

            white-space: nowrap;

        }

        .wishlist-success a:hover {

            color: #ffffff;

            text-decoration: underline;

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

            transition: 0.25s ease;

        }

        .product:hover {

            transform: translateY(-4px);

            border-color: rgba(129, 140, 248, 0.3);

        }

        .product-image {

            height: 210px;

            display: flex;

            align-items: center;

            justify-content: center;

            background:

                radial-gradient(

                    circle at 50% 30%,

                    rgba(99, 102, 241, 0.2),

                    transparent 60%

                ),

                #111522;

        }

        .product-image img {

            width: 100%;

            height: 100%;

            object-fit: cover;

        }

        .no-image {

            color: #687386;

            font-size: 13px;

        }

        .product-content {

            padding: 20px;

        }

        .category {

            display: inline-block;

            margin-bottom: 10px;

            padding: 5px 9px;

            border-radius: 999px;

            color: #a5b4fc;

            background: rgba(99, 102, 241, 0.1);

            font-size: 10px;

            font-weight: 700;

            text-transform: uppercase;

        }

        .assurance-badge {

            display: inline-block;

            margin-left: 7px;

            margin-bottom: 10px;

            padding: 5px 9px;

            border-radius: 999px;

            color: #6ee7b7;

            background: rgba(34, 197, 94, 0.08);

            border: 1px solid rgba(34, 197, 94, 0.18);

            font-size: 9px;

            font-weight: 800;

        }

        .product h2 {

            margin-bottom: 8px;

            font-size: 17px;

        }

        .description {

            min-height: 42px;

            margin-bottom: 15px;

            color: #7f899a;

            font-size: 12px;

            line-height: 1.6;

        }

        .seller-label {

            display: block;

            margin-bottom: 7px;

            color: #a5b4fc;

            font-size: 11px;

            font-weight: 700;

        }

        .seller-select {

            width: 100%;

            padding: 11px 12px;

            border: 1px solid rgba(255, 255, 255, 0.1);

            border-radius: 10px;

            outline: none;

            background: #111522;

            color: #ffffff;

            font-size: 12px;

            cursor: pointer;

        }

        .seller-select:focus {

            border-color: #6366f1;

        }

        .seller-select option {

            background: #111522;

            color: #ffffff;

        }

        .seller-info {

            margin-top: 8px;

            color: #687386;

            font-size: 10px;

        }

        .cart-form {

            margin-top: 16px;

        }

        .cart-button {

            width: 100%;

            padding: 11px;

            border: 0;

            border-radius: 10px;

            cursor: pointer;

            color: #ffffff;

            background: linear-gradient(

                135deg,

                #6366f1,

                #8b5cf6

            );

            font-size: 12px;

            font-weight: 700;

        }

        .cart-button:hover {

            opacity: 0.9;

        }

        .cart-button.disabled {

            cursor: not-allowed;

            opacity: 0.5;

        }

        .wishlist-form {

            margin-top: 9px;

        }

        .wishlist-button {

            width: 100%;

            padding: 11px;

            border: 1px solid rgba(236, 72, 153, 0.3);

            border-radius: 10px;

            cursor: pointer;

            color: #f9a8d4;

            background: rgba(236, 72, 153, 0.08);

            font-size: 12px;

            font-weight: 700;

        }

        .wishlist-button:hover {

            background: rgba(236, 72, 153, 0.16);

            border-color: rgba(236, 72, 153, 0.5);

        }

        .empty {

            padding: 60px 20px;

            text-align: center;

            border-radius: 18px;

            background: rgba(255, 255, 255, 0.035);

            border: 1px solid rgba(255, 255, 255, 0.07);

        }

        .empty h2 {

            margin-bottom: 8px;

            font-size: 20px;

        }

        .empty p {

            color: #7f899a;

            font-size: 13px;

        }

        @media (max-width: 900px) {

            .products {

                grid-template-columns: 1fr 1fr;

            }

        }

        @media (max-width: 600px) {

            .page {

                padding: 20px;

            }

            .products {

                grid-template-columns: 1fr;

            }

            .topbar {

                margin-bottom: 25px;

            }

            .heading h1 {

                font-size: 28px;

            }

            .top-links {

                gap: 10px;

            }

            .wishlist-success {

                align-items: flex-start;

                flex-direction: column;

            }

        }

        .filter-panel {

            margin-bottom: 28px;

            padding: 18px;

            border-radius: 16px;

            background: rgba(255, 255, 255, 0.035);

            border: 1px solid rgba(255, 255, 255, 0.07);

        }

        .filter-form {

            display: grid;

            grid-template-columns: 2fr 1fr 1fr 1fr 1fr auto;

            gap: 10px;

            align-items: end;

        }

        .filter-field {

            display: flex;

            flex-direction: column;

            gap: 6px;

        }

        .filter-field label {

            color: #a5b4fc;

            font-size: 10px;

            font-weight: 700;

            text-transform: uppercase;

            letter-spacing: 0.6px;

        }

        .filter-field input,

        .filter-field select {

            width: 100%;

            padding: 10px 11px;

            border: 1px solid rgba(255, 255, 255, 0.1);

            border-radius: 9px;

            outline: none;

            background: #111522;

            color: #ffffff;

            font-size: 12px;

        }

        .filter-field input:focus,

        .filter-field select:focus {

            border-color: #6366f1;

        }

        .filter-field option {

            background: #111522;

            color: #ffffff;

        }

        .filter-actions {

            display: flex;

            gap: 8px;

        }

        .filter-button,

        .clear-button {

            display: inline-flex;

            align-items: center;

            justify-content: center;

            min-height: 38px;

            padding: 0 15px;

            border-radius: 9px;

            text-decoration: none;

            font-size: 11px;

            font-weight: 700;

            white-space: nowrap;

            cursor: pointer;

        }

        .filter-button {

            border: 0;

            color: #ffffff;

            background: linear-gradient(135deg, #6366f1, #8b5cf6);

        }

        .clear-button {

            color: #a5b4fc;

            border: 1px solid rgba(129, 140, 248, 0.25);

            background: rgba(99, 102, 241, 0.06);

        }

        .filter-button:hover {

            opacity: 0.9;

        }

        .clear-button:hover {

            color: #ffffff;

            border-color: rgba(129, 140, 248, 0.45);

        }

        @media (max-width: 1050px) {

            .filter-form {

                grid-template-columns: 1fr 1fr 1fr;

            }

        }

        @media (max-width: 600px) {

            .filter-form {

                grid-template-columns: 1fr;

            }

            .filter-actions {

                width: 100%;

            }

            .filter-button,

            .clear-button {

                flex: 1;

            }

        }

        .ai-assistant {
            position: fixed;
            right: 24px;
            bottom: 24px;
            z-index: 9999;
            font-family: Arial, Helvetica, sans-serif;
        }

        .ai-toggle {
            width: 58px;
            height: 58px;
            border: 0;
            border-radius: 50%;
            cursor: pointer;
            color: #ffffff;
            background: linear-gradient(135deg, #6366f1, #8b5cf6);
            box-shadow: 0 14px 35px rgba(0, 0, 0, 0.45);
            font-size: 24px;
            transition: transform 0.2s ease, opacity 0.2s ease;
        }

        .ai-toggle:hover {
            transform: translateY(-2px) scale(1.03);
            opacity: 0.95;
        }

        .ai-panel {
            display: none;
            width: 370px;
            max-width: calc(100vw - 32px);
            height: 520px;
            max-height: calc(100vh - 110px);
            margin-bottom: 12px;
            overflow: hidden;
            border-radius: 18px;
            background: #0d111d;
            border: 1px solid rgba(129, 140, 248, 0.25);
            box-shadow: 0 25px 70px rgba(0, 0, 0, 0.55);
            flex-direction: column;
        }

        .ai-panel.open {
            display: flex;
        }

        .ai-header {
            display: flex;
            align-items: center;
            justify-content: space-between;
            gap: 12px;
            padding: 16px 18px;
            background: linear-gradient(
                135deg,
                rgba(99, 102, 241, 0.22),
                rgba(139, 92, 246, 0.16)
            );
            border-bottom: 1px solid rgba(255, 255, 255, 0.07);
        }

        .ai-title {
            display: flex;
            align-items: center;
            gap: 10px;
        }

        .ai-title-icon {
            width: 34px;
            height: 34px;
            display: flex;
            align-items: center;
            justify-content: center;
            border-radius: 10px;
            background: rgba(99, 102, 241, 0.18);
            font-size: 18px;
        }

        .ai-title strong {
            display: block;
            font-size: 13px;
        }

        .ai-title span {
            display: block;
            margin-top: 3px;
            color: #7f899a;
            font-size: 10px;
        }

        .ai-close {
            border: 0;
            background: transparent;
            color: #7f899a;
            cursor: pointer;
            font-size: 18px;
        }

        .ai-close:hover {
            color: #ffffff;
        }

        .ai-messages {
            flex: 1;
            overflow-y: auto;
            padding: 16px;
        }

        .ai-message {
            max-width: 88%;
            margin-bottom: 10px;
            padding: 10px 12px;
            border-radius: 12px;
            color: #dbe4f5;
            background: rgba(255, 255, 255, 0.055);
            font-size: 12px;
            line-height: 1.55;
            white-space: pre-wrap;
        }

        .ai-message.user {
            margin-left: auto;
            color: #ffffff;
            background: linear-gradient(
                135deg,
                rgba(99, 102, 241, 0.82),
                rgba(139, 92, 246, 0.82)
            );
        }

        .ai-message.loading {
            color: #a5b4fc;
        }

        .ai-products {
            display: grid;
            gap: 8px;
            margin: 0 0 12px 0;
        }

        .ai-product {
            padding: 11px;
            border-radius: 12px;
            background: rgba(255, 255, 255, 0.035);
            border: 1px solid rgba(255, 255, 255, 0.07);
        }

        .ai-product-name {
            margin-bottom: 4px;
            color: #ffffff;
            font-size: 12px;
            font-weight: 700;
        }

        .ai-product-meta {
            color: #a5b4fc;
            font-size: 10px;
        }

        .ai-product-desc {
            margin-top: 5px;
            color: #7f899a;
            font-size: 10px;
            line-height: 1.45;
        }

        .ai-input-area {
            display: flex;
            gap: 8px;
            padding: 12px;
            border-top: 1px solid rgba(255, 255, 255, 0.07);
            background: #0a0e18;
        }

        .ai-input {
            min-width: 0;
            flex: 1;
            padding: 11px 12px;
            border: 1px solid rgba(255, 255, 255, 0.1);
            border-radius: 10px;
            outline: none;
            background: #111522;
            color: #ffffff;
            font-size: 12px;
        }

        .ai-input:focus {
            border-color: #6366f1;
        }

        .ai-send {
            min-width: 62px;
            padding: 0 13px;
            border: 0;
            border-radius: 10px;
            cursor: pointer;
            color: #ffffff;
            background: linear-gradient(135deg, #6366f1, #8b5cf6);
            font-size: 11px;
            font-weight: 700;
        }

        .ai-send:disabled {
            cursor: not-allowed;
            opacity: 0.55;
        }

        @media (max-width: 600px) {
            .ai-assistant {
                right: 14px;
                bottom: 14px;
            }

            .ai-panel {
                width: calc(100vw - 28px);
                height: 500px;
            }
        }

    </style>

</head>

<body>

<main class="page">

    <header class="topbar">

        <div class="brand">

            <div class="logo">

                V

            </div>

            <div class="brand-name">

                VR <span>Mart</span>

            </div>

        </div>

        <div class="top-links">

            <% if (user != null

                    && User.ROLE_BUYER.equals(user.getRole())) { %>

                <a class="cart-link"

                   href="<%= request.getContextPath() %>/buyer/cart">

                    🛒 Cart

                </a>

                <a class="cart-link"

                   href="<%= request.getContextPath() %>/buyer/wishlist">

                    ♡ Wishlist

                </a>

            <% } %>

            <a class="back"

               href="<%= request.getContextPath() %>/buyer/categories.jsp">

                ← Categories

            </a>

            <a class="back"

               href="<%= request.getContextPath() %>/buyer/dashboard.jsp">

                Dashboard

            </a>

        </div>

    </header>

    <section class="heading">

        <small>VR Mart Marketplace</small>

        <h1>Explore Products</h1>

        <p>

            Discover products and choose your preferred seller.

        </p>

    </section>

    <section class="filter-panel">

        <form class="filter-form"

              method="get"

              action="<%= request.getContextPath() %>/products">

            <div class="filter-field">

                <label for="search">Search</label>

                <input type="text" id="search" name="search"

                       value="<%= searchTerm %>"

                       placeholder="Search products...">

            </div>

            <div class="filter-field">

                <label for="category">Category</label>

                <select id="category" name="category">

                    <option value=""

                            <%= selectedCategory.isEmpty()

                                    ? "selected"

                                    : "" %>>

                        All Categories

                    </option>

                    <%

                        if (categories != null) {

                            for (final String categoryOption : categories) {

                    %>

                    <option value="<%= categoryOption %>"

                            <%= categoryOption.equalsIgnoreCase(

                                    selectedCategory)

                                    ? "selected"

                                    : "" %>>

                        <%= categoryOption %>

                    </option>

                    <%

                            }

                        }

                    %>

                </select>

            </div>

            <div class="filter-field">

                <label for="minPrice">Min Price</label>

                <input type="number" id="minPrice" name="minPrice"

                       value="<%= minPrice %>" min="0" step="0.01"

                       placeholder="₹ Min">

            </div>

            <div class="filter-field">

                <label for="maxPrice">Max Price</label>

                <input type="number" id="maxPrice" name="maxPrice"

                       value="<%= maxPrice %>" min="0" step="0.01"

                       placeholder="₹ Max">

            </div>

            <div class="filter-field">

                <label for="availability">Availability</label>

                <select id="availability" name="availability">

                    <option value="" <%= availability.isEmpty() ? "selected" : "" %>>

                        All Products

                    </option>

                    <option value="in-stock"

                            <%= "in-stock".equalsIgnoreCase(availability) ? "selected" : "" %>>

                        In Stock

                    </option>

                </select>

            </div>

            <div class="filter-field">

                <label for="sort">Sort By</label>

                <select id="sort" name="sort">

                    <option value="" <%= sort.isEmpty() ? "selected" : "" %>>

                        Default

                    </option>

                    <option value="price-asc"

                            <%= "price-asc".equalsIgnoreCase(sort) ? "selected" : "" %>>

                        Price: Low to High

                    </option>

                    <option value="price-desc"

                            <%= "price-desc".equalsIgnoreCase(sort) ? "selected" : "" %>>

                        Price: High to Low

                    </option>

                    <option value="name-asc"

                            <%= "name-asc".equalsIgnoreCase(sort) ? "selected" : "" %>>

                        Name: A to Z

                    </option>

                    <option value="name-desc"

                            <%= "name-desc".equalsIgnoreCase(sort) ? "selected" : "" %>>

                        Name: Z to A

                    </option>

                    <option value="stock-desc"

                            <%= "stock-desc".equalsIgnoreCase(sort) ? "selected" : "" %>>

                        Stock: High to Low

                    </option>

                    <option value="stock-asc"

                            <%= "stock-asc".equalsIgnoreCase(sort) ? "selected" : "" %>>

                        Stock: Low to High

                    </option>

                </select>

            </div>

            <div class="filter-actions">

                <button type="submit" class="filter-button">Apply</button>

                <a class="clear-button"

                   href="<%= request.getContextPath() %>/products">Clear</a>

            </div>

        </form>

    </section>

    <% if (cartError != null) { %>

        <div class="error">

            <%= cartError %>

        </div>

    <% } %>

    <% if (wishlistSuccess != null) { %>

        <div class="wishlist-success">

            <span>

                ✓ <%= wishlistSuccess %>

            </span>

            <a href="<%= request.getContextPath() %>/buyer/wishlist">

                Go to Wishlist →

            </a>

        </div>

    <% } %>

    <% if (products == null || products.isEmpty()) { %>

        <section class="empty">

            <h2>No products available yet</h2>

            <p>

                Products added by sellers will appear here.

            </p>

        </section>

    <% } else { %>

        <section class="products">

            <% for (Product product : products) {

                final String productName =

                        product.getName() == null

                                ? ""

                                : product.getName().trim();

                final String productKey =

                        productName.toLowerCase(Locale.ROOT);

                if (!displayedProducts.add(productKey)) {

                    continue;

                }

                final List<Product> sellerOptions =

                        new ArrayList<>();

                for (Product candidate : products) {

                    final String candidateName =

                            candidate.getName() == null

                                    ? ""

                                    : candidate.getName().trim();

                    if (candidateName.equalsIgnoreCase(

                            productName)) {

                        sellerOptions.add(candidate);

                    }

                }

                Product firstAvailableSeller = null;

                for (Product seller : sellerOptions) {

                    if (seller.getStockQty() > 0) {

                        firstAvailableSeller = seller;

                        break;

                    }

                }

                /*

                  * Wishlist works even when all sellers

                  * are out of stock.

                  */

                final Product wishlistSeller =

                        firstAvailableSeller != null

                                ? firstAvailableSeller

                                : sellerOptions.isEmpty()

                                        ? product

                                        : sellerOptions.get(0);

                final boolean hasStock =

                        firstAvailableSeller != null;

                final String sellerSelectId =

                        "seller-" + product.getId();

                final String cartProductId =

                        "cart-product-" + product.getId();

                final String wishlistProductId =

                        "wishlist-product-" + product.getId();

            %>

                <article class="product">

                    <div class="product-image">

                        <% if (product.getImageUrl() != null

                                && !product.getImageUrl().isBlank()) { %>

                            <img

                                src="<%= product.getImageUrl() %>"

                                alt="<%= productName %>">

                        <% } else { %>

                            <span class="no-image">

                                No image available

                            </span>

                        <% } %>

                    </div>

                    <div class="product-content">

                        <span class="category">

                            <%= product.getCategory() %>

                        </span>

                        <% if (product.isVrMartAssured()) { %>

                            <span class="assurance-badge">

                                ✓ VR Mart Assured

                            </span>

                        <% } %>

                        <h2>

                            <%= productName %>

                        </h2>

                        <p class="description">

                            <%= product.getDescription() == null

                                    ? "No description available."

                                    : product.getDescription() %>

                        </p>

                        <label class="seller-label"

                               for="<%= sellerSelectId %>">

                            Choose Seller

                        </label>

                        <% if (hasStock) { %>

                            <select

                                class="seller-select"

                                id="<%= sellerSelectId %>"

                                onchange="updateProductIds(

                                    this,

                                    '<%= cartProductId %>',

                                    '<%= wishlistProductId %>')"

                                required>

                                <% for (Product seller

                                        : sellerOptions) { %>

                                    <option

                                        value="<%= seller.getId() %>"

                                        <%= seller.getId()

                                                == firstAvailableSeller

                                                    .getId()

                                                ? "selected"

                                                : "" %>

                                        <%= seller.getStockQty() <= 0

                                                ? "disabled"

                                                : "" %>>

                                        <%= seller.getSellerName() == null

                                                ? "Seller"

                                                : seller.getSellerName() %>

                                        -

                                        ₹<%= seller.getPrice() %>

                                        -

                                        Stock:

                                        <%= seller.getStockQty() %>

                                    </option>

                                <% } %>

                            </select>

                            <div class="seller-info">

                                Selected seller's product will be

                                added to your cart or wishlist.

                            </div>

                            <form method="post"

                                  action="<%= request.getContextPath() %>/buyer/cart/add"

                                  class="cart-form">

                                <input type="hidden"

                                       name="productId"

                                       id="<%= cartProductId %>"

                                       value="<%= firstAvailableSeller.getId() %>">

                                <input type="hidden"

                                       name="quantity"

                                       value="1">

                                <button type="submit"

                                        class="cart-button">

                                    Add to Cart

                                </button>

                            </form>

                            <form method="post"

                                  action="<%= request.getContextPath() %>/buyer/wishlist"

                                  class="wishlist-form">

                                <input type="hidden"

                                       name="productId"

                                       id="<%= wishlistProductId %>"

                                       value="<%= firstAvailableSeller.getId() %>">

                                <input type="hidden"

                                       name="action"

                                       value="add">

                                <button type="submit"

                                        class="wishlist-button">

                                    ♡ Add to Wishlist

                                </button>

                            </form>

                        <% } else { %>

                            <select class="seller-select"

                                    disabled>

                                <option>

                                    All sellers are out of stock

                                </option>

                            </select>

                            <div class="seller-info">

                                This product is currently unavailable,

                                but you can save it to your wishlist.

                            </div>

                            <button type="button"

                                    class="cart-button disabled"

                                    disabled>

                                Out of Stock

                            </button>

                            <form method="post"

                                  action="<%= request.getContextPath() %>/buyer/wishlist"

                                  class="wishlist-form">

                                <input type="hidden"

                                       name="productId"

                                       value="<%= wishlistSeller.getId() %>">

                                <input type="hidden"

                                       name="action"

                                       value="add">

                                <button type="submit"

                                        class="wishlist-button">

                                    ♡ Add to Wishlist

                                </button>

                            </form>

                        <% } %>

                    </div>

                </article>

            <% } %>

        </section>

    <% } %>

</main>

    <div class="ai-assistant">
        <section class="ai-panel" id="aiPanel" aria-label="VR Mart Shopping Assistant">
            <div class="ai-header">
                <div class="ai-title">
                    <div class="ai-title-icon">🤖</div>
                    <div>
                        <strong>VR Mart Shopping Assistant</strong>
                        <span>Ask me to find products from VR Mart</span>
                    </div>
                </div>
                <button type="button" class="ai-close"
                        onclick="toggleAI()" aria-label="Close assistant">×</button>
            </div>

            <div class="ai-messages" id="aiMessages">
                <div class="ai-message">
                    Hi! 👋 Tell me what you are looking for.
                    Try: "phones under 30000" or "in stock electronics".
                </div>
            </div>

            <form class="ai-input-area" onsubmit="askAI(event)">
                <input type="text" id="aiInput" class="ai-input"
                       placeholder="Ask for a product..." autocomplete="off">
                <button type="submit" class="ai-send" id="aiSend">Ask</button>
            </form>
        </section>

        <button type="button" class="ai-toggle" id="aiToggle"
                onclick="toggleAI()"
                aria-label="Open VR Mart Shopping Assistant">🤖</button>
    </div>

<script>

    function updateProductIds(

        select,

        cartProductId,

        wishlistProductId) {

        var selectedValue = select.value;

        var cartInput =

            document.getElementById(cartProductId);

        var wishlistInput =

            document.getElementById(wishlistProductId);

        if (cartInput) {

            cartInput.value = selectedValue;

        }

        if (wishlistInput) {

            wishlistInput.value = selectedValue;

        }

    }

        function toggleAI() {
            var panel = document.getElementById("aiPanel");
            var input = document.getElementById("aiInput");

            if (!panel) {
                return;
            }

            panel.classList.toggle("open");

            if (panel.classList.contains("open") && input) {
                setTimeout(function () {
                    input.focus();
                }, 50);
            }
        }

        function appendAIMessage(message, type) {
            var messages = document.getElementById("aiMessages");

            if (!messages) {
                return null;
            }

            var element = document.createElement("div");
            element.className = "ai-message" + (type ? " " + type : "");
            element.textContent = message;
            messages.appendChild(element);
            messages.scrollTop = messages.scrollHeight;

            return element;
        }

        function escapeAIHtml(value) {
            var element = document.createElement("div");
            element.textContent = value == null ? "" : String(value);
            return element.innerHTML;
        }

        function renderAIProducts(products) {
            if (!Array.isArray(products) || products.length === 0) {
                return;
            }

            var messages = document.getElementById("aiMessages");
            var wrapper = document.createElement("div");
            wrapper.className = "ai-products";

            products.forEach(function (product) {
                var card = document.createElement("div");
                card.className = "ai-product";

                var name = escapeAIHtml(product.name || "Product");
                var category = escapeAIHtml(product.category || "Uncategorized");
                var price = escapeAIHtml(product.price || "0.00");
                var stock = Number(product.stock || 0);
                var stockText = stock > 0 ? stock + " in stock" : "Out of stock";

                var description = product.description || "";
                if (description.length > 110) {
                    description = description.substring(0, 110) + "...";
                }

                card.innerHTML =
                    '<div class="ai-product-name">' + name + '</div>' +
                    '<div class="ai-product-meta">₹' + price +
                    ' • ' + category +
                    ' • ' + escapeAIHtml(stockText) + '</div>' +
                    (description
                        ? '<div class="ai-product-desc">' +
                          escapeAIHtml(description) + '</div>'
                        : '');

                wrapper.appendChild(card);
            });

            messages.appendChild(wrapper);
            messages.scrollTop = messages.scrollHeight;
        }

        function askAI(event) {
            event.preventDefault();

            var input = document.getElementById("aiInput");
            var sendButton = document.getElementById("aiSend");

            if (!input || !sendButton) {
                return;
            }

            var query = input.value.trim();

            if (!query) {
                input.focus();
                return;
            }

            appendAIMessage(query, "user");
            input.value = "";
            input.disabled = true;
            sendButton.disabled = true;

            var loading = appendAIMessage(
                "Searching the VR Mart catalog...",
                "loading"
            );

            var contextPath = "<%= request.getContextPath() %>";
            var url = contextPath + "/buyer/ai-assistant?query="
                + encodeURIComponent(query);

            fetch(url, {
                method: "GET",
                headers: {
                    "Accept": "application/json"
                }
            })
                .then(function (response) {
                    if (!response.ok) {
                        throw new Error("Assistant request failed.");
                    }
                    return response.json();
                })
                .then(function (data) {
                    if (loading && loading.parentNode) {
                        loading.parentNode.removeChild(loading);
                    }

                    if (!data || data.success !== true) {
                        appendAIMessage(
                            data && data.message
                                ? data.message
                                : "I could not process that request."
                        );
                        return;
                    }

                    appendAIMessage(
                        data.message || "Here are some products I found."
                    );
                    renderAIProducts(data.products);
                })
                .catch(function () {
                    if (loading && loading.parentNode) {
                        loading.parentNode.removeChild(loading);
                    }

                    appendAIMessage(
                        "Sorry, I could not connect to the shopping assistant. "
                        + "Please try again."
                    );
                })
                .finally(function () {
                    input.disabled = false;
                    sendButton.disabled = false;
                    input.focus();
                });
        }

    </script>

</body>

</html>
