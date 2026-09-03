<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="en">

<head>

    <meta charset="UTF-8">

    <meta name="viewport"
          content="width=device-width, initial-scale=1.0">

    <title>VR Mart | Register</title>

    <style>

        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
        }

        body {
            min-height: 100vh;
            font-family: Arial, Helvetica, sans-serif;
            background:
                radial-gradient(
                    circle at 15% 20%,
                    rgba(99, 102, 241, 0.22),
                    transparent 30%
                ),
                radial-gradient(
                    circle at 85% 80%,
                    rgba(168, 85, 247, 0.18),
                    transparent 30%
                ),
                #080b14;
            color: #ffffff;
            display: flex;
            align-items: center;
            justify-content: center;
            padding: 25px;
        }

        .background-glow {
            position: fixed;
            width: 420px;
            height: 420px;
            border-radius: 50%;
            background: rgba(99, 102, 241, 0.12);
            filter: blur(100px);
            top: -180px;
            left: -120px;
            pointer-events: none;
        }

        .background-glow.two {
            top: auto;
            left: auto;
            right: -150px;
            bottom: -180px;
            background: rgba(168, 85, 247, 0.12);
        }

        .page {
            width: 100%;
            max-width: 1050px;
            display: grid;
            grid-template-columns: 1fr 1fr;
            gap: 30px;
            position: relative;
            z-index: 1;
        }

        .brand-panel {
            display: flex;
            flex-direction: column;
            justify-content: center;
            padding: 45px;
        }

        .brand {
            display: flex;
            align-items: center;
            gap: 14px;
            margin-bottom: 35px;
        }

        .brand-logo {
            width: 52px;
            height: 52px;
            border-radius: 16px;
            display: flex;
            align-items: center;
            justify-content: center;
            font-size: 24px;
            font-weight: 800;
            background: linear-gradient(
                135deg,
                #6366f1,
                #8b5cf6
            );
            box-shadow:
                0 15px 40px rgba(99, 102, 241, 0.35);
        }

        .brand-name {
            font-size: 25px;
            font-weight: 800;
        }

        .brand-name span {
            color: #818cf8;
        }

        .eyebrow {
            display: inline-flex;
            width: fit-content;
            padding: 8px 13px;
            border-radius: 999px;
            background: rgba(99, 102, 241, 0.10);
            border: 1px solid rgba(129, 140, 248, 0.25);
            color: #a5b4fc;
            font-size: 12px;
            font-weight: 700;
            letter-spacing: 1px;
            text-transform: uppercase;
            margin-bottom: 22px;
        }

        .brand-panel h1 {
            font-size: clamp(42px, 5vw, 62px);
            line-height: 1.03;
            letter-spacing: -3px;
            margin-bottom: 22px;
        }

        .brand-panel h1 span {
            background: linear-gradient(
                90deg,
                #818cf8,
                #c084fc
            );
            background-clip: text;
            -webkit-background-clip: text;
            color: transparent;
            -webkit-text-fill-color: transparent;
        }

        .brand-panel p {
            max-width: 450px;
            color: #9ca3af;
            font-size: 15px;
            line-height: 1.8;
        }

        .register-card {
            width: 100%;
            max-width: 450px;
            margin: auto;
            padding: 42px;
            border-radius: 28px;
            background: rgba(17, 20, 31, 0.82);
            border: 1px solid rgba(255, 255, 255, 0.09);
            box-shadow:
                0 30px 80px rgba(0, 0, 0, 0.45),
                inset 0 1px 0 rgba(255, 255, 255, 0.04);
            backdrop-filter: blur(22px);
        }

        .register-card h2 {
            font-size: 30px;
            margin-bottom: 9px;
        }

        .subtitle {
            color: #8f98aa;
            font-size: 14px;
            line-height: 1.6;
            margin-bottom: 28px;
        }

        .role-list {
            display: grid;
            gap: 14px;
        }

        .role-card {
            display: flex;
            align-items: center;
            justify-content: space-between;
            padding: 20px;
            border-radius: 16px;
            text-decoration: none;
            color: #ffffff;
            background: rgba(255, 255, 255, 0.035);
            border: 1px solid rgba(255, 255, 255, 0.08);
            transition: 0.25s ease;
        }

        .role-card:hover {
            transform: translateY(-3px);
            border-color: rgba(129, 140, 248, 0.55);
            background: rgba(99, 102, 241, 0.08);
        }

        .role-content {
            display: flex;
            align-items: center;
            gap: 15px;
        }

        .role-icon {
            width: 46px;
            height: 46px;
            display: flex;
            align-items: center;
            justify-content: center;
            border-radius: 13px;
            background: rgba(99, 102, 241, 0.13);
            color: #a5b4fc;
            font-size: 19px;
            font-weight: 800;
        }

        .role-title {
            font-size: 15px;
            font-weight: 700;
            margin-bottom: 4px;
        }

        .role-description {
            color: #737d90;
            font-size: 11px;
        }

        .arrow {
            color: #818cf8;
            font-size: 20px;
        }

        .divider {
            display: flex;
            align-items: center;
            gap: 12px;
            margin: 28px 0;
            color: #596273;
            font-size: 11px;
            text-transform: uppercase;
            letter-spacing: 1px;
        }

        .divider::before,
        .divider::after {
            content: "";
            height: 1px;
            flex: 1;
            background: rgba(255, 255, 255, 0.07);
        }

        .login-text {
            text-align: center;
            color: #7f899a;
            font-size: 13px;
        }

        .login-text a {
            color: #a5b4fc;
            font-weight: 700;
            text-decoration: none;
        }

        .login-text a:hover {
            color: #c4b5fd;
        }

        .security {
            margin-top: 25px;
            text-align: center;
            color: #596273;
            font-size: 11px;
        }

        @media (max-width: 850px) {

            body {
                padding: 20px;
            }

            .page {
                grid-template-columns: 1fr;
            }

            .brand-panel {
                padding: 20px 10px 5px;
                text-align: center;
                align-items: center;
            }

            .brand {
                margin-bottom: 22px;
            }

            .brand-panel h1 {
                font-size: 42px;
            }

            .brand-panel p {
                font-size: 14px;
            }

            .register-card {
                padding: 30px 24px;
            }
        }

    </style>

