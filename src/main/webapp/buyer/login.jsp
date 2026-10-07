<%@ page contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport"
          content="width=device-width, initial-scale=1.0">

    <title>Buyer Login | VR Mart</title>

    <style>
        * {
            box-sizing: border-box;
            margin: 0;
            padding: 0;
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
            padding: 20px;
        }

        .card {
            width: 100%;
            max-width: 450px;
            padding: 42px;
            border-radius: 28px;
            background: rgba(17, 20, 31, 0.88);
            border: 1px solid rgba(255, 255, 255, 0.09);
            box-shadow: 0 30px 80px rgba(0, 0, 0, 0.45);
        }

        .logo {
            width: 52px;
            height: 52px;
            margin-bottom: 25px;
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
        }

        h1 {
            font-size: 30px;
            margin-bottom: 8px;
        }

        .subtitle {
            color: #8f98aa;
            font-size: 14px;
            margin-bottom: 28px;
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

        .field {
            margin-bottom: 20px;
        }

        label {
            display: block;
            margin-bottom: 8px;
            color: #d7dce5;
            font-size: 13px;
            font-weight: 600;
        }

        input {
            width: 100%;
            padding: 15px;
            border-radius: 13px;
            border: 1px solid rgba(255, 255, 255, 0.09);
            background: rgba(255, 255, 255, 0.045);
            color: #ffffff;
            font-size: 14px;
            outline: none;
        }

        input:focus {
            border-color: rgba(129, 140, 248, 0.7);
            box-shadow:
                0 0 0 4px rgba(99, 102, 241, 0.10);
        }

        button {
            width: 100%;
            padding: 15px;
            margin-top: 5px;
            border: none;
            border-radius: 13px;
            color: #ffffff;
            font-size: 14px;
            font-weight: 700;
            cursor: pointer;
            background: linear-gradient(
                135deg,
                #6366f1,
                #8b5cf6
            );
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
            text-decoration: underline;
        }

        .register {
            margin-top: 25px;
            text-align: center;
            color: #7f899a;
            font-size: 13px;
        }

        .register a {
            color: #a5b4fc;
            font-weight: 700;
            text-decoration: none;
        }
    </style>
</head>

<body>

<div class="card">

    <div class="logo">V</div>

    <h1>Buyer Login</h1>

    <p class="subtitle">
        Sign in to continue shopping on VR Mart.
    </p>

    <% if (request.getAttribute("error") != null) { %>

        <div class="error">
            <%= request.getAttribute("error") %>
        </div>

    <% } %>

    <form
        action="${pageContext.request.contextPath}/buyer/login"
        method="post">

        <div class="field">
            <label for="username">Username</label>

            <input
                type="text"
                id="username"
                name="username"
                placeholder="Enter your username"
                autocomplete="username"
                required>
        </div>

        <div class="field">
            <label for="password">Password</label>

            <input
                type="password"
                id="password"
                name="password"
                placeholder="Enter your password"
                autocomplete="current-password"
                required>
        </div>

        <button type="submit">
            Sign in as Buyer
        </button>

    </form>

    <div class="forgot">
        <a href="${pageContext.request.contextPath}/forgot-password">
            Forgot Password?
        </a>
    </div>

    <div class="register">
        Don't have a buyer account?
        <a href="${pageContext.request.contextPath}/buyer/register">
            Create account
        </a>
    </div>

</div>

</body>
</html>
