<%@ page contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>
<%@ page import="com.vrmart.model.User" %>

<%
    final User user = (User) session.getAttribute("user");

    if (user == null
            || !User.ROLE_BUYER.equals(user.getRole())) {
        response.sendRedirect(
                request.getContextPath() + "/buyer/login");
        return;
    }
%>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport"
          content="width=device-width, initial-scale=1.0">

    <title>VR Mart | Shop by Category</title>

    <style type="text/css">
        * {
            box-sizing: border-box;
        }

        html,
        body {
            margin: 0;
            padding: 0;
            min-height: 100%;
        }

        body {
            min-height: 100vh;
            font-family: Arial, Helvetica, sans-serif;
            background: #080b14;
            color: #ffffff;
        }

        .page {
            width: min(1100px, 92%);
            margin: 0 auto;
            padding: 40px 0 60px;
        }

        .top {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 52px;
        }

        .brand {
            display: flex;
            align-items: center;
            gap: 11px;
            font-size: 20px;
            font-weight: 900;
        }

        .logo {
            width: 44px;
            height: 44px;
            display: grid;
            place-items: center;
            border-radius: 13px;
            background: linear-gradient(
                135deg,
                #635bff,
                #9b5cff
            );
        }

        .brand span {
            color: #a78bfa;
        }

        .back {
            color: #a5b4fc;
            text-decoration: none;
            font-size: 12px;
            font-weight: 700;
        }

        .back:hover {
            color: #ffffff;
        }

        .eyebrow {
            color: #818cf8;
            font-size: 10px;
            font-weight: 800;
            letter-spacing: 1.5px;
            text-transform: uppercase;
        }

        h1 {
            margin: 9px 0;
            font-size: 38px;
            line-height: 1.2;
        }

        .intro {
            max-width: 670px;
            margin: 0;
            color: #7f899a;
            font-size: 13px;
            line-height: 1.6;
        }

        .assured {
            display: inline-flex;
            align-items: center;
            gap: 8px;
            margin: 25px 0 28px;
            padding: 9px 13px;
            border: 1px solid rgba(52, 211, 153, 0.20);
            border-radius: 999px;
            color: #6ee7b7;
            background: rgba(52, 211, 153, 0.06);
            font-size: 10px;
            font-weight: 800;
        }

        .assured span {
            color: #7f899a;
            font-weight: 600;
        }

        .grid {
            display: grid;
            grid-template-columns: repeat(3, 1fr);
            gap: 15px;
        }

        .card {
            display: block;
            min-height: 175px;
            padding: 21px;
            border: 1px solid rgba(255, 255, 255, 0.08);
            border-radius: 18px;
            background: rgba(255, 255, 255, 0.035);
            color: inherit;
            text-decoration: none;
            transition:
                transform 0.2s ease,
                border-color 0.2s ease,
                background 0.2s ease;
        }

        .card:hover {
            transform: translateY(-4px);
            border-color: rgba(167, 139, 250, 0.35);
            background: rgba(124, 92, 255, 0.08);
        }

        .icon {
            width: 45px;
            height: 45px;
            display: grid;
            place-items: center;
            margin-bottom: 17px;
            border-radius: 13px;
            background: rgba(124, 92, 255, 0.12);
            color: #b4a9ff;
            font-size: 20px;
            font-weight: 800;
        }

        .card h2 {
            margin: 0 0 8px;
            font-size: 16px;
        }

        .card p {
            margin: 0;
            color: #6f788b;
            font-size: 11px;
            line-height: 1.5;
        }

        .arrow {
            display: block;
            margin-top: 17px;
            color: #a78bfa;
            font-size: 12px;
            font-weight: 800;
        }

        @media (max-width: 750px) {
            .grid {
                grid-template-columns: 1fr 1fr;
            }
        }

        @media (max-width: 520px) {
            .top {
                margin-bottom: 35px;
            }

            .grid {
                grid-template-columns: 1fr;
            }

            h1 {
                font-size: 30px;
            }

            .assured {
                border-radius: 15px;
                flex-wrap: wrap;
            }
        }
    </style>
