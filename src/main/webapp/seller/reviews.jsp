<%@ page contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>

<%@ page import="com.vrmart.model.ProductReview" %>
<%@ page import="com.vrmart.model.User" %>
<%@ page import="java.util.List" %>

<%
    final User user = (User) session.getAttribute("user");

    if (user == null
            || !User.ROLE_SELLER.equals(user.getRole())) {

        response.sendRedirect(
                request.getContextPath() + "/seller/login");
        return;
    }

    final List<ProductReview> reviews =
            (List<ProductReview>) request.getAttribute("reviews");

    final int reviewCount =
            reviews == null ? 0 : reviews.size();
%>

<!DOCTYPE html>
<html lang="en">

<head>

    <meta charset="UTF-8">

    <meta name="viewport"
          content="width=device-width, initial-scale=1.0">

    <title>VR Mart | Product Reviews</title>

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
            margin-bottom: 45px;
        }

        .brand {
            font-size: 22px;
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

        .back:hover {
            color: #c4b5fd;
            text-decoration: underline;
        }

        .heading small {
            color: #818cf8;
            font-size: 11px;
            font-weight: 700;
            letter-spacing: 1.5px;
            text-transform: uppercase;
        }

        .heading h1 {
            margin-top: 10px;
            font-size: 34px;
        }

        .heading p {
            margin-top: 10px;
            color: #7f899a;
            font-size: 14px;
        }

        .summary {
            margin-top: 28px;
            padding: 18px 22px;
            border-radius: 14px;
            background: rgba(129, 140, 248, 0.08);
            border: 1px solid rgba(129, 140, 248, 0.18);
            color: #c7d2fe;
            font-size: 14px;
        }

        .reviews {
            margin-top: 25px;
            display: grid;
            gap: 18px;
        }

        .review-card {
            padding: 25px;
            border-radius: 18px;
            background: rgba(255, 255, 255, 0.035);
            border: 1px solid rgba(255, 255, 255, 0.07);
        }

        .review-top {
            display: flex;
            justify-content: space-between;
            gap: 20px;
            align-items: flex-start;
        }

        .product-name {
            font-size: 18px;
            font-weight: 700;
        }

        .buyer {
            margin-top: 7px;
            color: #7f899a;
            font-size: 13px;
        }

        .rating {
            color: #fbbf24;
            font-size: 18px;
            white-space: nowrap;
            letter-spacing: 2px;
        }

        .review-text {
            margin-top: 20px;
            color: #d1d5db;
            font-size: 14px;
            line-height: 1.7;
            white-space: pre-wrap;
            word-break: break-word;
        }

        .date {
            margin-top: 18px;
            color: #687386;
            font-size: 12px;
        }

        .empty {
            margin-top: 25px;
            padding: 55px 25px;
            text-align: center;
            border-radius: 18px;
            background: rgba(255, 255, 255, 0.035);
            border: 1px solid rgba(255, 255, 255, 0.07);
        }

        .empty-icon {
            font-size: 40px;
            margin-bottom: 15px;
        }

        .empty h2 {
            font-size: 19px;
        }

        .empty p {
            margin-top: 9px;
            color: #7f899a;
            font-size: 13px;
        }

        @media (max-width: 600px) {

            .page {
                padding: 30px 20px;
            }

            .topbar {
                margin-bottom: 35px;
            }

            .heading h1 {
                font-size: 28px;
            }

            .review-top {
                flex-direction: column;
                gap: 12px;
            }

            .rating {
                white-space: normal;
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
            ← Back to Dashboard
        </a>

    </header>

    <section class="heading">

        <small>Seller Portal</small>

        <h1>Product Reviews</h1>

        <p>
            See what buyers are saying about your products.
        </p>

    </section>

    <div class="summary">
        Total Reviews: <strong><%= reviewCount %></strong>
    </div>

    <% if (reviews == null || reviews.isEmpty()) { %>

        <section class="empty">

            <div class="empty-icon">☆</div>

            <h2>No Reviews Yet</h2>

            <p>
                Reviews for your products will appear here
                after buyers submit them.
            </p>

        </section>

    <% } else { %>

        <section class="reviews">

            <% for (final ProductReview review : reviews) { %>

                <article class="review-card">

                    <div class="review-top">

                        <div>

                            <div class="product-name">
                                <%= review.getProductName() == null
                                        ? "Product"
                                        : review.getProductName() %>
                            </div>

                            <div class="buyer">
                                Buyer:
                                <%= review.getBuyerName() == null
                                        ? "Buyer"
                                        : review.getBuyerName() %>
                            </div>

                        </div>

                        <div class="rating">
                            <%
                                final int rating =
                                        review.getRating();

                                for (int index = 1;
                                     index <= 5;
                                     index++) {
                            %>
                                <%= index <= rating
                                        ? "★" : "☆" %>
                            <% } %>
                        </div>

                    </div>

                    <div class="review-text">
                        <%= review.getReviewText() %>
                    </div>

                    <div class="date">
                        Reviewed on:
                        <%= review.getCreatedAt() %>
                    </div>

                </article>

            <% } %>

        </section>

    <% } %>

</main>

</body>

</html>
