<%@ page contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport"
          content="width=device-width, initial-scale=1.0">
    <title>Verify Email | VR Mart</title>
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
            line-height: 1.6;
            margin-bottom: 24px;
        }
        .email {
            color: #c7d2fe;
            font-weight: 700;
            word-break: break-word;
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
        .success {
            padding: 13px 15px;
            margin-bottom: 20px;
            border-radius: 12px;
            color: #bbf7d0;
            background: rgba(34, 197, 94, 0.10);
            border: 1px solid rgba(34, 197, 94, 0.25);
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
            font-size: 20px;
            font-weight: 700;
            letter-spacing: 7px;
            text-align: center;
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
        .resend {
            margin-top: 18px;
            text-align: center;
        }
        .resend button {
            width: auto;
            padding: 0;
            margin: 0;
            background: none;
            color: #a5b4fc;
            font-size: 13px;
            font-weight: 700;
        }
        .login {
            margin-top: 25px;
            text-align: center;
            color: #7f899a;
            font-size: 13px;
        }
        .login a {
            color: #a5b4fc;
            font-weight: 700;
            text-decoration: none;
        }
        .hint {
            margin-top: 18px;
            color: #697386;
            font-size: 12px;
            line-height: 1.6;
            text-align: center;
        }
    </style>
</head>
<body>
<div class="card">
    <div class="logo">V</div>

    <h1>Verify Email</h1>

    <p class="subtitle">
        We sent a 6-digit verification code to
        <span class="email">
            <%= session.getAttribute("verificationEmail") != null
                    ? session.getAttribute("verificationEmail")
                    : "your email address" %>
        </span>.
    </p>

    <% if (request.getAttribute("error") != null) { %>
        <div class="error">
            <%= request.getAttribute("error") %>
        </div>
    <% } %>

    <% if (request.getAttribute("success") != null) { %>
        <div class="success">
            <%= request.getAttribute("success") %>
        </div>
    <% } %>

    <form
        action="${pageContext.request.contextPath}/verify-email"
        method="post">

        <div class="field">
            <label for="otp">Verification Code</label>

            <input
                type="text"
                id="otp"
                name="otp"
                placeholder="000000"
                inputmode="numeric"
                autocomplete="one-time-code"
                pattern="[0-9]{6}"
                maxlength="6"
                required>
        </div>

        <button type="submit">
            Verify Email
        </button>
    </form>

    <div class="resend">
        <form
            action="${pageContext.request.contextPath}/resend-verification"
            method="post">

            <button type="submit">
                Resend verification code
            </button>
        </form>
    </div>

    <p class="hint">
        The verification code expires after 10 minutes.
    </p>

    <div class="login">
        Already verified?
        <a href="${pageContext.request.contextPath}/buyer/login">
            Go to Buyer Login
        </a>
    </div>
</div>
</body>
</html>
