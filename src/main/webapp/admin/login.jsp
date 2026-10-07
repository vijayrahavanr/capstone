<%@ page contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>
<%
    final String error =
            (String) request.getAttribute("error");
%>
<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8">
<meta name="viewport"
      content="width=device-width, initial-scale=1.0">
<title>VR Mart | Admin Login</title>
<style>
* { box-sizing: border-box; }
body {
    min-height: 100vh;
    margin: 0;
    display: grid;
    place-items: center;
    padding: 25px;
    font-family: Arial, Helvetica, sans-serif;
    background:
        radial-gradient(circle at 50% 0%,
        rgba(124, 92, 255, 0.20),
        transparent 38%),
        #070914;
    color: #ffffff;
}
.card {
    width: min(430px, 100%);
    padding: 32px;
    border: 1px solid rgba(255,255,255,.09);
    border-radius: 22px;
    background: rgba(17,20,31,.94);
    box-shadow: 0 25px 70px rgba(0,0,0,.45);
}
.logo {
    width: 48px;
    height: 48px;
    display: grid;
    place-items: center;
    margin-bottom: 20px;
    border-radius: 14px;
    background: linear-gradient(135deg,#6366f1,#a855f7);
    font-size: 21px;
    font-weight: 900;
}
.eyebrow {
    color: #a78bfa;
    font-size: 10px;
    font-weight: 800;
    letter-spacing: 1.5px;
}
h1 { margin: 9px 0 8px; font-size: 29px; }
p {
    margin: 0 0 24px;
    color: #7f899a;
    font-size: 12px;
    line-height: 1.6;
}
.error {
    margin-bottom: 17px;
    padding: 11px 13px;
    border: 1px solid rgba(239,68,68,.25);
    border-radius: 10px;
    color: #fecaca;
    background: rgba(239,68,68,.08);
    font-size: 11px;
}
.field { margin-bottom: 15px; }
label {
    display: block;
    margin-bottom: 7px;
    color: #c7cedb;
    font-size: 11px;
    font-weight: 700;
}
input {
    width: 100%;
    padding: 13px 14px;
    border: 1px solid rgba(255,255,255,.10);
    border-radius: 10px;
    outline: 0;
    background: #111522;
    color: #ffffff;
    font-size: 13px;
}
button {
    width: 100%;
    margin-top: 7px;
    padding: 13px;
    border: 0;
    border-radius: 10px;
    color: #ffffff;
    background: linear-gradient(135deg,#6366f1,#8b5cf6);
    font-size: 13px;
    font-weight: 800;
    cursor: pointer;
}
.back {
    display: block;
    margin-top: 18px;
    color: #818cf8;
    text-align: center;
    text-decoration: none;
    font-size: 11px;
}
</style>
</head>
<body>
<section class="card">
    <div class="logo">V</div>
    <div class="eyebrow">VR MART ADMINISTRATION</div>
    <h1>Administrator Login</h1>
    <p>
        Dedicated control-center access for VR Mart.
        Buyer and seller login cannot enter this area.
    </p>

    <% if (error != null) { %>
        <div class="error"><%= error %></div>
    <% } %>

    <form method="post"
          action="<%= request.getContextPath() %>/admin/login">
        <div class="field">
            <label for="username">Admin Username</label>
            <input id="username"
                   name="username"
                   type="text"
                   autocomplete="username"
                   required>
        </div>
        <div class="field">
            <label for="password">Password</label>
            <input id="password"
                   name="password"
                   type="password"
                   autocomplete="current-password"
                   required>
        </div>
        <button type="submit">
            Enter Admin Control Center
        </button>
    </form>

    <a class="back"
       href="<%= request.getContextPath() %>/">
        ← Back to VR Mart
    </a>
</section>
</body>
</html>
