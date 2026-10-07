<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">

    <title>Seller Login | VR Mart</title>

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
        }

        .page {
            width: 100%;
            max-width: 1180px;
            min-height: 680px;
            padding: 30px;
            display: grid;
            grid-template-columns: 1fr 1fr;
            gap: 30px;
        }

        .brand-panel {
            display: flex;
            flex-direction: column;
            justify-content: center;
            padding: 50px;
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
            font-size: clamp(42px, 5vw, 68px);
            line-height: 1.02;
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
            max-width: 480px;
            color: #9ca3af;
            font-size: 16px;
            line-height: 1.8;
        }

        .features {
            display: flex;
            gap: 12px;
            flex-wrap: wrap;
            margin-top: 32px;
        }

        .feature {
            padding: 10px 14px;
            border: 1px solid rgba(255, 255, 255, 0.08);
            background: rgba(255, 255, 255, 0.035);
            border-radius: 12px;
            color: #cbd5e1;
            font-size: 13px;
        }

        .login-wrapper {
            display: flex;
            align-items: center;
            justify-content: center;
        }

        .login-card {
            width: 100%;
            max-width: 460px;
            padding: 42px;
            border-radius: 28px;
            background: rgba(17, 20, 31, 0.82);
            border: 1px solid rgba(255, 255, 255, 0.09);
            box-shadow:
                0 30px 80px rgba(0, 0, 0, 0.45),
                inset 0 1px 0 rgba(255, 255, 255, 0.04);
            backdrop-filter: blur(22px);
        }

        .login-card h2 {
            font-size: 30px;
            margin-bottom: 9px;
            letter-spacing: -1px;
        }

        .subtitle {
            color: #8f98aa;
            font-size: 14px;
            margin-bottom: 30px;
            line-height: 1.6;
        }

        .error {
            padding: 13px 15px;
            margin-bottom: 20px;
            border-radius: 12px;
            color: #fecaca;
            background: rgba(239, 68, 68, 0.10);
            border: 1px solid rgba(239, 68, 68, 0.25);
            font-size: 13px;
        }

        .form-group {
            margin-bottom: 20px;
        }

        .form-group label {
            display: block;
            color: #d7dce5;
            font-size: 13px;
            font-weight: 600;
            margin-bottom: 9px;
        }

        .form-control {
            width: 100%;
            padding: 15px 16px;
            border-radius: 13px;
            border: 1px solid rgba(255, 255, 255, 0.09);
            background: rgba(255, 255, 255, 0.045);
            color: #ffffff;
            font-size: 14px;
            outline: none;
            transition: 0.25s ease;
        }

        .form-control::placeholder {
            color: #626b7c;
        }

        .form-control:focus {
            border-color: rgba(129, 140, 248, 0.7);
            background: rgba(255, 255, 255, 0.065);
            box-shadow:
                0 0 0 4px rgba(99, 102, 241, 0.10);
        }

        .login-button {
            width: 100%;
            border: none;
            border-radius: 13px;
            padding: 15px;
            margin-top: 8px;
            color: #ffffff;
            font-size: 14px;
            font-weight: 700;
            cursor: pointer;
            background: linear-gradient(
                135deg,
                #6366f1,
                #8b5cf6
            );
            box-shadow:
                0 12px 30px rgba(99, 102, 241, 0.25);
            transition: 0.25s ease;
        }

        .login-button:hover {
            transform: translateY(-2px);
            box-shadow:
                0 17px 35px rgba(99, 102, 241, 0.35);
        }

        .forgot {
            margin-top: 14px;
            text-align: right;
            font-size: 13px;
        }

        .forgot a {
            color: #a5b4fc;
            font-weight: 700;
            text-decoration: none;
        }

        .forgot a:hover {
            color: #c4b5fd;
            text-decoration: underline;
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

        .register-text {
            text-align: center;
            color: #7f899a;
            font-size: 13px;
        }

        .register-text a {
            color: #a5b4fc;
            font-weight: 700;
            text-decoration: none;
        }

        .register-text a:hover {
            color: #c4b5fd;
        }

        .security {
            margin-top: 25px;
            display: flex;
            align-items: center;
            justify-content: center;
            gap: 8px;
            color: #596273;
            font-size: 11px;
        }

        @media (max-width: 850px) {
            body {
                overflow-y: auto;
            }

            .page {
                grid-template-columns: 1fr;
                padding: 20px;
            }

            .brand-panel {
                padding: 25px 10px 5px;
                text-align: center;
                align-items: center;
            }

            .brand {
                margin-bottom: 25px;
            }

            .brand-panel h1 {
                font-size: 44px;
            }

            .brand-panel p {
                font-size: 14px;
            }

            .features {
                justify-content: center;
            }

            .login-card {
                padding: 30px 24px;
            }
        }
    </style>
</head>

<body>

<main class="page">

    <section class="brand-panel">

        <div class="brand">

            <div class="brand-logo">V</div>

            <div class="brand-name">
                VR <span>Mart</span>
            </div>

        </div>

        <div class="eyebrow">
            Seller Portal
        </div>

        <h1>
            Sell more.<br>
            <span>Grow faster.</span>
        </h1>

        <p>
            Sign in to manage your products, track your
            marketplace activity and grow your business
            with VR Mart.
        </p>

        <div class="features">
            <div class="feature">✦ Product Management</div>
            <div class="feature">◈ Seller Dashboard</div>
            <div class="feature">◆ Secure Account</div>
        </div>

    </section>

    <section class="login-wrapper">

        <div class="login-card">

            <h2>Seller Sign in</h2>

            <p class="subtitle">
                Enter your seller credentials to access
                your dashboard.
            </p>

            <% if (request.getAttribute("error") != null) { %>

                <div class="error">
                    <%= request.getAttribute("error") %>
                </div>

            <% } %>

            <form
                action="<%= request.getContextPath() %>/seller/login"
                method="post">

                <div class="form-group">

                    <label for="username">
                        Username
                    </label>

                    <input
                        class="form-control"
                        type="text"
                        id="username"
                        name="username"
                        placeholder="Enter your seller username"
                        autocomplete="username"
                        required>

                </div>

                <div class="form-group">

                    <label for="password">
                        Password
                    </label>

                    <input
                        class="form-control"
                        type="password"
                        id="password"
                        name="password"
                        placeholder="Enter your password"
                        autocomplete="current-password"
                        required>

                </div>

                <button
                    class="login-button"
                    type="submit">
                    Sign in as Seller →
                </button>

            </form>

            <div class="forgot">
                <a href="<%= request.getContextPath() %>/forgot-password">
                    Forgot Password?
                </a>
            </div>

            <div class="divider">
                New seller?
            </div>

            <div class="register-text">

                Don't have a seller account?

                <a
                    href="<%= request.getContextPath() %>/seller/register">
                    Create Seller Account
                </a>

            </div>

            <div class="security">
                🔒 Your seller account is protected
            </div>

        </div>

    </section>

</main>

</body>
</html>
