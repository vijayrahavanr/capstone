<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="en">

<head>
    <meta charset="UTF-8">
    <meta name="viewport"
          content="width=device-width, initial-scale=1.0">

    <title>VR Mart</title>

    <style>
        * {
            box-sizing: border-box;
            margin: 0;
            padding: 0;
        }

        body {
            min-height: 100vh;
            font-family: "Segoe UI", Arial, sans-serif;
            background:
                radial-gradient(
                    circle at 15% 20%,
                    rgba(99, 102, 241, 0.25),
                    transparent 35%
                ),
                radial-gradient(
                    circle at 85% 80%,
                    rgba(168, 85, 247, 0.20),
                    transparent 35%
                ),
                #070914;
            color: #ffffff;
            display: flex;
            align-items: center;
            justify-content: center;
            padding: 25px;
        }

        .container {
            width: 100%;
            max-width: 900px;
            text-align: center;
        }

        .logo {
            width: 65px;
            height: 65px;
            margin: 0 auto 22px;
            display: flex;
            align-items: center;
            justify-content: center;
            border-radius: 18px;
            background: linear-gradient(
                135deg,
                #6366f1,
                #a855f7
            );
            font-size: 30px;
            font-weight: 800;
            box-shadow:
                0 15px 40px rgba(99, 102, 241, 0.30);
        }

        h1 {
            font-size: 48px;
            margin-bottom: 12px;
            letter-spacing: -2px;
        }

        .gradient {
            background: linear-gradient(
                90deg,
                #a5b4fc,
                #c084fc
            );
            -webkit-background-clip: text;
            -webkit-text-fill-color: transparent;
            background-clip: text;
        }

        .subtitle {
            color: #9ca3af;
            font-size: 16px;
            margin-bottom: 45px;
        }

        .cards {
            display: grid;
            grid-template-columns: 1fr 1fr;
            gap: 25px;
        }

        .card {
            padding: 35px;
            border-radius: 24px;
            background: rgba(17, 20, 31, 0.85);
            border: 1px solid rgba(255, 255, 255, 0.09);
            box-shadow:
                0 25px 60px rgba(0, 0, 0, 0.40);
        }

        .card h2 {
            font-size: 25px;
            margin-bottom: 10px;
        }

        .card p {
            color: #8f98aa;
            font-size: 14px;
            line-height: 1.6;
            margin-bottom: 25px;
        }

        .buttons {
            display: flex;
            gap: 12px;
        }

        .btn {
            flex: 1;
            padding: 13px;
            border-radius: 12px;
            text-decoration: none;
            font-size: 13px;
            font-weight: 700;
            transition: 0.2s ease;
        }

        .login {
            color: #ffffff;
            background: linear-gradient(
                135deg,
                #6366f1,
                #8b5cf6
            );
        }

        .register {
            color: #c7d2fe;
            background: rgba(255, 255, 255, 0.06);
            border: 1px solid rgba(255, 255, 255, 0.10);
        }

        .btn:hover {
            transform: translateY(-2px);
        }

        .footer {
            margin-top: 35px;
            color: #596273;
            font-size: 12px;
        }

        @media (max-width: 650px) {
            h1 {
                font-size: 38px;
            }

            .cards {
                grid-template-columns: 1fr;
            }

            .card {
                padding: 28px;
            }
        }
    </style>
</head>

<body>

<div class="container">

    <div class="logo">V</div>

    <h1>
        Welcome to <span class="gradient">VR Mart</span>
    </h1>

    <p class="subtitle">
        Your trusted marketplace for buyers and sellers.
    </p>

    <div class="cards">

        <!-- Buyer -->
        <div class="card">

            <h2>Buyer</h2>

            <p>
                Browse products, add items to your cart
                and manage your orders.
            </p>

            <div class="buttons">

                <a class="btn login"
                   href="${pageContext.request.contextPath}/buyer/login">
                    Buyer Login
                </a>

                <a class="btn register"
                   href="${pageContext.request.contextPath}/buyer/register">
                    Register
                </a>

            </div>

        </div>

        <!-- Seller -->
        <div class="card">

            <h2>Seller</h2>

            <p>
                List products, manage your inventory
                and grow your business.
            </p>

            <div class="buttons">

                <a class="btn login"
                   href="${pageContext.request.contextPath}/seller/login">
                    Seller Login
                </a>

                <a class="btn register"
                   href="${pageContext.request.contextPath}/seller/register">
                    Register
                </a>

            </div>

        </div>

    </div>

    <div class="footer">
        Secure Buyer &amp; Seller Marketplace
    </div>

</div>

</body>
</html>
