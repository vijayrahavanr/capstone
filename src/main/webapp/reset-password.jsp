<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Reset Password - VR Mart</title>
    <style>
        * {
            box-sizing: border-box;
            margin: 0;
            padding: 0;
        }
        body {
            min-height: 100vh;
            display: flex;
            align-items: center;
            justify-content: center;
            padding: 24px;
            background: #0b0f19;
            color: #f8fafc;
            font-family: Arial, Helvetica, sans-serif;
        }
        .container {
            width: 100%;
            max-width: 460px;
        }
        .card {
            padding: 38px;
            border: 1px solid rgba(255, 255, 255, 0.08);
            border-radius: 20px;
            background: #111827;
            box-shadow: 0 24px 70px rgba(0, 0, 0, 0.35);
        }
        .logo {
            margin-bottom: 10px;
            color: #60a5fa;
            font-size: 28px;
            font-weight: 800;
            text-align: center;
        }
        h1 {
            margin-bottom: 10px;
            font-size: 26px;
            text-align: center;
        }
        .subtitle {
            margin-bottom: 28px;
            color: #94a3b8;
            font-size: 14px;
            line-height: 1.6;
            text-align: center;
        }
        .message {
            margin-bottom: 20px;
            padding: 12px 14px;
            border-radius: 10px;
            font-size: 14px;
            line-height: 1.5;
        }
        .error {
            border: 1px solid rgba(248, 113, 113, 0.25);
            background: rgba(248, 113, 113, 0.08);
            color: #fca5a5;
        }
        .success {
            border: 1px solid rgba(74, 222, 128, 0.25);
            background: rgba(74, 222, 128, 0.08);
            color: #86efac;
        }
        .form-group {
            margin-bottom: 20px;
        }
        label {
            display: block;
            margin-bottom: 8px;
            color: #cbd5e1;
            font-size: 14px;
            font-weight: 600;
        }
        input {
            width: 100%;
            padding: 13px 14px;
            border: 1px solid #334155;
            border-radius: 10px;
            outline: none;
            background: #0f172a;
            color: #f8fafc;
            font-size: 15px;
            transition: border-color 0.2s, box-shadow 0.2s;
        }
        input:focus {
            border-color: #60a5fa;
            box-shadow: 0 0 0 3px rgba(96, 165, 250, 0.12);
        }
        .otp {
            letter-spacing: 8px;
            text-align: center;
            font-size: 20px;
            font-weight: 700;
        }
        .hint {
            margin-top: 7px;
            color: #64748b;
            font-size: 12px;
        }
        button {
            width: 100%;
            padding: 14px;
            border: 0;
            border-radius: 10px;
            background: #2563eb;
            color: #ffffff;
            cursor: pointer;
            font-size: 15px;
            font-weight: 700;
            transition: background 0.2s, transform 0.2s;
        }
        button:hover {
            background: #1d4ed8;
            transform: translateY(-1px);
        }
        .footer {
            margin-top: 24px;
            text-align: center;
            font-size: 13px;
        }
        .footer a {
            color: #60a5fa;
            text-decoration: none;
        }
        .footer a:hover {
            text-decoration: underline;
        }
        .requirements {
            margin-top: 8px;
            color: #64748b;
            font-size: 12px;
            line-height: 1.6;
        }
        @media (max-width: 520px) {
            body {
                padding: 16px;
            }
            .card {
                padding: 28px 22px;
            }
        }
    </style>
</head>
<body>
<div class="container">
    <div class="card">
        <div class="logo">VR Mart</div>
        <h1>Reset Password</h1>
        <p class="subtitle">
            Enter the OTP sent to your email and create a new password.
        </p>

        <%
            String error = (String) request.getAttribute("error");
            String success = (String) request.getAttribute("success");
            if (error != null && !error.isBlank()) {
        %>
            <div class="message error"><%= error %></div>
        <%
            }
            if (success != null && !success.isBlank()) {
        %>
            <div class="message success"><%= success %></div>
        <%
            }
        %>

        <form method="post" action="<%= request.getContextPath() %>/reset-password">
            <div class="form-group">
                <label for="otp">Verification OTP</label>
                <input
                    id="otp"
                    name="otp"
                    class="otp"
                    type="text"
                    inputmode="numeric"
                    pattern="[0-9]{6}"
                    maxlength="6"
                    minlength="6"
                    autocomplete="one-time-code"
                    placeholder="000000"
                    required>
                <div class="hint">Enter the 6-digit OTP. It expires in 10 minutes.</div>
            </div>

            <div class="form-group">
                <label for="newPassword">New Password</label>
                <input
                    id="newPassword"
                    name="newPassword"
                    type="password"
                    minlength="8"
                    autocomplete="new-password"
                    placeholder="Enter new password"
                    required>
                <div class="requirements">
                    Minimum 8 characters with uppercase, lowercase, number
                    and special character.
                </div>
            </div>

            <div class="form-group">
                <label for="confirmPassword">Confirm Password</label>
                <input
                    id="confirmPassword"
                    name="confirmPassword"
                    type="password"
                    minlength="8"
                    autocomplete="new-password"
                    placeholder="Confirm new password"
                    required>
            </div>

            <button type="submit">Reset Password</button>
        </form>

        <div class="footer">
            <a href="<%= request.getContextPath() %>/login.jsp">
                Back to Login
            </a>
        </div>
    </div>
</div>
</body>
</html>
