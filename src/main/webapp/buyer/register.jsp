<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">

    <title>Buyer Registration | VR Mart</title>

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
            padding: 30px;
        }

        .page {
            width: 100%;
            max-width: 1100px;
            min-height: 680px;
            display: grid;
            grid-template-columns: 1fr 1fr;
            overflow: hidden;
            border: 1px solid rgba(255, 255, 255, 0.12);
            border-radius: 28px;
            background: rgba(15, 18, 35, 0.88);
            box-shadow:
                0 30px 80px rgba(0, 0, 0, 0.55),
                inset 0 1px 0 rgba(255, 255, 255, 0.06);
            backdrop-filter: blur(20px);
        }

        .brand-panel {
            position: relative;
            padding: 55px;
            display: flex;
            flex-direction: column;
            justify-content: space-between;
            background:
                linear-gradient(
                    145deg,
                    rgba(99, 102, 241, 0.25),
                    rgba(124, 58, 237, 0.08)
                );
            overflow: hidden;
        }

        .brand-panel::before {
            content: "";
            position: absolute;
            width: 280px;
            height: 280px;
            border-radius: 50%;
            background: rgba(99, 102, 241, 0.18);
            filter: blur(10px);
            top: -100px;
            right: -100px;
        }

        .brand-panel::after {
            content: "";
            position: absolute;
            width: 220px;
            height: 220px;
            border-radius: 50%;
            background: rgba(168, 85, 247, 0.15);
            filter: blur(20px);
            bottom: -80px;
            left: -80px;
        }

        .logo {
            position: relative;
            z-index: 2;
            display: flex;
            align-items: center;
            gap: 12px;
            font-size: 25px;
            font-weight: 800;
        }

        .logo-icon {
            width: 43px;
            height: 43px;
            display: flex;
            align-items: center;
            justify-content: center;
            border-radius: 13px;
            background: linear-gradient(135deg, #6366f1, #a855f7);
            font-size: 20px;
        }

        .brand-content {
            position: relative;
            z-index: 2;
        }

        .eyebrow {
            color: #a5b4fc;
            font-size: 13px;
            font-weight: 700;
            text-transform: uppercase;
            letter-spacing: 2px;
            margin-bottom: 18px;
        }

        .brand-content h1 {
            max-width: 430px;
            font-size: clamp(38px, 4vw, 58px);
            line-height: 1.05;
            letter-spacing: -2.5px;
            margin-bottom: 22px;
        }

        .gradient-text {
            background: linear-gradient(90deg, #a5b4fc, #c084fc);
            -webkit-background-clip: text;
            -webkit-text-fill-color: transparent;
            background-clip: text;
        }

        .brand-content p {
            max-width: 430px;
            color: #b7bdd3;
            font-size: 16px;
            line-height: 1.7;
        }

        .features {
            position: relative;
            z-index: 2;
            display: flex;
            flex-direction: column;
            gap: 15px;
        }

        .feature {
            display: flex;
            align-items: center;
            gap: 13px;
            color: #d9dded;
            font-size: 14px;
        }

        .feature-icon {
            width: 34px;
            height: 34px;
            display: flex;
            align-items: center;
            justify-content: center;
            border-radius: 10px;
            background: rgba(255, 255, 255, 0.07);
            border: 1px solid rgba(255, 255, 255, 0.08);
        }

        .form-panel {
            padding: 55px;
            display: flex;
            align-items: center;
            justify-content: center;
        }

        .form-container {
            width: 100%;
            max-width: 400px;
        }

        .form-header {
            margin-bottom: 25px;
        }

        .form-header h2 {
            font-size: 31px;
            letter-spacing: -1px;
            margin-bottom: 9px;
        }

        .form-header p {
            color: #8f96ae;
            font-size: 14px;
            line-height: 1.5;
        }

        .error {
            margin-bottom: 20px;
            padding: 13px 15px;
            border: 1px solid rgba(248, 113, 113, 0.35);
            border-radius: 12px;
            background: rgba(248, 113, 113, 0.09);
            color: #fca5a5;
            font-size: 13px;
        }

        .field {
            margin-bottom: 16px;
        }

        .field label {
            display: block;
            margin-bottom: 8px;
            color: #d8dbea;
            font-size: 13px;
            font-weight: 600;
        }

        .field input {
            width: 100%;
            height: 48px;
            padding: 0 15px;
            border: 1px solid rgba(255, 255, 255, 0.10);
            border-radius: 12px;
            outline: none;
            background: rgba(255, 255, 255, 0.045);
            color: #ffffff;
            font-size: 14px;
            transition: 0.2s ease;
        }

        .field input::placeholder {
            color: #656d86;
        }

        .field input:focus {
            border-color: rgba(129, 140, 248, 0.8);
            background: rgba(255, 255, 255, 0.07);
            box-shadow:
                0 0 0 4px rgba(99, 102, 241, 0.10);
        }

        .password-help {
            margin-top: 7px;
            color: #777f97;
            font-size: 11px;
            line-height: 1.5;
        }

        .submit-btn {
            width: 100%;
            height: 52px;
            border: none;
            border-radius: 13px;
            background: linear-gradient(135deg, #6366f1, #8b5cf6);
            color: #ffffff;
            font-size: 14px;
            font-weight: 700;
            cursor: pointer;
            margin-top: 5px;
            box-shadow:
                0 12px 30px rgba(99, 102, 241, 0.25);
            transition: 0.2s ease;
        }

        .submit-btn:hover {
            transform: translateY(-2px);
            box-shadow:
                0 16px 35px rgba(99, 102, 241, 0.35);
        }

        .login-link {
            margin-top: 24px;
            text-align: center;
            color: #858ca4;
            font-size: 13px;
        }

        .login-link a {
            color: #a5b4fc;
            font-weight: 600;
            text-decoration: none;
        }

        .login-link a:hover {
            text-decoration: underline;
        }

        @media (max-width: 800px) {
            body {
                padding: 15px;
            }

            .page {
                grid-template-columns: 1fr;
                max-width: 500px;
            }

            .brand-panel {
                min-height: 300px;
                padding: 35px;
            }

            .features {
                display: none;
            }

            .form-panel {
                padding: 35px;
            }
        }

        @media (max-width: 430px) {
            .brand-panel,
            .form-panel {
                padding: 28px 22px;
            }

            .page {
                border-radius: 20px;
            }
        }
    </style>
</head>

<body>

<div class="page">

    <section class="brand-panel">

        <div class="logo">
            <div class="logo-icon">V</div>
            <span>VR Mart</span>
        </div>

        <div class="brand-content">

            <div class="eyebrow">
                Buyer Marketplace
            </div>

            <h1>
                Shop smarter.
                <span class="gradient-text">
                    Live better.
                </span>
            </h1>

            <p>
                Create your buyer account and discover
                products from trusted sellers on VR Mart.
            </p>

        </div>

        <div class="features">

            <div class="feature">
                <div class="feature-icon">✓</div>
                <span>Simple and secure shopping</span>
            </div>

            <div class="feature">
                <div class="feature-icon">★</div>
                <span>Trusted marketplace experience</span>
            </div>

            <div class="feature">
                <div class="feature-icon">↗</div>
                <span>Easy order management</span>
            </div>

        </div>

    </section>

    <section class="form-panel">

        <div class="form-container">

            <div class="form-header">
                <h2>Create Buyer Account</h2>

                <p>
                    Join VR Mart and start your shopping journey.
                </p>
            </div>

            <% if (request.getAttribute("error") != null) { %>

                <div class="error">
                    <%= request.getAttribute("error") %>
                </div>

            <% } %>

            <form
                action="${pageContext.request.contextPath}/buyer/register"
                method="post"
                onsubmit="return validateForm();">

                <div class="field">

                    <label for="username">
                        Username
                    </label>

                    <input
                        type="text"
                        id="username"
                        name="username"
                        placeholder="Choose a username"
                        maxlength="50"
                        autocomplete="username"
                        required>

                </div>

                <div class="field">

                    <label for="email">
                        Email address
                    </label>

                    <input
                        type="email"
                        id="email"
                        name="email"
                        placeholder="you@example.com"
                        maxlength="255"
                        autocomplete="email"
                        required>

                </div>

                <div class="field">

                    <label for="phone">
                        Phone number
                    </label>

                    <input
                        type="tel"
                        id="phone"
                        name="phone"
                        placeholder="Enter phone number"
                        maxlength="15"
                        autocomplete="tel"
                        required>

                </div>

                <div class="field">

                    <label for="password">
                        Password
                    </label>

                    <input
                        type="password"
                        id="password"
                        name="password"
                        placeholder="Create a strong password"
                        minlength="8"
                        autocomplete="new-password"
                        required>

                    <div class="password-help">
                        8+ characters, uppercase, lowercase,
                        number and special character.
                    </div>

                </div>

                <div class="field">

                    <label for="confirmPassword">
                        Confirm password
                    </label>

                    <input
                        type="password"
                        id="confirmPassword"
                        name="confirmPassword"
                        placeholder="Enter password again"
                        minlength="8"
                        autocomplete="new-password"
                        required>

                </div>

                <button
                    class="submit-btn"
                    type="submit">
                    Create Buyer Account
                </button>

            </form>

            <div class="login-link">

                Already a buyer?

                <a
                    href="${pageContext.request.contextPath}/buyer/login">
                    Sign in
                </a>

            </div>

        </div>

    </section>

</div>

<script>
    function validateForm() {

        const password =
            document.getElementById("password").value;

        const confirmPassword =
            document.getElementById("confirmPassword").value;

        if (password !== confirmPassword) {
            alert("Passwords do not match.");
            return false;
        }

        if (password.length < 8) {
            alert(
                "Password must contain at least 8 characters."
            );
            return false;
        }

        if (!/[A-Z]/.test(password)) {
            alert(
                "Password must contain at least one uppercase letter."
            );
            return false;
        }

        if (!/[a-z]/.test(password)) {
            alert(
                "Password must contain at least one lowercase letter."
            );
            return false;
        }

        if (!/[0-9]/.test(password)) {
            alert(
                "Password must contain at least one number."
            );
            return false;
        }

        if (!/[^A-Za-z0-9]/.test(password)) {
            alert(
                "Password must contain at least one special character."
            );
            return false;
        }

        return true;
    }
</script>

</body>
</html>