</head>

<body>

<div class="background-glow"></div>
<div class="background-glow two"></div>

<main class="page">

    <section class="brand-panel">

        <div class="brand">

            <div class="brand-logo">
                V
            </div>

            <div class="brand-name">
                VR <span>Mart</span>
            </div>

        </div>

        <div class="eyebrow">
            Join VR Mart
        </div>

        <h1>
            Start your<br>
            <span>journey.</span>
        </h1>

        <p>
            Create your VR Mart account and choose
            whether you want to shop as a buyer or
            manage products as a seller.
        </p>

    </section>

    <section>

        <div class="register-card">

            <h2>
                Create Account
            </h2>

            <p class="subtitle">
                Select the type of account you want to create.
            </p>

            <div class="role-list">

                <a
                    class="role-card"
                    href="<%= request.getContextPath() %>/buyer/register.jsp">

                    <div class="role-content">

                        <div class="role-icon">
                            B
                        </div>

                        <div>

                            <div class="role-title">
                                Buyer Registration
                            </div>

                            <div class="role-description">
                                Create an account to shop products
                            </div>

                        </div>

                    </div>

                    <div class="arrow">
                        →
                    </div>

                </a>

                <a
                    class="role-card"
                    href="<%= request.getContextPath() %>/seller/register.jsp">

                    <div class="role-content">

                        <div class="role-icon">
                            S
                        </div>

                        <div>

                            <div class="role-title">
                                Seller Registration
                            </div>

                            <div class="role-description">
                                Create an account to sell products
                            </div>

                        </div>

                    </div>

                    <div class="arrow">
                        →
                    </div>

                </a>

            </div>

            <div class="divider">
                Already registered?
            </div>

            <div class="login-text">

                Already have an account?

                <a
                    href="<%= request.getContextPath() %>/login.jsp">
                    Sign In
                </a>

            </div>

            <div class="security">
                🔒 Secure role-based registration
            </div>

        </div>

    </section>

</main>

</body>

</html>
