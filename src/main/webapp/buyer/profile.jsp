<%@ page contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>

<%@ page import="com.vrmart.model.User" %>

<%
    final User user =
            (User) session.getAttribute("user");

    if (user == null
            || !User.ROLE_BUYER.equals(user.getRole())) {

        response.sendRedirect(
                request.getContextPath() + "/login");
        return;
    }

    final String message =
            (String) request.getAttribute("profileMessage");

    final String success =
            request.getParameter("success");
%>

<!DOCTYPE html>
<html lang="en">

<head>

    <meta charset="UTF-8">

    <meta name="viewport"
          content="width=device-width, initial-scale=1.0">

    <title>VR Mart | My Profile</title>

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
                    circle at 75% 0%,
                    rgba(124, 92, 255, 0.13),
                    transparent 30%
                ),
                #070914;
            color: #f7f7fb;
        }

        .page {
            max-width: 850px;
            margin: 0 auto;
            padding: 45px 25px;
        }

        .topbar {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 35px;
        }

        .brand {
            font-size: 23px;
            font-weight: 800;
        }

        .brand span {
            color: #7c5cff;
        }

        .back {
            color: #aeb4c7;
            text-decoration: none;
            font-size: 14px;
        }

        .back:hover {
            color: #ffffff;
        }

        .heading small {
            color: #7c5cff;
            font-size: 11px;
            font-weight: 700;
            letter-spacing: 1.5px;
        }

        .heading h1 {
            margin-top: 10px;
            font-size: 34px;
        }

        .heading p {
            margin-top: 10px;
            color: #858ca3;
            font-size: 14px;
        }

        .card {
            margin-top: 30px;
            padding: 32px;
            border-radius: 20px;
            background: rgba(255, 255, 255, 0.035);
            border: 1px solid rgba(255, 255, 255, 0.08);
        }

        .message {
            margin-bottom: 22px;
            padding: 13px 16px;
            border-radius: 10px;
            background: rgba(52, 211, 153, 0.10);
            border: 1px solid rgba(52, 211, 153, 0.25);
            color: #6ee7b7;
            font-size: 13px;
        }

        .error {
            background: rgba(248, 113, 113, 0.10);
            border-color: rgba(248, 113, 113, 0.25);
            color: #fca5a5;
        }

        .field {
            margin-bottom: 22px;
        }

        label {
            display: block;
            margin-bottom: 8px;
            color: #aeb4c7;
            font-size: 13px;
            font-weight: 600;
        }

        input {
            width: 100%;
            padding: 13px 15px;
            border-radius: 10px;
            border: 1px solid rgba(255, 255, 255, 0.09);
            outline: none;
            background: #0d1020;
            color: #ffffff;
            font-size: 14px;
        }

        input:focus {
            border-color: #7c5cff;
        }

        .readonly {
            opacity: 0.65;
        }

        .actions {
            display: flex;
            gap: 12px;
            margin-top: 28px;
        }

        button,
        .cancel {
            padding: 12px 20px;
            border-radius: 10px;
            font-size: 14px;
            cursor: pointer;
            text-decoration: none;
        }

        button {
            border: none;
            background: #7c5cff;
            color: #ffffff;
            font-weight: 700;
        }

        button:hover {
            background: #6d4ff0;
        }

        .cancel {
            border: 1px solid rgba(255, 255, 255, 0.10);
            color: #aeb4c7;
        }

        .cancel:hover {
            color: #ffffff;
        }

        .account {
            margin-top: 25px;
            padding-top: 22px;
            border-top: 1px solid rgba(255, 255, 255, 0.07);
        }

        .account-row {
            display: flex;
            justify-content: space-between;
            padding: 10px 0;
            color: #858ca3;
            font-size: 13px;
        }

        .account-row strong {
            color: #ffffff;
            font-weight: 500;
        }

        @media (max-width: 600px) {

            .page {
                padding: 30px 18px;
            }

            .card {
                padding: 22px;
            }

            .heading h1 {
                font-size: 28px;
            }

            .actions {
                flex-direction: column;
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
           href="<%= request.getContextPath() %>/buyer/dashboard.jsp">
            ← Back to Dashboard
        </a>

    </header>

    <section class="heading">

        <small>ACCOUNT</small>

        <h1>My Profile</h1>

        <p>
            View and update your VR Mart account information.
        </p>

    </section>

    <section class="card">

        <% if (message != null && !message.isBlank()) { %>

            <div class="message error">
                <%= message %>
            </div>

        <% } %>

        <% if (success != null && !success.isBlank()) { %>

            <div class="message">
                <%= success %>
            </div>

        <% } %>

        <form method="post"
              action="<%= request.getContextPath() %>/profile">

            <div class="field">

                <label for="username">
                    Username
                </label>

                <input
                    id="username"
                    name="username"
                    type="text"
                    value="<%= user.getUsername() %>"
                    maxlength="50"
                    required>

            </div>

            <div class="field">

                <label for="email">
                    Email
                </label>

                <input
                    id="email"
                    name="email"
                    type="email"
                    value="<%= user.getEmail() %>"
                    required>

            </div>

            <div class="field">

                <label for="phone">
                    Phone Number
                </label>

                <input
                    id="phone"
                    name="phone"
                    type="tel"
                    value="<%= user.getPhone() %>"
                    maxlength="10"
                    pattern="[6-9][0-9]{9}"
                    required>

            </div>

            <div class="actions">

                <button type="submit">
                    Save Changes
                </button>

                <a class="cancel"
                   href="<%= request.getContextPath() %>/buyer/dashboard.jsp">
                    Cancel
                </a>

            </div>

        </form>

        <div class="account">

            <div class="account-row">
                <span>Account Type</span>
                <strong>Buyer</strong>
            </div>

            <div class="account-row">
                <span>Account ID</span>
                <strong>#<%= user.getId() %></strong>
            </div>

        </div>

    </section>

</main>

</body>

</html>