</head>

<body>

<main class="page">

    <header class="top">

        <div class="brand">
            <div class="logo">V</div>
            <div>
                VR <span>Mart</span>
            </div>
        </div>

        <a class="back"
           href="<%= request.getContextPath() %>/buyer/dashboard.jsp">
            &larr; Dashboard
        </a>

    </header>

    <div class="eyebrow">
        VR Mart Marketplace
    </div>

    <h1>
        Shop by Category
    </h1>

    <p class="intro">
        Explore products by category from sellers across
        the VR Mart marketplace.
    </p>

    <div class="assured">
        <strong>&#10003; VR Mart Assured</strong>
        <span>Marketplace assurance from VR Mart</span>
    </div>

    <section class="grid">

        <a class="card"
           href="<%= request.getContextPath() %>/products?category=Electronics">

            <div class="icon">&#9881;</div>

            <h2>Electronics</h2>

            <p>
                Mobiles, laptops, gadgets and electronics.
            </p>

            <span class="arrow">
                Explore Electronics &rarr;
            </span>

        </a>


        <a class="card"
           href="<%= request.getContextPath() %>/products?category=Fashion">

            <div class="icon">&#9733;</div>

            <h2>Fashion</h2>

            <p>
                Dresses, clothing, footwear and style.
            </p>

            <span class="arrow">
                Explore Fashion &rarr;
            </span>

        </a>


        <a class="card"
           href="<%= request.getContextPath() %>/products?category=Home%20%26%20Kitchen">

            <div class="icon">&#8962;</div>

            <h2>Home &amp; Kitchen</h2>

            <p>
                Home essentials and kitchen products.
            </p>

            <span class="arrow">
                Explore Home &amp; Kitchen &rarr;
            </span>

        </a>


        <a class="card"
           href="<%= request.getContextPath() %>/products?category=Beauty">

            <div class="icon">&#10024;</div>

            <h2>Beauty</h2>

            <p>
                Beauty, personal care and grooming.
            </p>

            <span class="arrow">
                Explore Beauty &rarr;
            </span>

        </a>


        <a class="card"
           href="<%= request.getContextPath() %>/products?category=Grocery">

            <div class="icon">&#9632;</div>

            <h2>Grocery</h2>

            <p>
                Daily-use groceries and essentials.
            </p>

            <span class="arrow">
                Explore Grocery &rarr;
            </span>

        </a>


        <a class="card"
           href="<%= request.getContextPath() %>/products?category=Sports%20%26%20Fitness">

            <div class="icon">&#9679;</div>

            <h2>Sports &amp; Fitness</h2>

            <p>
                Sports equipment and fitness essentials.
            </p>

            <span class="arrow">
                Explore Sports &amp; Fitness &rarr;
            </span>

        </a>


        <a class="card"
           href="<%= request.getContextPath() %>/products?category=Books">

            <div class="icon">&#9776;</div>

            <h2>Books</h2>

            <p>
                Books and study materials.
            </p>

            <span class="arrow">
                Explore Books &rarr;
            </span>

        </a>


        <a class="card"
           href="<%= request.getContextPath() %>/products?category=Toys">

            <div class="icon">&#9734;</div>

            <h2>Toys</h2>

            <p>
                Toys, games and kids products.
            </p>

            <span class="arrow">
                Explore Toys &rarr;
            </span>

        </a>


        <a class="card"
           href="<%= request.getContextPath() %>/products?category=Accessories">

            <div class="icon">&#9674;</div>

            <h2>Accessories</h2>

            <p>
                Useful accessories for everyday needs.
            </p>

            <span class="arrow">
                Explore Accessories &rarr;
            </span>

        </a>

    </section>

</main>

</body>
</html>
